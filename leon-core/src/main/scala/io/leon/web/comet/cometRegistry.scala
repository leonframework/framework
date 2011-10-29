/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet

import collection.mutable
import java.util.logging.Logger
import org.atmosphere.cpr.{BroadcastFilter, Meteor}
import org.atmosphere.util.XSSHtmlFilter
import com.google.inject.{Inject, Injector}
import dispatch.json.JsValue
import io.leon.resources.htmltagsprocessor.LeonTagRewriter
import javax.servlet.http.{HttpSession, HttpServletRequest}
import net.htmlparser.jericho.{OutputDocument, Source, Segment}


class ClientConnection(val clientId: String,
                       private var _uplink: Option[Meteor],
                       var connectTime: Long = System.currentTimeMillis()) {

  private val lock = new Object

  private val logger = Logger.getLogger(getClass.getName)

  private val queue = new mutable.ArrayBuffer[String] with mutable.SynchronizedBuffer[String]

  def uplink = _uplink

  def uplink_=(meteor: Option[Meteor]) {
    lock.synchronized {
      try {
        _uplink foreach { _.resume() }
      } catch {
        case e: Exception => logger.warning("Cannot resume existing comet connection.")
      }
      _uplink = meteor
      if (meteor.isDefined) {
        connectTime = System.currentTimeMillis()
        flushQueue()
      }
    }
  }

  def send(msg: String) {
    lock.synchronized {
      queue.append(msg)
      flushQueue()
    }
  }

  private def flushQueue() {
    while (queue.size > 0) {
      val success = sendPackage(queue(0))
      if (success) {
        queue.remove(0)
        logger.info("Successfully send message to page: " + clientId)
      } else {
        logger.info("Error while sending message to page: " + clientId)
        return
      }
    }
  }

  private def sendPackage(msg: String): Boolean = {
    try {
      uplink map { meteor =>
      val res = meteor.getAtmosphereResource.getResponse
        val writer = res.getWriter

        // send message
        writer.write(msg.toString)
        res.flushBuffer()

        // make sconnection was/is open
        writer.write(' ')
        res.flushBuffer()
        true
      } getOrElse false
    } catch {
      case _ => false
    }
  }

}

object Clients {

  def generateNewPageId(): String = {
    System.currentTimeMillis().toString
  }

  def generateNewClientId(session: HttpSession): String = {
    generateExistingClientId(session.getId, generateNewPageId())
  }

  def generateExistingClientId(sessionId: String, pageId: String): String = {
    sessionId + "__" + pageId
  }

}
class Clients {

  private val lock = new Object

  private val all = new mutable.ArrayBuffer[ClientConnection]

  private val byClientId = new mutable.HashMap[String, ClientConnection]

  def allClients = all.toList

  def getByClientId(id: String) = byClientId.get(id)

  def add(client: ClientConnection) {
    lock.synchronized {
      all.append(client)
      byClientId(client.clientId) = client
    }
  }

  def remove(client: ClientConnection) {
    lock.synchronized {
      all.remove(all.indexOf(client))
      byClientId.remove(client.clientId)
    }
  }

}

case class ClientSubscription(clientId: String,
                              topicId: String,
                              filterOn: List[String],
                              activeFilters: Map[String, String])


// TODO locking, sync, etc checken
class CometRegistry @Inject()(clients: Clients) {

  private val logger = Logger.getLogger(getClass.getName)

  private val checkClientsInterval = 1 * 1000

  private val reconnectTimeout = 10 * 1000

  private val disconnectTimeout = reconnectTimeout + 60 * 1000

  private val filter: List[BroadcastFilter] = List(new XSSHtmlFilter)

  private val clientSubscriptions = new mutable.HashMap[String, List[ClientSubscription]]()

  private var shouldStop = false

  private def createMeteor(req: HttpServletRequest): Meteor = {
    import scala.collection.JavaConverters._
    val meteor = Meteor.build(req, filter.asJava, null)
    meteor
  }

  def start() {
    shouldStop = false
    new Thread(new Runnable {
      def run() {
        logger.info("Checking for client connection timeouts every " + checkClientsInterval + "ms")
        while (!shouldStop) {
          Thread.sleep(checkClientsInterval)
          val now = System.currentTimeMillis
          clients.allClients foreach { client =>
            if (client.uplink.isDefined) {
              if ((now - client.connectTime) > reconnectTimeout) {
                logger.info("Client connection for [" + client.clientId + "] too old. Forcing reconnect.")
                client.uplink = None
              }
              if ((now - client.connectTime) > disconnectTimeout) {
                logger.info("Client connection for [" + client.clientId + "] too old. Forcing disconnect.")
                client.uplink foreach { _.resume() } // Should not be required, just to be safe
                clients.remove(client)
              }
            }
          }
        }
      }
    }).start()
  }
 
  def stop() {
    shouldStop = true
    clients.allClients foreach { _.uplink foreach { m => m.resume() } }
  }

  def registerClientSubscription(pageId: String, topicId: String, filterOn: List[String]) {
    val clientsForTopic = clientSubscriptions.getOrElse(topicId, Nil)
    val cs = ClientSubscription(pageId, topicId, filterOn, Map())
    clientSubscriptions += topicId -> (cs :: clientsForTopic)
  }

  def registerUplink(sessionId: String, pageId: String, req: HttpServletRequest) {
    val meteor = createMeteor(req)
    val clientId = Clients.generateExistingClientId(sessionId, pageId)
    logger.info("Registering meteor for client [" + clientId + "]")

    clients.getByClientId(clientId) match {
      case None => {
        // TODO This is only allowed during DEVELOPMENT mode. Add check!
        // This cant work right now, since we lost the ClientSubscription
        logger.info("No client connection found. Adding a new ClientConnection to client list.")
        clients.add(new ClientConnection(clientId, Some(meteor)))
      }
      case Some(cc) => {
        logger.info("Client connection found. Updating existing ClientConnection with new meteor.")
        cc.uplink = Some(meteor)
      }
    }
    meteor.suspend(-1, true)
    clients.getByClientId(clientId).get.send("\n")
  }

  def allClients: List[ClientConnection] =
    clients.allClients

  def clientById(clientId: String): Option[ClientConnection] =
    clients.getByClientId(clientId)


  def publish(topicId: String, filters: collection.Map[String, Any], data: Any) {
    val serialized = JsValue.toJson(JsValue(data))
    val message = """$$$MESSAGE$$${
      "type": "publishedEvent",
      "topicId": "%s",
      "data": %s
    }
    """.format(topicId, serialized).replace('\n', ' ') + "\n"

    val requiredFilterSet = filters.toSet

    clientSubscriptions.get(topicId) foreach { clientsForTopic =>
      clientsForTopic.foreach { client =>
        val clientFilterSet = client.activeFilters.toSet

        println("!!!## found client subscription for topic " + topicId)
        println("!!!## required filter set: " + requiredFilterSet)
        println("!!!## client's active filter set: " + clientFilterSet)

        if (requiredFilterSet == clientFilterSet) {

          val connection = clientById(client.clientId)
          connection.map { _.send(message) }
        }
      }
    }
  }

  def updateClientFilter(topicId: String, clientId: String, key: String, value: String) {
    // TODO check if filter key was declared earlier

    val cs = (clientSubscriptions(topicId).find { _.clientId == clientId}).get

    println("##### Updating filter for client subscription: " + cs.clientId)
    println("##### Current filters: " + cs.activeFilters)
    println("##### New filter: " + key + "=" + value)

    val updatedFilters = cs.activeFilters + (key -> value)
    val updatedCs = cs.copy(activeFilters = updatedFilters)
    println("##### New filters list: " + updatedCs.activeFilters)

    val updatedList = clientSubscriptions(topicId).updated(clientSubscriptions(topicId).indexOf(cs), updatedCs)
    clientSubscriptions(topicId) = updatedList
  }

}


class CometSubscribeTagRewriter @Inject()(injector: Injector,
                                          clients: Clients,
                                          cometRegistry: CometRegistry) extends LeonTagRewriter {

  private def request = injector.getInstance(classOf[HttpServletRequest])

  def process(doc: Source): Seq[OutputDocument => Unit] = {
    import scala.collection.JavaConverters._

    val subscribeTags = doc.getAllStartTags("leon:subscribe")
    if(subscribeTags.size() == 0)
      return Nil

    val pageId = Option(request.getAttribute("pageId")) map {
      _.toString
    } getOrElse {
      Clients.generateNewPageId()
    }
    request.setAttribute("pageId", pageId)

    val clientId = Clients.generateExistingClientId(request.getSession.getId, pageId)
    clients.add(new ClientConnection(clientId, None, 0))

    for (subscribeTag <- subscribeTags.asScala) yield {

      val topicId = Option(subscribeTag.getAttributeValue("topic")) getOrElse sys.error("attribute topic is missing in <leon:subscribe>!")
      val filterOn = Option(subscribeTag.getAttributeValue("filterOn")) map { _.split(",").toList map { _.trim() } } getOrElse Nil
      val handlerFn = Option(subscribeTag.getAttributeValue("handlerFn")) getOrElse sys.error("attribute handlerFn is missing in <leon:subscribe>!")

      cometRegistry.registerClientSubscription(clientId, topicId, filterOn)

      val scriptToInclude =
        ("""
        |<script type="text/javascript">
        |  leon.comet.addHandler("%s", %s);
        |  leon.comet.connect(%s);
        |</script>
        """).stripMargin.format(topicId, handlerFn, pageId)

      out: OutputDocument => out.replace(subscribeTag, scriptToInclude)
    }
  }
}

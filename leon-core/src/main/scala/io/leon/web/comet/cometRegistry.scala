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
import javax.servlet.http.HttpServletRequest
import org.atmosphere.cpr.{BroadcastFilter, Meteor}
import org.atmosphere.util.XSSHtmlFilter
import com.google.inject.{Inject, Injector}
import dispatch.json.JsValue
import io.leon.resources.leon.LeonTagRewriter
import net.htmlparser.jericho.{Source, Segment}


class ClientConnection(val pageId: String,
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
        logger.info("Successfully send message to page: " + pageId)
      } else {
        logger.info("Error while sending message to page: " + pageId)
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

class Clients {

  private val lock = new Object

  private val all = new mutable.ArrayBuffer[ClientConnection]

  private val byPageId = new mutable.HashMap[String, ClientConnection]

  def allClients = all.toList

  def clientByPageId(id: String) = byPageId.get(id)

  def add(client: ClientConnection) {
    lock.synchronized {
      all.append(client)
      byPageId(client.pageId) = client
    }
  }

  def remove(client: ClientConnection) {
    lock.synchronized {
      all.remove(all.indexOf(client))
      byPageId.remove(client.pageId)
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
                logger.info("Client connection for [" + client.pageId + "] too old. Forcing reconnect.")
                client.uplink = None
              }
              if ((now - client.connectTime) > disconnectTimeout) {
                logger.info("Client connection for [" + client.pageId + "] too old. Forcing disconnect.")
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
    // TODO es müsste jetzt schon ne CC geben, aber ohne uplink
    // TODO security check

    val meteor = createMeteor(req)
    val id = sessionId + "__" + pageId
    logger.info("Adding Client comet connection: " + id)
    clients.clientByPageId(id) match {
      case None => {
        //logger.info("Creating new client connection.")
        //clients.add(new ClientConnection(id, Some(meteor)))

        // darf nicht passiert
        throw new RuntimeException("ARG!!!")
      }
      case Some(cc) => {
        logger.info("Updating existing client connection.")
        cc.uplink = Some(meteor)
      }
    }
    meteor.suspend(-1, true)

    clients.clientByPageId(id).get.send("\n")
  }

  def allClients: List[ClientConnection] =
    clients.allClients

  def clientById(clientId: String): Option[ClientConnection] =
    clients.clientByPageId(clientId)

  def publish(topicId: String, filters: Map[String, Any], data: Any) {
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

  def process(doc: Source): Seq[(Segment, String)] = {
    import scala.collection.JavaConverters._

    val subscribeTags = doc.getAllStartTags("leon:subscribe")
    if(subscribeTags.size() == 0)
      return Nil

    val pageId = Option(request.getAttribute("pageId")) map {
      _.toString
    } getOrElse {
      System.currentTimeMillis().toString // TODO add MD5 bla bla
    }
    request.setAttribute("pageId", pageId)

    val clientId = request.getSession.getId + "__" + pageId
    clients.add(new ClientConnection(clientId, None, 0))

    for (subscribeTag <- subscribeTags.asScala) yield {

      // TODO make sure the tag was defined inside <body>

      val topicId = subscribeTag.getAttributeValue("topic")
      val filterOn = subscribeTag.getAttributeValue("filterOn").split(",") map { _.trim() }
      val handlerFn = subscribeTag.getAttributeValue("handlerFn")

      cometRegistry.registerClientSubscription(clientId, topicId, filterOn.toList)

      val scriptToInclude =
        ("""
        |<script type="text/javascript">
        |  leon.comet.addHandler("%s", %s);
        |  leon.comet.connect(%s);
        |</script>
        """).stripMargin.format(topicId, handlerFn, pageId)

      subscribeTag -> scriptToInclude
    }
  }
}

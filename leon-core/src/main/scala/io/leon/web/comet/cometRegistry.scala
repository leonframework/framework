/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet

import java.util.logging.Logger
import org.atmosphere.cpr.{BroadcastFilter, Meteor}
import org.atmosphere.util.XSSHtmlFilter
import com.google.inject.{Inject, Injector}
import dispatch.json.JsValue
import io.leon.resources.htmltagsprocessor.LeonTagRewriter
import javax.servlet.http.{HttpSession, HttpServletRequest}
import net.htmlparser.jericho.{OutputDocument, Source}
import collection.Iterable
import java.util.concurrent.ConcurrentHashMap
import java.lang.IllegalStateException
import java.util.ArrayList
import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}
import com.google.gson.Gson


class ClientConnection(val clientId: String,
                       var meteor: Option[Meteor]) {

  private val logger = Logger.getLogger(getClass.getName)

  // message queue
  private val queue = new ArrayList[(Int, String)]

  // topicID -> list of filters
  private val topicSubscriptions = new ConcurrentHashMap[String, List[String]] 

  // topic name#filter name -> filter value
  private val topicActiveFilters = new ConcurrentHashMap[String, String]

  private val nextMessageId = new AtomicInteger(0)

  private val messageSendIndex = new AtomicInteger(0)

  private val lock = new Object

  var connectTime = System.currentTimeMillis()
  
  def setNewMeteor(lastMessageReceived: Int, newMeteor: Meteor): Unit = lock.synchronized {
    logger.info("ClientConnection received a new meteor instance. Last message received: " + lastMessageReceived)
    resumeAndRemoveUplink()
    meteor = Some(newMeteor)
    connectTime = System.currentTimeMillis()

    // First connect or empty queue. Nothing to do here.
    if (lastMessageReceived != -1) {
      while (queue.size() > 0 && queue.get(0)._1 != lastMessageReceived) {
        queue.remove(0)
      }
      if (queue.size() > 0) {
        queue.remove(0)
      }
    }
    messageSendIndex.set(0)
    flushQueue()
  }

  def resumeAndRemoveUplink() {
    try {
      meteor map { m =>
        logger.info("Removing uplink for ClientConnection")
        m.resume()
      }
    } catch {
      case e: Exception => logger.warning("Cannot resume existing comet connection.")
    }
    meteor = None
  }
  
  /**
   * Add the message to the queue and flush the queue.
   */
  def enqueue(topicName: String, data: String) = lock.synchronized {
    val id = nextMessageId.getAndIncrement
    val message = """$$$MESSAGE$$${
      "messageId": %s,
      "topicName": "%s",
      "data": %s
    }
    """.format(id, topicName, data).replace('\n', ' ') + "\n"
    queue.add(id -> message)
    flushQueue()
  }

  /**
   * Send all messages waiting in the queue. 
   */
  private def flushQueue(): Unit = lock.synchronized {
    if (meteor.isEmpty) {
      return
    }
    while (queue.size() > messageSendIndex.get()) {
      val success = sendPackage(queue.get(messageSendIndex.getAndIncrement))
      if (!success) {
        logger.info("Error while flushing queue. Aborting and waiting for a new client connection.")
        meteor = None
        return
      }
    }
  }

  private def sendPackage(msg: (Int, String)): Boolean = {
    val data = msg._2
    try {
      meteor map { meteor =>
        val res = meteor.getAtmosphereResource.getResponse
        val writer = res.getWriter
        writer.write(data)
        //logger.info("Sending package: " + data)
        res.flushBuffer()
        true
      } getOrElse false
    } catch {
      case e: Throwable => {
        logger.info("Could not send comet message: ID [%s], Error message [%s]".format(msg._1, e.getMessage))
        false
      }
    }
  }

  def registerTopicSubscriptions(topicName: String, filterOn: List[String]) {
    topicSubscriptions.put(topicName, filterOn)
  }

  def updateTopicFilter(topicName: String,  filterName: String, filterValue: String) {
    // check that the client declared the filter
    if (!topicSubscriptions.get(topicName).contains(filterName)) {
      throw new IllegalStateException(
        "Security issue: Can not set the filter [%s] for topic [%s] to value [%s] since client [%s] did not declare this on connect.".format(filterName, topicName, filterValue, clientId)
      )
    }

    // update the filter
    val key = topicName + "#" + filterName
    topicActiveFilters.put(key, filterValue)
  }

  def hasFilterValue(topicName: String, filterName: String, requiredFilterValue: String): Boolean = {
    val key = topicName + "#" + filterName
    topicActiveFilters.get(key) == requiredFilterValue
  }

}

object Clients {

  private val ids = new AtomicLong

  def generateNewPageId(): String = {
    // TODO check deployment mode to use the appropriate way
    //System.currentTimeMillis().toString // more save
    ids.getAndIncrement.toString // useful more debugging
  }

  def generateNewClientId(session: HttpSession): String = {
    generateExistingClientId(session, generateNewPageId())
  }

  def generateExistingClientId(session: HttpSession, pageId: String): String = {
    session.getId + "__" + pageId
  }

}
class Clients {
  
  import scala.collection.JavaConverters._

  private val byClientId = new ConcurrentHashMap[String,  ClientConnection]

  def allClients: Iterable[ClientConnection] = byClientId.values().asScala

  def getByClientId(id: String): Option[ClientConnection] = {
    if (byClientId.containsKey(id)) Some(byClientId.get(id)) else None
  }

  def add(client: ClientConnection) = byClientId.put(client.clientId, client)

  def remove(client: ClientConnection) = byClientId.remove(client.clientId)

}


class CometRegistry @Inject()(clients: Clients) {

  private val logger = Logger.getLogger(getClass.getName)

  private val checkClientsInterval = 1 * 1000

  // the time in seconds, when the server will close the connection to force a reconnect
  private val reconnectTimeout = 30 * 1000

  // after this time, the server will treat the client as disconnected and remove all information
  private val disconnectTimeout = reconnectTimeout + 60 * 1000

  private val filter: List[BroadcastFilter] = List(new XSSHtmlFilter)

  //private val clientSubscriptions = new mutable.HashMap[String, List[ClientSubscription]]()

  private var shouldStop = false

  private def createMeteor(req: HttpServletRequest): Meteor = {
    import scala.collection.JavaConverters._
    Meteor.build(req, filter.asJava, null)
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
            if (client.meteor.isDefined) {
              if ((now - client.connectTime) > reconnectTimeout) {
                logger.info("Client connection for [" + client.clientId + "] too old. Forcing reconnect.")
                client.resumeAndRemoveUplink()
              }
              if ((now - client.connectTime) > disconnectTimeout) {
                logger.info("Client connection for [" + client.clientId + "] too old. Forcing disconnect.")
                client.resumeAndRemoveUplink()
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
    clients.allClients foreach { _.resumeAndRemoveUplink() }
  }

  def registerUplink(req: HttpServletRequest, pageId: String, lastMessageId: Int) {
    val meteor = createMeteor(req)
    val clientId = Clients.generateExistingClientId(req.getSession, pageId)
    logger.info("Registering meteor for client [" + clientId + "]")

    clients.getByClientId(clientId) match {
      case None => {
        throw new IllegalStateException(
          "Can not register client uplink because the client is unknown. In case you restarted the server, you need to refresh the browser page since the list of clients stored on the server is not (yet) persistent."
        )
      }
      case Some(cc) => {
        logger.info("Client connection found. Updating existing ClientConnection with new meteor.")
        meteor.suspend(-1, true)
        val res = meteor.getAtmosphereResource.getResponse
        val writer = res.getWriter
        writer.write("\n")
        writer.flush()
        cc.setNewMeteor(lastMessageId, meteor)
      }
    }
  }

  def publish(topicName: String, filters: java.util.Map[String, Any], data: String) {
    import scala.collection.JavaConverters._

    val requiredFilters = filters.asScala
    val matchingClients = clients.allClients filter { c =>
      requiredFilters forall { case (filterName, filterValue) =>
        c.hasFilterValue(topicName, filterName, filterValue.toString)
      }
    }

    if (matchingClients.isEmpty) {
      logger.info("No clients found for topic [%s], filter map [%s]".format(topicName, requiredFilters))
    } else {
      logger.info("Found [%s] clients for filter map [%s].".format(matchingClients.size, requiredFilters))
    }

    //val dataSerialized = JsValue.toJson(JsValue(data)) // TODO use gson
    val dataSerialized = new Gson().toJson(data)

    matchingClients foreach { _.enqueue(topicName, dataSerialized) }
  }

  def updateClientFilter(topicId: String, clientId: String, filterName: String, filterValue: String) {
    logger.info("Updating topic filter for client [%s], topic [%s], filter name [%s], filter value [%s].".format(clientId, topicId, filterName, filterValue))
    clients.getByClientId(clientId).get.updateTopicFilter(topicId, filterName, filterValue)
  }

}


class CometSubscribeTagRewriter @Inject()(injector: Injector,
                                          clients: Clients,
                                          cometRegistry: CometRegistry) extends LeonTagRewriter {

  private val logger = Logger.getLogger(getClass.getName)

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

    val clientId = Clients.generateExistingClientId(request.getSession(true), pageId)
    val cc = new ClientConnection(clientId, None)
    clients.add(cc)
    logger.info("Registering client [%s].".format(cc.clientId))

    for (subscribeTag <- subscribeTags.asScala) yield {

      val topicId = Option(subscribeTag.getAttributeValue("topic")) getOrElse sys.error("attribute topic is missing in <leon:subscribe>!")
      val filterOn = Option(subscribeTag.getAttributeValue("filterOn")) map { _.split(",").toList map { _.trim() } } getOrElse Nil
      val handlerFn = Option(subscribeTag.getAttributeValue("handlerFn")) getOrElse sys.error("attribute handlerFn is missing in <leon:subscribe>!")

      cc.registerTopicSubscriptions(topicId, filterOn)

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

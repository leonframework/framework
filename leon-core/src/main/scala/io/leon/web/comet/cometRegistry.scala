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
import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue}
import java.util.concurrent.atomic.AtomicLong
import java.lang.IllegalStateException


class ClientConnection(val clientId: String,
                       private var _uplink: Option[Meteor]) {

  private val logger = Logger.getLogger(getClass.getName)

  // message queue
  private val queue = new ConcurrentLinkedQueue[String]

  // topicID -> list of filters
  private val topicSubscriptions = new ConcurrentHashMap[String, List[String]] 

  // topic name#filter name -> filter value
  private val topicActiveFilters = new ConcurrentHashMap[String, String]

  private val setNewUplinkLock = new Object
  private val flushLock = new Object

  // the time when the client connected
  var connectTime = System.currentTimeMillis()
  
  def uplink: Meteor = _uplink.get

  def uplink_=(meteor: Meteor) = setNewUplinkLock.synchronized {
    try {
      _uplink map { m => 
        logger.info("ClientConnection received a new meteor instance. Resuming the current meteor.")
        m.resume()
      }
    } catch {
      case e: Exception => logger.warning("Cannot resume existing comet connection.")
    }
    _uplink = Some(meteor)
    connectTime = System.currentTimeMillis()

    // We need this! Otherwise we loose the first message in the queue after a reconnect.
    //sendStringAndFlush(" ")
    //flushQueue()
  }
  
  def resumeAndRemoveUplink() {
    try {
      _uplink map { m =>
        logger.info("Removing uplink for ClientConnection")
        m.resume()
      }
    } catch {
      case e: Exception => logger.warning("Cannot resume existing comet connection.")
    }
    _uplink = None
  }
  
  def hasActiveUplink: Boolean = _uplink.isDefined

  /**
   * Add the message to the queue and flush the queue.
   */
  def send(message: String) {
    queue.add(message)
    flushQueue()
  }

  /**
   * Send all messages waiting in the queue. 
   * This method will abort once it failed sending a message.
   * 
   * @return true, if all messages have been send successfully, false otherwise. 
   */
  private def flushQueue(): Boolean = flushLock.synchronized {
    var nextMessage = queue.peek()
    while (nextMessage != null) {
      val success = sendPackage(nextMessage)
      if (success) {
        val messageSend = queue.remove()
        nextMessage = queue.peek()
        logger.info("Successfully send message [" + messageSend + "] to page: " + clientId)
      } else {
        logger.info("Error while sending message [" + nextMessage + "] to page: " + clientId)
        return false
      }
    }
    true
  }

  private def sendStringAndFlush(msg: String) = synchronized {
    _uplink map { meteor =>
      val res = meteor.getAtmosphereResource.getResponse
      val writer = res.getWriter
      writer.write(msg.toString)
      res.flushBuffer()
    }
  }

  private def sendPackage(msg: String): Boolean = synchronized {
    try {
      _uplink map { meteor =>
        val res = meteor.getAtmosphereResource.getResponse
        val writer = res.getWriter

        // send message
        writer.write(msg.toString)
        res.flushBuffer()

        // make sure connection was/is open
        writer.write(" ")
        res.flushBuffer()
        true
      } getOrElse false
    } catch {
      case e: Throwable => {
        logger.info("Could not send comet data: " + e.getMessage)
        _uplink = None
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
            if (client.hasActiveUplink) {
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

  def registerUplink(req: HttpServletRequest, pageId: String) {
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
        writer.write("\n\n ")
        writer.flush()

        cc.uplink = meteor
        //cc.send("\n")
      }
    }
  }

  def publish(topicName: String, filters: java.util.Map[String, Any], data: String) {
    import scala.collection.JavaConverters._

    val dataSerialized = JsValue.toJson(JsValue(data))

    val message = """$$$MESSAGE$$${
      "type": "publishedEvent",
      "topicName": "%s",
      "data": %s
    }
    """.format(topicName, dataSerialized).replace('\n', ' ') + "\n"

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

    matchingClients foreach { _.send(message) }
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

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet

import org.atmosphere.cpr.{BroadcastFilter, Meteor}
import org.atmosphere.util.XSSHtmlFilter
import com.google.inject.Inject
import javax.servlet.http.HttpServletRequest
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import io.leon.config.ConfigMap
import io.leon.web.TopicsService
import java.util.Map
import com.google.common.collect.Maps

class CometRegistry @Inject()(clients: Clients, gson: Gson, configMap: ConfigMap) extends TopicsService {

  private val logger = LoggerFactory.getLogger(getClass.getName)

  private val checkClientsInterval = 1 * 1000

  // the time in seconds, when the server will close the connection to force a reconnect
  private val reconnectTimeout = 30 * 1000

  // after this time, the server will treat the client as disconnected and remove all information
  private val disconnectTimeout = reconnectTimeout + 60 * 1000

  private val filter: List[BroadcastFilter] = List(new XSSHtmlFilter)

  //private val clientSubscriptions = new mutable.HashMap[String, List[ClientSubscription]]()

  @volatile
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

  def registerUplink(req: HttpServletRequest, clientId: String, lastMessageId: Int) {
    val meteor = createMeteor(req)
    logger.info("Registering meteor for client [" + clientId + "]")

    clients.getByClientId(clientId) match {
      case None => {
        if (configMap.isDevelopmentMode) {
          logger.debug("Re-registering client comet connect request since we are in development mode.")
          val cc = new ClientConnection(clientId, None)
          clients.add(cc)
          registerUplink(req, clientId, lastMessageId)
        } else {
          logger.debug(
            "Can not register client uplink because the client is unknown. In case you restarted the server, you need to refresh the browser page since the list of clients stored on the server is not (yet) persistent.")
        }
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

  def publish(topicName: String, filters: java.util.Map[String, AnyRef], data: String) {
    import scala.collection.JavaConverters._

    val requiredFilters = filters.asScala
    val matchingClients = clients.allClients filter { c =>
      if (configMap.isDevelopmentMode && topicName.startsWith("leon.developmentMode")) {
        true
      } else {
        requiredFilters forall { case (filterName, filterValue) =>
          c.hasFilterValue(topicName, filterName, filterValue.toString)
        }
      }
    }

    if (matchingClients.isEmpty) {
      logger.info("No clients found for topic [%s], filter map [%s]".format(topicName, requiredFilters))
    } else {
      logger.info("Found [%s] clients for filter map [%s].".format(matchingClients.size, requiredFilters))
    }

    val dataSerialized = new Gson().toJson(data)
    matchingClients foreach { _.enqueue(topicName, dataSerialized) }
  }

  def updateClientFilter(topicId: String, clientId: String, filterName: String, filterValue: String) {
    logger.info("Updating topic filter for client [%s], topic [%s], filter name [%s], filter value [%s].".format(clientId, topicId, filterName, filterValue))
    clients.getByClientId(clientId).get.updateTopicFilter(topicId, filterName, filterValue)
  }

  def send(topicId: String, data: AnyRef) {
    send(topicId, data, Maps.newHashMap())
  }

  def send(topicId: String, data: AnyRef, filters: Map[String, AnyRef]) {
    publish(topicId, filters, gson.toJson(data))
  }

}

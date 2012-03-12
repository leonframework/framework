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
import javax.servlet.http.HttpServletRequest
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import io.leon.config.ConfigMap
import io.leon.web.TopicsService
import java.util.Map
import com.google.common.collect.Maps
import com.google.inject.{Injector, Inject}
import io.leon.guice.GuiceUtils

class CometRegistry @Inject()(injector: Injector,
                              clients: Clients,
                              gson: Gson,
                              configMap: ConfigMap) extends TopicsService {

  import scala.collection.JavaConverters._

  private val logger = LoggerFactory.getLogger(getClass.getName)

  private val checkClientsInterval = 10 * 1000

  // the time in seconds, when the server will close the connection to force a reconnect
  private val reconnectTimeout = 30 * 1000

  // after this time, the server will treat the client as disconnected and remove all information
  private val disconnectTimeout = reconnectTimeout + 60 * 1000

  private val filter: List[BroadcastFilter] = List(new XSSHtmlFilter)

  private val allDefinedTopics = (GuiceUtils.getByType(injector, classOf[Topic]).asScala map { b =>
    b.getProvider.get().getName
  }).toSet

  @volatile
  private var shouldStop = false

  private def createMeteor(req: HttpServletRequest): Meteor = {
    Meteor.build(req, filter.asJava, null)
  }

  private def checkIfTopicIsConfigured(topicName: String) {
    if (!allDefinedTopics.contains(topicName)) {
      throw new IllegalArgumentException(
        "Topic [" + topicName + "] was not configured. Add e.g. 'addTopic(\""
          + topicName + "\");' to your module configuration.")
    }
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
                logger.info("Client connection for [" + client.clientId + "] too old. Removing client information.")
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
    logger.debug("Registering meteor for client [" + clientId + "]")

    val meteor = createMeteor(req)
    meteor.suspend(-1, true)
    val res = meteor.getAtmosphereResource.getResponse
    val writer = res.getWriter
    writer.write("\n")
    writer.flush()

    clients.getByClientId(clientId) match {
      case None => {
        logger.debug("Registering new meteor for client [" + clientId + "]")
        val cc = new ClientConnection(clientId, Some(meteor))
        clients.add(cc)
      }
      case Some(cc) => {
        logger.debug("Updating existing meteor for client [" + clientId + "]")
        cc.setNewMeteor(lastMessageId, meteor)
      }
    }
  }

  def publish(topicName: String, filters: java.util.Map[String, AnyRef], data: String) {
    checkIfTopicIsConfigured(topicName)

    val requiredFilters = filters.asScala
    val matchingClients = clients.allClients filter { c =>
      requiredFilters forall { case (filterName, filterValue) =>
        c.hasFilterValue(topicName, filterName, filterValue.toString)
      }
    }

    if (matchingClients.isEmpty) {
      logger.debug("No clients found for topic [%s], filter map [%s]".format(topicName, requiredFilters))
    } else {
      logger.debug("Found [%s] clients for filter map [%s].".format(matchingClients.size, requiredFilters))
      val dataSerialized = new Gson().toJson(data)
      matchingClients foreach { _.enqueue(topicName, dataSerialized) }
    }
  }

  def updateClientFilter(topicId: String, clientId: String, filterName: String, filterValue: String) {
    checkIfTopicIsConfigured(topicId)

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

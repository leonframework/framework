/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet

import annotations.CometThreadPoolExecutor
import org.atmosphere.cpr.{BroadcastFilter, Meteor}
import org.atmosphere.util.XSSHtmlFilter
import javax.servlet.http.HttpServletRequest
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import io.leon.config.ConfigMap
import java.util.Map
import scala.collection.JavaConverters._
import com.google.common.collect.Maps
import io.leon.guice.GuiceUtils
import io.leon.web.{TopicsSend, TopicsService}
import com.google.inject.{Provider, Injector, Inject}
import java.util.concurrent.Executor

class CometRegistry @Inject()(injector: Injector,
                              @CometThreadPoolExecutor executor: Executor,
                              clients: Clients,
                              gson: Gson,
                              configMap: ConfigMap,
                              httpServletRequestProvider: Provider[HttpServletRequest])
  extends TopicsService with ClientSubscriptions {

  private val logger = LoggerFactory.getLogger(getClass.getName)

  // the time seconds, how often the server will check for client timeouts
  private val checkClientsInterval = 10 * 1000

  // the time in seconds, when the server will close the connection to force a reconnect
  private val reconnectTimeout = 30 * 1000

  // after this time, the server will treat the client as disconnected and remove all information
  private val disconnectTimeout = reconnectTimeout + (30 * 1000)

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
        "Topic [" + topicName + "] was not configured. Add 'addTopic(\""
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

          // Create a copy of the list and check all clients
          clients.getAllClients.asScala foreach { client =>
            logger.trace("Checking client [{}] for connection timeouts.", client.clientId)
            if ((now - client.connectTime) > reconnectTimeout) {
              if (client.meteor.isDefined) {
                logger.debug("Client connection for [" + client.clientId + "] too old. Forcing reconnect.")
                client.resumeAndRemoveUplink()
              }
            }
            if ((now - client.connectTime) > disconnectTimeout) {
              logger.debug("Client connection for [" + client.clientId + "] too old. Removing client information.")
              client.resumeAndRemoveUplink()
              clients.remove(client)
            }
          }
        }
      }
    }).start()
  }
 
  def stop() {
    shouldStop = true
    clients.getAllClients.asScala foreach { _.resumeAndRemoveUplink() }
  }

  def registerUplink(req: HttpServletRequest, clientId: String, lastMessageId: Int) = synchronized {
    logger.debug("Registering meteor for client [" + clientId + "]")

    val meteor = createMeteor(req)
    meteor.suspend(-1, true)

    val res = meteor.getAtmosphereResource.getResponse
    val writer = res.getWriter
    writer.write("\n")
    writer.flush()

    clients.getByClientIdOption(clientId) match {
      case None => {
        logger.trace("Registering   new meteor for client [" + clientId + "]")
        val cc = new ClientConnection(clientId, Some(meteor))
        clients.add(cc)
      }
      case Some(cc) => {
        logger.trace("Updating existing meteor for client [" + clientId + "]")
        cc.setNewMeteor(lastMessageId, meteor)
      }
    }
  }

  def publish(targets: List[ClientConnection],
              clientFilter: ClientConnection => Boolean,
              topicName: String,
              filters: java.util.Map[String, _],
              data: String) {

    Topic.checkTopicName(topicName);

    val job = new Runnable {
      def run() {
        val requiredFilters = filters.asScala
        val matchingClients = targets filter { c =>
        // RR: I used this ugly code style to use the short-circuit test
          (c.hasSubscribedTopic(topicName)) && (
            clientFilter(c)) && (
            requiredFilters forall { case (filterName, filterValue) =>
              c.hasFilterValue(topicName, filterName, filterValue.toString)
            })
        }

        if (matchingClients.isEmpty) {
          logger.debug("No clients found for topic [%s] and filter [%s].".format(topicName, requiredFilters))
          if (logger.isTraceEnabled) {
            val all = clients.getAllClients.asScala
            for (c <- all) {
              logger.trace("\n" + c.getDebugStateString)
            }
          }
        } else {
          logger.debug("Found [%s] clients for topic [{}] with filter map [%s].".format(
            matchingClients.size, topicName, requiredFilters))
          val dataSerialized = new Gson().toJson(data)
          matchingClients foreach { _.enqueue(topicName, dataSerialized) }
        }
      }
    }
    executor.execute(job)
  }

  def updateClientFilter(topicId: String, clientId: String, filterName: String, filterValue: String) {
    checkIfTopicIsConfigured(topicId)
    clients.getOrCreateByClientId(clientId).updateTopicFilter(topicId, filterName, filterValue)
  }

  def send(topicId: String, data: AnyRef) {
    send(topicId, data, Maps.newHashMap[String, AnyRef]())
  }

  def send(topicId: String, data: AnyRef, filters: Map[String, _]) {
    publish(clients.getAllClients.asScala.toList, _ => true, topicId, filters, gson.toJson(data))
  }

  override def toOtherSessions = new TopicsSend {
    def send(topicId: String, data: AnyRef) {
      send(topicId, data, Maps.newHashMap[String, AnyRef]())
    }
    def send(topicId: String, data: AnyRef, filters: Map[String, _]) {
      val filter = (clientConnection: ClientConnection) => {
        clientConnection.meteor match {
          case None => false
          case Some(cc) => {
            val ccSessionId = cc.getAtmosphereResource.getRequest.getSession.getId
            val currentSessionId = httpServletRequestProvider.get().getSession.getId
            !ccSessionId.equals(currentSessionId)
          }
        }
      }
      publish(clients.getAllClients.asScala.toList, filter, topicId, filters, gson.toJson(data))
    }
  }

  override def toCurrentSession = new TopicsSend {
    def send(topicId: String, data: AnyRef) {
      send(topicId, data, Maps.newHashMap[String, AnyRef]())
    }
    def send(topicId: String, data: AnyRef, filters: Map[String, _]) {
      val filter = (clientConnection: ClientConnection) => {
        clientConnection.meteor match {
          case None => false
          case Some(cc) => {
            val ccSessionId = cc.getAtmosphereResource.getRequest.getSession.getId
            val currentSessionId = httpServletRequestProvider.get().getSession.getId
            ccSessionId.equals(currentSessionId)
          }
        }
      }
      publish(clients.getAllClients.asScala.toList, filter, topicId, filters, gson.toJson(data))
    }
  }

  def getAllClientSubscriptions: java.util.List[_ <: ClientSubscriptionInformation] = {
    clients.getAllClients
  }
}

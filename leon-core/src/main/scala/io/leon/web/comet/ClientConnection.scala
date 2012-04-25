/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet

import org.atmosphere.cpr.Meteor
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.ArrayList
import scala.collection.JavaConverters._

class ClientConnection(val clientId: String,
                       var meteor: Option[Meteor]) {

  private val logger = LoggerFactory.getLogger(getClass.getName)

  // message queue
  private val queue = new ArrayList[(Int, String)]

  // topicID->true
  private val topicSubscriptions = new ConcurrentHashMap[String, Boolean]

  // topicName#filterName -> filterValue
  private val topicActiveFilters = new ConcurrentHashMap[String, String]

  private val nextMessageId = new AtomicInteger(0)

  private val messageSendIndex = new AtomicInteger(0)

  private val lock = new Object

  var connectTime = System.currentTimeMillis()

  def setNewMeteor(lastMessageReceived: Int, newMeteor: Meteor): Unit = lock.synchronized {
    logger.debug("ClientConnection received a new meteor instance. Last message received: " + lastMessageReceived)
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
      meteor map {
        m =>
          logger.debug("Removing uplink for ClientConnection")
          m.resume()
      }
    } catch {
      case e: Exception => // ignore
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
        logger.debug("Error while flushing queue. Aborting and waiting for a new client connection.")
        meteor = None
        return
      }
    }
  }

  private def sendPackage(msg: (Int, String)): Boolean = {
    val data = msg._2
    try {
      meteor map {
        meteor =>
          val res = meteor.getAtmosphereResource.getResponse
          val writer = res.getWriter
          writer.write(data)
          res.flushBuffer()
          true
      } getOrElse false
    } catch {
      case e: Throwable => {
        logger.debug("Could not send comet message: ID [%s], Error message [%s]".format(msg._1, e.getMessage))
        false
      }
    }
  }

  def updateTopicFilter(topicName: String, filterName: String, filterValue: String) = {
    logger.debug("Updating topic filter for client [%s], topic [%s], filter name [%s], filter value [%s].".format(
      clientId, topicName, filterName, filterValue))

    // register topic subscription
    topicSubscriptions.put(topicName, true)

    if (filterName != null) {
      // update the filter
      val key = topicName + "#" + filterName
      topicActiveFilters.put(key, filterValue)
    }

    if (logger.isTraceEnabled) {
      logger.trace("\n" + getDebugStateString())
    }
  }

  def getAllSubscribedTopics(): Set[String] = {
    topicSubscriptions.keySet().asScala.toSet
  }

  def hasSubscribedTopic(topicName: String): Boolean = {
    topicSubscriptions.containsKey(topicName)
  }

  def hasFilterValue(topicName: String, filterName: String, requiredFilterValue: String): Boolean = {
    val key = topicName + "#" + filterName
    topicActiveFilters.get(key) == requiredFilterValue
  }

  def getDebugStateString(): String = {
    val sb = new StringBuilder
    sb.append("Client: " + clientId + ", subscriptions:" + "\n")
    sb.append("  Topics:" + "\n")
    for (topic <- topicSubscriptions.asScala.keys) {
      sb.append("    * topic: '" + topic + "'\n")
    }
    sb.append("  Filters:" + "\n")
    for (filter <- topicActiveFilters.asScala.keys) {
      sb.append("    * filter name: '" + filter + "' value: '" + topicActiveFilters.get(filter) + "'\n")
    }
    sb.toString()
  }

}

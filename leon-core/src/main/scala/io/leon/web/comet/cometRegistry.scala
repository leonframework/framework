/*
 * Copyright (c) 2010 WeigleWilczek and others.
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

class ClientConnection(val pageId: String, private var _uplink: Meteor, var lastPing: Long) {

  val sessionId = uplink.getAtmosphereResource.getRequest.getSession.getId

  private val lock = new Object

  private val logger = Logger.getLogger(getClass.getName)

  private val queue = new mutable.ArrayBuffer[String] with mutable.SynchronizedBuffer[String]

  def uplink = _uplink

  def uplink_=(meteor: Meteor) {
    lock.synchronized {
      _uplink.resume()
      _uplink = meteor
      flushQueue()
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
      val res = uplink.getAtmosphereResource.getResponse
      val writer = res.getWriter

      // send message
      writer.write(msg.toString)
      res.flushBuffer()

      // make sure connection was/is open
      writer.write(' ')
      res.flushBuffer()
      true
    } catch {
      case _ => false
    }
  }

}

class Clients {

  private val lock = new Object

  private val all = new mutable.ArrayBuffer[ClientConnection]

  private val byPageId = new mutable.HashMap[String, ClientConnection]

  private val bySession = new mutable.HashMap[String, mutable.ArrayBuffer[ClientConnection]]

  def allClients = all.toList

  def clientByPageId(id: String) = byPageId.get(id)

  def clientsBySessionId(id: String) = bySession.getOrElse(id, new mutable.ArrayBuffer[ClientConnection])

  def add(client: ClientConnection) {
    lock.synchronized {
      all.append(client)
      byPageId(client.pageId) = client
      bySession.getOrElseUpdate(client.sessionId, new mutable.ArrayBuffer[ClientConnection]).append(client)
    }
  }

  def remove(client: ClientConnection) {
    lock.synchronized {
      all.remove(all.indexOf(client))
      byPageId.remove(client.pageId)
      bySession.getOrElseUpdate(client.sessionId, new mutable.ArrayBuffer[ClientConnection])
      bySession(client.sessionId).remove(bySession(client.sessionId).indexOf(client))
    }
  }

}

class CometRegistry {

  private val logger = Logger.getLogger(getClass.getName)

  private val checkClientsInterval = 1000 * 10 // 10 seconds

  private val clientsTimeout = 1000 * 60 // 1 minute

  private val filter: List[BroadcastFilter] = List(new XSSHtmlFilter)

  private val clients = new Clients

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
        logger.info("Checking for dead client connections every " + checkClientsInterval + "ms")
        while (!shouldStop) {
          Thread.sleep(checkClientsInterval)
          val now = System.currentTimeMillis
          clients.allClients foreach { client =>
            if ((now - client.lastPing) > clientsTimeout) {
              logger.info("Last ping for client [" + client.pageId + "] too old. Removing client.")
              clients.remove(client)
              client.uplink.resume()
            }
          }
        }
      }
    }).start()
  }

  def stop() {
    shouldStop = true
    clients.allClients foreach { _.uplink.resume() }
  }

  def registerUplink(sessionId: String, pageId: String, req: HttpServletRequest) {
    val meteor = createMeteor(req)
    val id = sessionId + "__" + pageId
    logger.info("Adding Client comet connection: " + id)
    clients.clientByPageId(id) match {
      case None => clients.add(new ClientConnection(id, meteor, System.currentTimeMillis))
      case Some(cc) => cc.uplink = meteor
    }
    meteor.suspend(-1, true)
  }

  def processClientHeartbeat(sessionId: String, pageId: String) {
    val id = sessionId + "__" + pageId
    clients.clientByPageId(id) match {
      case Some(cc) => {
        logger.info("Updating heartbeat for: " + id)
        cc.lastPing = System.currentTimeMillis
      }
      case None => {
        logger.info("Got heartbeat, but no client connection yet.")
      }
    }
  }

  def allClients: List[ClientConnection] =
    clients.allClients

  def clientsBySessionId(id: String): List[ClientConnection] =
    clients.clientsBySessionId(id).toList

  def clientByPageId(sessionId: String, pageId: String): Option[ClientConnection] =
    clients.clientByPageId(sessionId + "__" + pageId)

}





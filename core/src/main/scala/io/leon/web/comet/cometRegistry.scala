/*
 * Copyright 2011 Weigle Wilczek GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.leon.web.comet

import collection.mutable
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest
import org.atmosphere.cpr.{BroadcastFilter, Meteor}
import org.atmosphere.util.XSSHtmlFilter

class ClientConnection(val pageId: String, private var _uplink: Meteor, var lastPing: Long) {

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

class CometRegistry {

  private val logger = Logger.getLogger(getClass.getName)

  private val checkClientsInterval = 1000 * 10 // 10 seconds

  private val clientsTimeout = 1000 * 60 // 1 minute

  private val filter: List[BroadcastFilter] = List(new XSSHtmlFilter)

  private val clients = new Clients

  private var shouldStop = false

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
    //val id = sessionId + pageId // TODO
    val id = pageId
    logger.info("Adding Client uplink: " + id)
    clients.clientByPageId(id) match {
      case None => clients.add(new ClientConnection(id, meteor, System.currentTimeMillis))
      case Some(cc) => cc.uplink = meteor
    }
    meteor.suspend(-1, true)
  }

  def processClientHeartbeat(sessionId: String, pageId: String) {
    //val id = sessionId + pageId // TODO
    val id = pageId
    clients.clientByPageId(id) match {
      case Some(cc) => {
        logger.info("Updating heartbeat for: " + id)
        cc.lastPing = System.currentTimeMillis
      }
      case None => {
        logger.info("Got heartbeat, but no client uplink yet.")
      }
    }
  }

  def broadcast(msg: String) {
    clients.allClients foreach { cc =>
      cc.send(msg)
    }
  }

  private def createMeteor(req: HttpServletRequest): Meteor = {
    import scala.collection.JavaConverters._
    val meteor = Meteor.build(req, filter.asJava, null)
    meteor
  }

}









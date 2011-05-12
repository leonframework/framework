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
package com.ww.sjs.comet

import org.atmosphere.util.XSSHtmlFilter
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import scala.collection.JavaConversions
import scala.collection.mutable
import java.util.logging.Logger
import org.atmosphere.cpr.{AtmosphereServlet, Meteor, BroadcastFilter}
import org.atmosphere.handler.ReflectorServletProcessor
import javax.servlet.ServletConfig
import com.google.inject.{AbstractModule, Inject}
import com.google.inject.servlet.ServletModule


class CometWebModule extends ServletModule {
  override def configureServlets() {
    install(new CometModule)
    val meteorParams = JavaConversions.asJavaMap(Map(
      "org.atmosphere.servlet" -> classOf[CometHandler].getName))
    serve("/comet*").`with`(classOf[CometServlet], meteorParams)
  }
}

class CometModule extends AbstractModule {
  def configure() {
    bind(classOf[CometRegistry]).asEagerSingleton()
    bind(classOf[CometHandler]).asEagerSingleton()
    bind(classOf[CometServlet]).asEagerSingleton()
  }
}

case class ClientConnection(meteor: Meteor, var lastPing: Long)

class CometRegistry {

  private val filter: List[BroadcastFilter] = List(new XSSHtmlFilter)

  private var shouldStop = false

  private val logger = Logger.getLogger(getClass.getName)

  val clients = new mutable.HashMap[String, ClientConnection] with mutable.SynchronizedMap[String, ClientConnection]

  def start() {
    shouldStop = false
    new Thread(new Runnable {
      def run() {
        logger.info("Checking for dead client connections every 10 seconds")
        while (!shouldStop) {
          Thread.sleep(1000)
          val now = System.currentTimeMillis
          clients foreach { case (id, cc) =>
            if ((now - cc.lastPing) > 5000) {
              logger.info("Last ping for client [" + id + "] too old. Removing client.")
              clients.remove(id)
              cc.meteor.resume()
            }
          }
        }
      }
    }).start()
  }

  def stop() {
    shouldStop = true
    clients.values foreach { cc =>
      cc.meteor.resume()
    }
  }

  def addClient(sessionId: String, pageId: String, req: HttpServletRequest) {
    val meteor = Meteor.build(req, JavaConversions.asJavaList(filter), null)
    //meteor.addListener(new EventsLogger())
    //val id = sessionId + pageId // TODO
    val id = pageId
    logger.info("Adding Client uplink: " + id)
    clients += (id -> ClientConnection(meteor, System.currentTimeMillis))
    meteor.suspend(-1, true)
  }

  def processClientHeartbeat(sessionId: String, pageId: String) {
    //val id = sessionId + pageId // TODO
    val id = pageId
    clients.get(id) match {
      case Some(cc) => {
        logger.info("Updating heartbeat for: " + id)
        cc.lastPing = System.currentTimeMillis
      }
      case None => {
        logger.info("Got heartbeat, but no client uplink yet.")
      }
    } 
  }

}

class CometHandler @Inject()(registry: CometRegistry) extends HttpServlet {

  override def init(config: ServletConfig) {
    registry.start()
  }

  override def destroy() {
    registry.stop()
  }

  override def doGet(req: HttpServletRequest, res: HttpServletResponse) {
    val sessionId = req.getSession.getId
    val pageId = req.getParameter("pageId")
    val uplink = req.getParameter("uplink")

    uplink match {
      case "true" => registry.addClient(sessionId, pageId, req)
      case _ => registry.processClientHeartbeat(sessionId, pageId)
    }
  }

  override def doPost(req: HttpServletRequest, res: HttpServletResponse) {
    val meteor = Meteor.build(req)
    val writer = res.getWriter
    res.setCharacterEncoding("UTF-8")
    val message = req.getParameter("message")
    meteor.broadcast("MSG:" + message)
    writer.flush()
  }

}

class CometServlet @Inject()(cometHandler: CometHandler) extends AtmosphereServlet {

  protected override def loadConfiguration(sc: ServletConfig) {
    val r = new ReflectorServletProcessor
    r.setServlet(cometHandler)
    addAtmosphereHandler("/*", r)
  }

  override def destroy() {
    super.destroy()
    cometHandler.destroy()
  }
}



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

import com.google.inject.Inject
import java.util.logging.Logger
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import javax.servlet.ServletConfig
import org.atmosphere.cpr.AtmosphereServlet
import org.atmosphere.handler.ReflectorServletProcessor

class CometHandler @Inject()(registry: CometRegistry) extends HttpServlet {

  private val logger = Logger.getLogger(getClass.getName)

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
      case "true" => {
        logger.info("Registering connection for client: " + sessionId + "-" + pageId)
        registry.registerUplink(sessionId, pageId, req)
      }
      case _ => {
        logger.info("Received heartbeat from client: " + sessionId + "-" + pageId)
        registry.processClientHeartbeat(sessionId, pageId)
      }
    }
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

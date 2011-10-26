/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
    logger.info("Registering connection for client: " + sessionId + "__" + pageId)
    registry.registerUplink(sessionId, pageId, req)
  }

}

class CometConnectionServlet @Inject()(cometHandler: CometHandler) extends AtmosphereServlet {

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

class CometUpdateFilterServlet @Inject()(cometRegisty: CometRegistry) extends HttpServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    val pageId = req.getParameter("pageId")
    val topicId = req.getParameter("topicId")
    val key = req.getParameter("key")
    val value = req.getParameter("value")

    cometRegisty.updateClientFilter(topicId, req.getSession.getId + "__" + pageId, key, value)
    
  }

}


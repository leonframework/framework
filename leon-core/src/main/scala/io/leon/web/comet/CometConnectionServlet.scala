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
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import javax.servlet.ServletConfig
import org.atmosphere.cpr.AtmosphereServlet
import org.atmosphere.handler.ReflectorServletProcessor
  

class CometConnectionServlet @Inject()(registry: CometRegistry) extends AtmosphereServlet {

  private val handlerServlet = new HttpServlet {

    override def init(config: ServletConfig) {
      registry.start()
    }

    override def destroy() {
      registry.stop()
    }

    override def doGet(req: HttpServletRequest, res: HttpServletResponse) {
      val pageId = req.getParameter("pageId")
      val lastMessageId = req.getParameter("lastMessageId").toInt

      res.setCharacterEncoding("utf-8")
      registry.registerUplink(req, pageId, lastMessageId)
    }

  }

  protected override def loadConfiguration(sc: ServletConfig) {
    val r = new ReflectorServletProcessor
    r.setServlet(handlerServlet)
    addAtmosphereHandler("/*", r)
  }

  override def destroy() {
    super.destroy()
    handlerServlet.destroy()
  }

}



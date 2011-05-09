/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.ww.sjs

import org.atmosphere.util.XSSHtmlFilter
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.atmosphere.cpr.{AtmosphereResource, AtmosphereHandler, AtmosphereResourceEvent}


object SJSAtmosphereQueueHandler {
  var INSTANCE: SJSAtmosphereQueueHandler = _

  def test() {
    INSTANCE.listener foreach { l =>
      println("sending test message to %s" format l.hashCode)
      l.getBroadcaster.broadcast("test message\n")

    }
  }
}

class SJSAtmosphereQueueHandler extends AtmosphereHandler[HttpServletRequest, HttpServletResponse] {

  SJSAtmosphereQueueHandler.INSTANCE = this

  val listener = new scala.collection.mutable.HashSet[AtmosphereResource[HttpServletRequest, HttpServletResponse]]

  def onRequest(event: AtmosphereResource[HttpServletRequest, HttpServletResponse]) {
    val req = event.getRequest
    val res = event.getResponse
    val sessionId = req.getSession.getId
    val page = req.getParameter("page")

    
    res.setContentType("multipart/x-mixed-replace")

    listener.add(event)

    println("# Listeners:" + listener.size)
    listener foreach { l =>
      println("Listener: " + l.hashCode)
    }

    
    event.suspend()

    println("### suspending connection: " + event.hashCode)

    event.getBroadcaster.getBroadcasterConfig.addFilter(new XSSHtmlFilter)

    res.getWriter.write("Server: registered page, event: %s".format(event.hashCode))
    res.getWriter.flush()
  }

  def onStateChange(event: AtmosphereResourceEvent[HttpServletRequest, HttpServletResponse]) {
    val req = event.getResource.getRequest
    val res = event.getResource.getResponse

    try {
      if (event.getMessage == null) {
        println("onStateChange: getMessage == null")
        return
      }

      if (event.isCancelled) {
        listener.remove(event.getResource)
        println("onStateChange: isCancelled")
        event.getResource.getBroadcaster.broadcast("onStateChange: isCancelled")
      }
      else if (event.isResuming || event.isResumedOnTimeout) {
        listener.remove(event.getResource)
        println("onStateChange: isResuming")
        event.getResource.getBroadcaster.broadcast("onStateChange: isResuming")
      } else {
        println("onStateChange: writing message: " + event.getMessage.toString)
        res.getWriter.write(event.getMessage.toString)
      }
      res.getWriter.flush()

    } catch {
      case e => throw e
    }
  }

  def destroy() {
  }

}



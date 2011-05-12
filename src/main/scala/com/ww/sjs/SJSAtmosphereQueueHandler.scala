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
import org.atmosphere.cpr.{Broadcaster, AtmosphereResource, AtmosphereHandler, AtmosphereResourceEvent}
import collection.JavaConversions


class SJSAtmosphereQueueHandler extends AtmosphereHandler[HttpServletRequest, HttpServletResponse] {

  def onRequest(event: AtmosphereResource[HttpServletRequest, HttpServletResponse]) {
    event.getRequest.getMethod match {
      case "GET" => handleSubscribe(event)
      case "POST" => handleMessage(event)
    }
  }

  def handleSubscribe(event: AtmosphereResource[HttpServletRequest, HttpServletResponse]) {
    val req = event.getRequest
    val res = event.getResponse

    res.setContentType("multipart/x-mixed-replace")

    event.suspend()
    

    println("### Broadcaster = " + event.getBroadcaster)
    println("### suspending connection: " + event.hashCode)

    event.getBroadcaster.getBroadcasterConfig.addFilter(new XSSHtmlFilter)

    res.getWriter.write("Server: registered page, event: %s".format(event.hashCode))
    res.getWriter.flush()
  }

  def handleMessage(event: AtmosphereResource[HttpServletRequest, HttpServletResponse]) {
    val req = event.getRequest
    val res = event.getResponse
    val message = req.getParameter("message")
    println("### sending message: " + message)
    event.getBroadcaster.broadcast(message)
  }

  def onStateChange(event: AtmosphereResourceEvent[HttpServletRequest, HttpServletResponse]) {
    //val req = event.getResource.getRequest
    val res = event.getResource.getResponse

    try {
      if (event.getMessage == null) {
        println("onStateChange: getMessage == null")
        return
      }

      if (event.isCancelled) {
        println("onStateChange: isCancelled")
        event.getResource.getBroadcaster.broadcast("onStateChange: isCancelled")
      }
      else if (event.isResuming || event.isResumedOnTimeout) {
        println("onStateChange: isResuming")
        event.getResource.getBroadcaster.broadcast("onStateChange: isResuming")
      } else {
        println("onStateChange: writing message: " + event.getMessage.toString)
        try {
          res.getWriter.write(event.getMessage.toString)
        } catch {
          case e => println("Error writing message: " + e.getMessage)
        }
      }
      res.getWriter.flush()
    } catch {
      case e => throw e
    }
  }

  def destroy() {
  }

}



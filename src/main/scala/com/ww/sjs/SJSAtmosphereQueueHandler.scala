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
import java.util.concurrent.TimeUnit


class SJSAtmosphereQueueHandler extends AtmosphereHandler[HttpServletRequest, HttpServletResponse] {

  private val listener = new scala.collection.mutable.HashSet[AtmosphereResource[HttpServletRequest, HttpServletResponse]]

  def onRequest(event: AtmosphereResource[HttpServletRequest, HttpServletResponse]) {
    val req = event.getRequest
    val res = event.getResponse
    val sessionId = req.getSession.getId
    val action = req.getParameter("action")
    println("### ACTION: " + action)
    res.setContentType("multipart/x-mixed-replace")

    println("#### EVENT:" + event)

    println("#### Listener: " + listener)

    if (action.equalsIgnoreCase("add")) {
      listener += event
      event.suspend()
      println("### suspending connection: " + event.hashCode)
      val bc = event.getBroadcaster
      bc.getBroadcasterConfig.addFilter(new XSSHtmlFilter)

      bc.broadcast("new client: " + sessionId + "\n")
      bc.scheduleFixedBroadcast("client " + sessionId + " still listening & öäü <div></div>  \n", 5, TimeUnit.SECONDS)

      res.getWriter.write("success")
      res.getWriter.flush()
    } else if (action.equalsIgnoreCase("remove")) {

    } else {
      println("'!#!#!# Unkown action !!!")
    }
  }

  def onStateChange(event: AtmosphereResourceEvent[HttpServletRequest, HttpServletResponse]) {
    import scala.collection.JavaConversions
    println("####### STATE CHANGE: " + event)
    val resources = JavaConversions.asScalaIterable(event.getResource.getBroadcaster.getAtmosphereResources)
    println("#### res length: " + resources.size)
    resources foreach { r =>
      println("### res hash: " + r.hashCode)
    }

    val req = event.getResource.getRequest
    val res = event.getResource.getResponse
    try {
      if (event.getMessage == null) {
        return
      }
      if (event.isCancelled) {
        println("####################CANCELED!!!!!!!!" + event)
        println("####################CANCELED!!!!!!!!" + event)
        println("####################CANCELED!!!!!!!!" + event)
        println("####################CANCELED!!!!!!!!" + event)
        event.getResource.getBroadcaster.broadcast(req.getRemoteAddr + " has left")
      }
      else if (event.isResuming || event.isResumedOnTimeout) {
        res.getWriter.write("isResuming || isResumedOnTimeout") // TODO: Connect im browser wiederherstellen
      }
      else {
        res.getWriter.write(event.getMessage.toString)
      }
      res.getWriter.flush()
    } catch {
      case _ => // catch broken pipe etc.
    }
  }

  def destroy() {
  }

}



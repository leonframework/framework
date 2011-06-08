/*
 * Copyright (c) 2010 WeigleWilczek and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet

import java.util.logging.Logger
import dispatch.json.JsValue
import com.google.inject.{Inject, Provider}
import javax.servlet.http.HttpServletRequest

sealed abstract class BrowserObjectScopes
case object BrowerObjectAllScope extends BrowserObjectScopes
case object BrowerObjectSessionScope extends BrowserObjectScopes
case object BrowerObjectPageScope extends BrowserObjectScopes

class BrowserObjectProvider(browserObjectName: String, browserScope: BrowserObjectScopes)
  extends Provider[BrowserObject] {

  @Inject
  var cometRegistry: CometRegistry = _

  @Inject
  var requestProvider: Provider[HttpServletRequest] = _

  private lazy val browserObject =
    new BrowserObject(cometRegistry, browserObjectName, browserScope: BrowserObjectScopes, requestProvider)

  def get() = browserObject

}

class BrowserObject(cometRegistry: CometRegistry,
                    browserObjectName: String,
                    browserScope: BrowserObjectScopes,
                    requestProvider: Provider[HttpServletRequest]) {

  private val logger = Logger.getLogger(getClass.getName)

  def jsonApply(methodName: String, jsonArgs: String) {
    logger.info("Calling browser object [%s.%s]".format(browserObjectName, methodName))

    val message = """ {
      "type": "browserObjectMethodCall",
      "object": "%s",
      "method": "%s",
      "args": %s
    } """.format(browserObjectName, methodName, jsonArgs)

    browserScope match {
      case BrowerObjectAllScope => {
        cometRegistry.allClients foreach { _.send(message) }
      }
      case BrowerObjectSessionScope => {
        val req = requestProvider.get()
        val sessionId = req.getSession.getId
        cometRegistry.clientsBySessionId(sessionId) foreach { _.send(message) }
      }
      case BrowerObjectPageScope => {
        val req = requestProvider.get()
        val sessionId = req.getSession.getId
        val pageId = req.getParameter("pageId")
        cometRegistry.clientByPageId(sessionId, pageId) map { _.send(message) }
      }
    }
  }

  def apply(methodName: String): (Any*) => Unit = {
    def call(args: Any*) {
      val out = JsValue.toJson(JsValue.apply(args))
      jsonApply(methodName, out)
    }
    call
  }

}

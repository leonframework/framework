/*
 * Copyright (c) 2010 WeigleWilczek and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet

import com.google.inject.{Inject, Provider}
import java.util.logging.Logger
import dispatch.json.JsValue


class BrowserObjectProvider(browserObjectName: String) extends Provider[BrowserObject] {

  @Inject
  var cometRegistry: CometRegistry = _

  private lazy val browserObject = new BrowserObject(cometRegistry, browserObjectName)

  def get() = browserObject

}

class BrowserObject(cometRegistry: CometRegistry, browserObjectName: String) {

  private val logger = Logger.getLogger(getClass.getName)

  def jsonApply(methodName: String, jsonArgs: String) {
    logger.info("Calling browser object [%s.%s]".format(browserObjectName, methodName))

    val message = """ {
      "type": "browserObjectMethodCall",
      "object": "%s",
      "method": "%s",
      "args": %s
    } """.format(browserObjectName, methodName, jsonArgs)

    cometRegistry.broadcast(message)
  }

  def apply(methodName: String): (Any*) => Unit = {
    def call(args: Any*) {
      val out = JsValue.toJson(JsValue.apply(args))
      jsonApply(methodName, out)
    }
    call
  }

}

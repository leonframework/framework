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

case class Person(first: String, last: String)

class UplinkFunctionProvider(target: String) extends Provider[UplinkFunction] {

  @Inject
  var cometRegistry: CometRegistry = _

  private lazy val uplinkFunction = new UplinkFunction(cometRegistry, target)

  def get() = uplinkFunction
}

class UplinkFunction(cometRegistry: CometRegistry, target: String) {

  private val logger = Logger.getLogger(getClass.getName)

  def jsonApply(jsonArgs: String) {
    logger.info("Calling uplink function [%s]" format target)
    val message = """ { "type": "uplinkFunction", "target": "%s", "args": %s } """.format(target, jsonArgs)
    cometRegistry.broadcast(message)
  }

  def apply(args: Any*) {
    val out = JsValue.toJson(JsValue.apply(args))
    jsonApply(out)
  }

}

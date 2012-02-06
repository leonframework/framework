/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import com.google.inject.{Inject, Provider}
import io.leon.web.ajax.AjaxHandler
import com.google.gson.{JsonParser, Gson}

class JavaScriptAjaxHandlerProvider(objName: String) extends Provider[AjaxHandler] {

  @Inject
  var engine: LeonScriptEngine = _

  @Inject
  var gson: Gson = _

  private lazy val handler = new AjaxHandler {
    def jsonApply(member: String, args: Seq[String]) = {

      val jp = new JsonParser
      val parsed = args map { a => jp.parse(a) }
      val argsString = parsed.mkString(",")
      val call = objName + "." + member + "(" + argsString + ")"
      gson.toJson(engine.eval(call))
    }
  }

  def get() = handler

}


/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.ajax

import com.google.inject.Inject
import com.google.gson.{JsonParser, Gson}
import io.leon.javascript.LeonScriptEngine

class JavaScriptAjaxHandler(targetObjectName: String) extends AjaxHandler {

  @Inject
  var engine: LeonScriptEngine = _

  @Inject
  var gson: Gson = _

  def jsonApply(member: String, args: Seq[String]) = {
    val jp = new JsonParser
    val parsed = args map jp.parse
    val argsString = parsed.mkString(",")
    val call = targetObjectName + "." + member + "(" + argsString + ")"
    val result = engine.eval(call)
    gson.toJson(result)
  }

}


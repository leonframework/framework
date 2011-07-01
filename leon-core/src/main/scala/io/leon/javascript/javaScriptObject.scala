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

class JavaScriptAjaxHandlerProvider(objName: String) extends Provider[AjaxHandler] {

  @Inject
  var engine: LeonScriptEngine = _

  private lazy val jsObject = new JavaScriptObject(engine, objName)

  private lazy val handler = new AjaxHandler {
    def jsonApply(member: String, args: String) = {
      jsObject.jsonApply(member, args)
    }
  }

  def get() = handler

}

class JavaScriptObject(val engine: LeonScriptEngine, val objName: String) {

  def jsonApply(member: String, args: String): String = {
    val target = objName + "." + member
    val inv = target + ".apply(" + objName + ", JSON.parse('" + args + "'));"
    engine.evalToJson(inv)
  }

}

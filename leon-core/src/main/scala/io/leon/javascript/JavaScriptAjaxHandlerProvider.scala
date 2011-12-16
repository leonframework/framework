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

  private lazy val handler = new AjaxHandler {
    def jsonApply(member: String, args: Seq[String]) = {
      val deserialized = args map { a => engine.invokeFunction("JSON.parse", a) }
      val target = objName + "." + member
      val result = engine.invokeFunction(target, deserialized: _*)
      engine.invokeFunction("JSON.stringify", result).toString
    }
  }

  def get() = handler

}


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
import org.mozilla.javascript.BaseFunction

class JavaObjectAjaxHandlerProvider(obj: AnyRef) extends Provider[AjaxHandler] {

  @Inject
  var engine: LeonScriptEngine = _

  private lazy val proxyObject = new JavaScriptProxy(engine.rhinoScope, obj, obj.getClass)

  private lazy val handler = new AjaxHandler {
    def jsonApply(member: String, args: String) = {
      engine.withContext { ctx => 
        val jsonArgs = Converter.jsToJava(engine.eval("JSON.parse('" + args + "')"), 
          classOf[Array[AnyRef]]).asInstanceOf[Array[AnyRef]]
      
        val result = proxyObject.get(member, proxyObject) match {
          case func: BaseFunction => func.call(ctx, engine.rhinoScope, proxyObject, jsonArgs)
          case _ => sys.error(member + " is not a function!")
        }
        engine.invokeFunction("JSON.stringify", result).asInstanceOf[String]
      }
    }
  }

  def get() = handler

}
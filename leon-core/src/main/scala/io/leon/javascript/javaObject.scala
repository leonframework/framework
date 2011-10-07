/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import io.leon.web.ajax.AjaxHandler
import org.mozilla.javascript.BaseFunction
import com.google.inject.{Key, Injector, Inject, Provider}

class JavaObjectAjaxHandlerProvider[T <: AnyRef](key: Key[T]) extends Provider[AjaxHandler] {

  @Inject
  var injector: Injector = _

  @Inject
  var engine: LeonScriptEngine = _

  @Inject
  var converter: Converter = _

  private lazy val obj = injector.getInstance(key)

  private lazy val proxyObject = new JavaScriptProxy(engine.rhinoScope, obj, obj.getClass)

  private lazy val handler = new AjaxHandler {
    def jsonApply(member: String, args: String) = {
      engine.withContext { ctx => 
        val argsArray = engine.invokeFunction("JSON.parse", args)
        val argsArrayJava = converter.jsToJava(argsArray, classOf[Array[AnyRef]]).asInstanceOf[Array[AnyRef]]
      
        val result = proxyObject.get(member, proxyObject) match {
          case func: BaseFunction => func.call(ctx, engine.rhinoScope, proxyObject, argsArrayJava)
          case _ => sys.error(member + " is not a function!")
        }
        engine.invokeFunction("JSON.stringify", result).asInstanceOf[String]
      }
    }
  }

  def get() = handler
}

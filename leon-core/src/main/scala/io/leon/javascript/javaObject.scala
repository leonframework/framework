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
import com.google.gson.Gson

class JavaObjectAjaxHandlerProvider[T <: AnyRef](key: Key[T]) extends Provider[AjaxHandler] {

  @Inject
  var injector: Injector = _

  @Inject
  var engine: LeonScriptEngine = _

  private lazy val obj = injector.getInstance(key)

  private lazy val handler = new AjaxHandler {
    def jsonApply(member: String, args: Seq[String]) = {
      val methods = obj.getClass.getMethods
      val possibleMethods = methods filter { m => m.getName == member && m.getParameterTypes.length == args.length }
      if(possibleMethods.isEmpty) {
        sys.error("No method named '%s' with a matching number of arguments (%s) found!".format(member, args.length))
      } else if(possibleMethods.size > 1) {
        sys.error("More than one possible methods found. name: %s, argument size: %s".format(member, args.length))
      }

      val gson = new Gson()

      val method = possibleMethods(0)

      val deserialized = args.zipWithIndex map { case (a, i) =>
        val requiredType = method.getParameterTypes()(i)
        gson.fromJson(a, requiredType).asInstanceOf[AnyRef]
      }

      val result = method.invoke(obj, deserialized.toArray: _*)

      gson.toJson(result)
    }
  }

  def get() = handler
}

/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

object RhinoUtils {

  def json2RhinoObject(engine: LeonScriptEngine, string: String): AnyRef = {
    //val invocable = engine.asInvocable
    //val json = engine.get("JSON")
    //invocable.invokeMethod(json, "parse", string)

    engine.invokeFunction("JSON.parse", string)
  }

  def rhinoObject2Json(engine: LeonScriptEngine, obj: AnyRef): String = {
    //val invocable = engine.asInvocable
    //val json = engine.get("JSON")
    //invocable.invokeMethod(json, "stringify", obj).asInstanceOf[String]

    engine.invokeFunction("JSON.stringify", obj).asInstanceOf[String]
  }

}

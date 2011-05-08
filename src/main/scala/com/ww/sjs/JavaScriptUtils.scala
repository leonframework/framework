/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.ww.sjs

import javax.script.{Invocable, ScriptEngine}

object JavaScriptUtils {

  def parseString(engine: ScriptEngine, string: String): AnyRef = {
    val invocable = engine.asInstanceOf[Invocable]
    val json = engine.get("JSON")
    invocable.invokeMethod(json, "parse", string)
  }

  def stringifyObject(engine: ScriptEngine, obj: AnyRef): String = {
    val invocable = engine.asInstanceOf[Invocable]
    val json = engine.get("JSON")
    invocable.invokeMethod(json, "stringify", obj).asInstanceOf[String]
  }

}

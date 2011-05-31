/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon

import javax.script.{Invocable, ScriptEngine}

trait SFSFunction {

  val fnName: String

  val engine: ScriptEngine

  private def invocable = engine.asInstanceOf[Invocable]

  private val function = engine.get(fnName)

  def applyJson(args: String): String = {
    val argsParsed = JavaScriptUtils.parseString(engine, args)
    val objResult = invocable.invokeMethod(function, "apply", function, argsParsed)
    JavaScriptUtils.stringifyObject(engine, objResult)
  }
  
}

class JavaScriptFunction(val engine: ScriptEngine, val fnName: String) extends ((String) => String) with SFSFunction {

  def apply(input: String): String = applyJson(input)

}

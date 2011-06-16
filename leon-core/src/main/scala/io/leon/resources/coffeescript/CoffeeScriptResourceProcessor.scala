/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.coffeescript

import io.leon.resources.ResourceProcessor
import java.io.InputStream
import io.leon.javascript.LeonScriptEngine
import com.google.inject.{Provider, Inject}

class CoffeeScriptResourceProcessor @Inject()(leonScriptEngineProvider: Provider[LeonScriptEngine])
  extends ResourceProcessor {

  private lazy val leonScriptEngine = leonScriptEngineProvider.get()

  def fromFileEnding = "coffee"

  def toFileEnding = "js"

  def transform(fileName: String, in: InputStream) = {
    val inStr = inputStreamToString(in)
    val cs = leonScriptEngine.invokeFunction("CoffeeScript.compile", inStr)
    stringToInputStream(cs.toString)
  }

}

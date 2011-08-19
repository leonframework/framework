/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.coffeescript

import java.io.InputStream
import io.leon.javascript.LeonScriptEngine
import com.google.inject.{Provider, Inject}
import io.leon.resources.{StreamResource, Resource, ResourceProcessor}

class CoffeeScriptResourceProcessor @Inject()(leonScriptEngineProvider: Provider[LeonScriptEngine])
  extends ResourceProcessor {

  private lazy val leonScriptEngine = leonScriptEngineProvider.get()

  def fromFileEnding = "coffee"

  def toFileEnding = "js"

  def transform(in: Resource) = {
    val inStr = inputStreamToString(in.getInputStream)
    val cs = leonScriptEngine.invokeFunction("CoffeeScript.compile", inStr)
    val stream = stringToInputStream(cs.toString)

    new StreamResource(in.name, stream)
  }

}

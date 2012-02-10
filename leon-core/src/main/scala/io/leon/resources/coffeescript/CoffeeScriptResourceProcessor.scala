/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.coffeescript

import io.leon.javascript.LeonScriptEngine
import com.google.inject.{Provider, Inject}
import io.leon.resourceloading.processor.ResourceProcessor
import io.leon.resourceloading.{ResourceUtils, Resource}

class CoffeeScriptResourceProcessor @Inject()(leonScriptEngineProvider: Provider[LeonScriptEngine])
  extends ResourceProcessor {

  private lazy val leonScriptEngine = {
    val lse = leonScriptEngineProvider.get()
    lse.loadResource("/io/leon/coffee-script.js", -1)
    lse
  }

  def fromFileEnding = "coffee"

  def toFileEnding = "js"

  def process(in: Resource) = {
    synchronized {
      val inStr = ResourceUtils.inputStreamToString(in.createInputStream())
      val cs = leonScriptEngine.invokeFunction("CoffeeScript.compile", inStr)
      new Resource(in.name, in.lastModifiedFunc , () => ResourceUtils.stringToInputStream(cs.toString))
    }
  }

  override def isCachingRequested = true

}

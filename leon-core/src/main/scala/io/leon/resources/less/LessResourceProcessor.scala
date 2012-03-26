/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.less

import io.leon.javascript.LeonScriptEngine
import com.google.inject.{Provider, Inject}
import io.leon.resourceloading.processor.ResourceProcessor
import io.leon.resourceloading.{ResourceUtils, Resource}


class LessResourceProcessor @Inject()(leonScriptEngineProvider: Provider[LeonScriptEngine],
                                      originalLessFilePathHolder: OriginalLessFilePathHolder)
  extends ResourceProcessor {

  private lazy val leonScriptEngine = {
    val lse = leonScriptEngineProvider.get()
    lse.loadResource("/io/leon/less-rhino-1.1.3.js", 9)
    lse
  }

  def fromFileEnding = "less"

  def toFileEnding = "css"

  def process(in: Resource) = {
    synchronized {
      originalLessFilePathHolder.set(in.name)

      val asLess = ResourceUtils.inputStreamToString(in.getInputStream())
      val asCss = leonScriptEngine.invokeFunction("leon.parseLess", asLess)

      new Resource(in.name) {
        def getLastModified() = in.getLastModified()
        def getInputStream() = ResourceUtils.stringToInputStream(asCss.toString)
      }
    }
  }

  override def isCachingRequested = true

}

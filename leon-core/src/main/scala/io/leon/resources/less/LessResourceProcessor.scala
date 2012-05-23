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
import io.leon.utils.ResourceUtils
import io.leon.resourceloading.Resource

class LessResourceProcessor @Inject()(leonScriptEngineProvider: Provider[LeonScriptEngine],
                                      originalLessFilePathHolder: OriginalLessFilePathHolder)
  extends ResourceProcessor {

  private def getLeonScriptEngine() = {
    leonScriptEngineProvider.get()
  }

  def fromFileEnding = "less"

  def toFileEnding = "css"

  def process(in: Resource) = synchronized {
    originalLessFilePathHolder.set(in.name)

    new Resource(in.name) {
      def getLastModified() = in.getLastModified()

      def getInputStream() = {
        val asLess = ResourceUtils.inputStreamToString(in.getInputStream())
        val asCss = getLeonScriptEngine().invokeFunction("leon.parseLess", asLess)
        ResourceUtils.stringToInputStream(asCss.toString)
      }

      override def isCachingDesired() = true
    }
  }


}

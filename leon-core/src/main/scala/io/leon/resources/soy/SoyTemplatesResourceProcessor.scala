/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.soy

import com.google.template.soy.jssrc.SoyJsSrcOptions
import com.google.template.soy.{SoyFileSet}
import collection.JavaConverters
import io.leon.resourceloading.processor.ResourceProcessor
import io.leon.resourceloading.{ResourceUtils, Resource}

class SoyTemplatesResourceProcessor ()
  extends ResourceProcessor {

  def fromFileEnding = "soy"

  def toFileEnding = "js"

  def process(in: Resource) = new Resource(in.name) {
    import JavaConverters._

    def getLastModified() = in.getLastModified()

    def getInputStream() = {
      val inStr = ResourceUtils.inputStreamToString(in.getInputStream())
      val sfs = (new SoyFileSet.Builder()).add(inStr,in.name).build()
      val res = sfs.compileToJsSrc(new SoyJsSrcOptions(), null)
      ResourceUtils.stringToInputStream(res.asScala.head)
    }

  }

}

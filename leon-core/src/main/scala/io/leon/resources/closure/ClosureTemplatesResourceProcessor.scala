/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.closure


import com.google.template.soy.jssrc.SoyJsSrcOptions
import io.leon.resources.{Resource, ResourceProcessor}
import com.google.template.soy.{SoyFileSet, SoyToJsSrcCompiler}
import collection.JavaConverters

class ClosureTemplatesResourceProcessor ()
  extends ResourceProcessor {

  def fromFileEnding = "soy"

  def toFileEnding = "js"

  def process(in: Resource) = new Resource(in.name, () => {

    import JavaConverters._

    // Bundle the given Soy file into a SoyFileSet
    val inStr = inputStreamToString(in.getInputStream)
    val sfs = (new SoyFileSet.Builder()).add(inStr,in.name).build()

    val res = sfs.compileToJsSrc(new SoyJsSrcOptions(), null)

    //println(res.asScala.head)

    stringToInputStream(res.asScala.head)
  })

}
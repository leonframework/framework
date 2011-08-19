/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.closure

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
import com.google.template.soy.SoyToJsSrcCompiler
import com.google.template.soy.jssrc.SoyJsSrcOptions
import io.leon.resources.{StreamResource, Resource, ResourceProcessor}

class ClosureTemplatesResourceProcessor ()
  extends ResourceProcessor {

  def fromFileEnding = "soy"

  def toFileEnding = "js"

  def transform(in: Resource) = {

    import scala.collection.JavaConverters._

    // Bundle the given Soy file into a SoyFileSet
    val inStr = inputStreamToString(in.getInputStream)
    val sfs = (new SoyFileSet.Builder()).add(inStr,in.name).build()

    val res = sfs.compileToJsSrc(new SoyJsSrcOptions(), null)

    //println(res.asScala.head)

    val stream = stringToInputStream(res.asScala.head)
    new StreamResource(in.name, stream)
  }

}
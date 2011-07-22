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
import io.leon.resources.ResourceProcessor
import java.io.{ByteArrayInputStream, File, InputStream}
import com.google.template.soy.{SoyToJsSrcCompiler, SoyFileSet}
import com.google.template.soy.tofu.SoyTofu
import com.google.template.soy.jssrc.SoyJsSrcOptions


class ClosureTemplatesResourceProcessor ()
  extends ResourceProcessor {

  def fromFileEnding = "soy"

  def toFileEnding = "js"

  def transform(fileName: String, in: InputStream) = {

    import scala.collection.JavaConverters._

    // Bundle the given Soy file into a SoyFileSet
    val inStr = inputStreamToString(in)
    val sfs = (new SoyFileSet.Builder()).add(inStr,fileName).build()

    val res = sfs.compileToJsSrc(new SoyJsSrcOptions(), null)

    //println(res.asScala.head)

    stringToInputStream(res.asScala.head)
  }

}
/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.htmltagsprocessor

import io.leon.resources.Resource
import org.slf4j.LoggerFactory
import com.google.inject.{Injector, Inject}
import net.htmlparser.jericho._
import java.io._
import io.leon.guice.GuiceUtils

class LeonTagProcessor @Inject()(injector: Injector) {
  import scala.collection.JavaConverters._

  private val logger = LoggerFactory.getLogger(getClass)

  private lazy val rewriters = GuiceUtils.getAllBindingsForType(
    injector, classOf[LeonTagRewriter]).asScala map { _.getProvider.get() }

  def transform(in: Resource) = new Resource(in.name, () => {

    println(rewriters.map(_.getClass.getName).mkString(","))

    measureTime(in.name) {

      val stream = in.createInputStream()

      val source = new Source(stream)
      val out = new OutputDocument(source)

      rewriters flatMap { _.process(source) } foreach { func => func(out) }

      val initialBufferSize = stream match {
        case buf: ByteArrayInputStream => buf.available() + 512
        case _ => 2048
      }

      val buf = new ByteArrayOutputStream(initialBufferSize)
      val writer = new OutputStreamWriter(buf)

      out.writeTo(writer)

      new ByteArrayInputStream(buf.toByteArray)
    }
  })

  private def measureTime[A](name: String)(func: => A) = {
    val start = System.currentTimeMillis()
    val result = func
    val end = System.currentTimeMillis()

    logger.debug("{} took {} ms", name, (end - start))

    result
  }
}

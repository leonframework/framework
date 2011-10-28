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
import com.google.inject.{Binder, TypeLiteral, Injector, Inject}
import com.google.inject.name.Names
import net.htmlparser.jericho._
import java.io._

abstract class LeonTagRewriter {
  def process(doc: Source): Seq[(Segment, String)]
}

object LeonTagRewriters {
  def bind[A <: LeonTagRewriter](binder: Binder, clazz:Class[A]) {
    binder.bind(classOf[LeonTagRewriter]).annotatedWith(Names.named(clazz.getName)).to(clazz).asEagerSingleton()
  }
}

class LeonTagProcessor @Inject()(injector: Injector) {
  import scala.collection.JavaConverters._

  private val logger = LoggerFactory.getLogger(getClass)

  private lazy val rewriters = injector.findBindingsByType(new TypeLiteral[LeonTagRewriter]() {}).asScala map { _.getProvider.get() }


  def transform(in: Resource) = new Resource(in.name, () => {

    measureTime(in.name) {

      // TODO by-pass documents which are known to have no server-side tags

      val stream = in.createInputStream()

      val source = new Source(stream)
      val out = new OutputDocument(source)

      rewriters flatMap { _.process(source) } foreach { case (tag, newContent) =>
        out.replace(tag, newContent)
      }

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

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources

import com.google.inject.{TypeLiteral, Injector, Inject}
import freemarker.FreeMarkerProcessor
import scala.collection.mutable
import java.io.{ByteArrayInputStream, InputStreamReader, BufferedReader, InputStream}

trait ResourceProcessor {

  def fromFileEnding: String

  def toFileEnding: String

  def transform(in: Resource): Resource

  protected def inputStreamToString(stream: InputStream): String = {
      val br = new BufferedReader(new InputStreamReader(stream))
    val sb = new StringBuilder
    var line: String = br.readLine()

    while (line != null) {
      sb.append(line + "\n")
      line = br.readLine()
    }

    br.close()
    sb.toString()
  }

  protected def stringToInputStream(string: String): InputStream = {
    new ByteArrayInputStream(string.getBytes)
  }
}

class NoOpResourceProcessor @Inject()(freeMarker: FreeMarkerProcessor) extends ResourceProcessor {

  def fromFileEnding = ""

  def toFileEnding = ""

  def transform(in: Resource) = {
    if (in.name.endsWith("html")) {
      freeMarker.transform(in)
    } else {
      in
    }
  }

}

class ResourceProcessorRegistry @Inject()(injector: Injector, noOpProcessor: NoOpResourceProcessor) {
  import scala.collection.JavaConverters._

  private val resourceProcessors: Map[String, mutable.ListBuffer[ResourceProcessor]] = {
    val processors = new mutable.HashMap[String, mutable.ListBuffer[ResourceProcessor]]

    val rps = injector.findBindingsByType(new TypeLiteral[ResourceProcessor]() {}).asScala
    rps foreach { binding =>
      val processor = binding.getProvider.get()
      val list = processors.getOrElseUpdate(processor.toFileEnding, new mutable.ListBuffer[ResourceProcessor])
      list.append(processor)
    }
    processors.toMap
  }

  private def getFileNameEnding(fileName: String): String = {
    val dot = fileName.lastIndexOf('.')
    fileName.substring(dot + 1)
  }

  def replaceFileNameEndingForProcessor(processor: ResourceProcessor, fileName: String): String = {
    val currentEndingStrippedLength = fileName.size - processor.toFileEnding.size
    fileName.substring(0, currentEndingStrippedLength) + processor.fromFileEnding
  }

  def processorsForFile(fileName: String): List[ResourceProcessor] = {
    val ending = getFileNameEnding(fileName)
    val processors = resourceProcessors.getOrElse(ending, new mutable.ListBuffer)
    processors.toList ::: noOpProcessor :: Nil
  }
}

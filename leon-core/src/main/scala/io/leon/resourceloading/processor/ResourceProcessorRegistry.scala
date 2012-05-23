/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resourceloading.processor

import com.google.inject.{Injector, Inject}
import scala.collection.mutable
import io.leon.utils.GuiceUtils
import scala.collection.JavaConverters._

class ResourceProcessorRegistry @Inject()(injector: Injector) {

  private val noOpProcessor = new NoOpResourceProcessor

  // e.g. CoffeeScript -> JavaScript
  private val transformers = new mutable.HashMap[String, mutable.ListBuffer[ResourceProcessor]]

  // e.g. HTML -> HTML
  private val enrichers = new mutable.HashMap[String, mutable.ListBuffer[ResourceProcessor]]

  // Init, differentiate between processors and enrichers
  GuiceUtils.getByType(injector, classOf[ResourceProcessor]).asScala foreach { binding =>
    val processor = binding.getProvider.get()
    if (processor.fromFileEnding != processor.toFileEnding) {
      val list = transformers.getOrElseUpdate(processor.toFileEnding, new mutable.ListBuffer[ResourceProcessor])
      list.append(processor)
    } else {
      val list = enrichers.getOrElseUpdate(processor.toFileEnding, new mutable.ListBuffer[ResourceProcessor])
      list.append(processor)
    }
  }

  private def getFileNameEnding(fileName: String): String = {
    val dot = fileName.lastIndexOf('.')
    fileName.substring(dot + 1)
  }

  def replaceFileNameEndingForProcessor(processor: ResourceProcessor, fileName: String): String = {
    val currentEndingStrippedLength = fileName.size - processor.toFileEnding.size
    fileName.substring(0, currentEndingStrippedLength) + processor.fromFileEnding
  }

  def getProcessorsForFile(fileName: String): List[ResourceProcessor] = {
    val ending = getFileNameEnding(fileName)
    val processors = transformers.getOrElse(ending, new mutable.ListBuffer)
    noOpProcessor :: processors.toList
  }

  def getEnrichersForFile(fileName: String): List[ResourceProcessor] = {
    val ending = getFileNameEnding(fileName)
    enrichers.getOrElse(ending, Nil).toList
  }

}

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources

import com.google.inject._
import java.lang.RuntimeException
import java.io.{File, InputStream}


trait ResourceLocation {

  def getResource(fileName: String): Option[Resource]
}

class ClassLoaderResourceLocation extends ResourceLocation {

  def getResource(fileName: String): Option[Resource] = {
    val try1 = Thread.currentThread().getContextClassLoader.getResource(fileName)
    if (try1 != null) {
      return Some(new URLResource(fileName, try1))
    }
    val try2 = getClass.getResource(fileName)
    if (try2 != null) {
      return Some(new URLResource(fileName, try2))
    }
    val try3 = getClass.getClassLoader.getResource(fileName)
    if (try3 != null) {
      return Some(new URLResource(fileName, try3))
    }
    None
  }
}

class FileSystemResourceLocation(val baseDir: File) extends ResourceLocation {

  if(! baseDir.exists()) require(baseDir.mkdirs(), baseDir.getAbsolutePath + " does not exist and could not be created!")
  else {
    require(baseDir.isDirectory, baseDir.getAbsolutePath + " is not a directory.")
    require(baseDir.canRead, baseDir.getAbsolutePath + " is not readable.")
  }

  def getResource(fileName: String) = {
    val file = new File(baseDir, fileName)

    if(file.exists() && file.canRead) Some(new FileResource(fileName, file))
    else None
  }
}

class ResourceLoader @Inject()(injector: Injector,
                               resourceProcessorRegistry: ResourceProcessorRegistry) {
  import scala.collection.JavaConverters._

  val resourceLocations: List[Binding[ResourceLocation]] = {
    injector.findBindingsByType(new TypeLiteral[ResourceLocation]() {}).asScala.toList
  }

  def getInputStream(fileName: String): InputStream = {
    getInputStreamOption(fileName) match {
      case Some(resource) => resource
      case None => throw new RuntimeException("Resource [%s] not found!".format(fileName))
    }
  }

  def getInputStreamOption(fileName: String): Option[InputStream] =
    withResource(fileName) { (res, proc) => Some(proc.transform(fileName, res.getInputStream)) }

  def getResource(fileName: String): Option[Resource] =
    withResource(fileName) { (res, proc) => Some(res) }

  private def withResource[T](fileName: String)(func: (Resource, ResourceProcessor) => Option[T]): Option[T] = {
    // TODO cache resolved mappings
    for (processor <- resourceProcessorRegistry.processorsForFile(fileName)) {
      val fileNameForProcessor = resourceProcessorRegistry.replaceFileNameEndingForProcessor(processor, fileName)
      for (rl <- resourceLocations) {
        rl.getProvider.get().getResource(fileNameForProcessor) match {
          case Some(res) => return func(res, processor)
          case None => None
        }
      }
    }
    None
  }
}



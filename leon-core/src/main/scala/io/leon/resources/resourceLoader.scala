/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources

import java.io.InputStream
import com.google.inject._
import java.lang.RuntimeException

// TODO Implement Resource class to group file/loader/lastModified/etc.

trait ResourceLocation {
  def getInputStreamOption(fileName: String): Option[InputStream]
}

class ClassLoaderResourceLocation extends ResourceLocation {
  def getInputStreamOption(fileName: String): Option[InputStream] = {
    val try1 = Thread.currentThread().getContextClassLoader.getResourceAsStream(fileName)
    if (try1 != null) {
      return Some(try1)
    }
    val try2 = getClass.getResourceAsStream(fileName)
    if (try2 != null) {
      return Some(try2)
    }
    val try3 = getClass.getClassLoader.getResourceAsStream(fileName)
    if (try3 != null) {
      return Some(try3)
    }
    None
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

  def getInputStreamOption(fileName: String): Option[InputStream] = {
    // TODO cache resolved mappings
    for (processor <- resourceProcessorRegistry.processorsForFile(fileName)) {
      val fileNameForProcessor = resourceProcessorRegistry.replaceFileNameEndingForProcessor(processor, fileName)
      for (rl <- resourceLocations) {
        rl.getProvider.get().getInputStreamOption(fileNameForProcessor) match {
          case Some(r) => return Some(processor.transform(fileName, r))
          case None => None
        }
      }
    }
    None
  }

}

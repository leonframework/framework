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


class ResourceLoader @Inject()(injector: Injector,
                               resourceProcessorRegistry: ResourceProcessorRegistry) {
  import scala.collection.JavaConverters._

  val resourceLocations: List[Binding[ResourceLocation]] = {
    injector.findBindingsByType(new TypeLiteral[ResourceLocation]() {}).asScala.toList
  }

  def getResource(fileName: String): Resource = {
    getResourceOption(fileName) match {
      case Some(resource) => resource
      case None => throw new RuntimeException("Resource [%s] not found!".format(fileName))
    }
  }

  def getResourceOption(fileName: String): Option[Resource] = {
    // TODO cache resolved mappings
    for (processor <- resourceProcessorRegistry.processorsForFile(fileName)) {
      val fileNameForProcessor = resourceProcessorRegistry.replaceFileNameEndingForProcessor(processor, fileName)
      for (rl <- resourceLocations) {
        rl.getProvider.get().getResource(fileNameForProcessor) match {
          case Some(res) => return Some(processor.process(res))
          case None => None
        }
      }
    }
    None
  }

}



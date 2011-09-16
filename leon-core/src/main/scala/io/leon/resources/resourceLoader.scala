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
import java.io.{FileInputStream, File}
import org.apache.commons.io.FileUtils
import scala.io.Source
import scala.collection.JavaConversions._
import org.slf4j.LoggerFactory

class ResourceLoader @Inject()(injector: Injector,
                               resourceProcessorRegistry: ResourceProcessorRegistry) {

  import scala.collection.JavaConverters._

  private val logger = LoggerFactory.getLogger(getClass)

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
    //TODO make cache location configurable
    val cacheLocation = System.getProperty("java.io.tmpdir")
    //System.out.println("Cache is at: " + cacheLocation)

    for (processor <- resourceProcessorRegistry.processorsForFile(fileName)) {
      val fileNameForProcessor = resourceProcessorRegistry.replaceFileNameEndingForProcessor(processor, fileName)

      for (rl <- resourceLocations) {
        rl.getProvider.get().getResource(fileNameForProcessor) match {
          case Some(res) => {

            val fileFromCache: File = getOrTransformResource(res, fileNameForProcessor, cacheLocation, processor)
            return Some(new Resource(fileName, () => res.lastModified, () => new FileInputStream(fileFromCache)))
          }
          case None => None
        }
      }
    }
    None
  }

  private def getOrTransformResource(res: Resource, fileNameForProcessor: String, cacheLocation: String, processor: ResourceProcessor): File = {
    val cachedFile = new File(cacheLocation, fileNameForProcessor)

    if (!cachedFile.exists() || res.lastModified > cachedFile.lastModified()) {
      logger.debug("File {} either too old or not in cache yet.", cachedFile.getAbsolutePath)
      val processedRes = processor.process(res)

      cachedFile.getParentFile.mkdirs()

      FileUtils.writeLines(cachedFile, Source.fromInputStream(processedRes.getInputStream).getLines().toList)
    }

    cachedFile
  }
}



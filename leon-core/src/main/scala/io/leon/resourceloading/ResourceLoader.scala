/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resourceloading

import com.google.inject._
import java.lang.RuntimeException
import org.slf4j.LoggerFactory
import collection.immutable.List
import processor.{ResourceProcessor, ResourceProcessorRegistry}
import io.leon.config.ConfigMap
import io.leon.utils.FileUtils

class ResourceLoader @Inject()(injector: Injector,
                               classAndResourceLoader: ClassAndResourceLoader,
                               resourceProcessorRegistry: ResourceProcessorRegistry,
                               configMap: ConfigMap,
                               resourceCache: ResourceCache,
                               resourceLoadingStack: ResourceLoadingStack) {

  private val logger = LoggerFactory.getLogger(getClass)

  private def convertRelativePathToAbsolutePathIfNecessary(fileName: String): String = {
    if (!fileName.startsWith("/")) {
      if (resourceLoadingStack.getResourceLoadingStack().size() == 0) {
        throw new IllegalStateException(
          "Relative paths are only possible for nested resource loading. Path: '" + fileName + "'")
      }
      val predecessor = resourceLoadingStack.getResourceLoadingStack().get(0)
      FileUtils.getDirectoryNameOfPath(predecessor) + fileName
    } else {
      fileName
    }
  }

  private def applyEnrichers(fileName: String, resource: Resource): Resource = {
    val es = resourceProcessorRegistry.getEnrichersForFile(fileName)
    es.foldLeft(resource) { (enriched, enricher) =>
      logger.debug("Applying enricher [{}] for resource [{}]", enricher, fileName)
      enricher.process(enriched)
    }
  }

  def getResource(fileName: String): Resource = {
    getResourceOption(fileName) match {
      case Some(resource) => {
        logger.trace("Loaded resource {}", resource.name)
        resource
      }
      case None => throw new RuntimeException("Resource [%s] not found!".format(fileName))
    }
  }

  def getResourceOption(_fileName: String): Option[Resource] = synchronized {
    val fileName = convertRelativePathToAbsolutePathIfNecessary(_fileName)
    resourceCache.doDependencyCheck(fileName)
    resourceLoadingStack.pushResourceOnStack(fileName)
    try {
      logger.trace("Searching resource [{}]", fileName)

      val processors = resourceProcessorRegistry.getProcessorsForFile(fileName)
      val combinations = for {
        processor <- processors
      } yield {
        (fileName, processor)
      }

      tryCombinations(combinations) match {
        case None => return None
        case Some((fileNameForProcessor, processor, processed)) => return processed
      }
    } finally {
      resourceLoadingStack.popResourceFromStack()
    }
    None
  }

  private def tryCombinations(combinations: List[(String, ResourceProcessor)])
      : Option[(String, ResourceProcessor, Option[Resource])] =

    combinations match {
      case Nil => None
      case (fileName, processor) :: xs => {
        val fileNameForProcessor = resourceProcessorRegistry.replaceFileNameEndingForProcessor(processor, fileName)
        val resourceOption = classAndResourceLoader.getResource(fileNameForProcessor)

        if (resourceOption.isDefined) {
          val resource = resourceOption.get
          logger.trace("Found resource [{}].", fileNameForProcessor)

          // Check if the processor requested caching (if caching is not disabled)
          val processed = processor.process(resource)
          val cachedOrNormal = if (!configMap.isCacheDisabled
            && processed.isCachingDesired()
            && processed.isCachingPossible()) {

            if (!resourceCache.isCacheUpToDate(resource, fileName, this)) {
              logger.info("Cache for resource [{}] is out of date", fileName)
              val enriched = applyEnrichers(fileName, processed)
              val cachedResource = resourceCache.put(fileName, enriched)
              cachedResource // RR: I used a variable here just to make it obvious that a cached resource gets returned
            } else {
              // cache is up to date
              logger.debug("Cache for resource [{}] is up to date", fileName)
              resourceCache.get(fileName)
            }
          } else {
            // No caching request. Normal processing.
            processor.process(applyEnrichers(fileName, resource))
          }
          Some((fileNameForProcessor, processor, Some(cachedOrNormal)))
        } else {
          tryCombinations(xs)
        }
      }
    }

}



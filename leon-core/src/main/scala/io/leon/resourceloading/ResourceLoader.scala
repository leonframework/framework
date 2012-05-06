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
import location.ResourceLocation
import org.slf4j.LoggerFactory
import collection.immutable.List
import processor.{ResourceProcessor, ResourceProcessorRegistry}
import watcher.{ResourceWatcher, ResourceChangedListener}
import scala.collection.JavaConverters._
import io.leon.config.ConfigMap
import io.leon.utils.{FileUtils, GuiceUtils}

class ResourceLoader @Inject()(injector: Injector,
                               resourceProcessorRegistry: ResourceProcessorRegistry,
                               resourceWatcher: ResourceWatcher,
                               configMap: ConfigMap,
                               resourceCache: ResourceCache) {

  private val logger = LoggerFactory.getLogger(getClass)

  private val resourceLoadingStack = new ThreadLocal[java.util.List[String]] {
    override def initialValue() = new java.util.concurrent.CopyOnWriteArrayList[String]()
  }

  val resourceLocations: List[Binding[ResourceLocation]] = {
    GuiceUtils.getByType(injector, classOf[ResourceLocation]).asScala.toList
  }

  def getResource(fileName: String): Resource = {
    getResource(fileName, null)
  }

  def getResource(fileName: String, changedListener: ResourceChangedListener): Resource = {
    getResourceOption(fileName, changedListener) match {
      case Some(resource) => {
        logger.trace("Loaded resource {}", resource.name)
        resource
      }
      case None => throw new RuntimeException("Resource [%s] not found!".format(fileName))
    }
  }

  def getResourceOption(fileName: String): Option[Resource] = {
    getResourceOption(fileName, null)
  }

  def getResourceOption(_fileName: String, changedListener: ResourceChangedListener): Option[Resource] = {
    // check if a relative path was used
    val fileName = if (!_fileName.startsWith("/")) {
      val predecessor = resourceLoadingStack.get().get(0)
      FileUtils.getDirectoryNameOfPath(predecessor) + _fileName
    } else {
      _fileName
    }
    resourceLoadingStack.get().add(0, fileName)
    logger.trace("Searching resource [{}]", fileName)

    val processors = resourceProcessorRegistry.getProcessorsForFile(fileName)
    val combinations = for {
      processor <- processors
      locationBinding <- resourceLocations
    } yield {
      val location = locationBinding.getProvider.get()
      (fileName, location, processor)
    }

    val result = tryCombinations(combinations) match {
      case None => None
      case Some((fileNameForProcessor, location, processor, processed)) =>
        resourceWatcher.addResource(fileNameForProcessor, location, processor, processed.get, changedListener)
        processed
    }
    resourceLoadingStack.get().remove(0)
    result
  }

  private def tryCombinations(combinations: List[(String, ResourceLocation, ResourceProcessor)])
      : Option[(String, ResourceLocation, ResourceProcessor, Option[Resource])] =

    combinations match {
      case Nil => None
      case (fileName, location, processor) :: xs => {
        val fileNameForProcessor = resourceProcessorRegistry.replaceFileNameEndingForProcessor(processor, fileName)
        val resourceOption = location.getResource(fileNameForProcessor)

        if (resourceOption.isDefined) {
          val resource = resourceOption.get
          logger.trace("Found resource [{}].", fileNameForProcessor)

          // Check if the processor requested caching (if caching is not disabled)
          val cachedOrNormal = if (!configMap.isCacheDisabled && resource.isCachable()) {
            logger.trace("Checking cache for resource [{}]", fileName)
            val cacheTimestamp = resourceCache.getTimestampOfCacheFile(fileName)
            val normalTimestamp = resource.getLastModified()

            if (normalTimestamp > cacheTimestamp) {
              // cache is out of date
              logger.debug("Cached version for resource [{}] is out of date.", fileName)
              val processed = processor.process(resource)

              val enriched = applyEnrichers(fileName, processed)
              val cachedResource = resourceCache.put(fileName, enriched)
              cachedResource // RR: I used a variable here just to make it obvious that a cached resource gets returned
            } else {
              // cache is up to date
              logger.trace("Cached version for resource [{}] is up to date.", fileName)
              resourceCache.get(fileName)
            }
          } else {
            // No caching request. Normal processing.
            processor.process(applyEnrichers(fileName, resource))
          }
          Some((fileNameForProcessor, location, processor, Some(cachedOrNormal)))
        } else {
          tryCombinations(xs)
        }
      }
    }

  private def applyEnrichers(fileName: String, resource: Resource): Resource = {
    val es = resourceProcessorRegistry.getEnrichersForFile(fileName)
    es.foldLeft(resource) { (enriched, enricher) =>
      logger.debug("Applying enricher {}", enricher)
      enricher.process(enriched)
    }
  }

}



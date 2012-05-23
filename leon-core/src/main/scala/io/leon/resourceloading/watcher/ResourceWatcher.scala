/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resourceloading.watcher

import org.slf4j.LoggerFactory
import io.leon.resourceloading.{ClassAndResourceLoader, Resource}
import io.leon.resourceloading.processor.ResourceProcessor
import com.google.inject.Inject
import io.leon.web.TopicsService
import scala.collection.JavaConverters._

// TODO get TopicsService with an Injector instance so that user code is not forced to install the CometModule all the time
class ResourceWatcher @Inject()(resourceLocations: ClassAndResourceLoader,
                                topicsService: TopicsService) {

  case class WatchedResource(fileNameForProcessor: String,
                             processor: ResourceProcessor,
                             resource: Resource,
                             var lastTimestamp: Long,
                             changedListener: ResourceChangedListener)

  private val logger = LoggerFactory.getLogger(this.getClass)

  @volatile
  private var started = false

  private val watchedResources = new java.util.Vector[WatchedResource]

  private val thread = new Thread {

    private val changedResources = new java.util.Vector[WatchedResource]()

    private var lastDetectedModification = -1L

    override def run() {
      while (started) {
        Thread.sleep(2000)
        for (watched <- watchedResources.asScala) {
          val currentLastModified = watched.resource.getLastModified()
          if (currentLastModified > watched.lastTimestamp) {
            watched.lastTimestamp = currentLastModified
            changedResources.add(watched)
            lastDetectedModification = System.currentTimeMillis()
            logger.debug("Resource [{}] changed.", watched.fileNameForProcessor)
          }
        }

        if (lastDetectedModification != -1L &&
          (lastDetectedModification + 1000) <= System.currentTimeMillis()) {
          logger.debug("Applying pending resource changes.")

          lastDetectedModification = -1L
          for (changed <- changedResources.asScala) {
            try {
              val newResource = changed.processor.process(
                resourceLocations.getResource(changed.resource.name).get)

              changed.changedListener.resourceChanged(newResource)
              topicsService.send(
                "/leon/developmentMode/resourceWatcher/resourceChanged",
                Map("name" -> newResource.name).asJava)
            } catch {
              case e => logger.error("Error while calling ResourceChangedLister", e)
            }
          }
          changedResources.clear()
        }
      }
    }
  }

  def start() {
    if (started) {
      return
    }
    thread.start()
    started = true
  }

  def stop() {
    started = false
  }

  def addResource(fileNameForProcessor: String,
                  processor: ResourceProcessor,
                  resource: Resource,
                  changedListener: ResourceChangedListener) {

    if (resource.getLastModified() == -1 || changedListener == null) {
      return
    }

    logger.debug("Watching resource [{}] for changes.", fileNameForProcessor)
    watchedResources.add(WatchedResource(
      fileNameForProcessor,
      processor,
      resource,
      resource.getLastModified(),
      changedListener))
  }

}

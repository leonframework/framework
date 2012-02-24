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
import io.leon.resourceloading.location.ResourceLocation
import io.leon.resourceloading.processor.ResourceProcessor
import io.leon.resourceloading.Resource
import io.leon.web.comet.CometRegistry
import com.google.inject.Inject
import com.google.common.collect.Maps

class ResourceWatcher @Inject()(cometRegistry: CometRegistry) {

  case class WatchedResource(fileNameForProcessor: String,
                             location: ResourceLocation,
                             processor: ResourceProcessor,
                             resource: Resource,
                             var lastTimestamp: Long,
                             changedListener: ResourceChangedListener)

  import scala.collection.JavaConverters._

  private val logger = LoggerFactory.getLogger(this.getClass)

  private var started = false

  private val watchedResources = new java.util.Vector[WatchedResource]

  private val thread = new Thread {

    private val changedResources = new java.util.Vector[WatchedResource]()

    private var lastDetectedModification = -1L

    override def run() {
      while (isStarted()) {
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
                changed.location.getResource(changed.resource.name).get)

              changed.changedListener.resourceChanged(newResource)
              cometRegistry.publish(
                "leon.developmentMode.resourceWatcher.resourceChanged",
                Maps.newHashMap(),
                "test")
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

  def isStarted() = {
    started
  }

  def addResource(fileNameForProcessor: String,
                  location: ResourceLocation,
                  processor: ResourceProcessor,
                  resource: Resource,
                  changedListener: ResourceChangedListener) {

    if (resource.getLastModified() == -1 || changedListener == null) {
      return
    }

    logger.debug("Watching resource [{}] for changes.", fileNameForProcessor)
    watchedResources.add(WatchedResource(
      fileNameForProcessor,
      location,
      processor,
      resource,
      resource.getLastModified(),
      changedListener))
  }

}

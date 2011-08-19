/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources

import com.google.inject.Inject
import collection.mutable
import org.slf4j.LoggerFactory


class ResourceWatcher @Inject()(resourceLoader: ResourceLoader) {
  val Interval = 500
  val Threshold = 2000

  type Action = Resource => Unit

  private type Value = (Resource, Long, Action)

  private val watchedResources = new mutable.ArrayBuffer[Value] with mutable.SynchronizedBuffer[Value]
  private val pendingActions = mutable.ArrayBuffer.empty[(Resource, Action, Int)]

  private var running = false
  private var lastModificationFound = -1L

  def watch(res: Resource, action: Action) {
    watchedResources += ((res, res.lastModified, action))
  }

  def start() {
    if(!running) {
      running = true
      new Thread(new Runnable() {
        def run() {
          logger.debug("ResourceWatcher is running now!")

          while(running) {
            doWatchFiles()
            Thread.sleep(Interval)

            val timeSinceLastModification = System.currentTimeMillis() - lastModificationFound
            if(lastModificationFound > 0 & timeSinceLastModification > Threshold)
              executePendingActions()
          }

          logger.debug("ResourceWatcher has been stopped!")
        }
      }, "ResourceWatcher").start()
    }
  }

  def stop() { running = false }

  private def doWatchFiles() {
    for (((resource, timestamp, action), index) <- watchedResources.zipWithIndex) {
      val lastModified = resource.lastModified
      if (lastModified > timestamp) {

        logger.debug("Resource {} has been modified ...", resource.name)

        watchedResources.update(index, (resource, lastModified, action))
        pendingActions += ((resource, action, index))
        lastModificationFound = System.currentTimeMillis()
      }
    }
  }

  private def executePendingActions() {
    logger.debug("Executing {} pending actions", pendingActions.size)

    // make sure we execute all actions in the same order they were added.
    val sortedActions = pendingActions sortWith { _._3 < _._3 }
    for ((resource, action, index) <- sortedActions) {
      try {
        action(resource)
      } catch {
        case e: Throwable => println("error while executing action ")
      }
    }

    pendingActions.clear()
    lastModificationFound = -1
  }

  private val logger = LoggerFactory.getLogger(this.getClass)
}

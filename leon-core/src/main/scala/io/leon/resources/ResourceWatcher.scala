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


class ResourceWatcher @Inject()(resourceLoader: ResourceLoader) {
  val Interval = 2000

  private type Value = (Long, (String) => Unit)

  private var running = false
  private val watchedFiles = new mutable.HashMap[Resource, Value] with mutable.SynchronizedMap[Resource, Value]

  def watch(filename: String, action: (String) => Unit) {
    for (res <- resourceLoader.getResourceOption(filename)) {
      watchedFiles += res -> (res.lastModified, action)
    }
  }

  def start() {
    if(!running) {
      running = true
      new Thread(new Runnable() {
        def run() {
          while(running) {
            doWatchFiles()
            Thread.sleep(Interval)
          }
        }
      }, "ResourceWatcher").start()
    }
  }

  def stop() { running = false }

  private def doWatchFiles() {
    for ((resource, (lastModified, action)) <- watchedFiles) {
      val timestamp = resource.lastModified
      if (timestamp > lastModified) {
        println("file " + resource.name + " has been changed ... reload")
        watchedFiles.update(resource, (timestamp, action))
        try {
          action(resource.name)
        } catch {
          case e: Throwable => println("error while executing action ")
        }
      }
    }
  }
}

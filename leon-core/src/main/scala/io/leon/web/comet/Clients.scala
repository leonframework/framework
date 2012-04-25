/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.ConcurrentHashMap
import io.leon.config.ConfigMapHolder
import java.util.HashMap
import java.util.concurrent.locks.ReentrantLock


object Clients {

  private val ids = new AtomicLong

  private def getConfigMap() = ConfigMapHolder.getInstance().getConfigMap

  def generateNewClientId(): String = {
    if (getConfigMap().isProductionMode) {
      (System.currentTimeMillis() + ids.getAndIncrement).toString
    } else {
      ids.getAndIncrement.toString
    }
  }

}

class Clients {

  import scala.collection.JavaConverters._

  private val lock = new ReentrantLock(false)

  private val byClientId = new HashMap[String, ClientConnection]

  def allClients: Iterable[ClientConnection] = byClientId.values().asScala

  def getByClientIdOption(id: String): Option[ClientConnection] = {
    lock.lock()
    try {
      if (byClientId.containsKey(id))
        Some(byClientId.get(id))
      else
        None
    } finally {
      lock.unlock()
    }
  }

  def getOrCreateByClientId(id: String): ClientConnection = {
    lock.lock()
    try {
      if (byClientId.containsKey(id)) {
        byClientId.get(id)
      }
      else {
        val newCc = new ClientConnection(id, None)
        byClientId.put(id, newCc)
        newCc
      }
    } finally {
      lock.unlock()
    }
  }

  def add(client: ClientConnection) = {
    lock.lock()
    try {
      byClientId.put(client.clientId, client)
    } finally {
      lock.unlock()
    }
  }

  def remove(client: ClientConnection) = {
    lock.lock()
    try {
      byClientId.remove(client.clientId)
    } finally {
      lock.unlock()
    }
  }

}

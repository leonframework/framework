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

  private val byClientId = new ConcurrentHashMap[String, ClientConnection]

  def allClients: Iterable[ClientConnection] = byClientId.values().asScala

  def getByClientId(id: String): Option[ClientConnection] = {
    if (byClientId.containsKey(id)) Some(byClientId.get(id)) else None
  }

  def add(client: ClientConnection) = byClientId.put(client.clientId, client)

  def remove(client: ClientConnection) = byClientId.remove(client.clientId)

}

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
import com.google.common.collect.Lists


object Clients {

  private val ids = new AtomicLong

  private def getConfigMap = ConfigMapHolder.getInstance().getConfigMap

  def generateNewClientId(): String = {
    if (getConfigMap.isProductionMode) {
      (System.currentTimeMillis() + ids.getAndIncrement).toString
    } else {
      ids.getAndIncrement.toString
    }
  }

}

class Clients {

  private val byClientId = new ConcurrentHashMap[String, ClientConnection]

  /**
   * @return a copyied List of all ClientConnection.
   */
  def getAllClients: java.util.List[ClientConnection] = {
    val copy = Lists.newLinkedList[ClientConnection]
    copy.addAll(byClientId.values())
    copy
  }

  def getByClientIdOption(id: String): Option[ClientConnection] = synchronized {
    if (byClientId.containsKey(id))
      Some(byClientId.get(id))
    else
      None
  }

  def getOrCreateByClientId(id: String): ClientConnection = synchronized {
    if (byClientId.containsKey(id)) {
      byClientId.get(id)
    }
    else {
      val newCc = new ClientConnection(id, None)
      byClientId.put(id, newCc)
      newCc
    }
  }

  def add(client: ClientConnection) = synchronized {
    byClientId.put(client.clientId, client)
  }

  def remove(client: ClientConnection) = synchronized {
    byClientId.remove(client.clientId)
  }

}

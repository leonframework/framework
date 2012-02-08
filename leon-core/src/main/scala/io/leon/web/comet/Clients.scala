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
import javax.servlet.http.HttpSession
import java.util.concurrent.ConcurrentHashMap


object Clients {

  private val ids = new AtomicLong

  def generateNewPageId(): String = {
    // TODO check deployment mode to use the appropriate way
    //System.currentTimeMillis().toString // more save, use in production
    ids.getAndIncrement.toString // useful more debugging, use in development
  }

  def generateNewClientId(session: HttpSession): String = {
    generateExistingClientId(session, generateNewPageId())
  }

  def generateExistingClientId(session: HttpSession, pageId: String): String = {
    session.getId + "__" + pageId
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

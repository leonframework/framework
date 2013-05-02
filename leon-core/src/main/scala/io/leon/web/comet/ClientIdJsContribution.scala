/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet

import io.leon.web.browser.VirtualJsFileContribution
import java.util.Map

class ClientIdJsContribution extends VirtualJsFileContribution {

  def getContent(params: Map[String, String]) = {
    "getLeon().comet.clientId = " + Clients.generateNewClientId()
  }

}

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet

import com.google.inject.Inject
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

class UpdateSubscriptionServlet @Inject()(cometRegisty: CometRegistry) extends HttpServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    val clientId = req.getParameter("clientId")
    val topicId = req.getParameter("topicId")
    val key = req.getParameter("key")
    val value = req.getParameter("value")

    cometRegisty.updateClientFilter(topicId, clientId, key, value)
  }

}

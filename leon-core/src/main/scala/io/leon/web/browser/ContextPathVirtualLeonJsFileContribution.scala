/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.browser

import javax.servlet.http.HttpServletRequest
import com.google.inject.{Inject, Provider}

class ContextPathVirtualLeonJsFileContribution @Inject()(httpRequestProvider: Provider[HttpServletRequest])
    extends VirtualLeonJsFileContribution {

  def content(): String = {
    val script = """ leon.contextPath = "%s"; """
    val cp = httpRequestProvider.get().getContextPath
    script.format(cp)
  }

}

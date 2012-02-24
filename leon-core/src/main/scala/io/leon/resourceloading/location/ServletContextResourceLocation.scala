/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resourceloading.location

import javax.servlet.ServletContext
import io.leon.resourceloading.Resource
import java.net.URL
import com.google.inject.{Injector, Inject}
import io.leon.web.StaticServletContextHolder

class ServletContextResourceLocation extends ResourceLocation {

  val delegate = new DelegatingResourceLocation(getFromServletContext)

  def getFromServletContext(fileName: String): URL = {
    StaticServletContextHolder.SERVLET_CONTEXT.getResource(fileName)
  }

  def getResource(fileName: String): Option[Resource] = {
    val _fileName = if (fileName.startsWith("/")) fileName else "/" + fileName
    delegate.getResource(_fileName)
  }

}

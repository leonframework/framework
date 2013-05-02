/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/*
 * Copyright (c) 2011 WeigleWilczek and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resourceloading

import java.net.URL
import io.leon.web.StaticServletContextHolder

class ResourceLoader {

  private def tryClassBased(name: String): URL = {
    getClass.getResource(name)
  }

  private def tryContext(name: String): URL = {
    Thread.currentThread().getContextClassLoader.getResource(name)
  }

  private def tryServletContext(name: String): URL = {
    // TODO this may be required
    //val _fileName = if (fileName.startsWith("/")) fileName else "/" + fileName
    StaticServletContextHolder.SERVLET_CONTEXT.getResource(name)
  }


  def getResource(fileName: String): Resource = {
    var trying = tryClassBased(fileName)
    if (trying == null) {
      trying = tryContext(fileName)
    }
    if (trying == null) {
      trying = tryServletContext(fileName)
    }
    if (trying == null) {
      throw new RuntimeException // TODO Add new Exception type to easily implement 404 pages
    }

    val resource = new Resource(fileName) {
      def getInputStream() = {
        trying.openStream()
      }
    }
    Some(resource)
  }

}

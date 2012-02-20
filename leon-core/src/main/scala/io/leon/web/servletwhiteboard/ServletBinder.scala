/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.servletwhiteboard

import com.google.inject.Binder
import com.google.inject.name.Names
import io.leon.web.resources.WebResourcesBinder

class ServletBinder(binder: Binder) {

  def registerServlet(servletBinding: ServletBinding) {
    // Expose URL
    new WebResourcesBinder(binder).exposeUrl(servletBinding.url)

    // Register Servlet
    binder.bind(classOf[ServletBinding]).annotatedWith(Names.named(servletBinding.url)).toInstance(servletBinding)
  }

}

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.resources

import com.google.inject.name.Names
import com.google.inject.Binder

class WebResourcesBinder(binder: Binder) {

  def exposeUrl(url: String) {
    binder.bind(classOf[ExposedUrl]).annotatedWith(Names.named(url)).toInstance(ExposedUrl(url))
  }

}

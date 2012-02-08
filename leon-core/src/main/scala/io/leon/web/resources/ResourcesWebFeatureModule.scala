/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.resources

import com.google.inject.Scopes
import com.google.inject.servlet.ServletModule

class ResourcesWebFeatureModule extends ServletModule {

  override def configureServlets() {
    bind(classOf[ResourcesServlet]).asEagerSingleton()

    bind(classOf[ExposedUrlCheckFilter]).in(Scopes.SINGLETON)
    filter("/*").through(classOf[ExposedUrlCheckFilter])

    serve("/*").`with`(classOf[ResourcesServlet])
  }

}

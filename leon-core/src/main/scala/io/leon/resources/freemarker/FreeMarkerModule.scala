/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.freemarker

import com.google.inject.AbstractModule

class FreeMarkerModule extends AbstractModule {

  def configure() {
    bind(classOf[FreeMarkerProcessor]).asEagerSingleton()
    bind(classOf[LeonFreeMarkerTemplateLoader]).asEagerSingleton()
  }

}

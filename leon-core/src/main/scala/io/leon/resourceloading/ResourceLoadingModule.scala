/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resourceloading

import io.leon.resourceloading.processor.ResourceProcessorRegistry
import com.google.inject.{Scopes, AbstractModule}

class ResourceLoadingModule extends AbstractModule {

  def configure() {
    bind(classOf[ClassAndResourceLoader]).in(Scopes.SINGLETON)
    bind(classOf[ResourceLoadingStack]).in(Scopes.SINGLETON)
    bind(classOf[ResourceCache]).in(Scopes.SINGLETON)
    bind(classOf[ResourceLoader]).in(Scopes.SINGLETON)
    bind(classOf[ResourceProcessorRegistry]).in(Scopes.SINGLETON)
  }

}







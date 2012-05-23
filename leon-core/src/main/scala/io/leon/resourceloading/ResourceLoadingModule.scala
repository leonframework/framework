/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resourceloading

import io.leon.resourceloading.processor.{NoOpResourceProcessor, ResourceProcessorRegistry}
import io.leon.config.ConfigMapHolder
import watcher.ResourceWatcher
import com.google.inject.{Scopes, Inject, AbstractModule}

class ResourceLoadingModule extends AbstractModule {

  def configure() {
    bind(classOf[ClassAndResourceLoader]).in(Scopes.SINGLETON)
    bind(classOf[ResourceLoadingStack]).in(Scopes.SINGLETON)
    bind(classOf[ResourceCache]).in(Scopes.SINGLETON)
    bind(classOf[ResourceLoader]).in(Scopes.SINGLETON)
    bind(classOf[ResourceProcessorRegistry]).in(Scopes.SINGLETON)
    bind(classOf[NoOpResourceProcessor]).in(Scopes.SINGLETON)

    // Resourcewatcher, start depends on the deployment mode
    bind(classOf[ResourceWatcher]).asEagerSingleton()
    if (ConfigMapHolder.getInstance().getConfigMap.isDevelopmentMode) {
      requestInjection(new Object {
        @Inject def init(watcher: ResourceWatcher) {
          watcher.start()
        }
      })
    }
  }

}







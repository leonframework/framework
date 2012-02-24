/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resourceloading

import com.google.inject.name.Names
import io.leon.resourceloading.processor.{NoOpResourceProcessor, ResourceProcessorRegistry}
import io.leon.config.ConfigMapHolder
import location.{ServletContextResourceLocation, ResourceLocation, DelegatingResourceLocation}
import watcher.ResourceWatcher
import io.leon.guice.GuiceUtils
import com.google.inject.{Scopes, Inject, AbstractModule}

class ResourceLoadingModule extends AbstractModule {

  def configure() {
    // --- Classloader-based ResourceLocations ---

    val clClass = new DelegatingResourceLocation((name) => getClass.getResource(name))
    val clClassName = clClass.getClass.getName + "- Leon class-based classloader"
    bind(classOf[ResourceLocation]).annotatedWith(Names.named(clClassName)).toInstance(clClass)

    val clThread = new DelegatingResourceLocation((name) => Thread.currentThread().getContextClassLoader.getResource(name))
    val clThreadName = clThread.getClass.getName + "- Thread local context classloader"
    bind(classOf[ResourceLocation]).annotatedWith(Names.named(clThreadName)).toInstance(clThread)

    // --- ServletContext-based ResourceLocations ---

    GuiceUtils.bindClassWithName(binder(), classOf[ResourceLocation], classOf[ServletContextResourceLocation]).in(Scopes.SINGLETON)


    bind(classOf[ResourceLoader]).in(Scopes.SINGLETON)
    bind(classOf[ResourceProcessorRegistry]).in(Scopes.SINGLETON)
    bind(classOf[NoOpResourceProcessor]).in(Scopes.SINGLETON)

    // Resourcewatcher, depending on the deployment mode
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







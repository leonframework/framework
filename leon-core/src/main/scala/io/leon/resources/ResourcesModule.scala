/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources

import com.google.inject.name.Names
import htmltagsprocessor.LeonTagProcessor
import com.google.inject.{Inject, AbstractModule}


class ResourcesModule extends AbstractModule {

  def configure() {
    bind(classOf[ResourceLoader]).asEagerSingleton()

    // --- Default classloader-based ResourceLocations ---

    val clCore = new DelegatingResourceLocation((name) => getClass.getClassLoader.getResource(name))
    val clCoreName = clCore.getClass.getName + "- Leon core classloader"
    bind(classOf[ResourceLocation]).annotatedWith(Names.named(clCoreName)).toInstance(clCore)

    val clClass = new DelegatingResourceLocation((name) => getClass.getResource(name))
    val clClassName = clClass.getClass.getName + "- Leon class-based classloader"
    bind(classOf[ResourceLocation]).annotatedWith(Names.named(clClassName)).toInstance(clClass)

    val clThread = new DelegatingResourceLocation((name) => Thread.currentThread().getContextClassLoader.getResource(name))
    val clThreadName = clThread.getClass.getName + "- Thread local context classloader"
    bind(classOf[ResourceLocation]).annotatedWith(Names.named(clThreadName)).toInstance(clThread)


    //bind(classOf[FreeMarkerProcessor]).asEagerSingleton()
    //bind(classOf[LeonFreeMarkerTemplateLoader]).asEagerSingleton()

    bind(classOf[LeonTagProcessor]).asEagerSingleton()

    bind(classOf[NoOpResourceProcessor]).asEagerSingleton()
    bind(classOf[ResourceProcessorRegistry]).asEagerSingleton()
    bind(classOf[ResourceWatcher]).asEagerSingleton()

    requestInjection(new Object {
      @Inject def init(watcher: ResourceWatcher) {
        watcher.start()
      }
    })
  }
}







/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources

import closure.ClosureTemplatesModule
import com.google.inject.name.Names
import freemarker.{LeonFreeMarkerTemplateLoader, FreeMarkerProcessor}
import htmltagsprocessor.LeonTagProcessor
import less.LessModule
import com.google.inject.{Inject, AbstractModule}


class ResourcesModule extends AbstractModule {

  private def addLocation(clazz: Class[_ <: ResourceLocation]) {
    bind(classOf[ResourceLocation]).annotatedWith(Names.named(clazz.getName)).to(clazz).asEagerSingleton()
  }
  
  def configure() {
    bind(classOf[ResourceLoader]).asEagerSingleton()
    addLocation(classOf[ClassLoaderResourceLocation])

    bind(classOf[LessModule]).asEagerSingleton()
    bind(classOf[ClosureTemplatesModule]).asEagerSingleton()
    bind(classOf[FreeMarkerProcessor]).asEagerSingleton()
    bind(classOf[LeonFreeMarkerTemplateLoader]).asEagerSingleton()
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







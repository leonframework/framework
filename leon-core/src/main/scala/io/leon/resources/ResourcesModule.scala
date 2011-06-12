/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources

import com.google.inject._
import freemarker.{LeonFreeMarkerTemplateLoader, FreeMarkerProcessor}
import name.Names

class ResourcesModule extends AbstractModule {

  private def addLocation(clazz: Class[_ <: ResourceLocation]) {
    bind(classOf[ResourceLocation]).annotatedWith(Names.named(clazz.getName)).to(clazz).asEagerSingleton()
  }
  
  def configure() {
    bind(classOf[ResourceLoader]).asEagerSingleton()
    addLocation(classOf[ClassLoaderResourceLocation])

    //bind(Key.get(classOf[ResourceProcessor], Names.named(classOf[StringTemplateResourceProcessor].getName))).
    //  to(classOf[StringTemplateResourceProcessor])

    bind(classOf[FreeMarkerProcessor]).asEagerSingleton()
    bind(classOf[LeonFreeMarkerTemplateLoader]).asEagerSingleton()
    bind(classOf[NoOpResourceProcessor]).asEagerSingleton()
    bind(classOf[ResourceProcessorRegistry]).asEagerSingleton()
  }
}







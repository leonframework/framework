/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources

import java.io.InputStream
import com.google.inject._
import name.Names
import java.lang.RuntimeException

class ResourceLoaderModule extends AbstractModule {

  private def addLocation(clazz: Class[_ <: ResourceLocation]) {
    bind(classOf[ResourceLocation]).annotatedWith(Names.named(clazz.getName)).to(clazz).asEagerSingleton()
  }
  
  def configure() {
    bind(classOf[ResourceLoader]).asEagerSingleton()
    addLocation(classOf[ClassLoaderResourceLocation])
  }
}

trait ResourceLocation {
  def getInputStreamOption(fileName: String): Option[InputStream]
}

class ClassLoaderResourceLocation extends ResourceLocation {
  def getInputStreamOption(fileName: String): Option[InputStream] = {
    val try1 = Thread.currentThread().getContextClassLoader.getResourceAsStream(fileName)
    if (try1 != null) {
      return Some(try1)
    }
    val try2 = getClass.getResourceAsStream(fileName)
    if (try2 != null) {
      return Some(try2)
    }
    val try3 = getClass.getClassLoader.getResourceAsStream(fileName)
    if (try3 != null) {
      return Some(try3)
    }
    None
  }
}

class ResourceLoader @Inject()(injector: Injector) {
  import scala.collection.JavaConverters._

  val resourceLocations = injector.findBindingsByType(new TypeLiteral[ResourceLocation]() {}).asScala

  def getInputStream(fileName: String): InputStream = {
    getInputStreamOption(fileName) match {
      case Some(resource) => resource
      case None => throw new RuntimeException("Resource [%s] not found!".format(fileName))
    }
  }

  def getInputStreamOption(fileName: String): Option[InputStream] = {
    for (rl <- resourceLocations) {
      rl.getProvider.get().getInputStreamOption(fileName) match {
        case Some(r) => return Some(r)
        case None => None
      }
    }
    None
  }

}
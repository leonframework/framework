/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.coffeescript

import io.leon.resourceloading.processor.ResourceProcessor
import io.leon.utils.GuiceUtils
import com.google.inject.{Scopes, AbstractModule}

class CoffeeScriptModule extends AbstractModule {
  def configure() {
    bind(classOf[CoffeeScriptInit]).asEagerSingleton()

    GuiceUtils.bindClassWithName(
      binder(), classOf[ResourceProcessor], classOf[CoffeeScriptResourceProcessor]).in(Scopes.SINGLETON)
  }
}

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.soy

import com.google.inject.{Key, AbstractModule}
import io.leon.resourceloading.processor.ResourceProcessor
import io.leon.guice.GuiceUtils

class SoyTemplatesModule extends AbstractModule {

  def configure() {
    GuiceUtils.bindClassWithName(
      binder(), classOf[ResourceProcessor], classOf[SoyTemplatesResourceProcessor]).asEagerSingleton()
  }

}

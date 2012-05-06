/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.less

import io.leon.resourceloading.processor.ResourceProcessor
import io.leon.utils.GuiceUtils
import com.google.inject.AbstractModule


class LessModule extends AbstractModule {

  def configure() {
    bind(classOf[OriginalLessFilePathHolder]).asEagerSingleton()
    GuiceUtils.bindClassWithName(
      binder(), classOf[ResourceProcessor], classOf[LessResourceProcessor]).asEagerSingleton()
  }

}

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.less

import com.google.inject.{Key, AbstractModule}
import io.leon.resources.ResourceProcessor
import com.google.inject.name.Names

class LessModule extends AbstractModule {

  def configure() {
    bind(Key.get(classOf[ResourceProcessor], Names.named(classOf[LessResourceProcessor].getName))).
      to(classOf[LessResourceProcessor]).asEagerSingleton()
  }

}

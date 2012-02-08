/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.htmltagsprocessor

import com.google.inject.{Binder, AbstractModule}
import com.google.inject.name.Names


abstract class HtmlTagsProcessorUserModule extends AbstractModule {

  def addTagRewriter[A <: LeonTagRewriter](clazz:Class[A]) {
    binder.bind(classOf[LeonTagRewriter]).annotatedWith(Names.named(clazz.getName)).to(clazz).asEagerSingleton()
  }

}

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.htmltagsprocessor

import com.google.inject.Binder
import io.leon.utils.GuiceUtils

class HtmlTagsProcessorBinder(binder: Binder) {

  def addTagRewriter[A <: LeonTagRewriter](clazz:Class[A]) {
    GuiceUtils.bindClassWithName(binder, classOf[LeonTagRewriter], clazz)
  }

}

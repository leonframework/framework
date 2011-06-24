/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import org.mozilla.javascript._
import io.leon.conversions.SJSONConverter


private[javascript] class LeonWrapFactory extends WrapFactory {

  private val converter = new SJSONConverter

  override def wrap(cx: Context, scope: Scriptable, obj: AnyRef, staticType: Class[_]) = {
    if (obj != null && converter.accept(obj.getClass)) converter.javaToJs(obj)
    else super.wrap(cx, scope, obj, staticType)
  }

  override def wrapAsJavaObject(cx: Context, scope: Scriptable, javaObject: AnyRef, staticType: Class[_]) = {
    // println("!!!! wrapAsJavaObject: " + javaObject)
    super.wrapAsJavaObject(cx, scope, javaObject, staticType)
  }

  override def wrapNewObject(cx: Context, scope: Scriptable, obj: AnyRef) = {
    // println("!!!! wrapNewObject: " + obj)
    super.wrapNewObject(cx, scope, obj)
  }
}
/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.conversions

import org.mozilla.javascript.NativeObject

class Converter {

  type RawJavaMap = java.util.Map[_, _]

  // --- JavaScript NativeObjects to Java Map -------------

  def rhinoNativeObjectToJavaMap(nativeObject: NativeObject): RawJavaMap = {
    null
  }

  def rhinoNativeObjectToJavaMapKeepNestedJavaObjects(nativeObject: NativeObject): RawJavaMap = {
    null
  }

  // --- Java Objects to Java Map -------------------------

  def javaObjectToJavaMap(obj: AnyRef): RawJavaMap = {
    null
  }

  // --- Java Map to JavaScriptNativeObject ---------------
  
  def javaMapToRhinoNativeObject(javaMap: RawJavaMap): NativeObject = {
    null
  }

  def javaMapToRhinoNativeObjectKeepNestedJavaObjects(javaMap: RawJavaMap): NativeObject = {
    null
  }

  // --- Java Map to Java Object --------------------------

  def javaMapToJavaObject[A <: AnyRef](javaMap: RawJavaMap): A = {
    null.asInstanceOf[A]
  }

}

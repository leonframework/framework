/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import org.mozilla.javascript.{Scriptable, Context, WrapFactory}

class LeonWrapFactory extends WrapFactory {

  private val excludedPackages =
     "java." ::
     "com.google.inject." ::
     "org.mozilla.javascript." :: Nil

  private def isExcluded(clazz: Class[_]) =
    excludedPackages exists { clazz.getName startsWith _ }

  override def wrapAsJavaObject(cx: Context, scope: Scriptable, javaObject: AnyRef, staticType: Class[_]) =
    if(isExcluded(javaObject.getClass)) super.wrapAsJavaObject(cx, scope, javaObject, staticType)
    else new JavaScriptProxy(scope, javaObject,javaObject.getClass)
}
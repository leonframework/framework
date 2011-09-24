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

private[javascript] class LeonWrapFactory extends WrapFactory {

  override def wrapAsJavaObject(cx: Context, scope: Scriptable, javaObject: AnyRef, staticType: Class[_]) = {
    new JavaScriptProxy(scope, javaObject,javaObject.getClass)
  }
}
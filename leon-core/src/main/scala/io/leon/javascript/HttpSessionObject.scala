/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import org.mozilla.javascript.{Scriptable, ScriptableObject}
import com.google.inject.Injector
import javax.servlet.http.HttpServletRequest

class HttpSessionObject(injector: Injector) extends ScriptableObject {

  def getClassName = getClass.getName

  private def getCurrentRequest = injector.getInstance(classOf[HttpServletRequest])

  private def getCurrentSession = getCurrentRequest.getSession

  override def get(name: String, start: Scriptable): AnyRef = {
    getCurrentSession.getAttribute(name)
  }

  override def put(name: String, start: Scriptable, value: AnyRef) {
    getCurrentSession.setAttribute(name, value)
  }

}



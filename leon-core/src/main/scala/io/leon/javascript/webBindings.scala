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
import com.google.inject.{Injector, Inject}
import javax.servlet.http.HttpServletRequest

class HttpSessionObject(injector: Injector) extends ScriptableObject {

  def getClassName = getClass.getName

  private def request = injector.getInstance(classOf[HttpServletRequest])

  private def session = request.getSession

  override def get(name: String, start: Scriptable): AnyRef = {
    session.getAttribute(name)
  }

  override def put(name: String, start: Scriptable, value: AnyRef) {
    session.setAttribute(name, value)
  }

}

class JavaScriptWebBindings @Inject()(injector: Injector, leonScriptEngine: LeonScriptEngine) {

  private val httpSessionObject = new HttpSessionObject(injector)

  leonScriptEngine.put("session", httpSessionObject)

}
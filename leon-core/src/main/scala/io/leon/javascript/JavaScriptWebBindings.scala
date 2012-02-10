/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import com.google.inject.{Injector, Inject}


class JavaScriptWebBindings @Inject()(injector: Injector, leonScriptEngine: LeonScriptEngine) {

  private val httpSessionObject = new HttpSessionObject(injector)

  leonScriptEngine.put("session", httpSessionObject)

}

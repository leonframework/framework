/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import com.google.inject.Inject 

//class HttpSessionObject extends ScriptableObject {
//  def getClassName = getClass.getName
//}

class JavaScriptWebBindings @Inject()(leonScriptEngine: LeonScriptEngine) {

  //private val httpSessionObject = new HttpSessionObject

  leonScriptEngine.put("session", "foo")

}

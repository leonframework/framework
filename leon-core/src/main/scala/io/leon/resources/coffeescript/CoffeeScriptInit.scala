/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.coffeescript

import com.google.inject.Inject
import io.leon.javascript.LeonScriptEngine

class CoffeeScriptInit @Inject()(leonScriptEngine: LeonScriptEngine) {

  leonScriptEngine.loadResource("/io/leon/coffee-script.js", -1)

}
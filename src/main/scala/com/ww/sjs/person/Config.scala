/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.ww.sjs.person

import com.ww.sjs.SJSConfig

class Config extends SJSConfig {

  loadJsFile("public/person_server.js")

  expose("savePerson", "savePerson")

}

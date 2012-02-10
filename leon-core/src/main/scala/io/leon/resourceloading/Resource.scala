/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/*
 * Copyright (c) 2011 WeigleWilczek and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resourceloading

import java.io.InputStream

class Resource(val name: String, val lastModifiedFunc: () => Long, streamFunc: () => InputStream) {

  def this(name: String, streamFunc: () => InputStream) = this(name, () => 0, streamFunc)

  def lastModified() = lastModifiedFunc.apply()

  def createInputStream() = streamFunc.apply()

}

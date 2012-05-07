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

abstract class Resource(val name: String) {

  /**
   * Returns the timestamp of the last modification. Implementations must return -1
   * if this method is not applicable.
   *
   * @return timestamp of the last modification
   */
  def getLastModified(): Long

  def getInputStream(): InputStream

  def isCachable(): Boolean

  def wasLoadedFromCache(): Boolean = false

}

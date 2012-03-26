/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.less


class OriginalLessFilePathHolder {

  private val threadLocal = new ThreadLocal[String]

  def get = threadLocal.get

  def set(path: String) {
    threadLocal.set(path)
  }
}

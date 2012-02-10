/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resourceloading.processor

import io.leon.resourceloading.Resource

trait ResourceProcessor {

  def fromFileEnding: String

  def toFileEnding: String

  def process(in: Resource): Resource

  def isCachingRequested = false

}

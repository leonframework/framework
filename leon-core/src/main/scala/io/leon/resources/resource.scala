/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources

import java.io.{FileInputStream, File, InputStream}
import java.net.URL


abstract class Resource(val name: String) {

  def lastModified: Long

  def getInputStream: InputStream
}

class FileResource(name: String, file: File) extends Resource(name) {

  def lastModified = file.lastModified()

  def getInputStream = new FileInputStream(file)
}

class URLResource(name: String, url: URL) extends Resource(name) {

  def lastModified = 0

  def getInputStream = url.openStream()
}
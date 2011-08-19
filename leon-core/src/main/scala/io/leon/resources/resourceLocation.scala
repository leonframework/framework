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


trait ResourceLocation {

  def getResource(fileName: String): Option[Resource]
}

class ClassLoaderResourceLocation extends ResourceLocation {

  def getResource(fileName: String): Option[Resource] = {
    val try1 = Thread.currentThread().getContextClassLoader.getResource(fileName)
    if (try1 != null) {
      return Some(new URLResource(fileName, try1))
    }
    val try2 = getClass.getResource(fileName)
    if (try2 != null) {
      return Some(new URLResource(fileName, try2))
    }
    val try3 = getClass.getClassLoader.getResource(fileName)
    if (try3 != null) {
      return Some(new URLResource(fileName, try3))
    }
    None
  }
}

class FileSystemResourceLocation(val baseDir: File) extends ResourceLocation {

  if(! baseDir.exists()) require(baseDir.mkdirs(), baseDir.getAbsolutePath + " does not exist and could not be created!")
  else {
    require(baseDir.isDirectory, baseDir.getAbsolutePath + " is not a directory.")
    require(baseDir.canRead, baseDir.getAbsolutePath + " is not readable.")
  }

  def getResource(fileName: String) = {
    val file = new File(baseDir, fileName)

    if(file.exists() && file.canRead) Some(new FileResource(fileName, file))
    else None
  }
}

abstract class Resource(val name: String) {

  def lastModified: Long

  def getInputStream: InputStream
}

class URLResource(name: String, url: URL) extends Resource(name) {

  def lastModified = 0

  def getInputStream = url.openStream()
}

class FileResource(name: String, file: File) extends Resource(name) {

  def lastModified = file.lastModified()

  def getInputStream = new FileInputStream(file)
}

class StreamResource(name: String, in: InputStream) extends Resource(name) {

  def lastModified = 0

  def getInputStream = in
}

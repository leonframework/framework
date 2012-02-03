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

class DelegatingResourceLocation(loaderFn: (String) => URL) extends ResourceLocation {

  def getResource(fileName: String): Option[Resource] = {
    val r = loaderFn(fileName)
    if (r != null) {
      return Some(new Resource(fileName, () => r.openStream()))
    }
    None
  }
}

class FileSystemResourceLocation(val baseDir: File) extends ResourceLocation {

  if(!baseDir.exists()) {
    require(baseDir.mkdirs(), baseDir.getAbsolutePath + " does not exist and could not be created!")
  }
  else {
    require(baseDir.isDirectory, baseDir.getAbsolutePath + " is not a directory.")
    require(baseDir.canRead, baseDir.getAbsolutePath + " is not readable.")
  }

  def getResource(fileName: String) = {
    val file = new File(baseDir, fileName)
    if(file.exists() && file.isFile) {
      Some(new Resource(fileName, () => file.lastModified(), () => new FileInputStream(file)))
    }
    else None
  }
}

class Resource(val name: String, val lastModifiedFunc: () => Long, streamFunc: () => InputStream) {

  def this(name: String, streamFunc: () => InputStream) = this(name, () => 0, streamFunc)

  def lastModified() = lastModifiedFunc.apply()

  def createInputStream() = streamFunc.apply()

}

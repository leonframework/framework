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


trait ResourceLocation {

  def getResource(fileName: String): Option[Resource]
}

class ClassLoaderResourceLocation extends ResourceLocation {

  def getResource(fileName: String): Option[Resource] = {
    val try1 = Thread.currentThread().getContextClassLoader.getResource(fileName)
    if (try1 != null) {
      return Some(new Resource(fileName, () => try1.openStream()))
    }
    val try2 = getClass.getResource(fileName)
    if (try2 != null) {
      return Some(new Resource(fileName, () => try2.openStream()))
    }
    val try3 = getClass.getClassLoader.getResource(fileName)
    if (try3 != null) {
      return Some(new Resource(fileName, () => try3.openStream()))
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

class Resource(val name: String, lastModifiedFunc: () => Long, streamFunc: () => InputStream) {

  def this(name: String, streamFunc: () => InputStream) = this(name, () => 0, streamFunc)

  def lastModified() = lastModifiedFunc.apply()

  def createInputStream() = streamFunc.apply()

}

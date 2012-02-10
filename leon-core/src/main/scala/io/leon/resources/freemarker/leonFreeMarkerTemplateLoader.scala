/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.freemarker

import freemarker.cache.TemplateLoader
import io.leon.resourceloading.ResourceLoader
import com.google.inject.{Provider, Inject}
import java.io.{InputStreamReader, InputStream, Reader}
import java.lang.IllegalArgumentException

case class FoundResource(fileName: String, inputStream: InputStream) {

  override def equals(other: Any): Boolean = other match {
    case that @ FoundResource(f, i) => this.fileName == that.fileName
    case _ => false
  }

}

class LeonFreeMarkerTemplateLoader@Inject()(resourceLoaderProvider: Provider[ResourceLoader]) 
  extends TemplateLoader {

  private lazy val resourceLoader = resourceLoaderProvider.get()

  def findTemplateSource(name: String): AnyRef = {
    resourceLoader.getResourceOption(name) match {
      case Some(res) => FoundResource(name, res.createInputStream())
      case None => null
    }
  }

  def closeTemplateSource(templateSource: AnyRef) {
    templateSource match {
      case FoundResource(n, i) => i.close()
      case _ =>
    }
  }

  def getReader(templateSource: AnyRef, encoding: String): Reader = {
    templateSource match {
      case FoundResource(n, i) => new InputStreamReader(i, encoding)
      case _ => throw new IllegalArgumentException("Invalid: FreeMarker did no pass in template source object!")
    }
  }

  def getLastModified(templateSource: AnyRef): Long = {
    System.currentTimeMillis()
  }

}

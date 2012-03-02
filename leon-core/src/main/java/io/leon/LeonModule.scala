package io.leon

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import config.{ConfigMapHolder, ConfigMap}
import javascript.LeonScriptEngine
import collection.mutable
import com.google.inject._
import servlet.ServletModule
import web.resources.WebResourcesBinder


abstract class LeonModule extends ServletModule {

  //private val logger = Logger.getLogger(getClass.getName)

  val javaScriptFilesToLoad = mutable.ArrayBuffer[String]()

  val exposedUrls = new mutable.ArrayBuffer[String]

  def exposeUrl(regex: String) {
    // We first use a list instead of directly creating a binding so that
    // users can change the default configuration
    exposedUrls.append(regex)
  }

  def setApplicationName(appName: String) {
    val map = ConfigMapHolder.getInstance().getConfigMap
    map.put(ConfigMap.APPLICATION_NAME_KEY, appName)
  }

  override def configureServlets() {
    exposeUrl(".*/$")
    exposeUrl(".*html$")
    exposeUrl(".*png$")
    exposeUrl(".*jpg$")
    exposeUrl(".*jpeg$")
    exposeUrl(".*gif$")
    exposeUrl(".*css$")
    exposeUrl("favicon.ico$")
    exposeUrl(".*/browser/.*js$")
    exposeUrl(".*/browser/.*json$")

    config()

    val rb = new WebResourcesBinder(binder())
    exposedUrls foreach rb.exposeUrl

    requestInjection(new Object {
      @Inject def init(injector: Injector, engine: LeonScriptEngine) {
        // Loading JavaScript files
        engine.loadResources(javaScriptFilesToLoad.toList)
      }
    })
  }

  // --- JavaScript methods ---

  def loadFile(fileName: String) {
    javaScriptFilesToLoad.append(fileName)
  }

  // --- Dependency injection ---

  override def bind[T](clazz: Class[T]) = super.bind(clazz)

  override def bind[T](key: Key[T]) = super.bind(key)

  override def bind[T](typeLiteral: TypeLiteral[T]) = super.bind(typeLiteral)

  // --- Abstract methods ---

  def config()

}

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon


import config.{ConfigMap, ConfigMapBuilder}
import javascript.LeonScriptEngine
import collection.mutable
import com.google.inject._
import servlet.ServletModule
import java.io.File
import web.resources.WebResourcesBinder


abstract class AbstractLeonConfiguration extends ServletModule {

  //private val logger = Logger.getLogger(getClass.getName)

  val configMap: ConfigMap = new ConfigMapBuilder()
    .readProperties()
    .readEnvironment()
    .create()

  val javaScriptFilesToLoad = mutable.ArrayBuffer[String]()

  val exposedUrls = new mutable.ArrayBuffer[String]

  var baseDirOption: Option[File] = None

  def exposeUrl(regex: String) {
    // We first use a list instead of directly creating a binding so that
    // users can change the default configuration
    exposedUrls.append(regex)
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

    bind(classOf[ConfigMap]).toInstance(configMap)

    val rb = new WebResourcesBinder(binder())
    exposedUrls foreach rb.exposeUrl


    requestInjection(new Object {
      @Inject def init(injector: Injector, engine: LeonScriptEngine) {
        // Loading JavaScript files
        engine.loadResources(javaScriptFilesToLoad.toList)

      }
    })
  }

  // --- Resources ----------------------------------------

  //def setBaseDir(baseDir: String) {
  //  baseDirOption = Some(new File(baseDir).getAbsoluteFile)
  //}

  /*
  def addLocation(path: String) {
    val name = "%s[%s]".format(classOf[FileSystemResourceLocation].getName, path)

    val filePath = new File(path)
    val file =
      if(filePath.isAbsolute) filePath
      else baseDirOption map { baseDir => new File(baseDir, path) } getOrElse filePath

    val res = new FileSystemResourceLocation(file)

    bind(classOf[ResourceLocation]).annotatedWith(Names.named(name)).toInstance(res)
  }
  */

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

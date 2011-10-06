/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon

import javascript.{LeonScriptEngine, JavaObjectAjaxHandlerProvider, JavaScriptAjaxHandlerProvider}
import resources.{FileSystemResourceLocation, ResourceLocation}
import web.ajax.AjaxHandler
import web.comet._
import collection.mutable
import com.google.inject._
import name.Names
import servlet.ServletModule
import java.io.File
import web.resources.ExposedUrl


abstract class AbstractLeonConfiguration extends ServletModule {

  //private val logger = Logger.getLogger(getClass.getName)

  val javaScriptFilesToLoad = mutable.ArrayBuffer[String]()

  val exposedUrls = new mutable.ArrayBuffer[String]

  var baseDirOption: Option[File] = None

  def config()

  override def configureServlets() {
    exposeUrl(".*/$")
    exposeUrl(".*html$")
    exposeUrl(".*css$")
    exposeUrl(".*/browser/.*js$")
    exposeUrl(".*/browser/images/.*png$")
    exposeUrl(".*/browser/charting/.*js$")
    exposeUrl(".*/browser/charting/.*css$")

    config()

    exposedUrls foreach { url => ExposedUrl.bind(binder(), url) }

    requestInjection(new Object {
      @Inject def init(injector: Injector, engine: LeonScriptEngine) {
        // Loading JavaScript files
        engine.loadResources(javaScriptFilesToLoad.toList)
      }
    })
  }

  // --- Resources ----------------------------------------

  def setBaseDir(baseDir: String) {
    baseDirOption = Some(new File(baseDir).getAbsoluteFile)
  }

  def addLocation(path: String) {
    val name = "%s[%s]".format(classOf[FileSystemResourceLocation].getName, path)

    val filePath = new File(path)
    val file =
      if(filePath.isAbsolute) filePath
      else baseDirOption map { baseDir => new File(baseDir, path) } getOrElse filePath

    val res = new FileSystemResourceLocation(file)

    bind(classOf[ResourceLocation]).annotatedWith(Names.named(name)).toInstance(res)
  }

  // --- Expose URL methods -----------------------------

  def exposeUrl(regex: String) {
    // We first use a list instead of directly creating a binding so that
    // users can change the default configuration
    exposedUrls.append(regex)
  }

  // --- JavaScript methods -------------------------------

  def loadFile(fileName: String) {
    javaScriptFilesToLoad.append(fileName)
  }

  // --- Ajax/Comet DSL -----------------------------------

  def browser(browserName: String) = new {
    def linksToServer() {
      linksToServer(browserName)
    }    
    def linksToServer(serverName: String) {
      bind(classOf[AjaxHandler]).annotatedWith(Names.named(browserName)).toProvider(
        new JavaScriptAjaxHandlerProvider(serverName)).asEagerSingleton()
    }
    def linksToServer(clazz: Class[AnyRef]) {
      linksToServer(Key.get(clazz))
    }
    def linksToServer[T <: AnyRef](key: Key[T]) {
      bind(classOf[AjaxHandler]).annotatedWith(Names.named(browserName)).toProvider(
        new JavaObjectAjaxHandlerProvider(key)).asEagerSingleton()
    }
  }

  def server(serverName: String) = new {
    def linksToAllPages(browserName: String) {
      linksToPagesWithScope(browserName, BrowerObjectAllScope)
    }
    def linksToCurrentPage(browserName: String) {
      linksToPagesWithScope(browserName, BrowerObjectPageScope)
    }
    def linksToSessionPages(browserName: String) {
      linksToPagesWithScope(browserName, BrowerObjectSessionScope)
    }
    private def linksToPagesWithScope(browserName: String, browserScope: BrowserObjectScopes) {
      bind(classOf[BrowserObject]).annotatedWith(Names.named(serverName)).toProvider(
        new BrowserObjectProvider(browserName, browserScope)).asEagerSingleton()
    }
  }

  // -- Dependency injection ----------------------------------

  override def bind[T](clazz: Class[T]) = super.bind(clazz)

  override def bind[T](key: Key[T]) = super.bind(key)

  override def bind[T](typeLiteral: TypeLiteral[T]) = super.bind(typeLiteral)
}

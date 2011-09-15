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
import web.resources.InternalPathFilter
import java.io.File


abstract class AbstractLeonConfiguration extends ServletModule {

  //private val logger = Logger.getLogger(getClass.getName)

  val javaScriptFilesToLoad = mutable.ArrayBuffer[String]()

  val internalPaths = new mutable.ArrayBuffer[String]

  var addModulePackageToInternalPaths = true

  var baseDirOption: Option[File] = None

  def config()

  override def configureServlets() {
    config()

    addInternalPath(classOf[LeonModule])
    if (addModulePackageToInternalPaths) {
      addInternalPath(getClass)
    }

    val internalPathFilter = new InternalPathFilter(internalPaths.toList)
    requestInjection(internalPathFilter)
    val filterKey = Key.get(classOf[InternalPathFilter], Names.named(getClass.getName))
    bind(filterKey).toInstance(internalPathFilter)
    filter("/*").through(filterKey)

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

  // --- Internal URL methods -----------------------------

  def addInternalPath(path: String) {
    internalPaths.append(path)
  }

  def addInternalPath(clazz: Class[_]) {
    val pckg = clazz.getPackage
    if(pckg != null)
      addInternalPath("/" + pckg.getName.replace('.', '/'))
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

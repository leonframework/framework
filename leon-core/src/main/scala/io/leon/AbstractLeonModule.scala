/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon

import guice.annotations.InternalPathsList
import javascript.JavaScriptAjaxHandlerProvider
import web.ajax.{AjaxHandler, AjaxWebModule}
import java.io.InputStreamReader
import java.util.logging.Logger
import java.lang.RuntimeException
import javax.script.{ScriptEngineManager, ScriptEngine}
import web.comet.{BrowserObjectProvider, BrowserObject, CometWebModule}
import web.resources.ResourcesWebModule
import collection.{JavaConversions, mutable}
import com.google.inject._
import name.{Named, Names}


abstract class AbstractLeonModule extends AbstractModule {

  private val logger = Logger.getLogger(getClass.getName)

  val scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript")

  val modules = mutable.ArrayBuffer(
    new AjaxWebModule, new CometWebModule, new ResourcesWebModule)

  val leonJavaScriptFiles = "/io/leon/json2.js" :: "/io/leon/leon.js" :: Nil

  val userJavaScriptFiles = mutable.ArrayBuffer[String]()

  val internalPaths = new mutable.ArrayBuffer[String]

  var addModulePackageToInternalPaths = true

  def config()

  @Provides @InternalPathsList
  def provideInternalPathsList(): List[String] = internalPaths.toList

  def configure() {
    config()

    addInternalPath(classOf[AbstractLeonModule])
    if (addModulePackageToInternalPaths) {
      addInternalPath(getClass)
    }

    userJavaScriptFiles foreach loadJsFile
    modules foreach install

    bind(classOf[AbstractLeonModule]).toInstance(this)
    bind(classOf[ScriptEngine]).toInstance(scriptEngine)

    requestInjection(new Object {
      @Inject def init(injector: Injector, engine: ScriptEngine) {
        engine.put("injector", injector)

        // Loading Leon JavaScript files
        leonJavaScriptFiles foreach { f => evalJavaScriptFile(engine, f) }

        // Binding server->browser objects
        val browserObjects = injector.findBindingsByType(new TypeLiteral[BrowserObject]() {})
        JavaConversions.asScalaBuffer(browserObjects) foreach { b =>
          // TODO support '.' in names
          val serverName = b.getKey.getAnnotation.asInstanceOf[Named].value()
          engine.eval("var %s = leon.getBrowserObject(\"%s\");".format(serverName, serverName))
        }

        // Loading User JavaScript files
        userJavaScriptFiles foreach { f => evalJavaScriptFile(engine, f) }
      }
    })
  }

  private def evalJavaScriptFile(engine: ScriptEngine, fileName: String) {
    logger.info("Loading JS file " + fileName)
    try {
      val jsFile = getClass.getClassLoader.getResourceAsStream(fileName)
      engine.eval(new InputStreamReader(jsFile))
    } catch {
      case e: Throwable => throw new RuntimeException("Can not load JavaScript file [" + fileName + "]", e)
    }
  }

  // --- Internal URL methods -----------------------------

  def addInternalPath(path: String) {
    internalPaths.append(path)
  }

  def addInternalPath(clazz: Class[_]) {
    addInternalPath("/" + clazz.getPackage.getName.replace('.', '/'))
  }

  // --- JavaScript methods -------------------------------

  def loadJsFile(fileName: String) {
    userJavaScriptFiles.append(fileName)
  }

  // --- Ajax/Comet DSL -----------------------------------

  def browser(browserName: String) = new {
    def linksToServer(serverName: String) {
      bind(classOf[AjaxHandler]).annotatedWith(Names.named(browserName)).toProvider(
        new JavaScriptAjaxHandlerProvider(serverName)).asEagerSingleton()
    }
  }

  def server(serverName: String) = new {
    def linksToBrowser(browserName: String) {
      bind(classOf[BrowserObject]).annotatedWith(Names.named(serverName)).toProvider(
        new BrowserObjectProvider(browserName)).asEagerSingleton()
    }
  }

}

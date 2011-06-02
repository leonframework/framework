/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon

import javascript.JavaScriptAjaxHandlerProvider
import web.ajax.{AjaxHandler, AjaxWebModule}
import java.io.InputStreamReader
import java.util.logging.Logger
import java.lang.RuntimeException
import com.google.inject.name.Names
import com.google.inject.{Inject, Injector, AbstractModule}
import scala.collection.mutable
import javax.script.{ScriptEngineManager, ScriptEngine}
import web.comet.{UplinkFunctionProvider, UplinkFunction, CometWebModule}
import web.resources.{ResourcesServlet, ResourcesWebModule}

abstract class LeonConfig extends AbstractModule {

  private val logger = Logger.getLogger(getClass.getName)

  val scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript")

  val modules = mutable.ArrayBuffer(
    new AjaxWebModule, new CometWebModule, new ResourcesWebModule)

  val javaScriptFiles = mutable.ArrayBuffer("/io/leon/json2.js", "/io/leon/leon.js")

  val internalPaths = new mutable.ArrayBuffer[String]

  def config()

  def configure() {
    addInternalPath(classOf[LeonConfig])

    javaScriptFiles foreach loadJsFile
    modules foreach install

    bind(classOf[LeonConfig]).toInstance(this)
    bind(classOf[ScriptEngine]).toInstance(scriptEngine)

    config()

    requestInjection(new Object {
      @Inject def init(injector: Injector, engine: ScriptEngine, resourcesServlet: ResourcesServlet) {
        engine.put("injector", injector)

        javaScriptFiles foreach { f =>
          logger.info("Loading JS file " + f)
          try {
            val jsFile = getClass.getClassLoader.getResourceAsStream(f)
            engine.eval(new InputStreamReader(jsFile))
          } catch {
            case e: Throwable => throw new RuntimeException("Can not load JavaScript file [" + f + "]", e)
          }
        }

        resourcesServlet.internalPaths = internalPaths
      }
    })
  }

  def addInternalPath(path: String) {
    internalPaths.append(path)
  }

  def addInternalPath(clazz: Class[_]) {
    addInternalPath("/" + clazz.getPackage.getName.replace('.', '/'))
  }

  def loadJsFile(fileName: String) {
    javaScriptFiles.append(fileName)
  }

  def expose(javaScriptObjectName: String) = new {
    def via(publicName: String) {
      bind(classOf[AjaxHandler]).annotatedWith(Names.named(publicName)).toProvider(
        new JavaScriptAjaxHandlerProvider(javaScriptObjectName)).asEagerSingleton()
    }
  }

  def uplink(clientFunction: String) = new {
    def via(localName: String) {
      bind(classOf[UplinkFunction]).annotatedWith(Names.named(localName)).toProvider(
        new UplinkFunctionProvider(clientFunction)).asEagerSingleton()
    }
  }

  def createApplicationJavaScript(): String = {
    //(exposedFunctions.keys map createJavaScriptFunctionDeclaration) mkString "\n"
    ""
  }

  /*
  private def createJavaScriptFunctionDeclaration(fnName: String): String = {
    """
    var %s = function () {
      var argLength = arguments.length - 1;
      var args = Array(argLength);
      for (var i = 0; i < argLength; i++) {
        args[i] = arguments[i];
      }
      var callback = arguments[arguments.length - 1];

      invoke("%s", args, callback);
    }
    """.format(fnName, fnName)
  }
  */

}

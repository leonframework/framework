/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon

import javascript.{JavaScriptObjectProvider, JavaScriptObject}
import web.comet.CometWebModule
import java.io.InputStreamReader
import java.util.logging.Logger
import java.lang.RuntimeException
import web.MainServletWebModule
import com.google.inject.name.Names
import javax.script.{ScriptEngineManager, ScriptEngine}
import com.google.inject.{Module, Inject, Injector, AbstractModule}
import scala.collection.mutable

abstract class LeonConfig extends AbstractModule {

  private val logger = Logger.getLogger(getClass.getName)

  val scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript")

  val modules = mutable.ArrayBuffer(new CometWebModule, new MainServletWebModule)

  val javaScriptFiles = mutable.ArrayBuffer("/leon/server/json2.js", "/leon/server/leon.js")

  def config()

  def configure() {
    javaScriptFiles foreach loadJsFile
    modules foreach install

    bind(classOf[LeonConfig]).toInstance(this)
    bind(classOf[ScriptEngine]).toInstance(scriptEngine)

    config()

    requestInjection(new Object {
      @Inject def init(injector: Injector, engine: ScriptEngine) {
        engine.put("injector", injector)

        javaScriptFiles foreach { f =>
          logger.info("Loading JS file " + f)
          try {
            val jsFile = getClass.getClassLoader.getResourceAsStream(f)
            engine.eval(new InputStreamReader(jsFile))
          } catch {
            case e: Throwable => throw new RuntimeException("Can not load JavaScript file [" + f + "]")
          }
        }
      }
    })
  }

  def loadJsFile(fileName: String) {
    javaScriptFiles.append(fileName)
  }

  def expose(javaScriptObjectName: String) = new {
    def as(publicName: String) {
      bind(classOf[JavaScriptObject]).annotatedWith(Names.named(publicName)).toProvider(
        new JavaScriptObjectProvider(javaScriptObjectName)).asEagerSingleton()
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

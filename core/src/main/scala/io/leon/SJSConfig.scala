/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package io.leon

import comet.CometWebModule
import java.io.InputStreamReader
import javax.script.ScriptEngineManager
import scala.collection.mutable
import com.google.inject.AbstractModule
import java.util.logging.Logger


abstract class SJSConfig extends AbstractModule {

  private val logger = Logger.getLogger(getClass.getName)

  private val factory = new ScriptEngineManager

  private val engine = factory.getEngineByName("JavaScript")

  private val exposedFunctions = new mutable.HashMap[String, JavaScriptFunction]

  initScriptEngine()

  def config()

  def initScriptEngine() {
    loadJsFile("/leon/server/json2.js")
    loadJsFile("/leon/server/leon.js")
    engine.put("config", this)
  }

  def configure() {
    install(new CometWebModule)
    install(new MainServletWebModule)
    bind(classOf[SJSConfig]).toInstance(this)
    config()
  }

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
  
  def createApplicationJavaScript(): String = {
    (exposedFunctions.keys map createJavaScriptFunctionDeclaration) mkString "\n"
  }

  def loadJsFile(fileName: String) {
    logger.info("Loading JS file " + fileName)
    val jsFile = getClass.getClassLoader.getResourceAsStream(fileName)
    engine.eval(new InputStreamReader(jsFile))
  }

  def expose(internalName: String) = new {
    def as(publicName: String) {
      exposedFunctions(publicName) = new JavaScriptFunction(engine, internalName)
    }
  }

  def getJavaScriptFunction(name: String): JavaScriptFunction = {
    exposedFunctions(name)
  }

}

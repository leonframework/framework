/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import com.google.inject.{Injector, Inject}
import java.lang.IllegalArgumentException

import org.mozilla.javascript.{ScriptableObject, Context, Function => RhinoFunction}
import org.slf4j.LoggerFactory
import java.io.InputStreamReader
import io.leon.resourceloading.ResourceLoader

class LeonScriptEngine @Inject()(injector: Injector,
                                 resourceLoader: ResourceLoader) {

  import scala.collection.JavaConverters._

  private val logger = LoggerFactory.getLogger(getClass.getName)

  private val rhinoScope = withContext { _.initStandardObjects() }

  put("injector", injector)

  // Load Leon core modules
  loadResource("/io/leon/leon.js")
  loadResource("/leon/browser/leon-shared.js")

  private def withContext[A](block: Context => A): A = {
    val ctx = Context.enter()
    try {
      block(ctx)
    } finally {
      Context.exit()
    }
  }

  def loadResource(fileName: String) {
    withContext { ctx =>
      loadResource(fileName, ctx.getOptimizationLevel)
    }
  }

  def loadResource(fileName: String, optimizationLevel: Int) {
    logger.info("Loading resource: " + fileName + " with optimization level " + optimizationLevel)

    val resource = resourceLoader.getResource(fileName)

    withContext { ctx =>
      val ol = ctx.getOptimizationLevel
      ctx.setOptimizationLevel(optimizationLevel)
      try {
        val reader = new InputStreamReader(resource.get.getInputStream())
        ctx.evaluateReader(rhinoScope, reader, fileName, 1, null)
      } finally {
        ctx.setOptimizationLevel(ol)
      }
    }
  }

  def loadResources(fileNames: java.util.List[String]) {
    fileNames.asScala foreach loadResource
  }

  def getObject(name: String): ScriptableObject = {
    withContext { ctx =>
      var segments = name.split('.').toList
      var currentRoot: ScriptableObject = rhinoScope

      while(!segments.isEmpty) {
        currentRoot = currentRoot.get(segments.head, currentRoot).asInstanceOf[ScriptableObject]
        segments = segments.tail
      }
      currentRoot
    }
  }

  def invokeFunction(name: String, args: AnyRef*): AnyRef = {
    withContext { ctx =>
      val (objectName, _fnName) = name.splitAt(name.lastIndexOf('.'))
      val fnName = _fnName.substring(1)

      val functionObject = getObject(objectName)
      val function = functionObject.get(fnName, functionObject)

      if (!(function.isInstanceOf[RhinoFunction])) {
        throw new IllegalArgumentException("JavaScript code [%s] does not resolve to a function!".format(name))
      } else {
        val fn = function.asInstanceOf[org.mozilla.javascript.Function]
        val argsWrapped = args map { a => Context.javaToJS(a, rhinoScope) }
        //logger.info("Calling function [%s] with arguments [%s]".format(name, Arrays.toString(argsWrapped.toArray)))
        val result = fn.call(ctx, rhinoScope, rhinoScope, argsWrapped.toArray)
        //logger.info("Result of calling function [%s] is [%s]".format(name, result))
        Context.jsToJava(result, classOf[Any])
      }
    }
  }

  def eval(script: String): AnyRef = {
    withContext { ctx =>
      //logger.debug("Eval [{}]", script)
      ctx.evaluateString(rhinoScope, script, "LeonScriptEngine.eval(...)", 1, null)
    }
  }

  def put(key: String, value: Any) {
    withContext { ctx =>
      val wrapped = Context.javaToJS(value, rhinoScope)
      ScriptableObject.putProperty(rhinoScope, key, wrapped)
    }
  }

  def get(key: String): Any = {
    withContext { ctx =>
      val wrapped = ScriptableObject.getProperty(rhinoScope, key)
      Context.jsToJava(wrapped, classOf[Any])
    }
  }

}

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
import java.io.InputStreamReader
import java.lang.IllegalArgumentException

import org.mozilla.javascript.{ScriptableObject, Context, Function => RhinoFunction}
import io.leon.resources.{Resource, ResourceWatcher, ResourceLoader}
import org.slf4j.LoggerFactory

class LeonScriptEngine @Inject()(injector: Injector, resourceLoader: ResourceLoader, resourceWatcher: ResourceWatcher) {

  private val logger = LoggerFactory.getLogger(getClass.getName)

  val rhinoScope = withContext { _.initStandardObjects() }

  put("injector", injector)

  // Load Leon core modules
  loadResource("/io/leon/leon.js")
  loadResource("/leon/browser/leon-shared.js")

  private[javascript] def withContext[A](block: Context => A): A = {
    val ctx = Context.enter()
    val result = block(ctx)
    Context.exit()
    result
  }

  def loadResource(fileName: String) {
    logger.info("Loading resource: " + fileName)
    def _loadResource(resource: Resource) {
      withContext { ctx =>
        val reader = new InputStreamReader(resource.createInputStream())
        ctx.evaluateReader(rhinoScope, reader, fileName, 1, null)
      }
    }

    val resource = resourceLoader.getResource(fileName)
    // TODO: Only watch in 'development mode'
    resourceWatcher.watch(resource, _loadResource _)

    _loadResource(resource)
  }

  def loadResource(fileName: String, optimizationLevel: Int) {
    withContext { ctx =>
      val ol = ctx.getOptimizationLevel
      ctx.setOptimizationLevel(optimizationLevel)
      loadResource(fileName)
      ctx.setOptimizationLevel(ol)
    }
  }

  def loadResources(fileNames: List[String]) {
    fileNames foreach loadResource
  }

  def getObject(name: String): ScriptableObject = {
    withContext { ctx =>
      var segments = name.split('.').toList
      var currentRoot: ScriptableObject = rhinoScope

      while(!segments.isEmpty) {
        currentRoot = currentRoot.get(segments.head, currentRoot).asInstanceOf[ScriptableObject]
        segments = segments.tail
      }
      currentRoot.asInstanceOf[ScriptableObject]
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
      val wrapped = Context.javaToJS(value, rhinoScope);
      ScriptableObject.putProperty(rhinoScope, key, wrapped);
    }
  }

  def get(key: String): Any = {
    withContext { ctx =>
      val wrapped = ScriptableObject.getProperty(rhinoScope, key)
      Context.jsToJava(wrapped, classOf[Any])
    }
  }

}

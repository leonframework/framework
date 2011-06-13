/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import java.io.InputStreamReader
import io.leon.resources.ResourceLoader
import com.google.inject.{Injector, Inject}
import org.mozilla.javascript.{NativeObject, ScriptableObject, Context, Function => RhinoFunction}
import java.lang.IllegalArgumentException

class LeonScriptEngine @Inject()(injector: Injector, resourceLoader: ResourceLoader) {

  //private val logger = Logger.getLogger(getClass.getName)

  private val rhinoScope = withContext { _.initStandardObjects() }

  put("injector", injector)

  // Load Leon core modules
  loadResource("/io/leon/json2.js")
  loadResource("/io/leon/leon.js")

  // Load Coffee script
  withContext { ctx =>
    val ol = ctx.getOptimizationLevel
    ctx.setOptimizationLevel(-1)
    loadResource("/io/leon/coffee-script.js")
    ctx.setOptimizationLevel(ol)
  }

  // We fake a browser with this code, so eval it at the end, it confuses the other server-side libs
  loadResource("/leon/leon_shared.js")

  private def withContext[A](block: Context => A): A = {
    val ctx = Context.enter()
    val result = block(ctx)
    Context.exit()
    result
  }

  def loadResource(fileName: String) {
    withContext { ctx =>
      val resource = resourceLoader.getInputStream(fileName)
      val reader = new InputStreamReader(resource)
      ctx.evaluateReader(rhinoScope, reader, fileName, 1, null)
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
        currentRoot = rhinoScope.get(segments.head, currentRoot).asInstanceOf[ScriptableObject]
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
        val result = fn.call(ctx, rhinoScope, rhinoScope, args.toArray)
        Context.jsToJava(result, classOf[Any])
      }
    }
  }

  def eval(script: String): AnyRef = {
    withContext { ctx =>
      ctx.evaluateString(rhinoScope, script, "<no source>", 0, null)
    }
  }

  def evalToJson(script: String): String = {
    withContext { ctx =>
      val result = eval(script)
      val json = invokeFunction("JSON.stringify", result)
      json.asInstanceOf[String]
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

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

  private def rhinoContext = Context.enter()
  private val rhinoScope = rhinoContext.initStandardObjects()

  put("injector", injector)

  loadResource("/io/leon/json2.js")
  loadResource("/io/leon/leon.js")
  loadResource("/leon/leon_shared.js")

 def loadResource(fileName: String) {
   val resource = resourceLoader.getInputStream(fileName)
   val reader = new InputStreamReader(resource)
   rhinoContext.evaluateReader(rhinoScope, reader, fileName, 1, null)
  }

  def loadResources(fileNames: List[String]) {
    fileNames foreach loadResource
  }

  def getObject(name: String): ScriptableObject = {
    var segments = name.split('.').toList
    var currentRoot: ScriptableObject = rhinoScope

    while(!segments.isEmpty) {
      currentRoot = rhinoScope.get(segments.head, currentRoot).asInstanceOf[ScriptableObject]
      segments = segments.tail
    }
    currentRoot.asInstanceOf[ScriptableObject]
  }

  def invokeFunction(name: String, args: AnyRef*): AnyRef = {
    val (objectName, _fnName) = name.splitAt(name.lastIndexOf('.'))
    val fnName = _fnName.substring(1)

    val functionObject = getObject(objectName)
    val function = functionObject.get(fnName, functionObject)
    
    if (!(function.isInstanceOf[RhinoFunction])) {
      throw new IllegalArgumentException("JavaScript code [%s] does not resolve to a function!".format(name))
    } else {
      val fn = function.asInstanceOf[org.mozilla.javascript.Function]
      val result = fn.call(rhinoContext, rhinoScope, rhinoScope, args.toArray)
      Context.jsToJava(result, classOf[Any])
    }
  }

  def eval(script: String): AnyRef = {
    rhinoContext.evaluateString(rhinoScope, script, "<no source>", 0, null)
  }

  def evalToJson(script: String): String = {
    val result = eval(script)
    val json = invokeFunction("JSON.stringify", result)
    json.asInstanceOf[String]
  }

  def put(key: String, value: Any) {
    val wrapped = Context.javaToJS(value, rhinoScope);
    ScriptableObject.putProperty(rhinoScope, key, wrapped);
  }

  def get(key: String): Any = {
    val wrapped = ScriptableObject.getProperty(rhinoScope, key)
    Context.jsToJava(wrapped, classOf[Any])
  }

}

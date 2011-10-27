/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import org.mozilla.javascript._
import java.lang.reflect.Method


private[javascript] class JavaScriptProxy[T <: AnyRef](scope: Scriptable, obj: AnyRef, targetClass: Class[_]) extends NativeJavaObject(scope, obj, targetClass) {

  override def getClassName = "JavaScriptProxy"

  private val toJsonFunction =
    new BaseFunction {
      override def call(cx: Context, scope: Scriptable, thisObj: Scriptable, args: Array[AnyRef]) = {
        Converter.javaToJs(obj, scope)
      }
    }

  override def get(name: String, start: Scriptable) = {
    if (name == "toJSON") toJsonFunction
    else
      super.get(name, start) match {
        case m: NativeJavaMethod => new DispatchFunction(name, m, obj)
        case x => x
      }
  }
}

private class DispatchFunction(name: String, javaMethod: NativeJavaMethod, targetObject: AnyRef) extends BaseFunction {

  private val scope = javaMethod.getParentScope
  private val targetClass = targetObject.getClass

  setParentScope(scope)
  setPrototype(ScriptableObject.getFunctionPrototype(scope))

  override def call(cx: Context, scope: Scriptable, thisObj: Scriptable, args: Array[AnyRef]) = {
    val argTypes = Option(args).getOrElse(Array.empty) collect {
      case null => null
      case x => x.getClass
    }

    def hasScriptableArg =
      argTypes exists { classOf[Scriptable].isAssignableFrom }

    def invokeMethod(method: Method, args: Array[AnyRef]) = {
      val result = method.invoke(targetObject, args: _*)
      cx.getWrapFactory.wrap(cx, scope, result, method.getReturnType)
    }

    val method = findObjectMethod(name, argTypes)

    if(method.isDefined && hasScriptableArg) {
      method map { m =>
        val paramTypes = m.getParameterTypes.asInstanceOf[Array[AnyRef]]
        val convertedArgs: Array[AnyRef] =
          if (args == null) Array.empty[AnyRef]
          else (args zip paramTypes) collect { case (obj, argType: Class[AnyRef]) =>
            Converter.jsToJava(obj, argType, Some(m))
          }
        invokeMethod(m, convertedArgs)

      } getOrElse sys.error("Method not found: " + name + "(" + argTypes.mkString(", ") + ")")

    } else javaMethod.call(cx, scope, thisObj, args)
  }

  private def findObjectMethod(name: String, args: Array[_]) = {
    val methods = targetClass.getMethods.filter { m =>
      m.getName == name && m.getParameterTypes.size == args.size
    }

    // currently we don't support object conversion for overloaded methods.
    if(methods.size == 1) methods.headOption
    else None
  }

  override def getArity = javaMethod.getArity

  override def getLength = javaMethod.getLength

  override def getFunctionName = javaMethod.getFunctionName

  override def getDefaultValue(typeHint: Class[_]) = javaMethod.getDefaultValue(typeHint)
}

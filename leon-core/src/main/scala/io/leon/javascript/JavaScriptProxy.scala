/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import io.leon.conversions.SJSONConverter
import org.mozilla.javascript._
import java.lang.reflect.{Method, ParameterizedType}


object JavaScriptProxy {
  def apply[T <: AnyRef](scope: Scriptable, target: T) =
    new JavaScriptProxy(scope, target, target.getClass)
}

class JavaScriptProxy[T <: AnyRef](scope: Scriptable, obj: AnyRef, targetClass: Class[_]) extends NativeJavaObject(scope, obj, targetClass) {

  override def getClassName = "JavaScriptProxy"

  private val toJsonFunction =
    new BaseFunction with RhinoTypeConversions {
      override def call(cx: Context, scope: Scriptable, thisObj: Scriptable, args: Array[AnyRef]) = {
        converter.javaToJs(obj)
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

class DispatchFunction(name: String, javaMethod: NativeJavaMethod, target: AnyRef) extends BaseFunction with RhinoTypeConversions {

  private val scope = javaMethod.getParentScope

  private val targetClass = target.getClass

  override def getArity = javaMethod.getArity

  override def getLength = javaMethod.getLength

  override def getFunctionName = javaMethod.getFunctionName

  override def getDefaultValue(typeHint: Class[_]) = javaMethod.getDefaultValue(typeHint)

  setParentScope(scope)
  setPrototype(ScriptableObject.getFunctionPrototype(scope))

  override def call(cx: Context, scope: Scriptable, thisObj: Scriptable, args: Array[AnyRef]) = {
    val argTypes = Option(args).getOrElse(Array.empty) map { _.getClass }

    // println("JavaScriptProxy.invoke: " + name + "(" + argTypes.mkString(", ") + ")")

    def createNativeJavaObject(obj: AnyRef) =
      if(obj == null) Undefined.instance
      else new JavaScriptProxy(scope, obj, obj.getClass)

    if (argTypes exists { classOf[Scriptable].isAssignableFrom }) {
      findObjectMethod(name, argTypes) map { m =>
        if (args == null) m.invoke(target)
        else {
          val params = (args zip m.getParameterTypes) collect { jsToJava(m) }
          m.invoke(target, params: _*)
        }
      } map { createNativeJavaObject } getOrElse sys.error("Method not found: " + name + "(" + argTypes.mkString(", ") + ")")

    } else {
      javaMethod.call(cx, scope, thisObj, args)
    }
  }

  private def findObjectMethod(name: String, args: Array[_]) = {
    targetClass.getMethods.find { m =>
      m.getName == name && m.getParameterTypes.size == args.size
    }
  }
}

trait RhinoTypeConversions {

  val converter = new SJSONConverter

  protected def jsToJava(m: Method): PartialFunction[(AnyRef, Class[_]), AnyRef] = {
    case (arg: NativeArray, argType) => convertNativeArray(m, arg, getTypeParameter(m))
    case (arg: ScriptableObject, argType: Class[AnyRef]) => converter.jsToJava(arg, argType)
    case (arg: AnyRef, argType: Class[AnyRef]) => Context.jsToJava(arg, argType)
  }

  protected def convertNativeArray[T <: AnyRef](m: Method, arr: NativeArray, targetType: Class[T]): Seq[T] = {
    val seq =
      for(i <- (0 until arr.getLength().toInt)) yield {
        jsToJava(m).apply((arr.get(i, arr), targetType))
      }
    seq.asInstanceOf[Seq[T]]
  }

  protected def getTypeParameter(m: Method): Class[AnyRef] = {
    // TODO: does not work for primitive types like int. In that case actualType is 'java.lang.Object'.

    m.getGenericParameterTypes.toList.asInstanceOf[List[ParameterizedType]] match {
      case param :: Nil =>
        val actualType = param.getActualTypeArguments.apply(0).asInstanceOf[Class[AnyRef]]
        println("actualType: " + actualType.getName)
        actualType
      case _ => sys.error("cannot get type parameter for method: " + m.getName)
    }
  }
}



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
import java.lang.reflect.{Method, ParameterizedType}
import org.mozilla.javascript._

object JavaScriptProxy {
  def apply[T <: AnyRef](target: T) =
    new JavaScriptProxy(target)
}

class JavaScriptProxy[T <: AnyRef](target: T) extends ScriptableObject {

  override def getClassName = target.getClass.getName

  override def get(name: String, start: Scriptable) =
    new DelegatingFunction(name, target)
}

class DelegatingFunction(name: String, target: AnyRef) extends BaseFunction with RhinoTypeConversions {

  private val targetClass = target.getClass

  override def call(cx: Context, scope: Scriptable, thisObj: Scriptable, args: Array[AnyRef]) = {
    val argTypes = Option(args).getOrElse(Array.empty) map { _.getClass }

    // println("JavaScriptProxy.invoke: " + name + "(" + argTypes.mkString(", ") + ")")

    def createNativeJavaObject(obj: AnyRef) =
      new JavaScriptProxyObject(scope, obj, obj.getClass)

    findObjectMethod(name, argTypes) map { m =>
      if (args == null) m.invoke(target)
      else {
        val params = (args zip m.getParameterTypes) collect { jsToJava(m) }
        m.invoke(target, params: _*)
      }
    } map { createNativeJavaObject } getOrElse sys.error("Method not found: " + name + "(" + argTypes.mkString(", ") + ")")
  }

  private def findObjectMethod(name: String, args: Array[_]) = {
    targetClass.getMethods.find { m =>
      m.getName == name && m.getParameterTypes.size == args.size
    }
  }
}

class JavaScriptProxyObject(scope: Scriptable, obj: AnyRef, targetClass: Class[_]) extends NativeJavaObject(scope, obj, targetClass) {

  private val toJsonFunction =
    new BaseFunction with RhinoTypeConversions {
      override def call(cx: Context, scope: Scriptable, thisObj: Scriptable, args: Array[AnyRef]) = {
        converter.javaToJs(obj)
      }
    }

  override def get(name: String, start: Scriptable) = {
    if (name == "toJSON") toJsonFunction
    else super.get(name, start)
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



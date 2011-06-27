/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import javassist._
import java.lang.reflect.{Modifier, InvocationHandler, Method, Proxy}
import org.mozilla.javascript.ScriptableObject
import io.leon.conversions.SJSONConverter
import java.lang.{Short, Byte}

object JavaScriptProxy {

  def apply[T <: AnyRef](obj: T): AnyRef = {
    val jsInterface = createJsInterface(obj.getClass)
    Proxy.newProxyInstance(obj.getClass.getClassLoader, Array(jsInterface), new JavaScriptProxy(obj))
  }

  private def createJsInterface(clazz: Class[_]): Class[_] = {

    // println("=== createJsInterface (" + clazz.getName + ") ===")

    val classPool = ClassPool.getDefault()
    classPool.insertClassPath(new LoaderClassPath(this.getClass.getClassLoader))

    val jsInterface = classPool.makeInterface(clazz.getName + "GeneratedJavaScriptInterface")

    val numberType = classPool.getCtClass("java.lang.Number")
    val scriptableObjectType = classPool.getCtClass("org.mozilla.javascript.ScriptableObject")

    // TODO: use getMethod instead and ignore java.lang.Object methods
    val publicMethods =
      clazz.getDeclaredMethods filter { m => Modifier.isPublic(m.getModifiers) }

    // TODO: add more cases (e.g. String)
    publicMethods foreach { m =>
      val returnType = classPool.getCtClass(m.getReturnType.getName)
      val params = m.getParameterTypes collect {
        case c if isNumberType(c) => numberType
        case _ =>  scriptableObjectType
      }

      // println(" + method " + m.getName + "(" + params.map(_.getName).mkString(",") + "): " + returnType.getName)

      val gm = CtNewMethod.abstractMethod(returnType, m.getName, params, Array.empty, jsInterface)

      jsInterface.addMethod(gm)
    }

    // println("=== createJsInterface =============")

    jsInterface.toClass
  }

  private def isNumberType(clazz: Class[_]) = clazz.getName match {
    case "byte" => true
    case "short" => true
    case "int" => true
    case "long" => true
    case "double" => true
    case "float" => true
    case _ => false
  }

}

class JavaScriptProxy(obj: AnyRef) extends InvocationHandler {

  private val converter = new SJSONConverter

  def invoke(proxy: AnyRef, m: Method, args: Array[AnyRef]) = {

    val argTypes = Option(args).getOrElse(Array.empty) map { _.getClass }

    // println("JavaScriptProxy.invoke: " + m.getName + "(" + argTypes.mkString(", ") + ")")

    findObjectMethod(m.getName, argTypes) map { m =>
      if (args == null) m.invoke(obj)
      else {
        val params = (args zip m.getParameterTypes) collect {
          case (arg: ScriptableObject, argType: Class[AnyRef]) =>
            converter.jsToJava(arg, argType)
          case (x: Number, argType) =>  convertNumber(x, argType)
          case (x: AnyRef, _) => x
        }
        m.invoke(obj, params: _*)

      }
    } getOrElse sys.error("Method not found: " + m.getName + "(" + argTypes.mkString(", ") + ")")
  }

  private def findObjectMethod(name: String, args: Array[Class[_]]) = {
    obj.getClass.getMethods.find { m =>
      m.getName == name && m.getParameterTypes.size == args.size
    }
  }

  private def convertNumber(num: Number, targetType: Class[_]): AnyRef = {
    targetType.getName match {
      case "byte" => new java.lang.Byte(num.byteValue())
      case "short" => new java.lang.Short(num.shortValue())
      case "int" => new java.lang.Integer(num.intValue())
      case "long" => new java.lang.Long(num.longValue())
      case "double" => new java.lang.Double(num.doubleValue())
      case "float" => new java.lang.Float(num.floatValue())
      case x => sys.error("convertNumber missed case for " + x)
    }
  }
}


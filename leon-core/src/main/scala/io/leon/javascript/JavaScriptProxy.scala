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
import org.mozilla.javascript.{ScriptableObject, NativeArray}
import io.leon.conversions.SJSONConverter
import java.lang.reflect.{Method, ParameterizedType, InvocationHandler, Proxy}

object JavaScriptProxy {

  def apply[T <: AnyRef](obj: T): AnyRef = {
    val jsInterface = createJsInterface(obj.getClass)
    Proxy.newProxyInstance(obj.getClass.getClassLoader, Array(jsInterface), new JavaScriptProxy(obj))
  }

  private def createJsInterface(clazz: Class[_]): Class[_] = {

    println("=== createJsInterface (" + clazz.getName + ") ===")

    val classPool = ClassPool.getDefault()
    classPool.insertClassPath(new LoaderClassPath(this.getClass.getClassLoader))

    val jsInterface = classPool.makeInterface(clazz.getName + "GeneratedJavaScriptInterface")

    val numberType = classPool.getCtClass("java.lang.Number")
    val stringType = classPool.getCtClass("java.lang.String")
    val scriptableObjectType = classPool.getCtClass("org.mozilla.javascript.ScriptableObject")
    val nativeArrayType = classPool.getCtClass("org.mozilla.javascript.NativeArray")

    // TODO: use getMethod instead and ignore java.lang.Object methods
    val publicMethods =
      clazz.getDeclaredMethods filter { m => Modifier.isPublic(m.getModifiers) }

    // TODO: add more cases (e.g. String)
    publicMethods foreach { m =>
      val returnType = classPool.getCtClass(m.getReturnType.getName)
      val params = m.getParameterTypes collect {
        case c if isNumberType(c) => numberType
        case x if x.getName == "java.lang.String" => stringType
        case c if classOf[Seq[_]].isAssignableFrom(c) =>
          println("List: " + c.getName)
          nativeArrayType
        case x => scriptableObjectType
      }

      println(" + method " + m.getName + "(" + params.map(_.getName).mkString(",") + "): " + returnType.getName)

      val gm = CtNewMethod.abstractMethod(returnType, m.getName, params, Array.empty, jsInterface)

      jsInterface.addMethod(gm)
    }

    println("=== createJsInterface =============")

    jsInterface.toClass
  }

  private def isNumberType(clazz: Class[_]) = clazz.getName match {
    case "byte" => true
    case "short" => true
    case "int" => true
    case "long" => true
    case "double" => true
    case "float" => true
    case "java.lang.Short" => true
    case "java.lang.Float" => true
    case x => false
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
        val params = (args zip m.getParameterTypes) collect { jsToJava(m) }
        m.invoke(obj, params: _*)
      }
    } getOrElse sys.error("Method not found: " + m.getName + "(" + argTypes.mkString(", ") + ")")
  }

  private def findObjectMethod(name: String, args: Array[Class[_]]) = {
    obj.getClass.getMethods.find { m =>
      m.getName == name && m.getParameterTypes.size == args.size
    }
  }

  private def jsToJava(m: Method): PartialFunction[(AnyRef, Class[_]), AnyRef] = {
    case (arg: NativeArray, argType) => convertNativeArray(m, arg, getTypeParameter(m))
    case (arg: ScriptableObject, argType: Class[AnyRef]) => converter.jsToJava(arg, argType)
    case (x: Number, argType) =>  convertNumber(x, argType)
    case (x: AnyRef, _) => x
  }

  private def convertNumber(num: Number, targetType: Class[_]): AnyRef = {
    targetType.getName match {
      case "byte" => new java.lang.Byte(num.byteValue())
      case "short" => new java.lang.Short(num.shortValue())
      case "int" => new java.lang.Integer(num.intValue())
      case "long" => new java.lang.Long(num.longValue())
      case "double" => new java.lang.Double(num.doubleValue())
      case "float" => new java.lang.Float(num.floatValue())
      case "java.lang.Short" => new java.lang.Short(num.shortValue())
      case "java.lang.Float" => new java.lang.Float(num.floatValue())
      case x => sys.error("convertNumber missed case for " + x)
    }
  }

  private def convertNativeArray[T <: AnyRef](m: Method, arr: NativeArray, targetType: Class[T]): Seq[T] = {
    val seq =
      for(i <- (0 until arr.getLength().toInt)) yield {
        jsToJava(m)((arr.get(i, arr), targetType))
      }
    seq.asInstanceOf[Seq[T]]
  }

  private def getTypeParameter(m: Method): Class[AnyRef] = {
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


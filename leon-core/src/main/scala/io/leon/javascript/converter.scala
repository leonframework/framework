/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import sjson.json.Serializer.SJSON
import dispatch.json._
import java.lang.Boolean
import org.apache.commons.beanutils.{BeanUtilsBean, ConvertUtilsBean, BeanMap, Converter => BeanUtilsConverter}
import java.lang.reflect.{ParameterizedType, Method}
import org.mozilla.javascript._


// ---------- Converter API definition -------------------------

private[javascript] trait Converter {

  def javaToJs(obj: AnyRef, scope: Scriptable): AnyRef

  def jsToJava[A <: AnyRef](js: AnyRef, targetType: Class[A], method: Method): AnyRef
}


// ----------- Scala Case-Class support using SJSON -------------------------

private class SJSONConverter extends Converter with RawMapConversion {

  def javaToJs(obj: AnyRef, scope: Scriptable) = {
    val data = SJSON.out(obj)
    val jsValue = JsValue.fromString(new String(data))

    def jsValueToAnyRef(js: JsValue): AnyRef = js match {
      case JsObject(m) => m map { case (k, v) => k.self -> jsValueToAnyRef(v) }
      case JsNumber(n) => n
      case JsString(s) => s
      case JsTrue => Boolean.TRUE
      case JsFalse => Boolean.FALSE
      case JsArray(arr) => arr
      case JsNull => Undefined.instance
    }

    val rawMap = jsValueToAnyRef(jsValue).asInstanceOf[RawMap]
    mapToScriptableObject(rawMap, obj, scope)
  }

  def jsToJava[A <: AnyRef](js: AnyRef, targetType: Class[A], method: Method): A = {
    require(js.isInstanceOf[ScriptableObject], "js is not an instance of ScriptableObject but " + js.getClass.getName)

    val rawMap = scriptableObjectToMap(js.asInstanceOf[ScriptableObject])
    SJSON.fromJSON(JsValue(rawMap), Some(targetType))
  }
}


// ----------- POJO support using common-beanutils ----------------------------

private class PojoConverter extends ConvertUtilsBean with Converter with RawMapConversion {
  import scala.collection.JavaConverters._

  private val utils = new BeanUtilsBean(this)

  private val converter = new BeanUtilsConverter {
    def convert(targetType: Class[_], obj: AnyRef) =  {
      mapToObject(obj.asInstanceOf[RawMap],
        targetType.asInstanceOf[Class[AnyRef]])
    }
  }

  // TODO: support cycle references

  def javaToJs(obj: AnyRef, scope: Scriptable) =  {

    def canConvert(obj: AnyRef) =
      ! (obj.getClass.getName startsWith "java.")

    val beanMap =
      new BeanMap(obj).asScala.toMap collect {
        case (k, v: AnyRef) if canConvert(v) => k.toString -> javaToJs(v, scope)
        case (k, v) => k.toString -> v
      }

    // BeanMap adds an entry ('class', java.lang.Class) to the map, which we don't need!
    val rawMap = beanMap - "class"

    mapToScriptableObject(rawMap, obj, scope)
  }

  def jsToJava[A <: AnyRef](js: AnyRef, targetType: Class[A], method: Method) = {
    require(js.isInstanceOf[ScriptableObject], "js is not an instance of ScriptableObject")

    mapToObject(scriptableObjectToMap(js.asInstanceOf[ScriptableObject]), targetType)
  }

  private def mapToObject[A <: AnyRef](rawMap: RawMap, targetType: Class[A]) = {
    val obj = targetType.newInstance()
    utils.populate(obj, rawMap.asJava)
    obj
  }

  override def lookup(clazz: Class[_]) = {
    val conv = super.lookup(clazz)
    if(conv == null) converter
    else conv
  }
}


// ----------- Java Collection support ------------

private class JCLConverter extends Converter with NativeArrayConversion {
  import java.util.{ Collection => JCollection, List => JList, Set => JSet }
  import scala.collection.JavaConverters._

  // TODO: Support maps and concrete collection types (e.g. ArrayList)

  def javaToJs(obj: AnyRef, scope: Scriptable) = {
    require(obj.isInstanceOf[JCollection[_]], "obj is not an instance of java.util.Collection but " + obj.getClass.getName)

    val seq = obj.asInstanceOf[JCollection[AnyRef]].asScala

    val jsSeq: Iterable[AnyRef] = seq map { e => Converter.javaToJs(e, scope) }

    new NativeArray(jsSeq.toArray)
  }

  def jsToJava[A <: AnyRef](js: AnyRef, targetType: Class[A], method: Method) = {
    require(js.isInstanceOf[NativeArray], "js is not an instance of NativeArray but" + js.getClass.getName)

    val arr = toArray(js.asInstanceOf[NativeArray], targetType, method)

    if(targetType.isAssignableFrom(classOf[JList[_]])) arr.toList.asJava.asInstanceOf[A]
    else if (targetType.isAssignableFrom(classOf[JSet[_]])) arr.toSet.asJava.asInstanceOf[A]
    else sys.error("unsupported java collection type: " + targetType.getName)
  }
}


// ----------- Scala Collection support ---------------

private class ScalaCollectionConverter extends Converter with NativeArrayConversion {

  // TODO: Support maps, mutable collections and concrete collection types

  def javaToJs(obj: AnyRef, scope: Scriptable) = {
    require(obj.isInstanceOf[Iterable[_]], "obj is not an instance of scala.Iterable but " + obj.getClass.getName)

    val seq = obj.asInstanceOf[Iterable[AnyRef]]

    val jsSeq: Iterable[AnyRef] = seq map { e => Converter.javaToJs(e, scope) }
    new NativeArray(jsSeq.toArray)
  }

  def jsToJava[A <: AnyRef](js: AnyRef, targetType: Class[A], method: Method) = {
    require(js.isInstanceOf[NativeArray], "js is not an instance of NativeArray but" + js.getClass.getName)

    val arr = toArray(js.asInstanceOf[NativeArray], targetType, method)

    if(targetType.isAssignableFrom(classOf[Seq[_]])) arr.toSeq.asInstanceOf[A]
    else if (targetType.isAssignableFrom(classOf[Set[_]])) arr.toSet.asInstanceOf[A]
    else sys.error("unsupported scala collection type: " + targetType.getName)
  }
}


// ----------- Rhino's default java interop (LiveConnect 3) -----------------

private class RhinoConverter extends Converter {

  def javaToJs(obj: AnyRef, scope: Scriptable) = Context.javaToJS(obj, scope)

  def jsToJava[A <: AnyRef](js: AnyRef, targetType: Class[A], method: Method) = Context.jsToJava(js, targetType)
}


// ------------  Converter object ------------------------

private[javascript] object Converter extends Converter {

  private val rhinoConverter = new RhinoConverter
  private val sjsonConverter = new SJSONConverter
  private val pojoConverter = new PojoConverter
  private val scalaCollectionConverter = new ScalaCollectionConverter
  private val jclConverter = new JCLConverter

  def javaToJs(obj: AnyRef, scope: Scriptable): AnyRef = {
    if (obj == null || classOf[Scriptable].isAssignableFrom(obj.getClass)) obj
    else {
      val conv = getConverterForType(obj, classOf[Scriptable])
      conv.javaToJs(obj, scope)
    }
  }

  def jsToJava[A <: AnyRef](js: AnyRef, targetType: Class[A], method: Method) = {
    if(js == null || targetType.isAssignableFrom(js.getClass)) js
    else {
      val conv = getConverterForType(js, targetType)
      conv.jsToJava(js, targetType, method)
    }
  }

  def getConverterForType(obj: AnyRef, targetType: Class[_]): Converter = {
    import java.util.{ Collection => JCollection }

    val conv =
      if(classOf[NativeArray].isAssignableFrom(obj.getClass)) {
        // from js array to java/scala collection
        if(classOf[JCollection[_]].isAssignableFrom(targetType)) jclConverter
        else scalaCollectionConverter
      }
      else if(classOf[ScriptableObject].isAssignableFrom(obj.getClass)) {
        // from js object to pojo
        if(classOf[Product].isAssignableFrom(targetType)) sjsonConverter
        else pojoConverter
      }
      else if(classOf[Scriptable].isAssignableFrom(targetType)) {
        // from pojo to js object

        if(classOf[Iterable[_]].isAssignableFrom(obj.getClass)) scalaCollectionConverter
        else if(classOf[JCollection[_]].isAssignableFrom(obj.getClass)) jclConverter
        else if(classOf[Product].isAssignableFrom(obj.getClass)) sjsonConverter
        else if(obj.getClass.getName.startsWith("java.lang.")) rhinoConverter
        else pojoConverter
      }
      else rhinoConverter

    // println("using converter %s for targetType %s / object %s".format(conv.getClass.getName, targetType.getName, obj.getClass.getName))

    conv
  }
}


// ---------------- common converter utils ----------------------------

private trait NativeArrayConversion {

  protected def toArray[A <: AnyRef](arg: NativeArray, argType: Class[A], method: Method) = {
    // TODO Scala's AnyVal types result into java.lang.Object (scalap)

    val actualType = {
      val _option = for {
        genericType <- method.getGenericParameterTypes.headOption
        paramType = genericType.asInstanceOf[ParameterizedType]
        actualType <- paramType.getActualTypeArguments.headOption
      } yield actualType.asInstanceOf[Class[AnyRef]]

      _option getOrElse sys.error("raw type collections are not supported")
    }

//    println("actualType: " + actualType.getName)

    for (id <- arg.getIds) yield {
      val index = id.asInstanceOf[Int]
      val elem = arg.get(index, null)
      Converter.jsToJava(elem, actualType, method)
    }
  }
}

private trait RawMapConversion {

  type RawMap = Map[String, _]

  def scriptableObjectToMap(obj: ScriptableObject): RawMap = {
    (obj.getAllIds map { id =>
      id.toString -> obj.get(id.toString, obj)
    } collect {
      case (k, v: ScriptableObject) => k -> scriptableObjectToMap(v)
      case x => x
    }).toMap
  }

  def mapToScriptableObject(map: RawMap, obj: AnyRef, scope: Scriptable): ScriptableObject = {
    new ScriptableObject() with Wrapper {

      setParentScope(scope)
      setPrototype(ScriptableObject.getObjectPrototype(this))

      def getClassName = "RawMapConversion"

      def unwrap = obj

      map collect {
        case (k, v: RawMap) => k -> mapToScriptableObject(v, obj, this)
        case x => x
      } foreach { case (k, v) =>
          defineProperty(k.toString, v, ScriptableObject.PERMANENT)
      }
    }
  }
}

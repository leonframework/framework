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
import org.mozilla.javascript.{Wrapper, Undefined, ScriptableObject}


private[javascript] object Converter {

  private val sjsonConverter = new SJSONConverter
  private val pojoConverter = new PojoConverter

  def javaToJs(obj: AnyRef) = {
    val conv = getConverterForType(obj.getClass)
    conv.mapToScriptableObject(conv.objectToMap(obj), obj)
  }

  def jsToJava[T <: AnyRef](obj: ScriptableObject, targetType: Class[T]) = {
    if(targetType.isAssignableFrom(obj.getClass)) obj
    else {
      val conv = getConverterForType(targetType)
      conv.mapToObject(conv.scriptableObjectToMap(obj), targetType)
    }
  }

  private def getConverterForType(clazz: Class[_]) = {
    if(classOf[Product].isAssignableFrom(clazz)) sjsonConverter
    else pojoConverter
  }

}

private trait Converter {

  type RawMap = Map[String, _]

  def objectToMap(obj: AnyRef): RawMap

  def mapToObject[A <: AnyRef](map: RawMap, targetType: Class[A]): A

  def scriptableObjectToMap(obj: ScriptableObject): RawMap = {
    (obj.getAllIds map { id =>
      id.toString -> obj.get(id.toString, obj)
    } collect {
      case (k, v: ScriptableObject) => k -> scriptableObjectToMap(v)
      case x => x
    }).toMap
  }

  def mapToScriptableObject(map: RawMap, obj: AnyRef): ScriptableObject = {
    new ScriptableObject() with Wrapper {
      def getClassName = "map"

      def unwrap = obj

      map collect {
        case (k, v: RawMap) => k -> mapToScriptableObject(v, obj)
        case x => x
      } foreach { case (k, v) =>
          defineProperty(k.toString, v, ScriptableObject.PERMANENT)
      }
    }
  }
}

private[javascript] class SJSONConverter extends Converter {

  def objectToMap(obj: AnyRef): RawMap = {
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

    jsValueToAnyRef(jsValue).asInstanceOf[RawMap]
  }

  def mapToObject[A <: AnyRef](map: RawMap, targetType: Class[A]): A = {
    SJSON.fromJSON(JsValue(map), Some(targetType))
  }
}

private class PojoConverter extends ConvertUtilsBean with Converter {
  import scala.collection.JavaConverters._

  private val utils = new BeanUtilsBean(this)

  private val converter = new BeanUtilsConverter {
    def convert(targetType: Class[_], obj: AnyRef) =  {
      mapToObject(obj.asInstanceOf[RawMap],
        targetType.asInstanceOf[Class[AnyRef]])
    }
  }

  def objectToMap(obj: AnyRef) =  {

    def canConvert(obj: AnyRef) =
      ! (obj.getClass.getName startsWith "java.")

    val beanMap =
      new BeanMap(obj).asScala.toMap collect {
        case (k, v: AnyRef) if canConvert(v) => k.toString -> objectToMap(v)
        case (k, v) => k.toString -> v
      }

    beanMap - "class" // BeanMap adds an entry ('class', java.lang.Class) to the map, that we don't need!
  }

  def mapToObject[A <: AnyRef](map: RawMap, targetType: Class[A]) = {
    val obj = targetType.newInstance()
    utils.populate(obj, map.asJava)
    obj
  }

  override def lookup(clazz: Class[_]) = {
    val conv = super.lookup(clazz)
    if(conv == null) converter
    else conv
  }
}

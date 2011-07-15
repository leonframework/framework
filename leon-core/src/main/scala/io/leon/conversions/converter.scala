/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.conversions

import sjson.json.Serializer.SJSON
import dispatch.json._
import java.lang.Boolean
import org.mozilla.javascript.{Wrapper, Undefined, ScriptableObject}

trait Converter {

  type RawMap = Map[String, _]

  def accept(clazz: Class[_]): Boolean

  def objectToMap(obj: AnyRef): RawMap

  def mapToObject[A <: AnyRef](map: RawMap, targetType: Class[A]): A


  // --- JavaScript Objects to Java Map -------------

  def scriptableObjectToMap(obj: ScriptableObject): RawMap = {
    (obj.getAllIds map { id =>
      id.toString -> obj.get(id.toString, obj)
    } collect {
      case (k, v: ScriptableObject) => k -> scriptableObjectToMap(v)
      case x => x
    }).toMap
  }

  // --- Java Map to JavaScriptNativeObject ---------------

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

  def javaToJs(obj: AnyRef) =
    mapToScriptableObject(objectToMap(obj), obj)

  def jsToJava[T <: AnyRef](obj: ScriptableObject, targetType: Class[T]) =
    mapToObject(scriptableObjectToMap(obj), targetType)

}

class SJSONConverter extends Converter {

  def accept(clazz: Class[_]) = classOf[Any].isAssignableFrom(clazz)

  // --- Java Objects to Java Map -------------------------

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

  // --- Java Map to Java Object --------------------------

  def mapToObject[A <: AnyRef](map: RawMap, targetType: Class[A]): A = {
    SJSON.fromJSON(JsValue(map), Some(targetType))
  }
}

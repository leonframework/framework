/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongo

import org.mozilla.javascript._
import org.mozilla.javascript.regexp.NativeRegExp
import org.bson.types.ObjectId
import java.util.regex.Pattern
import com.mongodb.{BasicDBObject, BasicDBList, DBObject}
import io.leon.javascript.ScriptableMap

private[mongo] object MongoUtils {
  import scala.collection.JavaConverters._

  def jsToJava(obj: Any) = obj match {
    case regex: NativeRegExp => nativeRegExpToPattern(regex)
    case array: NativeArray => nativeArrayToArray(array)
    case func: BaseFunction => Context.getCurrentContext.decompileFunction(func, 2)
    case so: ScriptableObject => toDbObject(so)
    case njo: NativeJavaObject => njo.unwrap
    case x => x
  }

  def javaToJs(obj: AnyRef): AnyRef = obj match {
    case dbList: BasicDBList => new NativeArray(dbList.toArray map { javaToJs })
    case dbObj: DBObject => toScriptableMap(dbObj)
    case objId: ObjectId => objId.toString
    case x => x
  }

  def toScriptableMap(obj: DBObject): ScriptableMap = {
    val jsMap = obj.toMap.asScala map { case (k: String, v: AnyRef) => k -> javaToJs(v) }

    new ScriptableMap(jsMap.asJava)
  }

  def toDbObject(obj: ScriptableObject): DBObject = {
    import scala.collection.JavaConverters._

    val tuples =
      obj.getAllIds.toList map {
        id =>
          val value = obj.get(id.toString, obj) match {
            case x if id == "_id" => new ObjectId(x.toString)
            case x => jsToJava(x)
          }

          (id.toString -> value)
      }
    new BasicDBObject(tuples.toMap.asJava)
  }

  def arrayToNativeArray(arr: Array[AnyRef]): NativeArray = {
    new NativeArray(arr map { javaToJs })
  }

  def nativeArrayToArray(arr: NativeArray): Array[Any] = {
    val seq: Seq[Any] =
      for (id <- arr.getIds) yield {
        val index = id.asInstanceOf[Int]
        val elem = arr.get(index, null)
        jsToJava(elem)
      }

    seq.toArray
  }

  def nativeRegExpToPattern(obj: NativeRegExp) = {
    import Pattern._

    val source = obj.get("source", obj).asInstanceOf[String]
    val multiline = obj.get("multiline", obj).asInstanceOf[Boolean]
    val ignoreCase = obj.get("ignoreCase", obj).asInstanceOf[Boolean]

    var flags = 0
    if(multiline) flags = flags | MULTILINE
    if(ignoreCase) flags = flags | CASE_INSENSITIVE

    compile(source, flags)
  }
}

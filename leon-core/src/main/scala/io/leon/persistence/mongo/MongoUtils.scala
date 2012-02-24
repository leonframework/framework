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

private[mongo] object MongoUtils {
  import scala.collection.JavaConverters._

  def jsToJava(obj: Any) = obj match {
    case regex: NativeRegExp => nativeRegExpToPattern(regex)
    case array: NativeArray => nativeArrayToArray(array)
    case func: BaseFunction => Context.getCurrentContext.decompileFunction(func, 2)
    case so: ScriptableObject => scriptableToDbObject(so)
    case x => x
  }

  def javaToJs(obj: AnyRef): AnyRef = {
    println("### START #################################")
    val r = obj match {
      case dbList: BasicDBList => println("1"); new NativeArray(dbList.toArray map { javaToJs })
      case dbObj: DBObject => println("2"); dbObjectToScriptable(dbObj)
      case objId: ObjectId => println("3"); objId.toString
      case x => println("4 - " + x.getClass + " - " + x); x
    }
    println("### ENDE #################################")
    r
  }

  def dbObjectToMap(obj: DBObject): java.util.Map[_, _] = {
    obj.toMap
  }

  implicit def dbObjectToScriptable(obj: DBObject): ScriptableObject =
    new ScriptableObject() {
      def getClassName = "DBObject"

      obj.toMap.asScala foreach { case (k: String, v: AnyRef) =>
        defineProperty(k, javaToJs(v), ScriptableObject.PERMANENT)
      }
    }

  implicit def scriptableToDbObject(obj: ScriptableObject): DBObject = {
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

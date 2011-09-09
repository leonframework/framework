/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongo

import com.google.inject.Inject
import com.mongodb.casbah.{MongoCursor, MongoCollection, MongoDB}
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.{BasicDBList, DBObject}
import org.bson.types.ObjectId
import org.mozilla.javascript._
import regexp.NativeRegExp
import java.util.regex.Pattern


class ScriptableMongoDB @Inject()(mongo: MongoDB) extends ScriptableObject {

  def getClassName = getClass.getName

  override def get(name: String, start: Scriptable): AnyRef = {
    new JavaScriptMongoCollection(mongo(name))
  }
}

class ScriptableDBCursor(dbCursor: MongoCursor) extends ScriptableObject {
  import MongoUtils._

  private val jsFunctionNames = Array(
    "toArray", "next", "hasNext", "skip", "limit", "sort",
    "size", "length", "count", "close", "forEach", "map")

  private lazy val list = dbCursor.toList map dbObjectToScriptable

  defineFunctionProperties(jsFunctionNames, getClass, ScriptableObject.READONLY)

  def getClassName = getClass.getName

  override def get(index: Int, start: Scriptable) = {
    // Attention: list is empty when next/ hasNext was called first
    list(index)
  }

  def hasNext() = {
    dbCursor.hasNext
  }

  def next() = {
    val dbo = dbCursor.next()
    dbObjectToScriptable(dbo)
  }

  def skip(n: Int) = {
    val newCursor = dbCursor.skip(n)
    new ScriptableDBCursor(newCursor)
  }

  def limit(n: Int) = {
    val newCursor = dbCursor.limit(n)
    new ScriptableDBCursor(newCursor)
  }

  def sort(orderBy: ScriptableObject) = {
    new ScriptableDBCursor(dbCursor.sort(orderBy))
  }

  def size() = {
    dbCursor.size
  }

  def length() = size()

  def count() = {
    dbCursor.count
  }

  def close() {
    dbCursor.close();
  }

  def toArray() = {
    arrayToNativeArray(list.toArray)
  }

  def forEach(func: Function) {
    val ctx = Context.enter()
    list foreach { obj =>
      func.call(ctx, this, this, Array(obj))
    }
    Context.exit()
  }

  def map(func: Function) = {
    val ctx = Context.enter()
    val result =
      list map { obj =>
        func.call(ctx, this, this, Array(obj))
      }
    Context.exit();

    arrayToNativeArray(result.toArray)
  }
}

class JavaScriptMongoCollection(coll: MongoCollection) {
  import MongoUtils._

  def insert(obj: ScriptableObject) {
    val dbo = scriptableToDBObject(obj)
    coll.insert(dbo)

    obj.put("_id", obj, dbo.get("_id").toString)
  }

  def save(obj: ScriptableObject) {
    val dbo = scriptableToDBObject(obj)
    coll.save(dbo)

    obj.put("_id", obj, dbo.get("_id").toString)
  }

  def find() = {
    new ScriptableDBCursor(coll.find())
  }

  def find(obj: ScriptableObject) = {
    new ScriptableDBCursor(coll.find(obj))
  }

  def findOne() = {
    coll.findOne map dbObjectToScriptable getOrElse null
  }

  def findOne(obj: ScriptableObject) = {
    coll.findOne(obj) map dbObjectToScriptable getOrElse null
  }

  def remove(obj: ScriptableObject) {
    coll.remove(obj)
  }

  def count = {
    coll.count
  }

  def drop() {
    coll.drop();
  }
}

private[mongo] object MongoUtils {
  import scala.collection.JavaConverters._

  def jsToJava(obj: Any) = obj match {
    case regex: NativeRegExp => nativeRegExpToPattern(regex)
    case array: NativeArray => nativeArrayToArray(array)
    case so: ScriptableObject => scriptableToDBObject(so)
    case x => x
  }

  def javaToJs(obj: AnyRef): AnyRef = obj match {
    case dbList: BasicDBList => new NativeArray(dbList.toArray map { javaToJs })
    case dbObj: DBObject => dbObjectToScriptable(dbObj)
    case objId: ObjectId => objId.toString
    case x => x
  }

  implicit def dbObjectToScriptable(obj: DBObject): ScriptableObject =
    new ScriptableObject() {
      def getClassName = "DBObject"

      obj.toMap.asScala foreach { case (k: String, v: AnyRef) =>
        defineProperty(k, javaToJs(v), ScriptableObject.PERMANENT)
      }
    }

  implicit def scriptableToDBObject(obj: ScriptableObject): DBObject = {
    val tuples =
      obj.getAllIds.toList map {
        id =>
          val value = obj.get(id.toString, obj) match {
            case x if id == "_id" => new ObjectId(x.toString)
            case x => jsToJava(x)
          }

          (id.toString -> value)
      }
    MongoDBObject(tuples)
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
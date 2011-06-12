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
import com.mongodb.casbah.{MongoCollection, MongoDB}
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.DBObject
import org.bson.types.ObjectId
import org.mozilla.javascript.{NativeArray, ScriptableObject, Scriptable}

class ScriptableMongoDB @Inject()(mongo: MongoDB) extends ScriptableObject {

  def getClassName = getClass.getName

  override def get(name: String, start: Scriptable): AnyRef = {
    new JavaScriptMongoCollection(mongo(name))
  }
}

class ScriptableDBObject(dbObject: DBObject) extends ScriptableObject {

  def getClassName = getClass.getName

  override def get(name: String, start: Scriptable): AnyRef = {
    dbObject.get(name) match {
      case dbObj: DBObject => new ScriptableDBObject(dbObj)
      case x => x
    }
  }

  override def put(name: String, start: Scriptable, value: AnyRef) {
    dbObject.put(name, value)
  }
}

class JavaScriptMongoCollection(coll: MongoCollection) {

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
    val result =
      coll.find().toList map dbObjectToScriptable

    arrayToNativeArray(result.toArray)
  }

  def find(obj: ScriptableObject) = {
    val result =
      coll.find(obj).toList map dbObjectToScriptable

    arrayToNativeArray(result.toArray)
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

  // ---------------------------------------------------

  private def dbObjectToScriptable(obj: DBObject): ScriptableObject = {
    new ScriptableDBObject(obj)
  }

  private implicit def scriptableToDBObject(obj: ScriptableObject): DBObject = {
    val tuples =
      obj.getAllIds.toList map {
        id =>
          val value = obj.get(id.toString, obj) match {
            case so: ScriptableObject => scriptableToDBObject(so)
            case x if id == "_id" => new ObjectId(x.toString)
            case x => x
          }

          (id.toString -> value)
      }
    MongoDBObject(tuples)
  }

  private def arrayToNativeArray[A](arr: Array[A]): NativeArray = {
    new NativeArray(arr.asInstanceOf[Array[Object]])
  }
}
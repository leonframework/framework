/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongo

import org.mozilla.javascript.{Context, BaseFunction, ScriptableObject}
import com.mongodb.{DBCollection, BasicDBObject}
import org.bson.types.ObjectId


private[mongo] class ScriptableDBCollection(collection: DBCollection) {
  import MongoUtils._

  def insert(obj: ScriptableObject) {
    val dbo = toDbObject(obj)
    collection.insert(dbo)

    obj.put("_id", obj, dbo.get("_id").toString)
  }

  def save(obj: ScriptableObject) {
    val dbo = toDbObject(obj)
    collection.save(dbo)

    obj.put("_id", obj, dbo.get("_id").toString)

  }

  // --- find ---

  def find = new ScriptableDBCursor(collection.find())

  def find(ref: ScriptableObject) =
    new ScriptableDBCursor(collection.find(toDbObject(ref)))

  def find(ref: ScriptableObject, keys: ScriptableObject) =
    new ScriptableDBCursor(collection.find(toDbObject(ref), toDbObject(keys)))

  // --- findAndModify ---

  def findAndModify(query: ScriptableObject, update: ScriptableObject) = {
    val result = collection.findAndModify(toDbObject(query), toDbObject(update))
    Option(result) map { toScriptableMap } getOrElse null
  }

  def findAndModify(query: ScriptableObject, sort: ScriptableObject, update: ScriptableObject) = {
    val result = collection.findAndModify(toDbObject(query), toDbObject(sort), toDbObject(update))
    Option(result) map { toScriptableMap } getOrElse null
  }

  def findAndModify(query: ScriptableObject, fields: ScriptableObject, sort: ScriptableObject,
                    remove: Boolean, update: ScriptableObject, returnNew: Boolean, upsert: Boolean) = {
    val result = collection.findAndModify(
      toDbObject(query),
      toDbObject(fields),
      toDbObject(sort),
      remove,
      toDbObject(update),
      returnNew,
      upsert)

    Option(result) map { toScriptableMap } getOrElse null
  }

  // -- findAndRemove --

  def findAndRemove(query: ScriptableObject) = {
    val result = collection.findAndRemove(toDbObject(query))
    Option(result) map { toScriptableMap } getOrElse null
  }

  // -- findOne --

  def findOne = Option(collection.findOne) map toScriptableMap getOrElse null

  def findOne(obj: ScriptableObject) = {
    val result = collection.findOne(toDbObject(obj))
    Option(result) map { toScriptableMap } getOrElse null
  }

  def findOne(obj: ScriptableObject, fields: ScriptableObject) = {
    val result = collection.findOne(toDbObject(obj), toDbObject(fields))
    Option(result) map { toScriptableMap } getOrElse null
  }

  def findOne(id: String): ScriptableObject = {
    import scala.collection.JavaConverters._
    val query = new BasicDBObject(Map("_id" -> new ObjectId(id)).asJava)
    Option(collection.findOne(query)) map { toScriptableMap } getOrElse null
  }

  def findOne(id: String, fields: ScriptableObject) = {
    import scala.collection.JavaConverters._

    val query = new BasicDBObject(Map("_id" -> new ObjectId(id)).asJava)
    Option(collection.findOne(query, toDbObject(fields))) map { toScriptableMap } getOrElse null
  }

  // -- remove --

  def remove(obj: ScriptableObject) {
    collection.remove(toDbObject(obj))
  }

  // -- count ---

  def count = collection.count

  def count(obj: ScriptableObject) =
    collection.count(toDbObject(obj))

  def drop() {
    collection.drop()
  }

  // -- create- /drop- /ensureIndex(s) --

  def createIndex(keys: ScriptableObject) {
    collection.createIndex(toDbObject(keys))
  }

  def createIndex(keys: ScriptableObject, options: ScriptableObject) {
    collection.createIndex(toDbObject(keys), toDbObject(options))
  }

  def ensureIndex(keys: ScriptableObject) {
    collection.ensureIndex(toDbObject(keys))
  }

  def ensureIndex(keys: ScriptableObject, name: String) {
    collection.ensureIndex(toDbObject(keys), name)
  }

  def ensureIndex(keys: ScriptableObject, name: String, unique: Boolean) {
    collection.ensureIndex(toDbObject(keys), name, unique)
  }

  def ensureIndex(name: String) {
    collection.ensureIndex(name)
  }

  def dropIndex(keys: ScriptableObject) {
    collection.dropIndex(toDbObject(keys))
  }

  def dropIndex(name: String) {
    collection.dropIndex(name)
  }

  def dropIndexes() {
    collection.dropIndexes()
  }

  def dropIndexes(name: String) {
    collection.dropIndexes(name)
  }

  // -- update --

  def update(query: ScriptableObject, obj: ScriptableObject) =
    collection.update(toDbObject(query), toDbObject(obj))

  def update(query: ScriptableObject, obj: ScriptableObject, upsert: Boolean, multi: Boolean) =
    collection.update(toDbObject(query), toDbObject(obj), upsert, multi)

  def updateMulti(query: ScriptableObject, obj: ScriptableObject) =
    collection.updateMulti(toDbObject(query), toDbObject(obj))

  // -- distinct --

  def distinct(key: String) =
    arrayToNativeArray(collection.distinct(key).asInstanceOf[List[AnyRef]].toArray)

  def distinct(key: String, query: ScriptableObject) =
    arrayToNativeArray(collection.distinct(key, toDbObject(query)).asInstanceOf[List[AnyRef]].toArray)

  // -- mapReduce --

  def mapReduce(mapFunc: BaseFunction, reduceFunc: BaseFunction, options: ScriptableObject) = {
    val ctx = Context.getCurrentContext
    val map = ctx.decompileFunction(mapFunc, 2)
    val reduce = ctx.decompileFunction(reduceFunc, 2)

    val cmd = new BasicDBObject()
    cmd.put("mapreduce", collection.getName())
    cmd.put("map", map)
    cmd.put("reduce", reduce)
    cmd.putAll(toDbObject(options))

    val output = collection.mapReduce(cmd)

    new ScriptableMapReduceOutput(output)
  }

  // -- convenient --

  def findByOId(oid: String) = findOne(oid)

  def removeByOId(oid: String) {
    val query = new BasicDBObject("_id", new ObjectId(oid))
    collection.remove(query)
  }
}
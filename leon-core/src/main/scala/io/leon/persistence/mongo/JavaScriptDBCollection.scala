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


private[mongo] class JavaScriptDBCollection(collection: DBCollection) {
  import MongoUtils._

  def insert(obj: ScriptableObject) {
    val dbo = scriptableToDbObject(obj)
    collection.insert(dbo)

    obj.put("_id", obj, dbo.get("_id").toString)
  }

  def save(obj: ScriptableObject) {
    val dbo = scriptableToDbObject(obj)
    collection.save(dbo)

    obj.put("_id", obj, dbo.get("_id").toString)

  }

  // --- find ---

  def find = new JavaScriptDBCursor(collection.find())

  def find(ref: ScriptableObject) = new JavaScriptDBCursor(collection.find(ref))

  def find(ref: ScriptableObject, keys: ScriptableObject) = new JavaScriptDBCursor(collection.find(ref, keys))

  // --- findAndModify ---

  def findAndModify(query: ScriptableObject, update: ScriptableObject) = {
    val result = collection.findAndModify(query, update)
    Option(result) map { dbObjectToScriptable } getOrElse null
  }

  def findAndModify(query: ScriptableObject, sort: ScriptableObject, update: ScriptableObject) = {
    val result = collection.findAndModify(query, sort, update)
    Option(result) map { dbObjectToScriptable } getOrElse null
  }

  def findAndModify(query: ScriptableObject, fields: ScriptableObject, sort: ScriptableObject,
                    remove: Boolean, update: ScriptableObject, returnNew: Boolean, upsert: Boolean) = {
    val result = collection.findAndModify(query, fields, sort, remove, update, returnNew, upsert)
    Option(result) map { dbObjectToScriptable } getOrElse null
  }

  // -- findAndRemove --

  def findAndRemove(query: ScriptableObject) = {
    val result = collection.findAndRemove(query)
    Option(result) map { dbObjectToScriptable } getOrElse null
  }

  // -- findOne --

  def findOne = Option(collection.findOne) map dbObjectToScriptable getOrElse null

  def findOne(obj: ScriptableObject) = {
    val query = scriptableToDbObject(obj)
    val result = collection.findOne(query)
    val r = Option(result) map { dbObjectToScriptable } getOrElse null

    println("----------------------")
    println("query = " + query)
    println("r = " + r)
    println("collection.findOne(query) = " + collection.findOne(query))
    println("----------------------")

    r
  }

  def findOne(obj: ScriptableObject, fields: ScriptableObject) = {
    val query = scriptableToDbObject(obj)
    val result = collection.findOne(query, fields)
    Option(result) map { dbObjectToScriptable } getOrElse null
  }

  def findOne(id: String) = {
    import scala.collection.JavaConverters._

    val query = new BasicDBObject(Map("_id" -> id).asJava)
    Option(collection.findOne(query)) map { dbObjectToScriptable } getOrElse null
  }

  def findOne(id: String, fields: ScriptableObject) = {
    import scala.collection.JavaConverters._

    val query = new BasicDBObject(Map("_id" -> id).asJava)
    Option(collection.findOne(query, fields)) map { dbObjectToScriptable } getOrElse null
  }

  // -- remove --

  def remove(obj: ScriptableObject) { collection.remove(obj) }

  // -- count ---

  def count = collection.count

  def count(obj: ScriptableObject) = collection.count(obj)

  def drop() { collection.drop() }

  // -- create- /drop- /ensureIndex(s) --

  def createIndex(keys: ScriptableObject) { collection.createIndex(keys) }

  def createIndex(keys: ScriptableObject, options: ScriptableObject) { collection.createIndex(keys, options) }

  def ensureIndex(keys: ScriptableObject) { collection.ensureIndex(keys) }

  def ensureIndex(keys: ScriptableObject, name: String) { collection.ensureIndex(keys, name) }

  def ensureIndex(keys: ScriptableObject, name: String, unique: Boolean) { collection.ensureIndex(keys, name, unique) }

  def ensureIndex(name: String) { collection.ensureIndex(name) }

  def dropIndex(keys: ScriptableObject) { collection.dropIndex(keys) }

  def dropIndex(name: String) { collection.dropIndex(name) }

  def dropIndexes() { collection.dropIndexes() }

  def dropIndexes(name: String) { collection.dropIndexes(name) }

  // -- update --

  def update(query: ScriptableObject, obj: ScriptableObject) = collection.update(query, obj)

  def update(query: ScriptableObject, obj: ScriptableObject, upsert: Boolean, multi: Boolean) = collection.update(query, obj, upsert, multi)

  def updateMulti(query: ScriptableObject, obj: ScriptableObject) = collection.updateMulti(query, obj)

  // -- distinct --

  def distinct(key: String) = arrayToNativeArray(collection.distinct(key).asInstanceOf[List[AnyRef]].toArray)

  def distinct(key: String, query: ScriptableObject) = arrayToNativeArray(collection.distinct(key, query).asInstanceOf[List[AnyRef]].toArray)

  // -- mapReduce --

  def mapReduce(mapFunc: BaseFunction, reduceFunc: BaseFunction, options: ScriptableObject) = {
    val ctx = Context.getCurrentContext
    val map = ctx.decompileFunction(mapFunc, 2)
    val reduce = ctx.decompileFunction(reduceFunc, 2)

    val cmd = new BasicDBObject()
    cmd.put("mapreduce", collection.getName())
    cmd.put("map", map)
    cmd.put("reduce", reduce)
    cmd.putAll(options)

    val output = collection.mapReduce(cmd)

    new JavaScriptMapReduceOutput(output)
  }

}
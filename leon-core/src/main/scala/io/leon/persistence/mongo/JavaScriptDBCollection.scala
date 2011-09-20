/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongo

import com.mongodb.casbah.MongoCollection
import org.mozilla.javascript.{Context, BaseFunction, ScriptableObject, Function => RhinoFunction}
import com.mongodb.MapReduceCommand

private[mongo] class JavaScriptDBCollection(coll: MongoCollection) {
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

  // -- find ---

  def find = new JavaScriptDBCursor(coll.find())

  def find(ref: ScriptableObject) = new JavaScriptDBCursor(coll.find(ref))

  def find(ref: ScriptableObject, keys: ScriptableObject) = new JavaScriptDBCursor(coll.find(ref, keys))

  // -- findAndModify ---

  def findAndModify(query: ScriptableObject, update: ScriptableObject) = {
    coll.findAndModify(query, update) map { dbObjectToScriptable } getOrElse null
  }

  def findAndModify(query: ScriptableObject, sort: ScriptableObject, update: ScriptableObject) = {
    coll.findAndModify(query, sort, update) map { dbObjectToScriptable } getOrElse null
  }

  def findAndModify(query: ScriptableObject, fields: ScriptableObject, sort: ScriptableObject,
                    remove: Boolean, update: ScriptableObject, returnNew: Boolean, upsert: Boolean) = {
    coll.findAndModify(query, fields, sort, remove, update, returnNew, upsert) map { dbObjectToScriptable } getOrElse null
  }

  // -- findAndRemove --

  def findAndRemove(query: ScriptableObject) = {
    coll.findAndRemove(query) map { dbObjectToScriptable } getOrElse null
  }

  // -- findOne --

  def findOne = coll.findOne map dbObjectToScriptable getOrElse null

  def findOne(obj: ScriptableObject) = coll.findOne(obj) map { dbObjectToScriptable } getOrElse null

  def findOne(obj: ScriptableObject, fields: ScriptableObject) = coll.findOne(obj, fields) map { dbObjectToScriptable } getOrElse null

  def findOne(id: String) = coll.findOneByID(id) map { dbObjectToScriptable } getOrElse null

  def findOne(id: String, fields: ScriptableObject) = coll.findOneByID(id, fields) map { dbObjectToScriptable } getOrElse null

  // -- remove --

  def remove(obj: ScriptableObject) { coll.remove(obj) }

  // -- count ---

  def count = coll.count

  def count(obj: ScriptableObject) = coll.count(obj)

  def drop() { coll.drop() }

  // -- create- /drop- /ensureIndex(s) --

  def createIndex(keys: ScriptableObject) { coll.createIndex(keys) }

  def createIndex(keys: ScriptableObject, options: ScriptableObject) { coll.createIndex(keys, options) }

  def ensureIndex(keys: ScriptableObject) { coll.ensureIndex(keys) }

  def ensureIndex(keys: ScriptableObject, name: String) { coll.ensureIndex(keys, name) }

  def ensureIndex(keys: ScriptableObject, name: String, unique: Boolean) { coll.ensureIndex(keys, name, unique) }

  def ensureIndex(name: String) { coll.ensureIndex(name) }

  def dropIndex(keys: ScriptableObject) { coll.dropIndex(keys) }

  def dropIndex(name: String) { coll.dropIndex(name) }

  def dropIndexes() { coll.dropIndexes() }

  def dropIndexes(name: String) { coll.dropIndexes(name) }

  // -- update --

  def update(query: ScriptableObject, obj: ScriptableObject) = coll.update(query, obj)

  def update(query: ScriptableObject, obj: ScriptableObject, upsert: Boolean, multi: Boolean) = coll.update(query, obj, upsert, multi)

  def updateMulti(query: ScriptableObject, obj: ScriptableObject) = coll.updateMulti(query, obj)

  // -- distinct --

  def distinct(key: String) = arrayToNativeArray(coll.distinct(key).asInstanceOf[List[AnyRef]].toArray)

  def distinct(key: String, query: ScriptableObject) = arrayToNativeArray(coll.distinct(key, query).asInstanceOf[List[AnyRef]].toArray)

  // -- TODO map reduce --

  def mapReduce(mapFunc: BaseFunction, reduceFunc: BaseFunction, options: ScriptableObject) = {
    val ctx = Context.getCurrentContext
    val map = ctx.decompileFunction(mapFunc, 2)
    val reduce = ctx.decompileFunction(reduceFunc, 2)

    val cmd = new MapReduceCommand(coll.underlying, map, reduce, "todo", MapReduceCommand.OutputType.REPLACE, null)
    val output = coll.underlying.mapReduce(cmd)

    new JavaScriptMapReduceOutput(output)
  }

}
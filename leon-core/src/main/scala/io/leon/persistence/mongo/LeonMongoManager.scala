/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongo

import com.mongodb._

import scala.collection.JavaConverters
import javax.inject.Inject

class MongoDatabase(db: DB) {

  def getCollectionNames() = {
    import JavaConverters._
    db.getCollectionNames.asScala
  }

  def getCollection(name: String) = {
    val coll = db.getCollection(name)
    new MongoCollection(coll)
  }

  def drop() = {
    db.dropDatabase()
  }
}

class MongoCollection(coll: DBCollection) {

  import MongoUtils._

  def insert(data: RawMap) {
    insert(List(data))
  }

  def insert(data: RawMap*) {
    import JavaConverters._
    val dbObjects = data map mapToDbObject

    coll.insert(dbObjects.asJava)
  }

  def insert(data: List[RawMap]) {
    insert(data: _*)
  }

  def find(): List[RawMap] = {
    import JavaConverters._

    val cursor = coll.find()
    val result = cursor.toArray.asScala.toList

    result map dbObjectToMap
  }

  def find(query: RawMap): List[RawMap] = {
    import JavaConverters._

    val cursor = coll.find(mapToDbObject(query))
    val result = cursor.toArray.asScala.toList

    result map dbObjectToMap
  }

  def findAndModify(query: RawMap, update: RawMap): RawMap = {
    coll.findAndModify(query, update)
  }

  def drop() {
    coll.drop()
  }
}

class LeonMongoManager @Inject() (mongo: Mongo) {

  def getDb(dbName: String) = {
    val db = mongo.getDB(dbName)
    new MongoDatabase(db)
  }
}
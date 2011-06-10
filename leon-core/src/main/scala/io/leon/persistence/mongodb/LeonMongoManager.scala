package io.leon.persistence.mongodb

import com.mongodb._

import collection._
import scala.collection.JavaConverters

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

  type MongoObject = Map[_,_]

  def insert(data: MongoObject*) {
    import JavaConverters._
    val dbObjects = data map MongoUtils.mapToDbObject
    coll.insert(dbObjects.asJava)
  }

  def insert(data: List[MongoObject]) {
    insert(data: _*)
  }

  def find(): List[MongoObject] = {
    import JavaConverters._

    val cursor = coll.find()
    val result = cursor.toArray.asScala.toList

    result map MongoUtils.dbObjectToMap
  }

  def find(query: MongoObject): List[MongoObject] = {
    import JavaConverters._

    val cursor = coll.find(MongoUtils.mapToDbObject(query))
    val result = cursor.toArray.asScala.toList

    result map MongoUtils.dbObjectToMap
  }

  def drop() {
    coll.drop()
  }
}

class LeonMongoManager(mongo: Mongo) {

  def getDb(dbName: String) = {
    val db = mongo.getDB(dbName)
    new MongoDatabase(db)
  }

}
package io.leon.persistence.mongo.test

import com.google.inject.{Guice, AbstractModule}
import com.mongodb.casbah.commons.MongoDBObject
import io.leon.resources.ResourceLoaderModule
import io.leon.javascript.LeonJavaScriptModule
import io.leon.persistence.mongo.{LeonMongoManager, LeonMongoModule}

class LeonMongoManagerSpec extends MongoSpecification {

  private def getCollection() = {
    val m = createManager()
    val coll = m.mongo("people")
    coll.drop()
    coll
  }

  private val personTestData = List(
    MongoDBObject("id" -> 1, "firstName" -> "first1", "lastName" -> "last1"),
    MongoDBObject("id" -> 2, "firstName" -> "first2", "lastName" -> "last2"),
    MongoDBObject("id" -> 3, "firstName" -> "first3", "lastName" -> "last3"))

  "A LeonMongoManager" should {

    "insert data into a collection" in {
      val coll = getCollection()

      coll.insert(personTestData)
      coll.find() must have size (3)
    }

    "find data by simple query" in {
      val coll = getCollection()
      coll.insert(personTestData)

      val query = MongoDBObject("firstName" -> "first3")
      coll.find(query) must have size (1)
    }

    "find data by regular expression" in {
      val coll = getCollection()
      coll.insert(personTestData)

      val query = MongoDBObject("firstName" -> "^first[12]$".r.pattern)
      coll.find(query) must have size (2)
    }
  }
}

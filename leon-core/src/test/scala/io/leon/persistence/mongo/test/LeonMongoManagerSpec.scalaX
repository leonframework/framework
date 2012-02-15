package io.leon.persistence.mongo.test


class LeonMongoManagerSpec extends MongoSpecification {

  private def getCollection() = {
    val manager = getManager()
    val collection = manager.db.getCollection("people")
    collection.drop()
    collection
  }

  private val personTestData = List(
    dbObject("id" -> 1, "firstName" -> "first1", "lastName" -> "last1"),
    dbObject("id" -> 2, "firstName" -> "first2", "lastName" -> "last2"),
    dbObject("id" -> 3, "firstName" -> "first3", "lastName" -> "last3"))

  "A LeonMongoManager" should {

    "insert data into a collection" in {
      val coll = getCollection()

      coll.insert(personTestData: _*)
      coll.find().size() must_== 3
    }

    "find data by simple query" in {
      val coll = getCollection()
      coll.insert(personTestData: _*)

      val query = dbObject("firstName" -> "first3")
      coll.find(query).size() must_== 1
    }

    "find data by regular expression" in {
      val coll = getCollection()
      coll.insert(personTestData: _*)

      val query = dbObject("firstName" -> "^first[12]$".r.pattern)
      coll.find(query).size() must_== 2
    }
  }
}

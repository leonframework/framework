package io.leon.persistence.mongo.test

class ScriptableMongoDBSpec extends MongoSpecification {

  "A ScriptableMongoDB" should {

   "insert data" in {
      invokeJsTest("insert") must beTrue
    }

    "insert/update data using collection.save" in {
      invokeJsTest("save") must beTrue
    }

    "find all data" in {
      invokeJsTest("find") must beTrue
    }

    "findOne " in {
      invokeJsTest("findOne") must beTrue
    }

    "remove data" in {
      invokeJsTest("remove") must beTrue
    }

    "cursor" in {
      invokeJsTest("cursor") must beTrue
    }

    "sort" in {
      invokeJsTest("sort") must beTrue
    }

    "DBCursor.forEach" in {
      invokeJsTest("cursor_forEach") must beTrue
    }

    "DBCursor.map" in {
      invokeJsTest("cursor_map") must beTrue
    }

    "Query with regular expression" in {
      invokeJsTest("regex_find") must beTrue
    }

    "Storing and querying arrays/lists" in {
      invokeJsTest("arrays") must beTrue
    }

    "mapReduce" in {
      invokeJsTest("mapReduce") must beTrue
    }

    "getStats" in {
      invokeJsTest("getStats") must beTrue
    }

    "setWriteConcern" in {
      invokeJsTest("setWriteConcern") must beTrue
    }
  }
}
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
      invokeJsTest("test.cursor") must beTrue
    }

    "sort" in {
      invokeJsTest("test.sort") must beTrue
    }

    "DBCursor.forEach" in {
      invokeJsTest("test.cursor_forEach") must beTrue
    }

    "DBCursor.map" in {
      invokeJsTest("test.cursor_map") must beTrue
    }
  }
}
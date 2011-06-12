package io.leon.persistence.mongo.test

class ScriptableMongoDBSpec extends MongoSpecification {

  "A ScriptableMongoDB" should {

    "insert data" in {
      invokeJsTest("test.insert") must beTrue
    }

    "insert/update data using collection.save" in {
      invokeJsTest("test.save") must beTrue
    }

    "find all data" in {
      invokeJsTest("test.find") must beTrue
    }

    "findOne " in {
      invokeJsTest("test.findOne") must beTrue
    }

    "remove data" in {
      invokeJsTest("test.remove") must beTrue
    }
  }
}
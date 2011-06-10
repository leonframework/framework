package io.leon.persistence.mongo.test

import org.specs2.mutable.Specification
import io.leon.persistence.mongodb.{LeonMongoManager, LeonMongoModule, LeonMongoConfig}
import com.google.inject.{Guice, AbstractModule}
import io.leon.resources.ResourceLoaderModule
import io.leon.javascript.LeonJavaScriptModule
import io.leon.{AbstractLeonConfiguration, LeonModule}

class LeonMongoManagerSpec extends Specification {

  private val TestDb = "leon_test"

  private val module = new AbstractLeonConfiguration {
    def config() {
      install(new LeonMongoModule)
    }
  }

  private def createManager(): LeonMongoManager = {
    val m = Guice.createInjector(module).getInstance(classOf[LeonMongoManager])
    m.getDb(TestDb).drop()
    m
  }

  private def getCollection() = {
    val m = createManager()
    val db = m.getDb(TestDb)
    val coll = db.getCollection("people")
    coll.drop()
    coll
  }

  private val personTestData = List(
    Map("id" -> 1, "firstName" -> "first1", "lastName" -> "last1"),
    Map("id" -> 2, "firstName" -> "first2", "lastName" -> "last2"),
    Map("id" -> 3, "firstName" -> "first3", "lastName" -> "last3"))

  "A LeonMongoManager" should {

    "insert data into a collection" in {
      val coll = getCollection()

      coll.insert(personTestData)
      coll.find() must have size (3)
    }

    "find data by simple query" in {
      val coll = getCollection()

      val query = Map("firstName" -> "first3")
      coll.find(query) must have size (1)
    }

    "find data by regular expression" in {
      val coll = getCollection()

      val query = Map("firstName" -> "^first[12]$".r.pattern)
      coll.find(query) must have size (2)
    }

  }

}
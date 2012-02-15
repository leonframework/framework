package io.leon.persistence.mongo.test

import org.scalatest.Assertions
import com.google.inject.{Module, Inject}
import org.testng.annotations.{Test, Guice}
import com.mongodb.{BasicDBObject, DB}


@Guice(modules = Array[Class[_ <: Module]](classOf[MongoTestModule]))
class LeonMongoManagerTest @Inject()(db: DB) extends Assertions {

  private val personTestData = List(
      dbObject("id" -> 1, "firstName" -> "first1", "lastName" -> "last1"),
      dbObject("id" -> 2, "firstName" -> "first2", "lastName" -> "last2"),
      dbObject("id" -> 3, "firstName" -> "first3", "lastName" -> "last3"))

  private def createCollection() = {
    val collection = db.getCollection("people")
    collection.drop()
    collection
  }

  @Test(groups = Array("nodefault"))
  def insertDataIntoACollection() {
    val coll = createCollection()

    coll.insert(personTestData: _*)
    assert(coll.find().size() === 3)
  }

  @Test(groups = Array("nodefault"))
  def findDataBySimpleQuery() {
    val coll = createCollection()
    coll.insert(personTestData: _*)

    val query = dbObject("firstName" -> "first3")
    assert(coll.find(query).size() === 1)
  }

  @Test(groups = Array("nodefault"))
  def findDataByRegularExpression() {
    val coll = createCollection()
    coll.insert(personTestData: _*)

    val query = dbObject("firstName" -> "^first[12]$".r.pattern)
    assert(coll.find(query).size() === 2)
  }

  private def dbObject(data: (String, Any)*) = {
    import scala.collection.JavaConverters._
    val map = Map(data: _*)
    new BasicDBObject(map.asJava)
  }

}

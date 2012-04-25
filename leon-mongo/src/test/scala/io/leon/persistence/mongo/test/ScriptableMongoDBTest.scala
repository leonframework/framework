package io.leon.persistence.mongo.test

import io.leon.javascript.LeonScriptEngine
import com.google.inject.{Inject, Module}
import org.testng.annotations.{Test, Guice}


@Guice(modules = Array[Class[_ <: Module]](classOf[MongoTestModule]))
@Test(groups = Array("nodefault"))
class ScriptableMongoDBTest @Inject()(engine: LeonScriptEngine) extends MongoJavaScriptTest(engine) {

 def insertData() {
    invokeJsTest("insert")
 }

 def insertOrUpdateDataUsingCollectionSave() {
    invokeJsTest("save")
  }

  def findAllData() {
    invokeJsTest("find")
  }

  def findOne() {
    invokeJsTest("findOne")
  }

  def removeData() {
    invokeJsTest("remove")
  }

  def cursor() {
    invokeJsTest("cursor")
  }

  def sort() {
    invokeJsTest("sort")
  }

  def DBCursorForEach() {
    invokeJsTest("cursor_forEach")
  }

  def DBCursorMap() {
    invokeJsTest("cursor_map")
  }

  def queryWithRegularExpression() {
    invokeJsTest("regex_find")
  }

  def storingAndQueryingArraysAndLists() {
    invokeJsTest("arrays")
  }

  def mapReduce() {
    invokeJsTest("mapReduce")
  }

  def getStats() {
    invokeJsTest("getStats")
  }

  def setWriteConcern() {
    invokeJsTest("setWriteConcern")
  }
}
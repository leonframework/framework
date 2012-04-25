package io.leon.persistence.mongo.test

import io.leon.javascript.LeonScriptEngine


protected class MongoJavaScriptTest(engine: LeonScriptEngine) {

  val namespace = this.getClass.getPackage.getName

  engine.loadResource("/io/leon/persistence/mongo/test/mongo_test.js")

  def invokeJsTest(func: String) {
    assert(engine.invokeFunction("%s.%s".format(namespace, func)).asInstanceOf[Boolean])
  }

}

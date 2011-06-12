package io.leon.persistence.mongo.test

import org.specs2.mutable.Specification
import io.leon.resources.ResourceLoaderModule
import io.leon.javascript.{LeonScriptEngine, LeonJavaScriptModule}
import io.leon.persistence.mongo.{LeonMongoManager, LeonMongoModule}
import com.google.inject.{Inject, Guice, AbstractModule}


class MongoSpecification extends Specification {

  override def is = args(sequential = true) ^ super.is

  private val module = new AbstractModule {
    def configure() {
      install(new ResourceLoaderModule)
      install(new LeonJavaScriptModule)
      install(new LeonMongoModule())
      bind(classOf[MongoTestModuleInit]).asEagerSingleton()
    }
  }

  def getLeonScriptEngine = {
    Guice.createInjector(module).getInstance(classOf[LeonScriptEngine])
  }

  def createManager(): LeonMongoManager = {
    Guice.createInjector(module).getInstance(classOf[LeonMongoManager])
  }

  def invokeJsTest(func: String) = {
    val engine = getLeonScriptEngine
    engine.invokeFunction(func).asInstanceOf[Boolean]
  }
}

class MongoTestModuleInit @Inject()(engine: LeonScriptEngine) {
  engine.loadResource("/io/leon/persistence/mongo/test/mongo_test.js")
}

package io.leon.persistence.mongodb

import com.google.inject.{Injector, Inject, AbstractModule}
import io.leon.javascript.LeonScriptEngine

class LeonMongoConfig {
  var host: String = "127.0.0.1"
  var port: Int = 27017
  var db: String = "test"
}

class LeonMongoModule(config: LeonMongoConfig) extends AbstractModule {

  def this() = this(new LeonMongoConfig())


  def configure() {
    bind(classOf[LeonMongoManager]).toProvider(new LeonMongoManagerFactory(config))

//    requestInjection(new Object {
//      @Inject def init(injector: Injector, engine: LeonScriptEngine) {
//        // Loading JavaScript files
//        engine.loadResource("/io/leon/mongo.js")
//      }
//    })
  }
}
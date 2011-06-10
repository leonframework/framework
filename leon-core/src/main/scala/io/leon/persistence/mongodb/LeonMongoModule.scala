package io.leon.persistence.mongodb

import io.leon.javascript.LeonScriptEngine
import com.google.inject._

class LeonMongoConfig {
  var host: String = "127.0.0.1"
  var port: Int = 27017
  var db: String = "test"
}

class LeonMongoModule(config: LeonMongoConfig) extends AbstractModule {

  def this() = this(new LeonMongoConfig())

  def configure() {
    bind(classOf[LeonMongoInit]).asEagerSingleton()
    bind(classOf[LeonMongoManager]).toProvider(new LeonMongoManagerFactory(config)).in(Scopes.SINGLETON)
  }
}

class LeonMongoInit @Inject()(engine: LeonScriptEngine) {
  engine.loadResource("/io/leon/mongo.js")
}

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongo

import io.leon.javascript.LeonScriptEngine
import com.google.inject._
import com.mongodb.{DB, Mongo}
import io.leon.config.ConfigMapHolder

class LeonMongoConfig {
  var host: String = "127.0.0.1"
  var port: Int = 27017
  var db: String = ConfigMapHolder.getInstance().getConfigMap.getApplicationName
}

class LeonMongoModule(config: LeonMongoConfig) extends AbstractModule {

  def this() = this(new LeonMongoConfig())

  private lazy val mongo = new Mongo(config.host, config.port)

  @Provides
  def getMongo() = {
    mongo
  }

  @Provides
  def createMongoDB() = {
    mongo.getDB(config.db)
  }

  def configure() {
    bind(classOf[MongoModuleInit]).asEagerSingleton()
    bind(classOf[LeonMongoManager]).asEagerSingleton()
    bind(classOf[ScriptableMongoDB]).asEagerSingleton()
  }
}

class LeonMongoManager @Inject() (val mongo: Mongo,  val db: DB)

class MongoModuleInit @Inject()(engine: LeonScriptEngine) {
  engine.loadResource("/io/leon/persistence/mongo/mongo.js")
}

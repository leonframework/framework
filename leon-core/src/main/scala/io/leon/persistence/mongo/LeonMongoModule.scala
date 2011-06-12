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
import com.mongodb.casbah.{MongoDB, MongoConnection}

class LeonMongoConfig {
  var host: String = "127.0.0.1"
  var port: Int = 27017
  var db: String = "leon_test" // TODO: use application name
}

class LeonMongoModule(config: LeonMongoConfig) extends AbstractModule {

  def this() = this(new LeonMongoConfig())

  private lazy val mongoConnection = MongoConnection(config.host, config.port)

  @Provides
  def getMongoConnection() = {
    mongoConnection
  }

  @Provides
  def createMongoDB() = {
    mongoConnection(config.db)
  }

  def configure() {
    bind(classOf[MongoModuleInit]).asEagerSingleton()
    bind(classOf[LeonMongoManager]).asEagerSingleton()
    bind(classOf[ScriptableMongoDB]).asEagerSingleton()
  }
}

class LeonMongoManager @Inject() (val conn: MongoConnection,  val mongo: MongoDB)

class MongoModuleInit @Inject()(engine: LeonScriptEngine) {
  engine.loadResource("/io/leon/persistence/mongo/mongo.js")
}

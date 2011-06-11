/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongo

import io.leon.javascript.LeonScriptEngine
import com.google.inject._
import com.mongodb.Mongo

class LeonMongoConfig {
  var host: String = "127.0.0.1"
  var port: Int = 27017
  var db: String = "test" // TODO: use application name
}

class LeonMongoModule(config: LeonMongoConfig) extends AbstractModule {

  def this() = this(new LeonMongoConfig())

  @Provides
  def createMongo() = {
    new Mongo(config.host, config.port)
  }

  def configure() {
    bind(classOf[LeonMongoInit]).asEagerSingleton()
    bind(classOf[LeonMongoManager]).asEagerSingleton()
  }
}

class LeonMongoInit @Inject()(engine: LeonScriptEngine) {
  MongoUtils.engine = engine
  engine.loadResource("/io/leon/persistence/mongo/mongo.js")
}


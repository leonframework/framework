package io.leon.persistence.mongodb

import com.google.inject.Provider
import com.mongodb.Mongo

class LeonMongoManagerFactory(config: LeonMongoConfig) extends Provider[LeonMongoManager] {

  private lazy val mongo = new Mongo(config.host, config.port)

  def get() = {
    new LeonMongoManager(mongo)
  }
}
/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongodb

import com.google.inject.Provider
import com.mongodb.Mongo

class LeonMongoManagerFactory(config: LeonMongoConfig) extends Provider[LeonMongoManager] {

  private lazy val mongo = new Mongo(config.host, config.port)

  def get() = {
    new LeonMongoManager(mongo)
  }
}
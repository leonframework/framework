/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package io.leon.sql

import com.google.inject.AbstractModule

class LeonSqlModule(config: LeonSqlConfig) extends AbstractModule {
  def configure() {
    bind(classOf[LeonSqlManager]).toProvider(new LeonSqlManagerFactory(config))
  }
}
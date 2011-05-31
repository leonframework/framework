/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.dummyapp.person

import io.leon.LeonConfig

class Config extends LeonConfig {

  loadJsFile("server/person/person.js")

  expose("savePerson") as "savePerson"

  def config() {
  }

}
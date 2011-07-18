/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.samples.mixed

import io.leon.AbstractLeonConfiguration
import io.leon.persistence.mongo.LeonMongoModule
import io.leon.resources.coffeescript.CoffeeScriptModule
import com.google.inject.Inject
import io.leon.javascript.{JavaScriptProxy, LeonScriptEngine}

class Module extends AbstractLeonConfiguration {

  def config() {
    install(new LeonMongoModule)
    install(new CoffeeScriptModule)
    bind(classOf[ModuleInit]).asEagerSingleton()

    loadFile("/io/leon/samples/mixed/person.js")

    browser("person").linksToServer("person")
    browser("personService").linksToServer("personService")

    server("leon.browser").linksToAllPages("leon")
  }

}

class ModuleInit @Inject() (engine: LeonScriptEngine) {
  engine.put("personService", JavaScriptProxy(engine.rhinoScope, new PersonService))
}
/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon

import com.google.inject.AbstractModule
import javascript.{GsonModule, LeonJavaScriptModule}
import resources.coffeescript.CoffeeScriptFeatureModule
import resources.less.LessFeatureModule
import resources.ResourcesModule
import unitofwork.UOWFeatureModule
import web.ajax.AjaxFeatureModule
import web.browser.BrowserWebFeatureModule
import web.comet.CometFeatureModule
import web.resources.ResourcesWebFeatureModule

class LeonDefaultWebAppGroupFeatureModule extends AbstractModule {

  def configure() {
    install(new UOWFeatureModule)
    install(new ResourcesModule)
    install(new GsonModule)
    install(new LeonJavaScriptModule)
    install(new AjaxFeatureModule)
    install(new CometFeatureModule)
    install(new BrowserWebFeatureModule)
    install(new CoffeeScriptFeatureModule)
    install(new LessFeatureModule)

    // must be at the last position!
    install(new ResourcesWebFeatureModule)
  }

}

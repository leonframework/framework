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
import resources.coffeescript.CoffeeScriptModule
import resources.less.LessModule
import resources.ResourcesModule
import unitofwork.UOWModule
import web.ajax.AjaxModule
import web.browser.BrowserModule
import web.comet.CometModule
import web.resources.WebResourcesModule

class LeonDefaultWebAppGroupingModule extends AbstractModule {

  def configure() {
    install(new UOWModule)
    install(new ResourcesModule)
    install(new GsonModule)
    install(new LeonJavaScriptModule)
    install(new AjaxModule)
    install(new CometModule)
    install(new BrowserModule)
    install(new CoffeeScriptModule)
    install(new LessModule)

    // must be at the last position!
    install(new WebResourcesModule)
  }

}

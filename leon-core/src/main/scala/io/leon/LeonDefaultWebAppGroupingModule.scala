/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon

import config.{ConfigModule, ConfigMapHolder}
import gson.GsonModule
import javascript.LeonJavaScriptModule
import resourceloading.ResourceLoadingModule
import resources.coffeescript.CoffeeScriptModule
import resources.soy.SoyTemplatesModule
import web.angular.AngularModule
import web.cockpit.CockpitModule
import web.htmltagsprocessor.HtmlTagsProcessorModule
import resources.less.LessModule
import unitofwork.UOWModule
import web.ajax.AjaxModule
import web.browser.BrowserModule
import web.comet.CometModule
import web.resources.WebResourcesModule
import com.google.inject.AbstractModule

class LeonDefaultWebAppGroupingModule extends AbstractModule {

  def configure() {
    install(new ConfigModule)
    install(new UOWModule)
    install(new ResourceLoadingModule)
    install(new AngularModule)
    install(new HtmlTagsProcessorModule)
    install(new GsonModule)
    install(new LeonJavaScriptModule)
    install(new AjaxModule)
    install(new CometModule)
    install(new BrowserModule)
    install(new CoffeeScriptModule)
    install(new LessModule)
    install(new SoyTemplatesModule)

    if (ConfigMapHolder.getInstance().getConfigMap.isDevelopmentMode) {
      install(new CockpitModule)
    }

    // must be at the last position!
    install(new WebResourcesModule)
  }

}

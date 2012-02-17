/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon

import config.{ConfigParameter, LeonConfigModule}
import gson.GsonModule
import javascript.LeonJavaScriptModule
import resourceloading.ResourceLoadingModule
import resources.coffeescript.CoffeeScriptModule
import resources.soy.SoyTemplatesModule
import web.htmltagsprocessor.HtmlTagsProcessorModule
import resources.less.LessModule
import unitofwork.UOWModule
import web.ajax.AjaxModule
import web.browser.BrowserModule
import web.comet.CometModule
import web.resources.WebResourcesModule
import javax.servlet.ServletContext
import com.google.inject.{Inject, AbstractModule}

class LeonDefaultWebAppGroupingModule extends AbstractModule {

  def configure() {
    install(new LeonConfigModule);
    install(new UOWModule)
    install(new ResourceLoadingModule(true)) // TODO true/false depends on deployment mode
    install(new HtmlTagsProcessorModule)
    install(new GsonModule)
    install(new LeonJavaScriptModule)
    install(new AjaxModule)
    install(new CometModule)
    install(new BrowserModule)
    install(new CoffeeScriptModule)
    install(new LessModule)
    install(new SoyTemplatesModule)
    //install(new FreeMarkerModule)

    binder().requestInjection(new {
      @Inject
      def initParameters(servletContext: ServletContext) {
        import scala.collection.JavaConverters._
        val parametersNames = servletContext.getInitParameterNames.asScala.asInstanceOf[Iterator[String]]
        parametersNames foreach { paramName =>
          val paramValue = servletContext.getInitParameter(paramName)
          bind(classOf[ConfigParameter]).toInstance(new ConfigParameter(paramName, paramValue))
        }
      }

    })

    // must be at the last position!
    install(new WebResourcesModule)
  }

}

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web

import com.google.inject.servlet.GuiceFilter
import io.leon.config.{ConfigMapHolder, ConfigReader}
import javax.servlet._
import io.leon.{DefaultWebAppGroupingModule, LeonAppMainModule}
import com.google.inject._

class LeonFilter(applicationModule: LeonAppMainModule) extends GuiceFilter {

  private var injector: Injector = _

  def this() = this(null)

  private def setupConfigMap(filterConfig: FilterConfig) {
    val configMap = ConfigMapHolder.getInstance().getConfigMap
    val servletConfig = new ConfigReader().readFilterConfig(filterConfig)

    configMap.putAll(servletConfig)
  }

  private def getLeonModule(filterConfig: FilterConfig): LeonAppMainModule = {
    new LeonAppMainModule
  }

  def getInjector: Injector = {
    injector
  }

  override def init(filterConfig: FilterConfig) {
    StaticServletContextHolder.SERVLET_CONTEXT = filterConfig.getServletContext

    val defaultWebModule = new DefaultWebAppGroupingModule
    defaultWebModule.init()
    setupConfigMap(filterConfig)

    val module = getLeonModule(filterConfig)

    // create a new module to ensure the binding ordering
    val app = new AbstractModule {
      def configure() {
        install(module)
        install(defaultWebModule)
      }
    }
    injector = Guice.createInjector(app)
    injector.injectMembers(this)
    super.init(filterConfig)
  }

}

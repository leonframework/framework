package com.ww.sjs

/*
 * Copyright 2011 Weigle Wilczek GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.google.inject.{Module, Guice}
import com.google.inject.servlet.{GuiceFilter, ServletModule}
import javax.servlet.FilterConfig
import org.atmosphere.cpr.AtmosphereServlet


class SJSFilter extends GuiceFilter {

  private val classLoader = Thread.currentThread.getContextClassLoader

  private var appModule: Module = _

  override def init(filterConfig: FilterConfig) {
    val moduleName = filterConfig.getInitParameter("module")
    val moduleClass = classLoader.loadClass(moduleName).asInstanceOf[Class[SJSConfig]]

    Guice.createInjector(new ServletModule {
      override def configureServlets() {
        bind(classOf[SJSConfig]).to(moduleClass)
        bind(classOf[SJSServlet]).asEagerSingleton()
        bind(classOf[AtmosphereServlet]).asEagerSingleton()

        serve("/sjs/*").`with`(classOf[SJSServlet])
        serve("/atmosphere/*").`with`(classOf[AtmosphereServlet])
      }
    })

    super.init(filterConfig)
  }

}

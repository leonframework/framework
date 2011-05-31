package io.leon.web

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

import com.google.inject.Guice
import com.google.inject.servlet.GuiceFilter
import javax.servlet.FilterConfig
import io.leon.LeonConfig

class LeonFilter extends GuiceFilter {

  private val classLoader = Thread.currentThread.getContextClassLoader

  override def init(filterConfig: FilterConfig) {
    val moduleName = filterConfig.getInitParameter("module")
    val moduleClass = classLoader.loadClass(moduleName).asInstanceOf[Class[LeonConfig]]
    Guice.createInjector(moduleClass.newInstance())
    super.init(filterConfig)
  }

}

/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
import com.google.inject.util.Modules
import io.leon.{LeonModule, AbstractLeonConfiguration}

class LeonFilter extends GuiceFilter {

  private val classLoader = Thread.currentThread.getContextClassLoader

  override def init(filterConfig: FilterConfig) {
    import scala.collection.JavaConverters._

    val moduleName = filterConfig.getInitParameter("module")
    val moduleClass = classLoader.loadClass(moduleName).asInstanceOf[Class[AbstractLeonConfiguration]]

    val leonIt = List(new LeonModule).toIterable.asJava
    val userIt = List(moduleClass.newInstance()).toIterable.asJava
    val combined = Modules.`override`(leonIt).`with`(userIt)
    Guice.createInjector(combined)

    super.init(filterConfig)
  }

}
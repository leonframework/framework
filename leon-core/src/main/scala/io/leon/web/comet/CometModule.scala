/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet

import annotations.CometThreadPoolExecutor
import com.google.inject.servlet.ServletModule
import org.atmosphere.cpr.AtmosphereServlet
import io.leon.web.resources.WebResourcesBinder
import io.leon.web.TopicsService
import com.google.inject.{Key, AbstractModule}
import io.leon.web.browser.VirtualJsFileBinder
import java.util.concurrent.{Executors, Executor}

class CometModule extends AbstractModule {

  def configure() {
    bind(classOf[Executor]).annotatedWith(classOf[CometThreadPoolExecutor]).toInstance(
      Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors()))

    new VirtualJsFileBinder(binder()).bindAndAddContribution(classOf[ClientIdJsContribution])

    bind(classOf[CometRegistry]).asEagerSingleton()
    bind(classOf[TopicsService]).to(Key.get(classOf[CometRegistry]))
    bind(classOf[ClientSubscriptions]).to(Key.get(classOf[CometRegistry]))

    bind(classOf[Clients]).asEagerSingleton()
    bind(classOf[CometConnectionServlet]).asEagerSingleton()
    bind(classOf[UpdateSubscriptionServlet]).asEagerSingleton()

    install(new ServletModule {
      override def configureServlets() {
        import scala.collection.JavaConverters._

        val meteorParams = Map(
          AtmosphereServlet.WEBSOCKET_SUPPORT -> "false",
          AtmosphereServlet.PROPERTY_NATIVE_COMETSUPPORT -> "true"
        ).asJava

        serve("/leon/comet/connect*").`with`(classOf[CometConnectionServlet], meteorParams)
        serve("/leon/comet/updateFilter").`with`(classOf[UpdateSubscriptionServlet])

        val rwb = new WebResourcesBinder(binder())
        rwb.exposeUrl("/leon/comet/connect")
        rwb.exposeUrl("/leon/comet/updateFilter")
      }
    })
  }

}

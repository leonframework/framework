/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet
  
import com.google.inject.AbstractModule
import com.google.inject.servlet.ServletModule
import org.atmosphere.cpr.AtmosphereServlet
import io.leon.web.resources.ResourcesWebUserModule
import io.leon.resources.htmltagsprocessor.HtmlTagsProcessorUserModule

class CometFeatureModule extends AbstractModule {

  def configure() {
    bind(classOf[CometRegistry]).asEagerSingleton()
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
        install(new ResourcesWebUserModule {
          def configure() {
            exposeUrl("/leon/comet/connect")
            exposeUrl("/leon/comet/updateFilter")
          }
        })
      }
    })

    install(new HtmlTagsProcessorUserModule {
      def configure() {
        addTagRewriter(classOf[CometSubscribeTagRewriter])
      }
    })
  }

}

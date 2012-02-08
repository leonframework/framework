/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.browser

import com.google.inject.servlet.ServletModule
import io.leon.web.resources.ResourcesWebUserModule
import io.leon.resources.htmltagsprocessor.HtmlTagsProcessorUserModule

class BrowserWebFeatureModule extends ServletModule {

  override def configureServlets() {
    install(new HtmlTagsProcessorUserModule {
      def configure() {
        addTagRewriter(classOf[HtmlContextPathRewriter])
      }
    })

    VirtualLeonJsFileContribution.bind(binder(), classOf[ContextPathVirtualLeonJsFileContribution])

    bind(classOf[VirtualLeonJsFile]).asEagerSingleton()
    serve("/leon/leon.js").`with`(classOf[VirtualLeonJsFile])

    install(new ResourcesWebUserModule {
      def configure() {
        exposeUrl("/leon/leon.js")
      }
    })
  }

}



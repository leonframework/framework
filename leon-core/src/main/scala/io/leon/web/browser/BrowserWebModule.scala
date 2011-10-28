/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.browser

import io.leon.resources.htmltagsprocessor.LeonTagRewriters
import com.google.inject.servlet.ServletModule

class BrowserWebModule extends ServletModule {

  override def configureServlets() {
    LeonTagRewriters.bind(binder(), classOf[HtmlContextPathRewriter])

    VirtualLeonJsFileContribution.bind(binder(), classOf[ContextPathVirtualLeonJsFileContribution])

    bind(classOf[VirtualLeonJsFile]).asEagerSingleton()
    serve("/leon/browser/browser.js").`with`(classOf[VirtualLeonJsFile])
  }

}



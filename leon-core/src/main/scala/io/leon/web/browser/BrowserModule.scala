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
import io.leon.web.resources.WebResourcesBinder
import io.leon.web.htmltagsprocessor.HtmlTagsProcessorBinder

class BrowserModule extends ServletModule {

  override def configureServlets() {
    val htpb = new HtmlTagsProcessorBinder(binder())
    htpb.addTagRewriter(classOf[HtmlContextPathRewriter])

    val vljs = new VirtualLeonJsFileBinder(binder())
    vljs.bindAndAddContribution(classOf[ContextPathVirtualLeonJsFileContribution])

    vljs.bindAndAddContribution(classOf[DeploymentModeLeonJsContribution])

    bind(classOf[VirtualLeonJsFile]).asEagerSingleton()
    serve("/leon/leon.js").`with`(classOf[VirtualLeonJsFile])

    val rwb = new WebResourcesBinder(binder())
    rwb.exposeUrl("/leon/leon.js")
  }

}



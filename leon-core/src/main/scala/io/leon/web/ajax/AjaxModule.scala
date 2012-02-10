/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.ajax

import com.google.inject.servlet.ServletModule
import io.leon.web.resources.WebResourcesBinder
import io.leon.web.browser.VirtualLeonJsFileBinder

class AjaxModule extends ServletModule {

  override def configureServlets() {
    bind(classOf[AjaxCallServlet]).asEagerSingleton()
    serve("/leon/ajax").`with`(classOf[AjaxCallServlet])

    val rwb = new WebResourcesBinder(binder())
    rwb.exposeUrl("/leon/ajax")

    val vljs = new VirtualLeonJsFileBinder(binder())
    vljs.bindAndAddContribution(classOf[AjaxVirtualLeonJsFileContribution])
  }

}

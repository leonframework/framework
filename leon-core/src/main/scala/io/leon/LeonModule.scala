/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon

import com.google.inject.AbstractModule
import javascript.LeonJavaScriptModule
import resources.ResourcesModule
import web.ajax.AjaxWebModule
import web.browser.BrowserWebModule
import web.comet.CometWebModule
import web.resources.ResourcesWebModule

class LeonModule extends AbstractModule {

  def configure() {
    install(new ResourcesModule)
    install(new LeonJavaScriptModule)
    install(new AjaxWebModule)
    install(new CometWebModule)
    install(new ResourcesWebModule)
    install(new BrowserWebModule)
  }

}

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
import io.leon.web.browser.VirtualLeonJsFileBinder

class AjaxModule extends ServletModule {

  override def configureServlets() {
    val vljs = new VirtualLeonJsFileBinder(binder())
    vljs.bindAndAddContribution(classOf[AjaxVirtualLeonJsFileContribution])
  }

}

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.browser

import com.google.inject.{Scopes, Binder}
import io.leon.utils.GuiceUtils

class VirtualJsFileBinder(binder: Binder) {

  def bindAndAddContribution(contribution: Class[_ <: VirtualJsFileContribution]) {
    binder.bind(contribution).in(Scopes.SINGLETON)
    GuiceUtils.bindClassWithName(binder, classOf[VirtualJsFileContribution], contribution)
  }

}

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources.soy

import io.leon.web.browser.VirtualLeonJsFileContribution
import java.util.Map
import io.leon.resourceloading.{ResourceUtils, ResourceLoader}
import com.google.inject.Inject

class SoyLeonJsContribution @Inject()(resourceLoader: ResourceLoader) extends VirtualLeonJsFileContribution {

  private final val soyJsPath = "/" +
    getClass.getPackage.getName.replace('.', '/') +
    "/soyutils.js"

  def content(params: Map[String, String]) = {
    if (!(("false" == params.get("loadSoy")))) {
      val resource = resourceLoader.getResource(soyJsPath)
      ResourceUtils.inputStreamToString(resource.getInputStream())
    } else {
      ""
    }
  }

}

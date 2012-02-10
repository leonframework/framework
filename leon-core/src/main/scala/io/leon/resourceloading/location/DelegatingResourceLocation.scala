/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resourceloading.location

import java.net.URL
import io.leon.resourceloading.Resource

class DelegatingResourceLocation(loaderFn: (String) => URL) extends ResourceLocation {

  def getResource(fileName: String): Option[Resource] = {
    val r = loaderFn(fileName)
    if (r != null) {
      return Some(new Resource(fileName, () => r.openStream()))
    }
    None
  }

}

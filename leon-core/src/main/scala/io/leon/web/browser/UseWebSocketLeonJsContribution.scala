/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/*
 * Copyright (c) 2011 WeigleWilczek and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.browser

import java.util.Map
import io.leon.config.ConfigMap
import com.google.inject.Inject

class UseWebSocketLeonJsContribution @Inject()(configMap: ConfigMap) extends VirtualLeonJsFileContribution {

  def content(params: Map[String, String]) = {
    "getLeon().useWebSocket = " + configMap.useWebSocket() + ";\n"
  }

}

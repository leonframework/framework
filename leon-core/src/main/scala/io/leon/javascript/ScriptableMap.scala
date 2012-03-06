/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import collection.JavaConverters
import org.mozilla.javascript.{ScriptableObject, NativeObject}


object ScriptableMap {
  import scala.collection.JavaConverters._

  def apply(values: (String, Any)*) = new ScriptableMap(Map(values: _*).asJava)
}

class ScriptableMap(map: java.util.Map[String, _ <: Any]) extends NativeObject {
  import JavaConverters._

  map.asScala foreach { case(key, value) =>
    defineProperty(key, value, ScriptableObject.PERMANENT)
  }
}

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.gson

import org.mozilla.javascript.NativeJavaObject
import com.google.inject.{Injector, Inject}
import java.lang.reflect.Type
import com.google.gson.{JsonSerializationContext, Gson, JsonSerializer}

class NativeJavaObjectSerializer extends JsonSerializer[NativeJavaObject] {

  @Inject
  var injector: Injector = _

  @Inject
  var gson: Gson = _

  def serialize(src: NativeJavaObject, typeOfSrc: Type, context: JsonSerializationContext) = {
    gson.toJsonTree(src.unwrap())
  }

}

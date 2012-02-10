/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.gson

import java.lang.reflect.Type
import org.mozilla.javascript.NativeObject
import com.google.inject.{Inject, Injector}
import com.google.gson.{JsonSerializer, Gson, JsonSerializationContext, JsonObject}

class NativeObjectSerializer extends JsonSerializer[NativeObject] {

  @Inject
  var gson: Gson = _

  def serialize(src: NativeObject, typeOfSrc: Type, context: JsonSerializationContext) = {
    val map = new JsonObject
    val ids = src.getAllIds
    for (id <- ids) {
      val value = src.get(id.toString, src)
      map.add(id.toString, gson.toJsonTree(value))
    }
    map
  }

}

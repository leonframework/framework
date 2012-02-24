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
import com.google.inject.Inject
import com.google.gson.{JsonSerializer, Gson, JsonSerializationContext, JsonObject}
import org.mozilla.javascript.ScriptableObject

class ScriptableObjectSerializer extends JsonSerializer[ScriptableObject] {

  @Inject
  var gson: Gson = _

  def serialize(src: ScriptableObject, typeOfSrc: Type, context: JsonSerializationContext) = {
    val map = new JsonObject
    val ids = src.getAllIds
    for (id <- ids) {
      val value = src.get(id.toString, src)
      map.add(id.toString, gson.toJsonTree(value))
    }
    map
  }

}

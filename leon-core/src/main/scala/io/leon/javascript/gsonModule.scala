/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.javascript

import java.lang.reflect.Type
import com.google.gson._
import com.google.inject.{Inject, Injector, AbstractModule}
import org.mozilla.javascript.{NativeArray, NativeJavaObject, NativeObject}


class NativeObjectSerializer extends JsonSerializer[NativeObject] {

  @Inject
  var injector: Injector = _

  private def getGson() = injector.getInstance(classOf[Gson])

  def serialize(src: NativeObject, typeOfSrc: Type, context: JsonSerializationContext) = {
    val map = new JsonObject
    val ids = src.getAllIds
    for (id <- ids) {
      val value = src.get(id.toString, src)
      map.add(id.toString, getGson().toJsonTree(value))
    }
    map
  }

}

class NativeJavaObjectSerializer extends JsonSerializer[NativeJavaObject] {

  @Inject
  var injector: Injector = _

  private def getGson() = injector.getInstance(classOf[Gson])

  def serialize(src: NativeJavaObject, typeOfSrc: Type, context: JsonSerializationContext) = {
    getGson().toJsonTree(src.unwrap())
  }

}

class NativeArraySerializer extends JsonSerializer[NativeArray] {

  @Inject
  var injector: Injector = _

  private def getGson() = injector.getInstance(classOf[Gson])

  def serialize(src: NativeArray, typeOfSrc: Type, context: JsonSerializationContext) = {
    getGson().toJsonTree(src.toArray)
  }

}


class GsonModule extends AbstractModule {

  def configure() {
    val gsonBuilder = new GsonBuilder

    val nativeObjectSerializer = new NativeObjectSerializer
    requestInjection(nativeObjectSerializer)
    gsonBuilder.registerTypeAdapter(classOf[NativeObject], nativeObjectSerializer)

    val nativeJavaObjectSerializer = new NativeJavaObjectSerializer
    requestInjection(nativeJavaObjectSerializer)
    gsonBuilder.registerTypeAdapter(classOf[NativeJavaObject], nativeJavaObjectSerializer)

    val nativeArraySerializer = new NativeArraySerializer
    requestInjection(nativeArraySerializer)
    gsonBuilder.registerTypeAdapter(classOf[NativeArray], nativeArraySerializer)

    val gson = gsonBuilder.create()
    bind(classOf[Gson]).toInstance(gson)
  }

}
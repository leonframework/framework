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
package io.leon.gson

import com.google.gson._
import com.google.inject.AbstractModule
import org.mozilla.javascript.{NativeArray, NativeJavaObject, NativeObject}

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

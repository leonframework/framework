/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.comet

import com.google.inject.servlet.ServletModule
import org.atmosphere.cpr._
import io.leon.javascript.LeonScriptEngine
import com.google.inject.name.Named
import com.google.inject.{Injector, TypeLiteral, Inject, AbstractModule}
import io.leon.web.resources.ExposedUrl

class CometWebModule extends ServletModule {

  override def configureServlets() {
    import scala.collection.JavaConverters._
    install(new CometModule)

    val meteorParams = Map(
      "org.atmosphere.servlet" -> classOf[CometHandler].getName,
      AtmosphereServlet.WEBSOCKET_SUPPORT -> "false",
      AtmosphereServlet.PROPERTY_NATIVE_COMETSUPPORT -> "true"
      ).asJava

    serve("/leon/comet/connect*").`with`(classOf[CometServlet], meteorParams)
    ExposedUrl.bind(binder(), "/leon/comet/connect")
  }
}

class CometModule extends AbstractModule {
  def configure() {
    bind(classOf[CometInit]).asEagerSingleton()
    bind(classOf[CometRegistry]).asEagerSingleton()
    bind(classOf[CometHandler]).asEagerSingleton()
    bind(classOf[CometServlet]).asEagerSingleton()
  }
}

class CometInit @Inject()(injector: Injector, engine: LeonScriptEngine) {
  import scala.collection.JavaConverters._

  val browserObjects = injector.findBindingsByType(new TypeLiteral[BrowserObject]() {})

  browserObjects.asScala foreach { b =>
    val serverName = b.getKey.getAnnotation.asInstanceOf[Named].value()
    engine.eval("""leon.utils.createVar("browser");""")
    engine.eval("""leon.utils.createVar("browser.%s");""".format(serverName))
    engine.eval("browser.%s = leon.getBrowserObject(\"%s\");".format(serverName, serverName))
  }

}

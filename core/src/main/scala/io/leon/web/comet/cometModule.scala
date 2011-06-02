/*
 * Copyright 2011 Weigle Wilczek GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.leon.web.comet

import com.google.inject.AbstractModule
import com.google.inject.servlet.ServletModule
import org.atmosphere.cpr._

class CometWebModule extends ServletModule {

  override def configureServlets() {
    import scala.collection.JavaConverters._
    install(new CometModule)

    val meteorParams = Map(
      "org.atmosphere.servlet" -> classOf[CometHandler].getName,
      AtmosphereServlet.WEBSOCKET_SUPPORT -> "true",
      AtmosphereServlet.PROPERTY_NATIVE_COMETSUPPORT -> "true"
      ).asJava

    serve("/leon/comet*").`with`(classOf[CometServlet], meteorParams)
  }
}

class CometModule extends AbstractModule {
  def configure() {
    bind(classOf[CometRegistry]).asEagerSingleton()
    bind(classOf[CometHandler]).asEagerSingleton()
    bind(classOf[CometServlet]).asEagerSingleton()
  }
}



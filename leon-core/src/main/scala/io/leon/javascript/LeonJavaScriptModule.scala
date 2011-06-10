package io.leon.javascript

import com.google.inject.AbstractModule

class LeonJavaScriptModule extends AbstractModule {

  def configure() {
    bind(classOf[LeonScriptEngine]).asEagerSingleton()
  }

}

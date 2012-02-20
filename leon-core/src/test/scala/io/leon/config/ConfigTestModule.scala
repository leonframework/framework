package io.leon.config

import com.google.inject.AbstractModule

class ConfigTestModule extends AbstractModule {

  def configure() {
    val configBinder = new ConfigBinder(binder())

    configBinder.configValue("configTestModule.a", "a")
    configBinder.configValue("configTestModule.b", "b")
    configBinder.configValue("leon.applicationName", "ConfigTestModule")
  }
}

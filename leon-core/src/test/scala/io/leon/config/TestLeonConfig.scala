package io.leon.config

import io.leon.{DefaultWebAppGroupingModule, LeonAppMainModule}


class TestLeonConfig extends LeonAppMainModule {

  def config() {
    setApplicationName("ConfigTestApp")

    val defaultWebModule = new DefaultWebAppGroupingModule
    defaultWebModule.init()

    install(defaultWebModule)
    install(new ConfigTestModule)
  }

}

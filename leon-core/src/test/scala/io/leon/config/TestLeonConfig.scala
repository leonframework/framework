package io.leon.config

import io.leon.LeonAppMainModule


class TestLeonConfig extends LeonAppMainModule {

  def config() {
    setApplicationName("ConfigTestApp")

    install(new ConfigTestModule)
  }

}

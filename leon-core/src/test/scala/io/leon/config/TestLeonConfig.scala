package io.leon.config

import io.leon.AbstractLeonConfiguration


class TestLeonConfig extends AbstractLeonConfiguration {

  def config() {
    setApplicationName("ConfigTestApp")

    install(new ConfigTestModule)
  }

}

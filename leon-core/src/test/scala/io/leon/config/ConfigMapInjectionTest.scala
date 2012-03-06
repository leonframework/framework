package io.leon.config

import org.testng.annotations.{Guice, Test}
import com.google.inject.{Inject, Module}
import org.scalatest.Assertions
import io.leon.{DefaultWebAppGroupingModule, LeonAppMainModule}


@Test
@Guice(modules = Array[Class[_ <: Module]](
  classOf[TestLeonConfig],
  classOf[DefaultWebAppGroupingModule])
)
class ConfigMapInjectionTest @Inject()(configMap: ConfigMap) {
  import Assertions._

  def testInjectedConfigMap() {
    assert(configMap.getApplicationName === "ConfigTestApp")
    //assert(configMap.isDevelopmentMode)
    assert(configMap.get("configTestModule.a") === "a")
    assert(configMap.get("configTestModule.b") === "b")
  }

}

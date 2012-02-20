package io.leon.config

import org.scalatest.Assertions
import org.testng.annotations.{Guice, Test}
import com.google.inject.{Injector, Inject, Module}

@Test
@Guice(modules = Array[Class[_ <: Module]](classOf[ConfigTestModule]))
class ConfigMapTest @Inject()(injector: Injector) {
  import Assertions._

  private val configReader = new ConfigReader()

  def readEnvironment() {
    val appName = "ConfigMapTest"

    val env = new java.util.Hashtable[String, String]()
    env.put("LEON_APPLICATION_NAME", appName)
    env.put("LEON_DEPLOYMENT_MODE", "development")

    val configMap = configReader.readEnvironment(env)

    assert(configMap.getApplicationName === appName)
    assert(configMap.isDevelopmentMode)
  }

  def readProperties() {
    val appName = "ConfigMapBuilderTestFromProperties"

    val configMap = configReader.readProperties()

    assert(configMap.getApplicationName === appName)
    assert(configMap.isDevelopmentMode)
  }

  def environmentOverridesProperties() {
    val appName = "ConfigMapTest"

    val env = new java.util.Hashtable[String, String]()
    env.put("LEON_APPLICATION_NAME", appName)
    env.put("LEON_DEPLOYMENT_MODE", "DEVELOPMENT")

    val configMap = configReader.readProperties()
    configMap.putAll(configReader.readEnvironment(env))

    assert(configMap.getApplicationName === appName)
    assert(configMap.isDevelopmentMode)
  }

  def defaultDeploymentModeIsProduction() {
    val configMap = new ConfigMap()

    assert(configMap.isProductionMode)
  }

  def readModuleParameters() {
    val configMap = configReader.readModuleParameters(injector)

    assert(configMap.get("configTestModule.a") === "a")
    assert(configMap.get("configTestModule.b") === "b")
  }

  def importConfigMap() {
    val configMap = configReader.readProperties()
    configMap.importConfigMap(configReader.readModuleParameters(injector))

    // check that applicationName was not taken from module configuration
    assert(configMap.getApplicationName === "ConfigMapBuilderTestFromProperties")

    assert(configMap.get("configTestModule.a") === "a")
    assert(configMap.get("configTestModule.b") === "b")
  }
}

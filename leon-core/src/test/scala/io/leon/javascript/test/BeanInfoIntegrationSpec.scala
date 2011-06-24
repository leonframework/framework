package io.leon.javascript.test

import org.specs2.mutable.Specification
import io.leon.resources.ResourcesModule
import com.google.inject.{Inject, Guice, AbstractModule}
import io.leon.javascript.{JavaScriptProxy, LeonScriptEngine, LeonJavaScriptModule}

class BeanInfoIntegrationSpec extends Specification {

  private val module = new AbstractModule {
    def configure() {
      install(new ResourcesModule)
      install(new LeonJavaScriptModule)
      bind(classOf[TestObjectModuleInit]).asEagerSingleton()
    }
  }

  private val injector = Guice.createInjector(module)

  private def getLeonScriptEngine = {
    injector.getInstance(classOf[LeonScriptEngine])
  }

  "A case class annotated with @BeanInfo" should {

    "be mapped to a ScriptableObject" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.testTestServiceGetTestBean")
      success
    }
  }
}

class TestObjectModuleInit @Inject()(engine: LeonScriptEngine) {
  engine.put("TestService", JavaScriptProxy(new TestServiceImpl))
  engine.loadResource("/io/leon/javascript/test/testObjects.js")
}

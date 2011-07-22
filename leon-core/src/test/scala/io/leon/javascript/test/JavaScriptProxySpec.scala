package io.leon.javascript.test

import org.specs2.mutable.Specification
import io.leon.resources.ResourcesModule
import com.google.inject.{Inject, Guice, AbstractModule}
import io.leon.javascript.{LeonScriptEngine, LeonJavaScriptModule}

class JavaScriptProxySpec extends Specification {

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

  "A JavaScriptProxy" should {

    "map return values to JavaScript objects" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.testGetBean")
      success
    }

    "automatically convert JavaScript objects to the corresponding type in method calls" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.testSetBean")
      success
    }

    "call a method with default args" in {
//      val engine = getLeonScriptEngine
//      engine.invokeFunction("io.leon.javascript.test.Tests.testMethodWithDefaultArgs")
//      success
      pending
    }

    "call a method with numeric arguments" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.testMethodWithNumericArgs")
      success
    }

    "call a method with a string argument" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.testMethodWithString")
      success
    }

    "call a method with a list argument" in {
//      val engine = getLeonScriptEngine
//      engine.invokeFunction("io.leon.javascript.test.Tests.testMethodWithList")
//      success
      pending
    }

    "call a method with an Int list argument" in {
//      val engine = getLeonScriptEngine
//      engine.invokeFunction("io.leon.javascript.test.Tests.testMethodWithIntList")
//      success
      pending
    }

    "call a method with its original java types" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.testMethodWithJavaType")
      success
    }

    "invoke a method by calling method.apply(...)" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.testApplyMethodCall")
      success
    }
  }
}

class TestObjectModuleInit @Inject()(engine: LeonScriptEngine) {
  engine.put("TestService", new TestService)
  engine.loadResource("/io/leon/javascript/test/testObjects.js")
}

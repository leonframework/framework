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

    "automatically convert JavaScript objects to the corresponding 'case class' in method calls" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.testSetBean")
      success
    }

    "support method calls with numeric arguments" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.testMethodWithNumericArgs")
      success
    }

    "support method calls with string arguments" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.testMethodWithString")
      success
    }

    "support method calls with java types" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.testMethodWithJavaType")
      success
    }

    "invoke a method by calling method.apply(...)" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.testApplyMethodCall")
      success
    }

    "support method calls with Plain Old Java Object" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.methodWithJavaTestBean")
      success
    }

    "suport method calls with java Lists" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.methodWithJavaList")
      success
    }

    "suport method calls with scala seq" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.methodWithSeq")
      success
    }

    "support method calls with arrays" in {
      val engine = getLeonScriptEngine
      engine.invokeFunction("io.leon.javascript.test.Tests.methodWithArray")
      success
    }
  }
}

class TestObjectModuleInit @Inject()(engine: LeonScriptEngine) {
  engine.put("TestService", new TestService)
  engine.loadResource("/io/leon/javascript/test/testObjects.js")
}

package io.leon.javascript

import org.specs2.mutable.Specification
import com.google.inject.{Guice, AbstractModule}
import io.leon.resources.ResourcesModule
import org.mozilla.javascript.NativeJavaObject


class GsonSupportSpec extends Specification {

  private val module = new AbstractModule {
    def configure() {
      install(new LeonJavaScriptModule)
      install(new GsonModule)
      install(new ResourcesModule)
    }
  }

  private val injector = Guice.createInjector(module)

  private val leonScriptEngine = injector.getInstance(classOf[LeonScriptEngine])

  "Gson Rhino adapters" should {

    "serialize Rhino's JavaScript types to JSON" in {
      val js = """
        var string = "string";
        var number = 123.4;
        var array = [1, 2, 3, string, number];
        var data = { a: string, b: number, c: array };
        leon.getGson().toJson(data);
        """

      val result = leonScriptEngine.eval(js).asInstanceOf[NativeJavaObject].unwrap()
      val expected = """{"a":"string","b":123.4,"c":[1.0,2,3,"string",123.4]}"""
      result must_== expected
    }

    "serialize Java types used in a JavaScript context to JSON" in {
      val js = """
        var s = Packages.java.lang.String.valueOf(3.0);
        leon.getGson().toJson(s);
        """
      val result = leonScriptEngine.eval(js).asInstanceOf[NativeJavaObject].unwrap()
      val expected = "\"3.0\""
      result must_== expected
    }

  }

}

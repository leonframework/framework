package io.leon.javascript

import org.specs2.mutable.Specification
import com.google.inject.{Guice, AbstractModule}
import io.leon.resources.ResourcesModule
import org.mozilla.javascript.NativeJavaObject
import com.google.gson.JsonParser


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

  "Gson parsers" can {

    "be used to parse arguments for a JavaScript function call" in {
      val jp = new JsonParser
      val text = jp.parse("\"foo\"") // string
      val arg1 = jp.parse("1") // int
      val arg2 = jp.parse("1.2") // double
      val arg3 = jp.parse(""" {
          a: 1,
          b: 1.2,
          c: "bar"
        }
        """) // object

      val js = """
        (function(){
          var testfn = function(text, arg1, arg2, arg3) {
            return text + arg3.c + ":" + (arg1 + arg2 + arg3.a + arg3.b);
          };
          return testfn(%s, %s, %s, %s);
        })();
        """.format(text, arg1, arg2, arg3)

      val result = leonScriptEngine.eval(js)
      val expected = "foobar:4.4"
      result must_== expected

      success
    }

  }

}

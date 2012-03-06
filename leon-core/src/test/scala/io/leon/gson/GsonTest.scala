package io.leon.gson

import org.testng.annotations.{Guice, Test}
import com.google.inject.{Inject, Module}
import org.scalatest.Assertions
import com.google.gson.{Gson, JsonParser}
import io.leon.javascript.{ScriptableMap, LeonScriptEngine}
import org.mozilla.javascript.{NativeArray, NativeJavaObject}

@Guice(modules = Array[Class[_ <: Module]](classOf[GsonTestModule]))
class GsonTest @Inject()(leonScriptEngine: LeonScriptEngine, gson: Gson) extends Assertions {

  @Test def gsonRhinoAdapterShouldSerializeRhinoJavaScriptTypesToJson() {
    val js = """
      var string = "string";
      var number = 123.4;
      var array = [1, 2, 3, string, number];
      var data = { a: string, b: number, c: array };
      leon.getGson().toJson(data);
      """

    val result = leonScriptEngine.eval(js).asInstanceOf[NativeJavaObject].unwrap()
    val expected = """{"a":"string","b":123.4,"c":[1.0,2,3,"string",123.4]}"""
    assert(result === expected)
  }

  @Test def gsonRhinoAdapterShouldSerializeJavaTypesUsedInJavaScriptContextToJson() {
    val js = """
      var s = Packages.java.lang.String.valueOf(3.0);
      leon.getGson().toJson(s);
      """
    val result = leonScriptEngine.eval(js).asInstanceOf[NativeJavaObject].unwrap()
    val expected = "\"3.0\""
    assert(result === expected)
  }

  @Test def gsonParsersCanBeUsedToParseArgumentsForAJavaScriptFunctionCall() {
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
    assert(result === expected)
  }

  @Test def gsonParserShouldSerializeScriptableMaps() {
    import scala.collection.JavaConverters._

    val map = Map(
      "a" -> "string",
      "b" -> 123,
      "c" -> new ScriptableMap(Map("ca" -> "test").asJava)
    ).asJava

    val scriptableMap = new ScriptableMap(map)

    val jsonTree = gson.toJsonTree(scriptableMap)

    val expected = """{"a":"string","b":123,"c":{"ca":"test"}}"""
    val result = jsonTree.toString

    assert(result === expected)
  }

  @Test def gsonParserShouldSerializeNativeArraysContainingScriptableMaps() {
    import scala.collection.JavaConverters._

    val scriptableMap = new ScriptableMap(Map("a" -> "string", "b" -> 123).asJava)
    val array = new NativeArray(Array[AnyRef](scriptableMap))

    val jsonTree = gson.toJsonTree(array)

    val expected = """[{"a":"string","b":123}]"""
    val result = jsonTree.toString

    assert(result === expected)
  }
}

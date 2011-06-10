package io.leon.javascript

object RhinoUtils {

  def json2RhinoObject(engine: LeonScriptEngine, string: String): AnyRef = {
    val invocable = engine.asInvocable
    val json = engine.get("JSON")
    invocable.invokeMethod(json, "parse", string)
  }

  def rhinoObject2Json(engine: LeonScriptEngine, obj: AnyRef): String = {
    val invocable = engine.asInvocable
    val json = engine.get("JSON")
    invocable.invokeMethod(json, "stringify", obj).asInstanceOf[String]
  }

}





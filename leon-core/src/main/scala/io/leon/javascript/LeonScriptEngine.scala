package io.leon.javascript

import java.io.InputStreamReader
import io.leon.resources.ResourceLoader
import javax.script.{Invocable, ScriptEngineManager}
import com.google.inject.{Injector, Inject}

class LeonScriptEngine @Inject()(injector: Injector, resourceLoader: ResourceLoader) {

  //private val logger = Logger.getLogger(getClass.getName)

  val scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript")

  put("injector", injector)

  loadResource("/io/leon/json2.js")
  loadResource("/io/leon/leon.js")
  loadResource("/leon/leon_shared.js")

  def asInvocable = {
    scriptEngine.asInstanceOf[Invocable]
  }

 def loadResource(fileName: String) {
    val resource = resourceLoader.getInputStream(fileName)
    scriptEngine.eval(new InputStreamReader(resource))
  }

  def loadResources(fileNames: List[String]) {
    fileNames foreach loadResource
  }

  def eval(script: String): AnyRef = {
    scriptEngine.eval(script)
  }

  def put(key: String, value: Any) {
    scriptEngine.put(key, value)
  }

  def get(key: String) = {
    scriptEngine.get(key)
  }

}


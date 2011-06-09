package io.leon.javascript

import java.io.InputStreamReader
import javax.script.{Invocable, ScriptEngineManager}
import io.leon.resources.ResourceLoader
import com.google.inject.{Inject, AbstractModule}

class LeonScriptEngine @Inject()(resourceLoader: ResourceLoader) {

  //private val logger = Logger.getLogger(getClass.getName)

  private val scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript")

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

class LeonJavaScriptModule extends AbstractModule {

  def configure() {
    bind(classOf[LeonScriptEngine]).asEagerSingleton()
  }

}

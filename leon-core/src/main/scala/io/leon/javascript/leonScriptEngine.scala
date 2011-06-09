package io.leon.javascript

import com.google.inject.AbstractModule
import java.io.InputStreamReader
import java.util.logging.Logger
import javax.script.{Invocable, ScriptEngine, ScriptEngineManager}

class LeonScriptEngine(scriptEngine: ScriptEngine) {

  private val logger = Logger.getLogger(getClass.getName)

  def asInvocable = {
    scriptEngine.asInstanceOf[Invocable]
  }

  def loadResource(fileName: String) {
    logger.info("Loading JavaScript file [%s]".format(fileName))
    try {
      val jsFile = getClass.getClassLoader.getResourceAsStream(fileName)
      scriptEngine.eval(new InputStreamReader(jsFile))
    } catch {
      case e: Throwable => throw new RuntimeException("Can not load JavaScript file [%s]".format(fileName), e)
    }
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

  private val scriptEngine = new LeonScriptEngine(new ScriptEngineManager().getEngineByName("JavaScript"))
  scriptEngine.loadResource("/io/leon/json2.js")
  scriptEngine.loadResource("/io/leon/leon.js")
  scriptEngine.loadResource("/leon/shared/utils.js")

  def configure() {
    bind(classOf[LeonScriptEngine]).toInstance(scriptEngine)
  }

}

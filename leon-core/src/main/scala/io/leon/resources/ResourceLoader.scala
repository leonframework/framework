package io.leon.resources

import java.io.InputStream
import com.google.inject._
import name.Names
import java.lang.RuntimeException

class ResourceLoaderModule extends AbstractModule {

  private def addLocation(clazz: Class[_ <: ResourceLocation]) {
    bind(classOf[ResourceLocation]).annotatedWith(Names.named(clazz.getName)).to(clazz).asEagerSingleton()
  }
  
  def configure() {
    bind(classOf[ResourceLoader]).asEagerSingleton()
    addLocation(classOf[ClassLoaderResourceLocation])
  }
}

trait ResourceLocation {
  def getInputStreamOption(fileName: String): Option[InputStream]
}

class ClassLoaderResourceLocation extends ResourceLocation {
  def getInputStreamOption(fileName: String): Option[InputStream] = {
    Option(getClass.getResourceAsStream(fileName))
  }
}

class ResourceLoader @Inject()(injector: Injector) {
  import scala.collection.JavaConverters._

  val resourceLocations = injector.findBindingsByType(new TypeLiteral[ResourceLocation]() {}).asScala

  def getInputStream(fileName: String): InputStream = {
    getInputStreamOption(fileName) match {
      case Some(resource) => resource
      case None => throw new RuntimeException("Resource [%s] not found!".format(fileName))
    }
  }

  def getInputStreamOption(fileName: String): Option[InputStream] = {
    for (rl <- resourceLocations) {
      rl.getProvider.get().getInputStreamOption(fileName) match {
        case Some(r) => return Some(r)
        case None => None
      }
    }
    None
  }

}

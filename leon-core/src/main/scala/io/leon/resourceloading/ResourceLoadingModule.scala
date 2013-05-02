package io.leon.resourceloading

import com.google.inject.{Scopes, AbstractModule}

class ResourceLoadingModule extends AbstractModule {

  def configure() {
    bind(classOf[ResourceLoader]).in(Scopes.SINGLETON)
  }

}







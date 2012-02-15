package io.leon.gson

import com.google.inject.AbstractModule
import io.leon.javascript.LeonJavaScriptModule
import io.leon.resourceloading.ResourceLoadingModule

class GsonTestModule extends AbstractModule {

  def configure() {
    install(new LeonJavaScriptModule)
    install(new GsonModule)
    install(new ResourceLoadingModule)
  }

}

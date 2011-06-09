package io.leon

import com.google.inject.AbstractModule
import javascript.LeonJavaScriptModule
import resources.ResourceLoaderModule
import web.ajax.AjaxWebModule
import web.comet.CometWebModule
import web.resources.ResourcesWebModule

class LeonModule extends AbstractModule {

  def configure() {
    install(new ResourceLoaderModule)
    install(new LeonJavaScriptModule)
    install(new AjaxWebModule)
    install(new CometWebModule)
    install(new ResourcesWebModule)
  }

}

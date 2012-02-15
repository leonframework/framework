package io.leon.persistence.mongo.test

import io.leon.persistence.mongo.LeonMongoModule
import io.leon.resourceloading.ResourceLoadingModule
import com.google.inject.AbstractModule
import io.leon.javascript.LeonJavaScriptModule


class MongoTestModule extends AbstractModule {

  def configure() {
    install(new LeonJavaScriptModule)
    install(new ResourceLoadingModule)
    install(new LeonMongoModule)
  }
}

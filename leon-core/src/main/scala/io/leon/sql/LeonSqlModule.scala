
package io.leon.sql

import com.google.inject.AbstractModule

class LeonSqlModule(config: LeonSqlConfig) extends AbstractModule {
  def configure() {
    bind(classOf[LeonSqlManager]).toProvider(new LeonSqlManagerFactory(config))
  }
}

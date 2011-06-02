
package io.leon.sql

import com.google.inject._
import java.sql.Connection
import org.yaml.snakeyaml.Yaml
import java.util.logging.Logger
import collection.mutable.Map

class LeonSqlConfig {
  var configFilePath: String = _
  var connectionFactory: () => Connection = _
  var autoCommit = false
}

class LeonSqlManagerFactory(config: LeonSqlConfig) extends Provider[LeonSqlManager] {

  import scala.collection.JavaConverters._

  type RawJavaMap = java.util.Map[_, _]

  type StringKeyJavaMap = java.util.Map[String, _]

  private val logger = Logger.getLogger(getClass.getName)

  val yaml = createConfigMap()

  def get() = {
    val connection = config.connectionFactory()
    connection.setAutoCommit(config.autoCommit)
    val tables = yaml filterKeys { _.startsWith("table ") } map { case (k, v) => extractTable(k, v) }
    new LeonSqlManager(connection, tables.toList)
  }

  private def createConfigMap(): Map[String, _] = {
    logger.info("Reading YAML configuration from file [%s]" format config.configFilePath)
    new Yaml().load(getClass.getResourceAsStream(
      config.configFilePath)).asInstanceOf[StringKeyJavaMap].asScala
  }

  private def extractTable(nameRaw: String, configRaw: Any): Table = {
    val name = nameRaw.substring(7)
    require(configRaw.isInstanceOf[RawJavaMap], "Table configuration must be a map")
    val config = configRaw.asInstanceOf[StringKeyJavaMap].asScala

    val sqlName = config.getOrElse("sqlName", name.toUpperCase).asInstanceOf[String]
    val columns = config filterKeys { _.startsWith(":") } map { case (k, v) => extractColumn(name, k, v) }
    val primaryKey = config("primaryKey").asInstanceOf[String].substring(1)
    Table(name, sqlName, primaryKey, columns.toList)
  }

  private def extractColumn(tableName: String, nameRaw: String, configRaw: Any): Column = {
    val name = nameRaw.substring(1)
    configRaw match {
      case ddl: String => Column(tableName, name, name.toUpperCase, ddl)
    }
  }

}

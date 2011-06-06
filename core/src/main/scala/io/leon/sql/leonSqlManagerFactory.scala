
package io.leon.sql

import com.google.inject._
import java.sql.Connection
import org.yaml.snakeyaml.Yaml
import java.util.logging.Logger

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
    val schemaName = extractSchemaName(yaml)
    val manager = new LeonSqlManager(connection, schemaName)
    val tables = yaml filterKeys { _.startsWith("table ") } map {
      case (k, v) => extractTable(manager, k, v)
    }
    manager.tables = tables
    manager 
  }

  private def createConfigMap(): Map[String, _] = {
    logger.info("Reading YAML configuration from file [%s]" format config.configFilePath)
    new Yaml().load(getClass.getResourceAsStream(
      config.configFilePath)).asInstanceOf[StringKeyJavaMap].asScala.toMap
  }

  private def extractSchemaName(yaml: Map[String, _]): Option[String] = {
    yaml.get("schema").asInstanceOf[Option[String]] map { s => s.toUpperCase }
  }

  private def extractTable(manager: LeonSqlManager, nameRaw: String, configRaw: Any): (String, Table) = {
    val name = nameRaw.substring(7)
    require(configRaw.isInstanceOf[RawJavaMap], "Table configuration must be a map")
    val config = configRaw.asInstanceOf[StringKeyJavaMap].asScala

    val sqlName = config.getOrElse("sqlName", name.toUpperCase).asInstanceOf[String]
    val columns = config filterKeys { _.startsWith(":") } map { case (k, v) => extractColumn(name, k, v) }
    val primaryKey = config("primaryKey").asInstanceOf[String].substring(1)

    name -> Table(manager, name, sqlName, primaryKey, columns.toList)
  }

  private def extractColumn(tableName: String, nameRaw: String, configRaw: Any): Column = {
    val name = nameRaw.substring(1)
    configRaw match {
      case ddl: String => Column(tableName, name, name.toUpperCase, ddl)
    }
  }

}


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

  // Configuration values

  private lazy val yaml = {
    logger.info("Reading YAML configuration from file [%s]" format config.configFilePath)
    new Yaml().load(getClass.getResourceAsStream(
      config.configFilePath)).asInstanceOf[StringKeyJavaMap].asScala.toMap
  }

  private lazy val schemaName =
    yaml.get("schema").asInstanceOf[Option[String]] map { s => s.toUpperCase }

  private lazy val tableDefs = {
    yaml filterKeys { _.startsWith("table ") } map {
      case (k, v) => extractTable(k, v)
    }
  }

  // Util methods

  private def extractTable(nameRaw: String, configRaw: Any): (String, TableDef) = {
    val name = nameRaw.substring(7)
    require(configRaw.isInstanceOf[RawJavaMap], "TableDef configuration must be a map")
    val config = configRaw.asInstanceOf[StringKeyJavaMap].asScala

    val sqlName = config.getOrElse("sqlName", name.toUpperCase).asInstanceOf[String]
    val columns = config filterKeys { _.startsWith(":") } map { case (k, v) => extractColumn(name, k, v) }
    val primaryKey = config("primaryKey").asInstanceOf[String].substring(1)

    name -> TableDef(name, schemaName, sqlName, primaryKey, columns.toMap)
  }

  private def extractColumn(tableName: String, nameRaw: String, configRaw: Any): (String, ColumnDef) = {
    val name = nameRaw.substring(1)
    configRaw match {
      case ddl: String => name -> ColumnDef(tableName, name, name.toUpperCase, ddl)
    }
  }

  private def createConnection(): Connection = {
    val connection = config.connectionFactory()
    connection.setAutoCommit(config.autoCommit)
    connection
  }

  // Provider interface

  def get() = {
    new LeonSqlManager(createConnection(), schemaName, tableDefs)
  }



}


package io.leon.sql

import java.sql.Connection
import java.util.logging.Logger

case class Column(tableName: String, name: String, sqlName: String, ddl: String)

case class Table(name: String, sqlName: String, primaryKey: String, columns: List[Column]) {

  private val logger = Logger.getLogger(getClass.getName)

  lazy val createTableDdl: String = {
    val start = "CREATE TABLE %s (".format(sqlName)
    val bodyColumns = columns map { c => "%s %s".format(c.sqlName, c.ddl) }
    val bodyPKey = "PRIMARY KEY (%s)".format(primaryKey) :: Nil
    val body = (bodyColumns ::: bodyPKey).mkString(",")
    val end = ");"
    start + body + end
  }

  lazy val dropTableDdl: String = {
    "DROP TABLE IF EXISTS %s;".format(sqlName) // TODO: check if IF EXITS is offical SQL
  }

  def insert(data: Map[String, Any]*) {
    data foreach { d =>
      logger.info("Insert [%s] into table [%s]".format(d, name))

      val withSqlNames = columns filter { c =>
        d.contains(c.name)
      } map { c =>
        (c.sqlName, d(c.name))
      }
      val (columnSqlNames, values) = withSqlNames.unzip

      println(columnSqlNames)
      println(values)


    }
  }

}

class LeonSqlManager(connection: Connection,
                      val tables: List[Table]) {

  def close() {
    connection.close()
  }

  def commit() {
    connection.commit()
  }

  def executeDropDdl() {
    tables foreach { t =>
      val stmt = connection.createStatement()
      stmt.execute(t.dropTableDdl)
    }
    commit()
  }

  def executeCreateDdl() {
    tables foreach { t =>
      val stmt = connection.createStatement()
      stmt.execute(t.createTableDdl)
    }
    commit()
  }

  def executeDropCreateDdl() {
    executeDropDdl()
    executeCreateDdl()
  }

}

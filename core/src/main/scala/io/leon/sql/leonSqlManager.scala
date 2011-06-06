
package io.leon.sql

import java.sql.Connection
import java.util.logging.Logger

case class Column(tableName: String, name: String, sqlName: String, ddl: String)

case class Table(manager: LeonSqlManager, name: String, _sqlName: String, primaryKey: String, columns: List[Column]) {

  private val logger = Logger.getLogger(getClass.getName)

  private lazy val sqlName = manager.schemaName match {
    case None => _sqlName
    case Some(s) => s + "." + _sqlName
  }

  lazy val createTableDdl: String = {
    val start = "CREATE TABLE %s (".format(sqlName)
    val bodyColumns = columns map { c => "%s %s".format(c.sqlName, c.ddl) }
    val bodyPKey = "PRIMARY KEY (%s)".format(primaryKey) :: Nil
    val body = (bodyColumns ::: bodyPKey).mkString(",")
    val end = ");"
    start + body + end
  }

  lazy val dropTableDdl: String = {
    "DROP TABLE %s;".format(sqlName)
  }

  def insert(data: Map[String, Any]*) {
    data foreach { d =>
      val withSqlNames = columns filter { c =>
        d.contains(c.name)
      } map { c =>
        (c.sqlName, d(c.name))
      }
      val (columnSqlNames, values) = withSqlNames.unzip

      println(columnSqlNames)
      println(values)

      val sql = "INSERT INTO %s (%s) VALUES (%s)".format(
        sqlName,
        columnSqlNames.mkString(","),
        "'" + values.mkString("','") + "'"
      )
      manager.executeRawSql(sql)
    }
  }

}

class LeonSqlManager(connection: Connection,
                     val schemaName: Option[String]) {

  private val logger = Logger.getLogger(getClass.getName)

  var tables: Map[String, Table] = _

  def table(name: String) = tables(name)

  def close() {
    connection.close()
  }

  def rollback() {
    connection.rollback()
  }

  def commit() {
    connection.commit()
  }

  def executeDropSchema() {
    schemaName foreach { s => executeRawSql("DROP SCHEMA %s;".format(s)) }
  }

  def executeDropDdl() {
    tables.values foreach { t =>
      executeRawSql(t.dropTableDdl)
    }
  }

  def executeCreateSchema() {
    schemaName foreach { s => executeRawSql("CREATE SCHEMA %s;".format(s)) }
  }

  def executeCreateDdl() {
    tables.values foreach { t =>
      executeRawSql(t.createTableDdl)
    }
  }

  def executeDropCreateDdl() {
    executeDropDdl()
    executeCreateDdl()
  }

  def executeRawSql(sql: String): Boolean = {
    logger.info("Executing SQL: " + sql)
    val stmt = connection.createStatement()
    stmt.execute(sql)
  }

}

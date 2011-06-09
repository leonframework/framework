
package io.leon.sql

import java.util.logging.Logger
import java.lang.IllegalStateException
import java.sql.{SQLException, ResultSet, Connection}

case class ColumnDef(tableName: String, name: String, sqlName: String, ddl: String)

case class TableDef(name: String,
                    schema: Option[String],
                    sqlName: String,
                    primaryKey: String,
                    columns: Map[String, ColumnDef]) {

  lazy val sqlFullName = schema match {
    case None => sqlName
    case Some(s) => s + "." + sqlName
  }

  lazy val createTableDdl: String = {
    val start = "CREATE TABLE %s (".format(sqlFullName)
    val bodyColumns = columns.values map { c => "%s %s".format(c.sqlName, c.ddl) }
    val bodyPKey = "PRIMARY KEY (%s)".format(primaryKey) :: Nil
    val body = (bodyColumns.toList ::: bodyPKey).mkString(",")
    val end = ");"
    start + body + end
  }

  lazy val dropTableDdl: String = {
    "DROP TABLE %s;".format(sqlFullName)
  }

  def insertInto(columns: List[String], values: List[Any]): String = {
    "INSERT INTO %s (%s) VALUES (%s)".format(
      sqlFullName,
      columns.mkString(","),
      "'" + values.mkString("','") + "'"
    )
  }

  def count: String = {
    "SELECT count(*) FROM %s;".format(sqlFullName)
  }

}

class Table(val manager: LeonSqlManager, val tableDef: TableDef) {

  private val logger = Logger.getLogger(getClass.getName)

  def insert(data: Map[String, Any]*): List[Int] = {
    val stmt = manager.connection.createStatement()
    data foreach { d =>
      val withSqlNames = d map { case (k, v) =>
        tableDef.columns(k).sqlName -> v
      }

      val (columns, values) = withSqlNames.unzip
      val sql = tableDef.insertInto(columns.toList, values.toList)
      logger.info("Adding BATCH SQL: " + sql)
      stmt.addBatch(sql)
    }
    stmt.executeBatch().toList
  }

  def insert(data: List[Map[String, Any]]): List[Int] = insert(data: _*)

  def size: Int = {
    manager.executeSql(tableDef.count) match {
      case ResultSetResult(rs) => {
        rs.next()
        rs.getInt(1)
      }
      case _ => throw new IllegalStateException
    }
  }

}


sealed class StatementResult
case class ResultSetResult(resultSet: ResultSet) extends StatementResult
case class CountResult(count: Int) extends StatementResult
case object NoResult extends  StatementResult

class LeonSqlManager(val connection: Connection,
                     schemaName: Option[String],
                     tableDefs: Map[String, TableDef]) {

  private val logger = Logger.getLogger(getClass.getName)

  private lazy val tables = tableDefs map { case (name, tableDef) =>
    name -> new Table(this, tableDef)
  }

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
    schemaName foreach { s => executeSql("DROP SCHEMA %s;".format(s)) }
  }

  def executeDropDdl() {
    tables.values foreach { t =>
      executeSql(t.tableDef.dropTableDdl)
    }
  }

  def executeCreateSchema() {
    schemaName foreach { s => executeSql("CREATE SCHEMA %s;".format(s)) }
  }

  def executeCreateDdl() {
    tables.values foreach { t =>
      executeSql(t.tableDef.createTableDdl)
    }
  }

  def executeSql(sql: String): StatementResult = {
    logger.info("Executing SQL: " + sql)
    try {
      val stmt = connection.createStatement()
      stmt.execute(sql) match {
        case true => ResultSetResult(stmt.getResultSet)
        case false => stmt.getUpdateCount match {
          case -1 => NoResult
          case i => CountResult(i)
        }
      }
    } catch {
      case e: SQLException => throw e  // TODO log error
    }
  }

}

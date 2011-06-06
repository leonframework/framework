
package io.leon.sql

import java.util.logging.Logger
import java.sql.{ResultSet, Connection}
import java.lang.IllegalStateException

case class ColumnDef(tableName: String, name: String, sqlName: String, ddl: String)

case class TableDef(name: String,
                    schema: Option[String],
                    sqlName: String,
                    primaryKey: String,
                    columns: List[ColumnDef]) {

  lazy val sqlFullName = schema match {
    case None => sqlName
    case Some(s) => s + "." + sqlName
  }

}

class Table(val manager: LeonSqlManager, val tableDef: TableDef) {

  lazy val createTableDdl: String = {
    val start = "CREATE TABLE %s (".format(tableDef.sqlFullName)
    val bodyColumns = tableDef.columns map { c => "%s %s".format(c.sqlName, c.ddl) }
    val bodyPKey = "PRIMARY KEY (%s)".format(tableDef.primaryKey) :: Nil
    val body = (bodyColumns ::: bodyPKey).mkString(",")
    val end = ");"
    start + body + end
  }

  lazy val dropTableDdl: String = {
    "DROP TABLE %s;".format(tableDef.sqlFullName)
  }

  def insert(data: Map[String, Any]*): List[Int] = {
    val stmt = manager.connection.createStatement()
    data foreach { d =>
      val withSqlNames = tableDef.columns filter { c =>
        d.contains(c.name)
      } map { c =>
        (c.sqlName, d(c.name))
      }
      val (columnSqlNames, values) = withSqlNames.unzip

      val sql = "INSERT INTO %s (%s) VALUES (%s)".format(
        tableDef.sqlFullName,
        columnSqlNames.mkString(","),
        "'" + values.mkString("','") + "'"
      )
      stmt.addBatch(sql)
    }
    stmt.executeBatch().toList
  }

  def size: Int = {
    manager.executeSql("SELECT count(*) FROM %s;".format(tableDef.sqlFullName)) match {
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
      executeSql(t.dropTableDdl)
    }
  }

  def executeCreateSchema() {
    schemaName foreach { s => executeSql("CREATE SCHEMA %s;".format(s)) }
  }

  def executeCreateDdl() {
    tables.values foreach { t =>
      executeSql(t.createTableDdl)
    }
  }

  def executeSql(sql: String): StatementResult = {
    logger.info("Executing SQL: " + sql)
    val stmt = connection.createStatement()
    stmt.execute(sql) match {
      case true => ResultSetResult(stmt.getResultSet)
      case false => stmt.getUpdateCount match {
        case -1 => NoResult
        case i => CountResult(i)
      }
    }
  }

}

package sorm.driver

import sorm._, ddl._, jdbc._
import sext._
import org.joda.time.DateTime
import sql.Sql.Sql

class Hsqldb (protected val connection : JdbcConnection)
  extends DriverConnection
  with StdQuery
  with StdSqlRendering
  with StdDropAllTables
  with StdAbstractSqlToSql
  with StdNow
  with StdModify
  with StdDropTables
  with StdQuote
  with StdTransaction
  with StdCreateTable
{
  override protected def columnDdl(c: Column)
    = quote(c.name) + " " + columnTypeDdl(c.t) +
      c.autoIncrement.option(" GENERATED BY DEFAULT AS IDENTITY").mkString +
      ( if( c.nullable ) " NULL" else " NOT NULL" )
  override protected def quote(x: String) = "\"" + x + "\""
  override protected def template(sql: Sql) = {
    import sorm.sql.Sql._
    sql match {
      case Insert(table, columns, values) if columns.isEmpty =>
        "INSERT INTO " + quote(table) + " VALUES (DEFAULT)"
      case Comparison(Value(l : Boolean), Value(r : Boolean), o) =>
        if ( o == Equal && l == r || o == NotEqual && l != r ) "TRUE"
        else "FALSE"
      case _ =>
        super.template(sql)
    }
  }

  override protected def data(sql: Sql) = {
    import sorm.sql.Sql._
    sql match {
      case Comparison(Value(l : Boolean), Value(r : Boolean), o) =>
        Stream()
      case _ =>
        super.data(sql)
    }
  }
  override protected def showTablesSql
    = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.SYSTEM_TABLES"
  override def now()
    = connection
        .executeQuery(Statement("VALUES(NOW())"))()
        .head.head
        .asInstanceOf[DateTime]

}
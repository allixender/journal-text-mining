package models

import anorm.{ RowParser, SQL }
import anorm.SqlParser.{ double, long, str }
import anorm.ResultSetParser
import anorm.SqlQuery
import anorm.~

import play.api.Play.current
import play.api.db.DB

case class SosProcedureDB(
  identifier: String,
  xmltext: String)

object SosProcedureDB {

  val getAllQuery: SqlQuery = SQL("select * from procedures")

  val procedureRowParser: RowParser[SosProcedureDB] = {
    str("identifier") ~
      str("xmltext")  map {
        case identifier ~ xmltext =>
          SosProcedureDB(identifier, xmltext)
      }
  }

  val procedureParser: ResultSetParser[List[SosProcedureDB]] = {
    import scala.language.postfixOps
    procedureRowParser *
  }

  def getAllWithParser: List[SosProcedureDB] = DB.withConnection {
    implicit connection =>
      getAllQuery.as(procedureParser)
  }
  
  def getByID(identifier: String): SosProcedureDB = DB.withConnection {
    implicit connection =>
      val getQueryById: SqlQuery = SQL(s"""select * from procedures where identifier = '$identifier'""")
   	  getQueryById.as(procedureParser).head
  }

  def insert(procedure: SosProcedureDB): Boolean =
    DB.withConnection { implicit connection =>
      val addedRows = SQL("""insert into procedures (identifier, xmltext)
    	values ( {identifier}, {xmltext} )
        """).on(
        "identifier" -> procedure.identifier,
        "xmltext" -> procedure.xmltext)
        .executeUpdate()
      addedRows == 1
    }

}
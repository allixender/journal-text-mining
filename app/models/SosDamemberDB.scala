package models

import anorm.{ RowParser, SQL }
import anorm.SqlParser.{ double, long, str }
import anorm.ResultSetParser
import anorm.SqlQuery
import anorm.~

import play.api.Play.current
import play.api.db.DB

case class SosDamemberDB(
  identifier: String,
  xmltext: String)

object SosDamemberDB {

  val getAllQuery: SqlQuery = SQL("select * from damembers")

  val damemberRowParser: RowParser[SosDamemberDB] = {
    str("identifier") ~
      str("xmltext")  map {
        case identifier ~ xmltext =>
          SosDamemberDB(identifier, xmltext)
      }
  }

  val damemberParser: ResultSetParser[List[SosDamemberDB]] = {
    import scala.language.postfixOps
    damemberRowParser *
  }

  def getAllWithParser: List[SosDamemberDB] = DB.withConnection {
    implicit connection =>
      getAllQuery.as(damemberParser)
  }
  
  def getByID(identifier: String): SosDamemberDB = DB.withConnection {
    implicit connection =>
      val getQueryById: SqlQuery = SQL(s"""select * from damembers where identifier = '$identifier'""")
   	  getQueryById.as(damemberParser).head
  }

  def insert(damember: SosDamemberDB): Boolean =
    DB.withConnection { implicit connection =>
      val addedRows = SQL("""insert into damembers (identifier, xmltext)
    	values ( {identifier}, {xmltext} )
        """).on(
        "identifier" -> damember.identifier,
        "xmltext" -> damember.xmltext)
        .executeUpdate()
      addedRows == 1
    }

}
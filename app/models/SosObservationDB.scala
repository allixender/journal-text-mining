package models

import anorm.{ RowParser, SQL }
import anorm.SqlParser.{ double, long, str }
import anorm.ResultSetParser
import anorm.SqlQuery
import anorm.~

import play.api.Play.current
import play.api.db.DB

case class SosObservationDB(
  identifier: String,
  procedureid: String,
  featureid: String,
  phenomenonid: String,
  xmltext: String)

object SosObservationDB {

  val getAllQuery: SqlQuery = SQL("select * from observations")

  val observationRowParser: RowParser[SosObservationDB] = {
    str("identifier") ~
    str("procedureid") ~
    str("featureid") ~
    str("phenomenonid") ~
      str("xmltext")  map {
        case identifier ~ procedureid ~ featureid ~ phenomenonid ~ xmltext =>
          SosObservationDB(identifier, procedureid, featureid, phenomenonid, xmltext)
      }
  }

  val observationParser: ResultSetParser[List[SosObservationDB]] = {
    import scala.language.postfixOps
    observationRowParser *
  }

  def getAllWithParser: List[SosObservationDB] = DB.withConnection {
    implicit connection =>
      getAllQuery.as(observationParser)
  }
  
  def getByID(identifier: String): SosObservationDB = DB.withConnection {
    implicit connection =>
      val getQueryById: SqlQuery = SQL(s"""select * from observations where identifier = '$identifier'""")
   	  getQueryById.as(observationParser).head
  }

  def insert(observation: SosObservationDB): Boolean =
    DB.withConnection { implicit connection =>
      val addedRows = SQL("""insert into observations (identifier, procedureid, featureid, phenomenonid, xmltext)
    	values ( {identifier}, {procedureid}, {featureid}, {phenomenonid}, {xmltext} )
        """).on(
        "identifier" -> observation.identifier,
        "procedureid" -> observation.procedureid,
        "featureid" -> observation.featureid,
        "phenomenonid" -> observation.phenomenonid,
        "xmltext" -> observation.xmltext)
        .executeUpdate()
      addedRows == 1
    }

}
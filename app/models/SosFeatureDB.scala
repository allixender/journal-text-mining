package models

import anorm.{ RowParser, SQL }
import anorm.SqlParser.{ double, long, str }
import anorm.ResultSetParser
import anorm.SqlQuery
import anorm.~

import play.api.Play.current
import play.api.db.DB

case class SosFeatureDB(
  identifier: String,
  xmltext: String)

object SosFeatureDB {

  val getAllQuery: SqlQuery = SQL("select * from features order by identifier asc")

  val featureRowParser: RowParser[SosFeatureDB] = {
    str("identifier") ~
      str("xmltext")  map {
        case identifier ~ xmltext =>
          SosFeatureDB(identifier, xmltext)
      }
  }

  val featureParser: ResultSetParser[List[SosFeatureDB]] = {
    import scala.language.postfixOps
    featureRowParser *
  }

  def getAllWithParser: List[SosFeatureDB] = DB.withConnection {
    implicit connection =>
      getAllQuery.as(featureParser)
  }
  
  def getByID(identifier: String): SosFeatureDB = DB.withConnection {
    implicit connection =>
      val getQueryById: SqlQuery = SQL(s"""select * from features where identifier = '$identifier'""")
   	  getQueryById.as(featureParser).head
  }

  def insert(feature: SosFeatureDB): Boolean =
    DB.withConnection { implicit connection =>
      val addedRows = SQL("""insert into features (identifier, xmltext)
    	values ( {identifier}, {xmltext} )
        """).on(
        "identifier" -> feature.identifier,
        "xmltext" -> feature.xmltext)
        .executeUpdate()
      addedRows == 1
    }

}
package models

import anorm.{ RowParser, SQL }
import anorm.SqlParser.{ double, long, str }
import anorm.ResultSetParser
import anorm.SqlQuery
import anorm.~

import play.api.Play.current
import play.api.db.DB

case class RawArticlePage(
  articleid: Long,
  fulltext: String,
  url: String,
  volnum: String,
  year: String)

object RawArticlePage {

  val getAllQuery: SqlQuery = SQL("select * from rawarticles order by articleid asc")

  val articleRowParser: RowParser[RawArticlePage] = {
    long("articleid") ~
      str("fulltext") ~
      str("url") ~
      str("volnum") ~
      str("year") map {
        case articleid ~ fulltext ~ url ~ volnum ~ year =>
          RawArticlePage(articleid, fulltext, url, volnum, year)
      }
  }

  val articlesParser: ResultSetParser[List[RawArticlePage]] = {
    import scala.language.postfixOps
    articleRowParser *
  }

  def getAllWithParser: List[RawArticlePage] = DB.withConnection {
    implicit connection =>
      getAllQuery.as(articlesParser)
  }

  def insert(article: RawArticlePage): Boolean =
    DB.withConnection { implicit connection =>
      val addedRows = SQL("""insert into rawarticles (fulltext, url, volnum, year)
    	values ( {fulltext}, {url}, {volnum}, {year}  )
        """).on(
        "fulltext" -> article.fulltext,
        "url" -> article.url,
        "volnum" -> article.volnum,
        "year" -> article.year)
        .executeUpdate()
      addedRows == 1
    }
  
  def insertWithId(article: RawArticlePage): Boolean =
		  DB.withConnection { implicit connection =>
		  val addedRows = SQL("""insert into rawarticles (articleid, fulltext, url, volnum, year)
				  values ( {articleid}, {fulltext}, {url}, {volnum}, {year}  )
				  """).on(
						  "articleid" -> article.articleid,
						  "fulltext" -> article.fulltext,
						  "url" -> article.url,
						  "volnum" -> article.volnum,
						  "year" -> article.year)
						  .executeUpdate()
						  addedRows == 1
  }
  
  def updateWithFullByID(article: RawArticlePage): Boolean =
    DB.withConnection { implicit connection =>
      val updatedRows = SQL("""update rawarticles
		set articleid = {articleid},
		    fulltext = {fulltext},
        url = {url},
		    volnum = {volnum},
    		  year = {year}
		where articleid = {articleid}
		""").on(
        "articleid" -> article.articleid,
			  "fulltext" -> article.fulltext,
			  "url" -> article.url,
			  "volnum" -> article.volnum,
			  "year" -> article.year)
        .executeUpdate()
      updatedRows == 1
    }

}

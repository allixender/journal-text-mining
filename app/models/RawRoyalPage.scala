package models

import anorm.{ RowParser, SQL }
import anorm.SqlParser.{ double, long, str }
import anorm.ResultSetParser
import anorm.SqlQuery
import anorm.~

import play.api.Play.current
import play.api.db.DB

case class RawRoyalPage(
                           articleid: Long,
                           fulltext: String,
                           url: String,
                           volnum: String,
                           year: String)

object RawRoyalPage {

  val getAllQuery: SqlQuery = SQL("select * from rawroyal order by articleid asc")

  val articleRowParser: RowParser[RawRoyalPage] = {
    long("articleid") ~
      str("fulltext") ~
      str("url") ~
      str("volnum") ~
      str("year") map {
      case articleid ~ fulltext ~ url ~ volnum ~ year =>
        RawRoyalPage(articleid, fulltext, url, volnum, year)
    }
  }

  val articlesParser: ResultSetParser[List[RawRoyalPage]] = {
    import scala.language.postfixOps
    articleRowParser *
  }

  def getAllWithParser: List[RawRoyalPage] = DB.withConnection {
    implicit connection =>
      getAllQuery.as(articlesParser)
  }

  def insert(article: RawRoyalPage): Boolean =
    DB.withConnection { implicit connection =>
      val addedRows = SQL("""insert into rawroyal (fulltext, url, volnum, year)
    	values ( {fulltext}, {url}, {volnum}, {year}  )
                          """).on(
          "fulltext" -> article.fulltext,
          "url" -> article.url,
          "volnum" -> article.volnum,
          "year" -> article.year)
        .executeUpdate()
      addedRows == 1
    }

  def insertWithId(article: RawRoyalPage): Boolean =
    DB.withConnection { implicit connection =>
      val addedRows = SQL("""insert into rawroyal (articleid, fulltext, url, volnum, year)
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

  def updateWithFullByID(article: RawRoyalPage): Boolean =
    DB.withConnection { implicit connection =>
      val updatedRows = SQL("""update rawroyal
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

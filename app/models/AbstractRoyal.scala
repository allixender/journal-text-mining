package models

import anorm.{ RowParser, SQL }
import anorm.SqlParser.{ double, long, str, get }
import anorm.ResultSetParser
import anorm.SqlQuery
import anorm.~
import com.datastax.driver.core.DataType._
import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder.{ eq => ceq }
import com.datastax.driver.core.schemabuilder.SchemaBuilder
import play.api.Logger._

import play.api.Play.current
import play.api.db.DB
import service.{CFKeys, ConfigCassandraCluster}

import scala.concurrent.Future
import scala.util.{Success,Failure}

case class AbstractRoyal(
                          articleid: Long,
                          authortitle: String,
                          textabs: String,
                          author: String,
                          title: String,
                          year: Long,
                          arturl: String,
                          fulltext: Option[String])

object AbstractRoyal extends ConfigCassandraCluster {

  val getAllQuery: SqlQuery = SQL("select articleid,authortitle,textabs,year,arturl from abstractroyal order by articleid asc")
  val getAllQueryUpdated: SqlQuery = SQL("select articleid,authortitle,textabs,author,title,year,arturl,fulltext from abstractroyal order by articleid asc")

  val articleRowParser: RowParser[AbstractRoyal] = {
    long("articleid") ~
      str("authortitle") ~
      str("textabs") ~
      long("year") ~
      str("arturl") map {
      case articleid ~ authortitle ~ textabs ~ year ~ arturl =>
        AbstractRoyal(articleid, authortitle, textabs, "", "", year, arturl, None)
    }
  }
  val articleRowParserUpdated: RowParser[AbstractRoyal] = {
    long("articleid") ~
      str("authortitle") ~
      str("textabs") ~
      str("author") ~
      str("title") ~
      long("year") ~
      str("arturl") ~
      get[Option[String]]("fulltext") map {
      case articleid ~ authortitle ~ textabs ~ author ~ title ~ year ~ arturl ~ fulltext =>
        AbstractRoyal(articleid, authortitle, textabs, author, title, year, arturl, fulltext)
    }
  }

  val articlesParser: ResultSetParser[List[AbstractRoyal]] = {
    // import as suggested
    import scala.language.postfixOps
    articleRowParser *
  }
  val articlesParserUpdated: ResultSetParser[List[AbstractRoyal]] = {
    // import as suggested
    import scala.language.postfixOps
    articleRowParserUpdated *
  }

  def getAllWithParser: List[AbstractRoyal] = DB.withConnection {
    implicit connection =>
      getAllQuery.as(articlesParser)
  }

  def getAllWithParserUpdated: List[AbstractRoyal] = DB.withConnection {
    implicit connection =>
      getAllQueryUpdated.as(articlesParserUpdated)
  }

  // insert without separated author title
  def insert(article: AbstractRoyal): Boolean =
    DB.withConnection { implicit connection =>
      val addedRows = SQL("""insert into abstractroyal (articleid,authortitle,textabs, year, arturl)
    	values ( {articleid}, {authortitle}, {textabs}, {year}, {arturl} )
                          """).on(
          "articleid" -> article.articleid,
          "authortitle" -> article.authortitle,
          "textabs" -> article.textabs,
          "year" -> article.year,
          "arturl" -> article.arturl)
        .executeUpdate()
      addedRows == 1
    }

  // full insert including separate author title
  def insertFull(article: AbstractRoyal): Boolean =
    DB.withConnection { implicit connection =>
      val addedRows = SQL("""insert into abstractroyal (articleid,authortitle,textabs,author,title, year, arturl)
				  values ( {articleid}, {authortitle}, {textabs}, {author}, {title}, {year}, {arturl} )
                          				  """).on(
          "articleid" -> article.articleid,
          "authortitle" -> article.authortitle,
          "textabs" -> article.textabs,
          "author" -> article.author,
          "title" -> article.title,
          "year" -> article.year,
          "arturl" -> article.arturl)
        .executeUpdate()
      addedRows == 1
    }

  def updateWithFullByID(article: AbstractRoyal): Boolean =
    DB.withConnection { implicit connection =>
      val updatedRows = SQL("""update abstractroyal
		set authortitle = {authortitle},
		    textabs = {textabs},
        author = {author},
		    title = {title},
    		  year = {year},
    		  arturl = {arturl},
    		  fulltext = {fulltext}
		where articleid = {articleid}
                            		""").on(
          "articleid" -> article.articleid,
          "authortitle" -> article.authortitle,
          "textabs" -> article.textabs,
          "author" -> article.author,
          "title" -> article.title,
          "year" -> article.year,
          "arturl" -> article.arturl,
          "fulltext" -> article.fulltext.getOrElse(""))
        .executeUpdate()
      updatedRows == 1
    }

  import play.api.libs.concurrent.Execution.Implicits._
  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters
  import cassandra.resultset._
  import scala.language.implicitConversions

  // not sure if this one session does the trick
  lazy val session = cluster.connect(CFKeys.playCassandra)

  val JOURNAL_MARINE = "New Zealand Journal of Marine and Freshwater Research"
  val JOURNAL_GEOLOGY = "New Zealand Journal of Geology and Geophysics"

  def createCassandraSchema = {
    val schema = SchemaBuilder.createTable(CFKeys.playCassandra, CFKeys.articles).ifNotExists()
      .addPartitionKey("articleid", bigint)
      .addClusteringColumn("journal", text)
      .addColumn("authortitle", text)
      .addColumn("textabs", text)
      .addColumn("author", text)
      .addColumn("title", text)
      .addColumn("year", bigint)
      .addColumn("arturl", text)
      .addColumn("fulltext", text)
    session.execute(schema)
  }

  /**
   * Refactor Cassandra
   */
  def buildParser(row: Row) : AbstractRoyal = {

    val articleid = row.getLong("articleid")
    val authortitle = row.getString("authortitle")
    val textabs = row.getString("textabs")
    val author = row.getString("author")
    val title = row.getString("title")
    val year = row.getLong("year")
    val arturl = row.getString("arturl")
    val fulltext = row.getString("fulltext")

    AbstractRoyal(
      articleid,
      authortitle,
      textabs,
      author,
      title,
      year,
      arturl,
      Some(fulltext))
  }

  def insertF(abs: AbstractRoyal) : Unit = {

    val preparedStatement = session.prepare( s"""INSERT INTO ${CFKeys.articles}
           (articleid,journal,authortitle,textabs,author,title,year,arturl,fulltext)
           VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);""")

    // real bad
    val JOURNALNAME = if (abs.fulltext.contains("New Zealand Journal of Marine and Freshwater Research")) {
      JOURNAL_MARINE
    } else {
      if (abs.fulltext.contains("Journal of Geology and Geophysics") ) {
        JOURNAL_GEOLOGY
      } else {
        JOURNAL_MARINE
      }
    }

    val execFuture = session.executeAsync(preparedStatement.bind(
      abs.articleid.asInstanceOf[java.lang.Long], JOURNALNAME, abs.authortitle,
      abs.textabs, abs.author, abs.title, abs.year.asInstanceOf[java.lang.Long], abs.arturl, abs.fulltext.getOrElse("")))
  }

  def insertGeologyF(abs: AbstractRoyal) : Boolean = {

    val preparedStatement = session.prepare( s"""INSERT INTO ${CFKeys.articles}
           (articleid,journal,authortitle,textabs,author,title,year,arturl,fulltext)
           VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);""")

    // real bad
    val JOURNALNAME = JOURNAL_GEOLOGY

    val execFuture = session.executeAsync(preparedStatement.bind(
      abs.articleid.asInstanceOf[java.lang.Long], JOURNALNAME, abs.authortitle,
      abs.textabs, abs.author, abs.title, abs.year.asInstanceOf[java.lang.Long], abs.arturl, abs.fulltext.getOrElse("_none_")))

    execFuture.onComplete{
      case Success(_) => logger.info(s"success insert")
      case Failure(e) => logger.error(s"error for ${e.getMessage}")
    }
    execFuture.isDone
  }

  /**
   * Cassandra delete Refactor, fire and forget
   */
  def updateF(abs: AbstractRoyal) : Boolean = {

    val preparedStatement = session.prepare( s"""UPDATE ${CFKeys.articles}
           set journal = ?,
           authortitle = ?,
           textabs = ?,
           author = ?,
           title = ?,
           year = ?,
           arturl = ?,
           fulltext = ?
           where articleid = ? and journal = ?;""")

    // real bad
    val JOURNALNAME = JOURNAL_GEOLOGY

    val execFuture = session.executeAsync(preparedStatement.bind(
      JOURNALNAME, abs.authortitle,
      abs.textabs, abs.author, abs.title, abs.year.asInstanceOf[java.lang.Long],
      abs.arturl, abs.fulltext.getOrElse("_none_"), abs.articleid.asInstanceOf[java.lang.Long], JOURNAL_GEOLOGY))

    // execFuture.onSuccess{
    //   case _ => println("ok")
    // }
    // execFuture.onFailure{
    //  case t => println("An error has occured: " + t.getMessage)
    // }
    execFuture.onComplete{
      case Success(_) => logger.info(s"success insert")
      case Failure(e) => logger.error(s"error for ${e.getMessage}")
    }
    execFuture.isDone
  }

  /**
   * Cassandra delete Refactor, fire and forget
   */
  def deleteF(abs: AbstractHydro) = {
    val query = QueryBuilder.delete().from(CFKeys.playCassandra, CFKeys.articles).where(ceq("articleid", abs.articleid))
    session.executeAsync(query)
  }

  /**
   * Refactor Cassandra
   */
  def getAllF: Future[List[AbstractRoyal]] = {
    val query = QueryBuilder.select().all().from(CFKeys.playCassandra, CFKeys.articles).allowFiltering() //.limit(1000)
    session.executeAsync(query) map (_.all().map(buildParser).toList)
  }

  /**
   * Refactor Cassandra
   */
  def getByIdF(articleid: Long): Future[List[AbstractRoyal]] = {
    val query = QueryBuilder.select().from(CFKeys.playCassandra, CFKeys.articles).where(ceq("articleid", articleid))
    //.orderBy(QueryBuilder.desc("createtimestamp")) //.limit(1000)
    session.executeAsync(query) map (_.all().map(buildParser).toList)
  }
}

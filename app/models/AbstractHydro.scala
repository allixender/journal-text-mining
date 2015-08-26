package models

import java.util.UUID

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

import play.api.Play.current
import play.api.db.DB
import service.{CFKeys, ConfigCassandraCluster}

import scala.concurrent.Future

case class AbstractHydro(
  articleid: Long,
  authortitle: String,
  textabs: String,
  author: String,
  title: String,
  year: Long,
  arturl: Long,
  fulltext: Option[String])

object AbstractHydro extends ConfigCassandraCluster {

  import play.api.libs.concurrent.Execution.Implicits._
  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters
  import cassandra.resultset._
  import scala.language.implicitConversions

  // not sure if this one session does the trick
  lazy val session = cluster.connect(CFKeys.playCassandra)

  val JOURNALNAME = "New Zealand Journal of Hydrology"

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
  def buildParser(row: Row) : AbstractHydro = {

    val articleid = row.getLong("articleid")
    val authortitle = row.getString("authortitle")
    val textabs = row.getString("textabs")
    val author = row.getString("author")
    val title = row.getString("title")
    val year = row.getLong("year")
    val arturl = row.getString("arturl")
    val fulltext = row.getString("fulltext")

    AbstractHydro(
      articleid,
      authortitle,
      textabs,
      author,
      title,
      year,
      arturl.toLong,
      Some(fulltext))
  }

  def insertF(abs: AbstractHydro) : Unit = {

    val preparedStatement = session.prepare( s"""INSERT INTO ${CFKeys.articles}
           (articleid,journal,authortitle,textabs,author,title,year,arturl,fulltext)
           VALUES (?, ?, ?, ?, ?, ?, ? ,?, ? );""")

    val execFuture = session.executeAsync(preparedStatement.bind(
      abs.articleid.asInstanceOf[java.lang.Long], JOURNALNAME, abs.authortitle,
      abs.textabs, abs.author, abs.title, abs.year.asInstanceOf[java.lang.Long], abs.arturl.toString, abs.fulltext.getOrElse("")))
  }

  /**
   * Cassandra delete Refactor, fire and forget
   */
  def updateF(abs: AbstractHydro) : Unit = {

    val preparedStatement = session.prepare( s"""UPDATE ${CFKeys.articles}
           set journal = ?,
           authortitle = ?,
           textabs = ?,
           author = ?,
           title = ?,
           year = ?,
           arturl = ?,
           fulltext = ?
           where articleid = ?;""")

    val execFuture = session.executeAsync(preparedStatement.bind(
      JOURNALNAME, abs.authortitle.asInstanceOf[java.lang.Long],
      abs.textabs, abs.author, abs.title, abs.year.asInstanceOf[java.lang.Long],
      abs.arturl.toString, abs.fulltext.getOrElse(""), abs.articleid.asInstanceOf[java.lang.Long]))
  }

  /**
   * Cassandra delete Refactor, fire and forget
   */
  def deleteF(abs: AbstractHydro) = {
    val query = QueryBuilder.delete().from(CFKeys.playCassandra, CFKeys.articles).where(ceq("articleid", abs.articleid)).and(ceq("journal", JOURNALNAME))
    session.executeAsync(query)
  }

  /**
   * Refactor Cassandra
   */
  def getAllF: Future[List[AbstractHydro]] = {
    val query = QueryBuilder.select().all().from(CFKeys.playCassandra, CFKeys.articles).allowFiltering().where(ceq("journal", JOURNALNAME)) //.limit(1000)
    session.executeAsync(query) map (_.all().map(buildParser).toList)
  }

  /**
   * Refactor Cassandra
   */
  def getByIdF(articleid: Long): Future[List[AbstractHydro]] = {
    val query = QueryBuilder.select().from(CFKeys.playCassandra, CFKeys.articles).where(ceq("articleid", articleid)).and(ceq("journal", JOURNALNAME))
    //.orderBy(QueryBuilder.desc("createtimestamp")) //.limit(1000)
    session.executeAsync(query) map (_.all().map(buildParser).toList)
  }


  val getAllQuery: SqlQuery = SQL("select articleid,authortitle,textabs,year,arturl from abstracthydro order by articleid asc")
  val getAllQueryUpdated: SqlQuery = SQL("select articleid,authortitle,textabs,author,title,year,arturl,fulltext from abstracthydro order by articleid asc")

  val articleRowParser: RowParser[AbstractHydro] = {
    long("articleid") ~
      str("authortitle") ~
      str("textabs") ~ 
      long("year") ~
    	long("arturl") map {
        case articleid ~ authortitle ~ textabs ~ year ~ arturl =>
          AbstractHydro(articleid, authortitle, textabs, "", "", year, arturl, None)
      }
  }
  val articleRowParserUpdated: RowParser[AbstractHydro] = {
    long("articleid") ~
      str("authortitle") ~
      str("textabs") ~
      str("author") ~
      str("title") ~ 
      long("year") ~
    	long("arturl") ~
    	get[Option[String]]("fulltext") map {
        case articleid ~ authortitle ~ textabs ~ author ~ title ~ year ~ arturl ~ fulltext =>
          AbstractHydro(articleid, authortitle, textabs, author, title, year, arturl, fulltext)
      }
  }

  val articlesParser: ResultSetParser[List[AbstractHydro]] = {
    // import as suggested
    import scala.language.postfixOps
    articleRowParser *
  }
  val articlesParserUpdated: ResultSetParser[List[AbstractHydro]] = {
    // import as suggested
    import scala.language.postfixOps
    articleRowParserUpdated *
  }

  def getAllWithParser: List[AbstractHydro] = DB.withConnection {
    implicit connection =>
      getAllQuery.as(articlesParser)
  }

  def getAllWithParserUpdated: List[AbstractHydro] = DB.withConnection {
    implicit connection =>
      getAllQueryUpdated.as(articlesParserUpdated)
  }

  // insert without separated author title
  def insert(article: AbstractHydro): Boolean =
    DB.withConnection { implicit connection =>
      val addedRows = SQL("""insert into abstracthydro (articleid,authortitle,textabs, year, arturl)
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
  def insertFull(article: AbstractHydro): Boolean =
    DB.withConnection { implicit connection =>
      val addedRows = SQL("""insert into abstracthydro (articleid,authortitle,textabs,author,title, year, arturl)
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

  def updateWithFullByID(article: AbstractHydro): Boolean =
    DB.withConnection { implicit connection =>
      val updatedRows = SQL("""update abstracthydro
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
}

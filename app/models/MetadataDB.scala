package models

import java.util.UUID

import anorm.{ RowParser, SQL }
import anorm.SqlParser.{ double, long, str }
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

case class MetadataDB(
  metadataid: Long,
  xml: String)

object MetadataDB extends ConfigCassandraCluster {

  val getAllQuery: SqlQuery = SQL("select * from metadata order by metadataid asc")

  val metadataRowParser: RowParser[MetadataDB] = {
    long("metadataid") ~
      str("xml")  map {
        case metadataid ~ xml =>
          MetadataDB(metadataid, xml)
      }
  }

  val metadataParser: ResultSetParser[List[MetadataDB]] = {
    import scala.language.postfixOps
    metadataRowParser *
  }

  def getAllWithParser: List[MetadataDB] = DB.withConnection {
    implicit connection =>
      getAllQuery.as(metadataParser)
  }
  
  def getByID(dbid: Long): MetadataDB = DB.withConnection {
    implicit connection =>
      val getQueryById: SqlQuery = SQL(s"""select * from metadata where metadataid = $dbid""")
   	  getQueryById.as(metadataParser).head
  }

  def insert(metadata: MetadataDB): Boolean =
    DB.withConnection { implicit connection =>
      val addedRows = SQL("""insert into metadata (metadataid, xml)
    	values ( {metadataid}, {xml} )
        """).on(
        "metadataid" -> metadata.metadataid,
        "xml" -> metadata.xml)
        .executeUpdate()
      addedRows == 1
    }

  import play.api.libs.concurrent.Execution.Implicits._
  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters
  import cassandra.resultset._
  import scala.language.implicitConversions

  // not sure if this one session does the trick
  lazy val session = cluster.connect(CFKeys.playCassandra)

  def createCassandraSchema = {
    val schema = SchemaBuilder.createTable(CFKeys.playCassandra, CFKeys.metaxml).ifNotExists()
      .addPartitionKey("metadataid", bigint)
      .addColumn("xml", text)
    session.execute(schema)
  }

  /**
   * Refactor Cassandra
   */
  def buildParser(row: Row) : MetadataDB = {

    val metadataid = row.getLong("metadataid")
    val xml = row.getString("xml")

    MetadataDB(
      metadataid,
      xml)
  }

  def insertF(meta: MetadataDB) : Unit = {

    val preparedStatement = session.prepare( s"""INSERT INTO ${CFKeys.metaxml}
           (metadataid,xml)
           VALUES (?, ?);""")

    val execFuture = session.executeAsync(preparedStatement.bind(
      meta.metadataid.asInstanceOf[java.lang.Long], meta.xml))
  }

  /**
   * Cassandra delete Refactor, fire and forget
   */
  def updateF(meta: MetadataDB) : Unit = {

    val preparedStatement = session.prepare( s"""UPDATE ${CFKeys.metaxml}
           set xml = ?,
           where metadataid = ?;""")

    val execFuture = session.executeAsync(preparedStatement.bind(
      meta.xml, meta.metadataid.asInstanceOf[java.lang.Long]))
  }

  /**
   * Cassandra delete Refactor, fire and forget
   */
  def deleteF(meta: MetadataDB) = {
    val query = QueryBuilder.delete().from(CFKeys.playCassandra, CFKeys.metaxml).where(ceq("metadataid", meta.metadataid))
    session.executeAsync(query)
  }

  /**
   * Refactor Cassandra
   */
  def getAllF: Future[List[MetadataDB]] = {
    val query = QueryBuilder.select().all().from(CFKeys.playCassandra, CFKeys.metaxml) //.limit(1000)
    session.executeAsync(query) map (_.all().map(buildParser).toList)
  }

  /**
   * Refactor Cassandra
   */
  def getByIdF(metadataid: Long): Future[List[MetadataDB]] = {
    val query = QueryBuilder.select().from(CFKeys.playCassandra, CFKeys.metaxml).where(ceq("metadataid", metadataid))
      //.orderBy(QueryBuilder.desc("createtimestamp")) //.limit(1000)
    session.executeAsync(query) map (_.all().map(buildParser).toList)
  }
}

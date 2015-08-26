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

import play.api.Play.current
import play.api.db.DB
import service.{CFKeys, ConfigCassandraCluster}

import scala.concurrent.Future

case class GeoName(
  name_id: Long,
  name: String,
  status: String,
  land_district: String,
  crd_projection: String,
  crd_north: Double,
  crd_east: Double,
  crd_datum: String,
  crd_latitude: Double,
  crd_longitude: Double)

object GeoName extends ConfigCassandraCluster {

  // insert
  def insert(georef: GeoName): Boolean =
    DB.withConnection { implicit connection =>
      val addedRows = SQL("""insert into georefs 
    	values ( {name_id}, {name}, {status}, {land_district}, {crd_projection}, {crd_north}, {crd_east}, {crd_datum}, {crd_latitude}, {crd_longitude} )
        """).on(
        "name_id" -> georef.name_id,
        "name" -> georef.name,
        "status" -> georef.status,
        "land_district" -> georef.land_district,
        "crd_projection" -> georef.crd_projection,
        "crd_north" -> georef.crd_north,
        "crd_east" -> georef.crd_east,
        "crd_datum" -> georef.crd_datum,
        "crd_latitude" -> georef.crd_latitude,
        "crd_longitude" -> georef.crd_longitude)
        .executeUpdate()
      addedRows == 1
    }

  def updateWithFullByID(georef: GeoName): Boolean =
    DB.withConnection { implicit connection =>
      val updatedRows = SQL("""update georefs
		set name_id = {name_id},
    		name = {name},
    		status = {status},
    		land_district = {land_district},
    		crd_projection = {crd_projection},
    		crd_north = {crd_north},
    		crd_east = {crd_east},
    		crd_datum = {crd_datum},
    		crd_latitude = {crd_latitude},
    		crd_longitude = {crd_longitude}
		where name_id = {name_id}
		""").on(
        "name_id" -> georef.name_id,
        "name" -> georef.name,
        "status" -> georef.status,
        "land_district" -> georef.land_district,
        "crd_projection" -> georef.crd_projection,
        "crd_north" -> georef.crd_north,
        "crd_east" -> georef.crd_east,
        "crd_datum" -> georef.crd_datum,
        "crd_latitude" -> georef.crd_latitude,
        "crd_longitude" -> georef.crd_longitude)
        .executeUpdate()
      updatedRows == 1
    }

  val georefRowParser: RowParser[GeoName] = {
    long("name_id") ~
      str("name") ~
      str("status") ~
      str("land_district") ~
      str("crd_projection") ~
      double("crd_north") ~
      double("crd_east") ~
      str("crd_datum") ~
      double("crd_latitude") ~
      double("crd_longitude") map {
        case name_id ~ name ~ status ~ land_district ~ crd_projection ~ crd_north ~ crd_east ~ crd_datum ~ crd_latitude ~ crd_longitude =>
          GeoName(name_id, name, status, land_district, crd_projection, crd_north, crd_east, crd_datum, crd_latitude, crd_longitude)
      }
  }

  val georefParser: ResultSetParser[List[GeoName]] = {
    // import as suggested
    import scala.language.postfixOps
    georefRowParser *
  }

  def getAllWithParser: List[GeoName] = DB.withConnection {
    implicit connection =>
      {
        val getAllQuery: SqlQuery = SQL("select * from georefs")
        getAllQuery.as(georefParser)
      }
  }

  import play.api.libs.concurrent.Execution.Implicits._
  import scala.collection.JavaConversions._
  import scala.collection.JavaConverters
  import cassandra.resultset._
  import scala.language.implicitConversions

  // not sure if this one session does the trick
  lazy val session = cluster.connect(CFKeys.playCassandra)

  def createCassandraSchema = {
    val schema = SchemaBuilder.createTable(CFKeys.playCassandra, CFKeys.linzgeo).ifNotExists()
      .addPartitionKey("name_id", bigint)
      .addClusteringColumn("name", text)
      .addColumn("status", text)
      .addColumn("land_district", text)
      .addColumn("crd_projection", text)
      .addColumn("crd_north", cdouble)
      .addColumn("crd_east", cdouble)
      .addColumn("crd_datum", text)
      .addColumn("crd_latitude", cdouble)
      .addColumn("crd_longitude", cdouble)
    session.execute(schema)
  }

  /**
   * Refactor Cassandra
   */
  def buildParser(row: Row) : GeoName = {

    val name_id = row.getLong("name_id")
    val name = row.getString("name")
    val status = row.getString("status")
    val land_district = row.getString("land_district")
    val crd_projection = row.getString("crd_projection")
    val crd_north = row.getDouble("crd_north")
    val crd_east = row.getDouble("crd_east")
    val crd_datum = row.getString("crd_datum")
    val crd_latitude = row.getDouble("crd_latitude")
    val crd_longitude = row.getDouble("crd_longitude")

    GeoName(
      name_id: Long,
      name: String,
      status: String,
      land_district: String,
      crd_projection: String,
      crd_north: Double,
      crd_east: Double,
      crd_datum: String,
      crd_latitude: Double,
      crd_longitude: Double)

  }

  def insertF(geo: GeoName) : Unit = {

    val preparedStatement = session.prepare( s"""INSERT INTO ${CFKeys.linzgeo}
           (name_id, name, status, land_district, crd_projection, crd_north, crd_east, crd_datum, crd_latitude, crd_longitude)
           VALUES (?, ?, ?, ?, ?, ? ,?, ?, ?, ? );""")

    val execFuture = session.executeAsync(preparedStatement.bind(
      geo.name_id.asInstanceOf[java.lang.Long], geo.name, geo.status, geo.land_district, geo.crd_projection,
      geo.crd_north.asInstanceOf[java.lang.Double], geo.crd_east.asInstanceOf[java.lang.Double],
      geo.crd_datum, geo.crd_latitude.asInstanceOf[java.lang.Double], geo.crd_longitude.asInstanceOf[java.lang.Double]))
  }

  /**
   * Cassandra delete Refactor, fire and forget
   */
  def updateF(geo: GeoName) : Unit = {

    val preparedStatement = session.prepare( s"""UPDATE ${CFKeys.linzgeo}
           set name = ?,
           status = ?,
           land_district = ?,
           crd_projection = ?,
           crd_north = ?,
           crd_east = ?,
           crd_datum = ?,
           crd_latitude = ?,
           crd_longitude = ?
           where name_id = ?;""")

    val execFuture = session.executeAsync(preparedStatement.bind(
      geo.name, geo.status, geo.land_district, geo.crd_projection, geo.crd_north.asInstanceOf[java.lang.Double], geo.crd_east.asInstanceOf[java.lang.Double],
      geo.crd_datum, geo.crd_latitude.asInstanceOf[java.lang.Double], geo.crd_longitude.asInstanceOf[java.lang.Double],
      geo.name_id.asInstanceOf[java.lang.Long]))
  }

  /**
   * Cassandra delete Refactor, fire and forget
   */
  def deleteF(geo: GeoName) = {
    val query = QueryBuilder.delete().from(CFKeys.playCassandra, CFKeys.linzgeo).where(ceq("name_id", geo.name_id))
    session.executeAsync(query)
  }

  /**
   * Refactor Cassandra
   */
  def getAllF: Future[List[GeoName]] = {
    val query = QueryBuilder.select().all().from(CFKeys.playCassandra, CFKeys.linzgeo) //.limit(1000)
    session.executeAsync(query) map (_.all().map(buildParser).toList)
  }

  /**
   * Refactor Cassandra
   */
  def getByIdF(name_id: Long): Future[List[GeoName]] = {
    val query = QueryBuilder.select().from(CFKeys.playCassandra, CFKeys.linzgeo).where(ceq("name_id", name_id))
    //.orderBy(QueryBuilder.desc("createtimestamp")) //.limit(1000)
    session.executeAsync(query) map (_.all().map(buildParser).toList)
  }

}

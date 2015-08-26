package controllers

import play.api.libs.ws._
import play.api.Logger._
import models.{AbstractRoyal, GeoName, AbstractHydro}
import play.api.mvc.{Action, Controller}

/**
 * Created by akmoch on 18/08/15.
 */
object CassandraCtl extends Controller {

  import play.api.libs.concurrent.Execution.Implicits._
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  def loadAndSave = Action {

    // should be idempotent
    val off1 = AbstractHydro.createCassandraSchema
    val off2 = AbstractRoyal.createCassandraSchema
    val off3 = GeoName.createCassandraSchema


    val t1 = AbstractHydro.getAllWithParserUpdated
    val t2 = AbstractRoyal.getAllWithParserUpdated
    val geos = GeoName.getAllWithParser

    logger.info(s"hydro ${t1.size}")
    logger.info(s"royal ${t2.size}")
    logger.info(s"geos ${geos.size}")

    t1.foreach( art =>
      AbstractHydro.insertF(art)
    )
    t2.foreach( art =>
      AbstractRoyal.insertF(art)
    )
    geos.foreach( geo =>
      GeoName.insertF(geo)
    )

    Ok(views.html.index("transferring from H2 to Cassandra."))
  }
}

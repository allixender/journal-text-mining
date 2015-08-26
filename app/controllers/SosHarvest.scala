package controllers

import play.api._
import play.api.Logger._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.Future
import models.SosProcedureDB
import models.SosFeatureDB
import models.SosDamemberDB
import models.SosObservationDB
import models.SosProcedureDB
import models.SosObservationDB

object SosHarvest extends Controller {

  val localSos = "http://localhost:9090/ngmp-sos/sos/"
  val smartSos = "http://portal.smart-project.info/sos-smart/sos/"

  def getcapa = Action.async {

    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    val futureResult: Future[String] = WS.url(localSos + "kvp?service=SOS&request=GetCapabilities").get().map {
      response =>
        {
          val xmlsource = scala.xml.Source.fromString(response.body)
          val xmltext = scala.xml.XML.load(xmlsource)
          val opsList = xmltext \\ "OperationsMetadata" \ "Operation"

          val nodes = for {
            ops <- xmltext \\ "OperationsMetadata" \ "Operation"
            if (ops \ "@name").text == "DescribeSensor"
            params <- ops \ "Parameter"
            if (params \ "@name").text.equalsIgnoreCase("procedure")
            node <- params \ "AllowedValues" \ "Value"
          } yield { node.text }

          for (procedure <- nodes) {
            getProcedureAndSave(procedure)
          }

          // nodes.mkString(", ")
          "more"
        }
    }

    futureResult.map(result => Ok(views.html.index(result)))
  }

  def getProcedureAndSave(procedure: String) = {
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    val request = getDescSensorReq(procedure)
    val futureResult: Future[String] = WS.url(localSos + "pox").withHeaders("headerKey" -> "headerValue").post(request).
      map {
        response =>
          {

            val xmlsource = scala.xml.Source.fromString(response.body)
            val xmltext = scala.xml.XML.load(xmlsource)
            val members: scala.xml.NodeSeq = xmltext \\ "SensorML" \ "member"

            val nodes = for {
              member <- members
            } yield { member.toString() }

            val xml = nodes.toList.mkString("")
            val proc = models.SosProcedureDB(procedure, xml)
            val retVal = SosProcedureDB.insert(proc)
            s"""inserted $procedure $retVal"""

          }
      }
  }

  def getFeaturesAndSave = Action.async {

    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    val request = getBboxFeatureQuery

    val futureResult: Future[String] = WS.url(localSos + "pox").withHeaders("headerKey" -> "headerValue").post(request).
      map {
        response =>
          {

            val xmlsource = scala.xml.Source.fromString(response.body)
            val xmltext = scala.xml.XML.load(xmlsource)
            val members: scala.xml.NodeSeq = xmltext \\ "SF_SpatialSamplingFeature"

            val nodes = for {
              member <- members
              identifier = member \ "identifier"
            } yield { (member.toString(), identifier.text) }

            for ((text, id) <- nodes) {
              val foi = models.SosFeatureDB(id, text)

              val retVal = SosFeatureDB.insert(foi)
              s"""inserted $id $retVal"""
            }
            
            
            "done"
          }
      }
    futureResult.map(result => Ok(views.html.index(result)))
  }
  
  def getDaMemebrsAndSave = Action {

    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    val procedures = SosProcedureDB.getAllWithParser
    
    for (proc <- procedures) {
      
    	val request = getDaQueryReq(proc.identifier)
    	val futureResult: Future[String] = WS.url(localSos + "pox").withHeaders("headerKey" -> "headerValue").post(request).
    		map {
    		response =>
    		{
    			
    			val xmlsource = scala.xml.Source.fromString(response.body)
					val xmltext = scala.xml.XML.load(xmlsource)
					val members: scala.xml.NodeSeq = xmltext \\ "dataAvailabilityMember"
					
					val nodes = for {
						member <- members
						identifier = proc.identifier + "_" + java.util.UUID.randomUUID().toString()
					} yield { (member.toString(), identifier) }
					
					for ((text, id) <- nodes) {
						val dam = models.SosDamemberDB(id, text)
								
								val retVal = SosDamemberDB.insert(dam)
								s"""inserted $id $retVal"""
					}
					
					
					"done"
    		}
    	}
    	futureResult.map(text => Logger.debug(text))
    }
    Ok(views.html.index("sent away"))
  }

    def getObsForProc(procnum: Long) = Action {

    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    val procid = s"http://vocab.smart-project.info/ngmp/procedure/${procnum}"
    val phenid = s"http://vocab.smart-project.info/ngmp/phenomenon/${procnum}"
    val procedure = SosProcedureDB.getByID(procid)
    val features = SosFeatureDB.getAllWithParser
    
    for (feature <- features) {
      Logger.debug(feature.identifier)
    	val request = getGetObsQueryReq(procid, feature.identifier)
    	val futureResult: Future[String] = WS.url(localSos + "pox").withHeaders("headerKey" -> "headerValue").post(request).
    		map {
    		response =>
    		{
    			
    			val xmlsource = scala.xml.Source.fromString(response.body)
					val xmltext = scala.xml.XML.load(xmlsource)
					val members: scala.xml.NodeSeq = xmltext \\ "OM_Observation"
					
					val nodes = for {
						member <- members
						identifier = member \ "identifier"
					} yield { (member.toString(), identifier.text) }
					
					for ((text, id) <- nodes) {
						val om = models.SosObservationDB(id, procid, feature.identifier, phenid, text)
								
								val retVal = SosObservationDB.insert(om)
								s"""inserted $id $retVal"""
					}
					
					
					"done"
    		}
    	}
    	futureResult.map(text => Logger.debug(text))
    }
    Ok(views.html.index("sent away"))
  }
  
  def getDescSensorReq(procedure: String): String = {
    val request = s"""<?xml version="1.0" encoding="UTF-8"?>
<swes:DescribeSensor
    xmlns:swes="http://www.opengis.net/swes/2.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" service="SOS" version="2.0.0" xsi:schemaLocation="http://www.opengis.net/swes/2.0 http://schemas.opengis.net/swes/2.0/swes.xsd">
    <swes:procedure>$procedure</swes:procedure>
    <swes:procedureDescriptionFormat>http://www.opengis.net/sensorML/1.0.1</swes:procedureDescriptionFormat>
</swes:DescribeSensor>"""

    request
  }
  
    def getDaQueryReq(procedure: String): String = {
    val request = s"""<?xml version="1.0" encoding="UTF-8"?>
<sos:GetDataAvailability
    xmlns:sos="http://www.opengis.net/sos/2.0" service="SOS" version="2.0.0">
      <sos:procedure>$procedure</sos:procedure>
</sos:GetDataAvailability>"""

    request
  }

  def getBboxFeatureQuery: String = {
    val request = s"""<?xml version="1.0" encoding="UTF-8"?>
<sos:GetFeatureOfInterest
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:sos="http://www.opengis.net/sos/2.0"
    xmlns:fes="http://www.opengis.net/fes/2.0"
    xmlns:gml="http://www.opengis.net/gml/3.2"
    xmlns:swe="http://www.opengis.net/swe/2.0"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:swes="http://www.opengis.net/swes/2.0" service="SOS" version="2.0.0" xsi:schemaLocation="http://www.opengis.net/sos/2.0 http://schemas.opengis.net/sos/2.0/sos.xsd">
    <!-- optional, multiple values possible -->
    <sos:spatialFilter>
        <fes:BBOX>
            <fes:ValueReference>sams:shape</fes:ValueReference>
            <gml:Envelope srsName="http://www.opengis.net/def/crs/EPSG/0/4326">
                <gml:lowerCorner>-52.618591 165.883804</gml:lowerCorner>
                <gml:upperCorner>-29.209970 179.987198</gml:upperCorner>
            </gml:Envelope>
        </fes:BBOX>
    </sos:spatialFilter>
</sos:GetFeatureOfInterest>"""

    request
  }
  
  def getGetObsQueryReq(procedure: String, feature: String) : String = {
    val request = s"""<?xml version="1.0" encoding="UTF-8"?>
<sos:GetObservation
    xmlns:sos="http://www.opengis.net/sos/2.0"
    xmlns:fes="http://www.opengis.net/fes/2.0"
    xmlns:gml="http://www.opengis.net/gml/3.2"
    xmlns:swe="http://www.opengis.net/swe/2.0"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:swes="http://www.opengis.net/swes/2.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" service="SOS" version="2.0.0" xsi:schemaLocation="http://www.opengis.net/sos/2.0 http://schemas.opengis.net/sos/2.0/sos.xsd">
      <sos:procedure>$procedure</sos:procedure>
    <sos:featureOfInterest>$feature</sos:featureOfInterest>
    <sos:responseFormat>http://www.opengis.net/om/2.0</sos:responseFormat>
</sos:GetObservation>"""
    
    request
  } 
}
package controllers

// import play.api._

import play.api.mvc._
import play.api.Logger
import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.Future

import models.AbstractHydro
import models.MetadataDB
import models.MetaData
import models.GeoName

import net.opengis.cat.csw.x202._
import com.vividsolutions.jts.geom.Envelope

object Application extends Controller {

  val myCswUrl = "http://portal.smart-project.info/mycsw/csw"
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def prepareMetadata = Action {
    
    
    val tl = AbstractHydro.getAllWithParserUpdated

    val noAbstractWords = List("No abstract", "No Abstract", "No abstract available.")
    
    val otherTitleWords = List("Book Review", "Book Reviews", "Book reviews", "Editorial", "Foreword", "News", "Presidential Address", "Forthcoming Events", 
        "Notices", "Notes", "Abstracts", "Reports", "Letters To The Editor", "IHD Bulletin", "Errata", "Notice" , "Reviews", "Forthcoming events" ,
        "Obituary", "List of Reviewers", "Summary Contents", "News, Future meetings, Corrigenda for Vol 19", 
        "Discussion - New Zealand Hydrological Society symposium on climate change, snow and ice, and lakes",
        "Corrigendum to Pearce, A.J., Rowe, L.K. 1984. Hydrology Of mid-altitude tussock grasslands, Upper Waipori Catchment: II - Water balance, flow duration, and storm runoff. 23(2):607-72",
        "Metrication in scientific publications", "Invited Editorial",
        "Abstracts of Hydrology Symposium: Soil conservators refresher course, Blenheim, 21 March 1963",
        "Reviews Of Theme 1 - Methods for assessing slope erosion and non-channel sediment sources in upland regions",
        "Reviews of Theme 3 - Human impact on erosion and sediment yield in steeplands",
        "New Zealand Hydrological Society List of Members At 31 May, 1963",
        "Reviews of Theme 2 - Stream channel dynamics and morphology",
        "Editorial: Units, coefficients and dimensions",
        "Editorial Comment", "Reviews On Theme 4 - Impacts and management of steeplands erosion",
        "Errata Subglacial sediment accumulation and basal smoothing -- a mechanism for initiating glacier surging",
        "Report On International Association of Hydrological Sciences Assembly",
        "Journal of Hydrology (N.Z.)Author Index Volumes 1 - 21",
        "List of Recent University Theses Reporting Hydrological Studies",
        "Forthcoming Events - Corrigenda Vol. 18.",
        "Obituary: Mr E.J. Speight", "Future Meetings",
        "Presidential Address 1963", "News And Book Reviews", "Forthcoming Meetings", "Appreciation",
        "Changes in emphases for the Hydrological Society",
        "Journal of Hydrology (NZ) 5 Year Index Volumes 31 - 35",
        "Journal of Hydrology (NZ) 5 Year Index Volumes 36 - 40",
        "Abstracts - Finkelstein, J.",
        "P. Report - International Hydrology Programme,WMO Hydrology and Water Resources Programme, and International Association of Hydrological Sciences",
        "Current research in hydrology in New Zealand universities",
        "Report 26th Congress of The International Geographical Union, August 21-26 1988 Sydney, Australia",
        "1981 Symposium Delegates",
        "Hydrology and the environment (Opening address to the N.Z. Hydrological Society Annual Symposium, University Of Auckland, November 1975)",
        "News - Worldwide survey collects information on artifical recharge of ground water",
        "N Cherry. Editorial: Responsible Science",
        "The Royal Society",
        "Hydrological impressions from a visit to the U.S.",
        "The slackline cableway used in England",
        "Presidential address",
        "A note from the editor",
        "Travel report. US Experience With Transferable Water Permits",
        "Abstracts - Grant, P.J.",
        "Abstracts - Campbell, A.P",
        "Letters to the Editor",
        "Report International Symposium On Erosion And Sedimentation In The Pacific Rim 3-7 August 1987, Corvaillis, Oregon, U.S.A",
        "Errata for Woods & Rowe, p51-86",
        "Report Symposium On Large Scale Effects Of Seasonal Snow Cover IAHS /IUGG Vancouver, August 1987",
        "Editorial: Hydrology and the administrator",
        "Book Review, Publications received, Forthcoming meetings",
        "New Publications and Forthcoming Meetings And Courses",
        "IHD Bulletin New Zealand",
        "Report Australian Study Tour And Groundwater Conference",
        "Report 1986 Hydrology And Water Resources Symposium",
        "Letter",
        "Notice - Travel Grants - Science Awards",
        "Journal of Hydrology (N.Z.) Ten Year Index",
        "In Memorium")
        
    var counter = 0
    
    val gazette = WordProcessing.loadGazetteer("misc/gaz_names.csv")
    val offGazette = gazette.filter(georef => georef.status.startsWith("Official"))
        
    for (abs <- tl) {
    
      if (!otherTitleWords.contains(abs.title)) {
        
    	  if ( (!abs.author.isEmpty()) ||
    	      (abs.author.isEmpty() && !(abs.textabs.isEmpty() || noAbstractWords.contains(abs.textabs)) ) ) {
    	    
    	    var tmpMeta: MetaData = new MetaData(abs.arturl, abs.author, abs.title, abs.year, abs.textabs)
    	    
    	    val fullText = s"$abs.title $abs.textabs"
    	    
    	    val hintFull = gazette.filter(refElem => fullText.contains(refElem.name))
    	    val hintOfficalFull = offGazette.filter(refElem => fullText.contains(refElem.name))
    	    
    	    val finalMeta = enrichGeoRef(hintFull, hintOfficalFull, tmpMeta)
    	    
    	    if (tmpMeta.validate() == true) {
    	      
    	    	val metaXML: MetadataDB = MetadataDB(abs.articleid, tmpMeta.getMDRecordXml())
    	    	
    	    	counter = counter + 1
    	    	
    	    	MetadataDB.insert(metaXML)
    	    	
    	    }
    	    
    	  } else {
    	    Logger.debug(s"NO ABS EMPTY AUTHOR? ${abs.arturl} ### ${abs.author} ### ${abs.title} ### ${abs.year}")
    	  }
    	  
      }
    }
    
    
    Ok(views.html.index(s"loaded and prepared $counter XML MD_Records"))
  }
  
  def enrichGeoRef(hintFull: List[GeoName], hintOfficalFull: List[GeoName], tmpMeta: MetaData) : MetaData = {
    
    val finalBbox = if (!hintOfficalFull.isEmpty) {
    	// val thing = hintOfficalFull.distinct.map(refElem => s"${refElem.name_id}: ${refElem.name}")
    	val bbox = getBboxFromRefs(hintOfficalFull)
    	// Logger.debug(s"OFFICIAL GEOREF ### $thing} ### $bbox")
    	tmpMeta.gmd_identificationInfo_MD_DataIdentification_extent_Description = 
    	  hintOfficalFull.map(refElem => refElem.name).distinct.mkString(", ")
    	bbox
    }
    else if (!hintFull.isEmpty) {
    	// val thing = hintFull.distinct.map(refElem => s"${refElem.name_id}: ${refElem.name}")
    	val bbox = getBboxFromRefs(hintFull)
    	// Logger.debug(s"GENERAL GEOREF ### $thing} ### $bbox")
    	tmpMeta.gmd_identificationInfo_MD_DataIdentification_extent_Description = 
    	  hintFull.map(refElem => refElem.name).distinct.mkString(", ")
    	bbox
    }
    else {
      
      val lowerLeft = models.GeoName(123456,"lowerLeft","Official","","",0.0,
          0.0,"NZGD2000",-47.93848,165.39060)
      
      val upperRight = models.GeoName(123456,"upperRight","Official","","",0.0,
        		  0.0,"NZGD2000",-34.02613,178.73954)
      
      val bbox = getBboxFromRefs(List(lowerLeft, upperRight))
    	// Logger.debug(s"NEW ZEALAND GEOREF ### $bbox")
      tmpMeta.gmd_identificationInfo_MD_DataIdentification_extent_Description = "generic article, or no geo-reference found"
    	bbox
    }
    
    tmpMeta.mapExtentCoordinates = s"BBOX ( ${finalBbox.getMinX()} W ${finalBbox.getMinY()} S  ${finalBbox.getMaxX()} E ${finalBbox.getMaxY()} N )"
		tmpMeta.gmd_identificationInfo_MD_DataIdentification_extent_BBOX_westBoundLongitude = finalBbox.getMinX()
		tmpMeta.gmd_identificationInfo_MD_DataIdentification_extent_BBOX_eastBoundLongitude = finalBbox.getMaxX()
		tmpMeta.gmd_identificationInfo_MD_DataIdentification_extent_BBOX_southBoundLatitude = finalBbox.getMinY()
		tmpMeta.gmd_identificationInfo_MD_DataIdentification_extent_BBOX_northBoundLatitude = finalBbox.getMaxY()

		tmpMeta
  }
  
  def getBboxFromRefs(refs:List[GeoName]) : Envelope = {
    
    import com.vividsolutions.jts.geom.Coordinate
    import com.vividsolutions.jts.geom.GeometryFactory
    import com.vividsolutions.jts.geom.MultiPoint
    import com.vividsolutions.jts.geom.Point
    import com.vividsolutions.jts.geom.PrecisionModel

    val bboxer = new StringBuilder
    
    // var points: Array[Point] = new Array(refs.size)
    val gf: GeometryFactory = new GeometryFactory()
    
    val pointList = for {
      
      ref <- refs
      
      fullPoint = if (ref.crd_datum != null && !ref.crd_datum.isEmpty()) {
        // Logger.debug(s"${ref.crd_datum} ${ref.crd_longitude} ${ref.crd_latitude} ")
        val srs = getEPSG4Name(ref.crd_datum)
        val coords = recodePointTo4326((ref.crd_longitude.toDouble, ref.crd_latitude.toDouble), srs)
        
        val newPoint: Point = gf.createPoint(new Coordinate(coords._1, coords._2))
        
        // bboxer.append(s"${srs} ${coords._1} ${coords._2} ")
        newPoint
      } 
      else if (ref.crd_projection != null && !ref.crd_projection.isEmpty()) {
        // Logger.debug(s"${ref.crd_projection} ${ref.crd_east} ${ref.crd_north} ")
        val srs = getEPSG4Name(ref.crd_datum)
        val coords = recodePointTo4326((ref.crd_east.toDouble, ref.crd_north.toDouble), srs)
        val newPoint: Point = gf.createPoint(new Coordinate(coords._1, coords._2))
        
        // bboxer.append(s"${srs} ${coords._1} ${coords._2} ")
        newPoint
      }
      else {
        gf.createPoint(new Coordinate(0.0, 0.0))
      }
    } yield fullPoint
    
    val points = pointList.filter(poi => ( !(poi.getCoordinate().x == 0.0) && !(poi.getCoordinate().y == 0.0)) )
    
    
    
    var geom: MultiPoint = new MultiPoint(points.toArray, gf)
    geom.setSRID(4326)
    val envelope = geom.getEnvelopeInternal()
    
    // bboxer.toString()
    // envelope.toString()
    envelope
  }
  
  def insertMetadata(dbid: Long) = Action.async {
    
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    
	  var counter = 0
	  
	  val recordText = MetadataDB.getByID(dbid)
	  val recordXml = scala.xml.XML.loadString(recordText.xml)
	  
	  val xmlOptions = models.XmlOptionsHelper.getInstance().getXmlOptions()
	  
	  val insertRequest = <csw:Transaction xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" 
 xmlns:ows="http://www.opengis.net/ows" 
 xmlns:ogc="http://www.opengis.net/ogc"
 xmlns:gmd="http://www.isotc211.org/2005/gmd" 
 xmlns:gco="http://www.isotc211.org/2005/gco" 
 xmlns:gml="http://www.opengis.net/gml" 
 xmlns:xlink="http://www.w3.org/1999/xlink" 
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
 xsi:schemaLocation="http://www.opengis.net/cat/csw/2.0.2 
  http://schemas.opengis.net/csw/2.0.2/CSW-publication.xsd 
  http://www.isotc211.org/2005/gmd http://schemas.opengis.net/csw/2.0.2/profiles/apiso/1.0.0/apiso.xsd" 
 service="CSW" version="2.0.2">
	<csw:Insert>
		{recordXml}
	</csw:Insert>
</csw:Transaction>;
	  
    
    
	  Logger.debug(insertRequest.toString())
    
		val futureResult: Future[String] = WS.url(myCswUrl)
		                     .post(insertRequest).map {

      response =>
        {
          val mytext = response.body
          // Logger.debug(s"saving ... $mytext")
          
          val ok = parseResponse(mytext, "insert")
          ok match {
            case true => mytext
            case false => "error"
          }
        }
    }
		
	  futureResult.map(result => Ok(views.html.index(result)))
    
  }
  
  def deleteByUUID(uuid: String) = Action.async {
    
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    
	  val deleteRequest = <csw:Transaction 
xmlns:ogc="http://www.opengis.net/ogc" 
xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" 
xmlns:ows="http://www.opengis.net/ows" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
xsi:schemaLocation="http://www.opengis.net/cat/csw/2.0.2 
 http://schemas.opengis.net/csw/2.0.2/CSW-publication.xsd" 
service="CSW" version="2.0.2">
  <csw:Delete>
    <csw:Constraint version="1.1.0">
      <ogc:Filter>
        <ogc:PropertyIsEqualTo>
          <ogc:PropertyName>apiso:Identifier</ogc:PropertyName>
          <ogc:Literal>{uuid}</ogc:Literal>
        </ogc:PropertyIsEqualTo>
      </ogc:Filter>
    </csw:Constraint>
  </csw:Delete>
</csw:Transaction>;
	  
    
	  // Logger.debug(insertRequest.toString())
    
		val futureResult: Future[String] = WS.url(myCswUrl)
		                     .post(deleteRequest).map {

      response =>
        {
          val mytext = response.body
          // Logger.debug(s"saving ... $mytext")
          
          // "inserted"
          val ok = parseResponse(mytext, "delete")
          ok match {
            case true => mytext
            case false => "error"
          }
        }
    }
		
	  futureResult.map(result => Ok(views.html.index(result)))
    
  }

  def parseResponse(response: String, transType: String) : Boolean = {
    
    val text = """<csw:TransactionSummary>
  <csw:totalInserted>1</csw:totalInserted>
  <csw:totalUpdated>0</csw:totalUpdated>
  <csw:totalDeleted>0</csw:totalDeleted>
</csw:TransactionSummary>
<csw:InsertResult>
  <csw:BriefRecord>
    <dc:identifier>02b434e4-5238-4184-a284-48e03d53332f</dc:identifier>
    <dc:title>Estimating rainfall factors from data obtained in actual storms AND Speight, E.J. Pivot end-play in Price type current meters</dc:title>
  </csw:BriefRecord>
</csw:InsertResult>"""
    
    val mytree = scala.xml.XML.loadString(response)
    
    val result = transType match {
      case "insert" => {
        
        val nodeList = mytree \\ "TransactionSummary" \ "totalInserted"
        val inserted = nodeList.map(node => node.text).toList.head
        val intInserted = inserted.toInt
        if (intInserted > 0) true else false
      }
      case "delete" => {
        
        val nodeList = mytree \\ "TransactionSummary" \ "totalDeleted"
        val deleted = nodeList.map(node => node.text).toList.head
        val intDeleted = deleted.toInt
        if (intDeleted > 0) true else false
      }
    }
    
    result
  }
  
  def parseUUID(response: String) : List[String] = {
    
    /**
     * gmd:MD_Metadata xsi:schemaLocation="http://www.isotc211.org/2005/gmd http://schemas.opengis.net/csw/2.0.2/profiles/apiso/1.0.0/apiso.xsd">
            <gmd:fileIdentifier>
                <gco:CharacterString>...
      
			 gmd:identificationInfo>
                <gmd:MD_DataIdentification id=".."
     */
    
     val mytree = scala.xml.XML.loadString(response)
     
     val fileNodeList = mytree \\ "MD_Metadata" \ "fileIdentifier" \ "CharacterString"
     
     fileNodeList.map(node => node.text).toList
     
  }
  
  def insertMetadataAll = Action {
    
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    import scala.language.postfixOps
    import scala.concurrent.duration.Duration
    import scala.concurrent.{ ExecutionContext, CanAwait }
    import scala.concurrent.duration.Duration
    import java.util.concurrent.TimeUnit
    
	  var counter = 0
	  
	  val metalist = MetadataDB.getAllWithParser

	  var resList:List[String] = List()
	  
	  for (record <- metalist) {
	    
	    val recordXml = scala.xml.XML.loadString(record.xml)
	    
	    val insertRequest = <csw:Transaction xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" 
 xmlns:ows="http://www.opengis.net/ows" 
 xmlns:ogc="http://www.opengis.net/ogc"
 xmlns:gmd="http://www.isotc211.org/2005/gmd" 
 xmlns:gco="http://www.isotc211.org/2005/gco" 
 xmlns:gml="http://www.opengis.net/gml" 
 xmlns:xlink="http://www.w3.org/1999/xlink" 
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
 xsi:schemaLocation="http://www.opengis.net/cat/csw/2.0.2 
  http://schemas.opengis.net/csw/2.0.2/CSW-publication.xsd 
  http://www.isotc211.org/2005/gmd http://schemas.opengis.net/csw/2.0.2/profiles/apiso/1.0.0/apiso.xsd" 
 service="CSW" version="2.0.2">
	<csw:Insert>
		{recordXml}
	</csw:Insert>
</csw:Transaction>;

  		val futureResult: Future[String] = WS.url(myCswUrl)
  		                     .post(insertRequest).map {
  
        response =>
          {
            val mytext = response.body
            // Logger.debug(s"saving ... $mytext")
            
            val ok = parseResponse(mytext, "insert")
            val result = ok match {
              case true => {
                val mytree = scala.xml.XML.loadString(mytext)
                
                val nodeList = mytree \\ "InsertResult" \ "BriefRecord" \ "identifier"
                val uuid = nodeList.map(node => node.text).toList.head
                Logger.debug(uuid + " inserted")
                uuid
              }
              case false => "error"
            }
            result
          }
      }
  		
  		futureResult.onComplete(responseTry => responseTry.getOrElse("unexpected future try fail"))
  		futureResult.map(futureString => Logger.debug(futureString))
	  }
    
    Ok(views.html.index("finished"))
    
  }
    
  def emptyCatalogue = Action {
    
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    
    val getAllRequest = <csw:GetRecords xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" maxRecords="500" 
 xmlns:ogc="http://www.opengis.net/ogc"
 xmlns:gmd="http://www.isotc211.org/2005/gmd" 
 xmlns:gco="http://www.isotc211.org/2005/gco" 
 xmlns:gml="http://www.opengis.net/gml" 
 xmlns:xlink="http://www.w3.org/1999/xlink" 
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
 xsi:schemaLocation="http://www.opengis.net/cat/csw/2.0.2 
  http://schemas.opengis.net/csw/2.0.2/CSW-publication.xsd 
  http://www.isotc211.org/2005/gmd http://schemas.opengis.net/csw/2.0.2/profiles/apiso/1.0.0/apiso.xsd"
service="CSW"   version="2.0.2" resultType="results" 
outputSchema="http://www.isotc211.org/2005/gmd" >
 <csw:Query typeNames="gmd:MD_Metadata">
   <csw:ElementSetName>summary</csw:ElementSetName>
  </csw:Query>
</csw:GetRecords>
    
    val futureResult: Future[List[String]] = WS.url(myCswUrl)
  		                     .post(getAllRequest).map {
  
        response =>
          {
            val mytext = response.body
            // Logger.debug(s"saving ... $mytext")
            
            val allUuids = parseUUID(mytext)
            allUuids.map { uuid =>
              Logger.debug(uuid)
            }
            allUuids
          }
      }
  		
  	  futureResult.map { result => result.map { singleElem =>
  	      val delReq = deleteRequestText(singleElem)
  	      
  	      val holder = WS.url(myCswUrl).post(delReq).map {
  
            response =>
              {
                val mytext = response.body
                
                val ok = parseResponse(response.body, "delete")
                Logger.debug(s"$singleElem del ok? $ok")
              }
          }
  	    } 
  	  }
  	  
    Ok(views.html.index("finished"))
  }
  
  def deleteRequestText(uuid: String) = <csw:Transaction 
xmlns:ogc="http://www.opengis.net/ogc" 
xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" 
xmlns:ows="http://www.opengis.net/ows" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
xsi:schemaLocation="http://www.opengis.net/cat/csw/2.0.2 
 http://schemas.opengis.net/csw/2.0.2/CSW-publication.xsd" 
service="CSW" version="2.0.2">
  <csw:Delete>
    <csw:Constraint version="1.1.0">
      <ogc:Filter>
        <ogc:PropertyIsEqualTo>
          <ogc:PropertyName>apiso:Identifier</ogc:PropertyName>
          <ogc:Literal>{uuid}</ogc:Literal>
        </ogc:PropertyIsEqualTo>
      </ogc:Filter>
    </csw:Constraint>
  </csw:Delete>
</csw:Transaction>;

  def getEPSG4Name(srs_name: String) : String = {
    srs_name match {
      case "NZGD2000" => "EPSG:4326" // 4167
      case "RSRGD2000" => "EPSG:4765"
      case "NZTM" => "EPSG:2193"
      case "NZGD49" => "EPSG:4272"
      case _ => "EPSG:4326"
    }
  }
  
  // lat, lon input
  def recodePointTo4326( coords:(Double, Double), source_srs: String ) :  (Double, Double) = {

    import org.geotools.geometry.DirectPosition2D
    import org.geotools.geometry.Envelope2D
    import org.geotools.referencing.CRS
    import org.geotools.referencing.GeodeticCalculator
    import org.opengis.feature.simple.SimpleFeature
    import org.opengis.feature.simple.SimpleFeatureType
    import org.opengis.geometry.MismatchedDimensionException
    import org.opengis.referencing.FactoryException
    import org.opengis.referencing.NoSuchAuthorityCodeException
    import org.opengis.referencing.crs.CoordinateReferenceSystem
    import org.opengis.referencing.operation.MathTransform
    import org.opengis.referencing.operation.TransformException
    
    val coordsswitchlist = List("EPSG:27200", "EPSG:900913", "EPSG:3785")
    
    var coordswitch = false;
    val lenient = true;
		
		val miny = coords._1
		val minx = coords._2
		
		val retTuple = try {
		  val sourceCrs: CoordinateReferenceSystem = CRS.decode(source_srs)
			val targetCrs: CoordinateReferenceSystem = CRS.decode("EPSG:4326")
			
			val mathTransform: MathTransform = CRS.findMathTransform(sourceCrs,
					targetCrs, lenient)
			val srcDirectPosition2D: DirectPosition2D = new DirectPosition2D(
					sourceCrs, minx, miny)
			var destDirectPosition2D: DirectPosition2D = new DirectPosition2D()
			mathTransform.transform(srcDirectPosition2D, destDirectPosition2D)
			
			(destDirectPosition2D.y,destDirectPosition2D.x)
			
		} catch {
		  case e:NoSuchAuthorityCodeException => {
		    Logger.error(s"$coords , $source_srs  " + e.getLocalizedMessage())
		    (0.0,0.0)
		  }
		  case e:FactoryException => {
		    Logger.error(s"$coords , $source_srs  " + e.getLocalizedMessage())
		    (0.0,0.0)
		  }
		  case e:MismatchedDimensionException => {
			  Logger.error(s"$coords , $source_srs  " + e.getLocalizedMessage())
			  (0.0,0.0)
		  }
		  case e:TransformException => {
			  Logger.error(s"$coords , $source_srs  " + e.getLocalizedMessage())
			  (0.0,0.0)
		  }
		}
		
		retTuple
    
  }
}

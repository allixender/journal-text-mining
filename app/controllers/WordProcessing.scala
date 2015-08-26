package controllers

import play.api.mvc._
import play.api.Logger
import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.Future
import scala.collection.mutable.ArrayBuffer

import models.AbstractHydro
import models.GeoName

import java.io.File
import java.io.FileReader

import scala.io.Source
import au.com.bytecode.opencsv.CSVReader

object WordProcessing extends Controller {

  val noAbstractWords = List("No abstract", "No Abstract", "No abstract available.")

  val otherTitleWords = List("Book Review", "Book Reviews", "Book reviews", "Editorial", "Foreword", "News", "Presidential Address", "Forthcoming Events",
    "Notices", "Notes", "Abstracts", "Reports", "Letters To The Editor", "IHD Bulletin", "Errata", "Notice", "Reviews", "Forthcoming events",
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

  def compareTest1 = Action {

    import scala.language.postfixOps

    val tl = AbstractHydro.getAllWithParserUpdated

    var counterArt = 0
    var counterGeo = 0
    var counterGeoOff = 0
    var counterGeoFull = 0
    var counterGeoOffFull = 0

    val gazetteer = loadGazetteer("misc/gaz_names.csv")
    val offGazette = gazetteer.filter(georef => georef.status.startsWith("Official"))

    for (abs <- tl) {

      // Logger.debug(s"gazetteer size ${gazetteer.size}")

      if (!otherTitleWords.contains(abs.title)) {

        if ((!abs.author.isEmpty()) ||
          (abs.author.isEmpty() && !(abs.textabs.isEmpty() || noAbstractWords.contains(abs.textabs)))) {

          val textdict = toSingleDict(abs.textabs) ++ toSingleDict(abs.title)

          val fullText = s"$abs.title $abs.textabs"

          counterArt = counterArt + 1

          //    	    val hint:List[GeoName] = for (ref <- gazetteer) yield {
          //    	      if (textdict.contains(ref.name) ) {
          //    	        ref
          //    	      }
          //    	    }
          val hint = gazetteer.filter(refElem => textdict.contains(refElem.name))
          val hintOffical = offGazette.filter(refElem => textdict.contains(refElem.name))

          val hintFull = gazetteer.filter(refElem => fullText.contains(refElem.name))
          val hintOfficalFull = offGazette.filter(refElem => fullText.contains(refElem.name))

          if (!hint.isEmpty) {

            val thing = hint.distinct.map(refElem => s"${refElem.name_id}: ${refElem.name}")
            counterGeo = counterGeo + 1
            Logger.debug(s"GEOREF found ${abs.arturl} ### geo hints ${hint.size} ### $thing}")
          }
          if (!hintOffical.isEmpty) {

            val thing = hintOffical.distinct.map(refElem => s"${refElem.name_id}: ${refElem.name}")
            counterGeoOff = counterGeoOff + 1
            Logger.debug(s"OFFICIAL GEOREF found ${abs.arturl} ### geo hints ${hintOffical.size} ### $thing}")
          }
          if (!hintFull.isEmpty) {

            val thing = hintFull.distinct.map(refElem => s"${refElem.name_id}: ${refElem.name}")
            counterGeoFull = counterGeoFull + 1
            Logger.debug(s"FULLTEXT GEOREF found ${abs.arturl} ### geo hints ${hintFull.size} ### $thing}")
          }
          if (!hintOfficalFull.isEmpty) {

            val thing = hintOfficalFull.distinct.map(refElem => s"${refElem.name_id}: ${refElem.name}")
            counterGeoOffFull = counterGeoOffFull + 1
            Logger.debug(s"OFFICIAL FULLTEXT GEOREF found ${abs.arturl} ### geo hints ${hintOfficalFull.size} ### $thing}")
          }

        } else {
          Logger.debug(s"NO ABS EMPTY AUTHOR? ${abs.arturl} ### ${abs.author} ### ${abs.title} ### ${abs.year}")
        }

        Logger.debug(s"----------------------------------------------------")

      }

    }
    Ok(views.html.index(s"getting started test1, $counterGeo of $counterArt articles have geo words, $counterGeoOff in official, $counterGeoFull via Full with unofficial, and $counterGeoOffFull via fulltext only official"))
  }

  def toSingleDict(plaintext: String) = {
    val words = plaintext.split("\\W+").toSeq
    words.distinct.zipWithIndex.toMap
  }

  def compare2 = Action {
    val gazetteer = loadGazetteer("misc/gaz_names.csv")

    val offGazette = gazetteer.filter(georef => georef.status.startsWith("Official"))

    var counter = 0

    for (ref <- gazetteer) {
      // Logger.debug(s"counter $counter | ${ref}")

      counter = counter + 1
    }

    Logger.debug(s"gazetteer size ${gazetteer.size} Officials ${offGazette.size}")

    Ok(views.html.index(s"getting started test2 ${gazetteer.size} counter though $counter"))
  }

  def loadGazetteer(filename: String): List[models.GeoName] = {
    val reader = new CSVReader(new FileReader(filename))

    // 0 name_id,
    // 1 name,
    // 2 status,
    // 3 feat_type,
    // 4 nzgb_ref,
    // 5 land_district,
    // 6 crd_projection,
    // 7 crd_north,
    // 8 crd_east,
    // 9 crd_datum,
    // 10 crd_latitude,
    // 11 crd_longitude,
    // 12 info_ref,
    // 13 info_origin,
    // 14 info_description,
    // 15 info_note,
    // 16 feat_note,
    // 17 maori_name,
    // 18 cpa_legislation,
    // 19 conservancy,
    // 20 doc_cons_unit_no,
    // 21 doc_gaz_ref,
    // 22 treaty_legislation,
    // 23 geom_type,accuracy,
    // 24 gebco,
    // 25 region,
    // 26 scufn,
    // 27 height,
    // 28 ant_pn_ref,
    // 29 ant_pgaz_ref,
    // 30 scar_id,
    // 31 scar_rec_by,
    // 32 accuracy_rating,
    // 33 desc_code,
    // 34 rev_gaz_ref,
    // 35 rev_treaty_legislation

    import scala.collection.JavaConversions._
    import scala.collection.mutable.ListBuffer
    import scala.collection.immutable.Set

    var x = 0

    //    val readin = for ( row <- reader.readAll) yield {
    //      Logger.debug(row.mkString("<>"))
    //      val newRef = GeoName(row(0).toLong,row(1),row(5),row(6),row(7).toDouble,row(8).toDouble,row(9),row(10).toDouble,row(11).toDouble)
    //      newRef
    //    }

    // readin.toSeq
    import scala.util.Try
    import scala.util.Success
    import scala.util.Failure

      def parseDouble(text: String): Try[Double] = {
        Try(text.toDouble)
      }

      def parseLong(text: String): Try[Long] = {
        Try(text.toLong)
      }

    val readintry = for (row <- reader.readAll) yield {

      val resultRef = try {
        val name_id = parseLong(row(0)).getOrElse(-1.toLong)
        val name = row(1)
        val status = row(2)
        val land_district = row(5)
        val crd_projection = row(6)
        val crd_north = parseDouble(row(7)).getOrElse(0.0)
        val crd_east = parseDouble(row(8)).getOrElse(0.0)
        val crd_datum = row(9)
        val crd_latitude = parseDouble(row(10)).getOrElse(0.0)
        val crd_longitude = parseDouble(row(11)).getOrElse(0.0)

        val newRef = GeoName(name_id, name, status, land_district, crd_projection, crd_north,
          crd_east, crd_datum, crd_latitude, crd_longitude)

        // Logger.debug("new ref #" + x + " " + newRef)
        x = x + 1
        newRef

      } catch {
        case e: Exception => {
          val badRef = GeoName(-1, "", "", "", "", 0, 0, "", 0, 0)
          badRef
        }
      }
      resultRef
    }

    readintry.filter { ref =>
      !(ref.name_id == -1)
    }.toList
  }

}

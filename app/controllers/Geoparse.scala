package controllers

import play.api.mvc._
import play.api.Logger
import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.Future
import scala.collection.mutable.ArrayBuffer

import models.AbstractHydro
import models.GeoName

import java.io._

import scala.io.Source
import scala.util.matching.Regex
import au.com.bytecode.opencsv.CSVReader

object Geoparse extends Controller {

  val geonames = GeoName.getAllWithParser

  val articles = AbstractHydro.getAllWithParserUpdated

  val noAbstractWords = List("No abstract", "No Abstract", "No abstract available.")

  val noTitleWords = List("Book Review", "Book Reviews", "Book reviews", "Editorial", "Foreword", "News", "Presidential Address", "Forthcoming Events",
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

  // returns true if more than 0 true matches for occurrence of stopword in testword happened
  def testNoTitleReverse(testW: String): Boolean = {

    val trueMatchesFound = noTitleWords.map { stopW =>
      val res1 = if (testW.toLowerCase().contains(stopW.toLowerCase())) {
        true
      } else {
        false
      }
      res1
    }.count(testB => testB && true)

    (trueMatchesFound > 0)

  }

  // returns true if more than 0 true matches for occurrence of stopword in testword happened
  def testNoAbstractReverse(testW: String): Boolean = {

    val trueMatchesFound = noAbstractWords.map { stopW =>
      val res1 = if (testW.toLowerCase().contains(stopW.toLowerCase())) {
        true
      } else {
        false
      }
      res1
    }.count(testB => testB && true)

    (trueMatchesFound > 0)

  }

  def loadAndSaveGazetteer = Action {

    import scala.collection.JavaConversions._
    import scala.collection.mutable.ListBuffer
    import scala.collection.immutable.Set
    import scala.util.Try
    import scala.util.Success
    import scala.util.Failure

      def parseDouble(text: String): Try[Double] = {
        Try(text.toDouble)
      }
      def parseLong(text: String): Try[Long] = {
        Try(text.toLong)
      }
    var x = 0
    var y = 0
    val reader = new CSVReader(new FileReader("misc/gaz_names.csv"))

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

        if (models.GeoName.insert(newRef)) {
          y = y + 1
        }
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

    Ok(views.html.index(s"parsed $x georefs, inserted $y in DB"))
  }

  def loadAndSaveArticles = Action {

    import scala.collection.JavaConversions._
    import scala.collection.mutable.ListBuffer
    import scala.collection.immutable.Set
    import scala.util.Try
    import scala.util.Success
    import scala.util.Failure

//    val baseFile = "C:/Users/kmoch/Dropbox/SMART/04_Papers/2015_01_SMART_Portal_CSW_JoHNZ/2015_01_Refocus_CSW/"
    val baseFile = "C:/Users/kmoch/Dropbox/SMART/04_Papers/2015_01_SMART_Portal_CSW_JoHNZ/2015_01_Refocus_CSW/pdfs-to-check-again/"

    val x = articles.count(x => x.isInstanceOf[AbstractHydro])

    val onlyThoseLoadAgain = List(69, 92, 106, 138, 171,  184, 256, 289, 301, 319, 341, 377, 383, 401, 441, 474, 483, 497, 526, 564, 575, 592, 651, 675, 689).map( _.toLong)
    
    val filtered1 = articles.filter { article =>

      !(noTitleWords.contains(article.title)) && !(testNoTitleReverse(article.title))

    }.filter { article2 =>

      !(noAbstractWords.contains(article2.textabs)) && !(testNoAbstractReverse(article2.title))

    }.filter( art => onlyThoseLoadAgain.contains(art.arturl.toLong))

    
    val newArticles = for {
      
      article3 <- filtered1
      arturl = article3.arturl
//      filename = baseFile + s"johnz-${arturl.toString}.txt.utf8"
      filename = baseFile + s"johnz-${arturl.toString}.txt"

      fulltext = try {
        val fileSource = Source.fromFile(filename).getLines().mkString
        fileSource
      } catch {
        case e: Exception => {
          Logger.error(e.getMessage())
          "EMPTY"
        }
      }

      article4 = article3.copy(fulltext = Some(fulltext))
      result = AbstractHydro.updateWithFullByID(article4)

    } yield article4

    val y = filtered1.count(x => x.isInstanceOf[AbstractHydro])

    // we want to count the ones
    val z = newArticles.filter{ article5 =>
      if (article5.fulltext.isDefined) {
        !article5.fulltext.get.equalsIgnoreCase("EMPTY")
      } else {
        false
      }
    }.count(x => x.isInstanceOf[AbstractHydro])

    Ok(views.html.index(s"took $x db articles, used after stopwords $y, found fulltext ${z}"))
  }

  def parse1 = Action {

    val y = geonames.count(x => x.isInstanceOf[GeoName])
    val x = articles.count(x => x.isInstanceOf[AbstractHydro])

    val filtered1 = articles.filter { article =>

      !(noTitleWords.contains(article.title)) && !(testNoTitleReverse(article.title))

    }.filter { article2 =>

      !(noAbstractWords.contains(article2.textabs)) && !(testNoAbstractReverse(article2.title))

    }.filter{ article5 =>
      if (article5.fulltext.isDefined) {
        !article5.fulltext.get.equalsIgnoreCase("EMPTY")
      } else {
        false
      }
    }
    val z = filtered1.count(x => x.isInstanceOf[AbstractHydro])

    Ok(views.html.index(s"have $y georefs, $x articles in DB, and $z filtered (no stopwords and fulltext) to work with "))

  }

  // from http://stackoverflow.com/questions/4604237/how-to-write-to-a-file-in-scala
  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try { op(p) } finally { p.close() }
  }

  def parse2 = Action {

    val long1 = System.currentTimeMillis()
    
    val y = geonames.count(x => x.isInstanceOf[GeoName])

    val articles = AbstractHydro.getAllWithParserUpdated
    val x = articles.count(x => x.isInstanceOf[AbstractHydro])

    /*#########################################################################
     * PRE-FILTERING
     ########################################################################*/
    val filtered1 = articles.filter { article =>

      !(noTitleWords.contains(article.title)) && !(testNoTitleReverse(article.title))

    }.filter { article2 =>

      !(noAbstractWords.contains(article2.textabs)) && !(testNoAbstractReverse(article2.title))

    }.filter{ article5 =>
      if (article5.fulltext.isDefined) {
        !article5.fulltext.get.equalsIgnoreCase("EMPTY")
      } else {
        false
      }
    }
    val z = filtered1.count(x => x.isInstanceOf[AbstractHydro])

    val collector1 = for {
      article3 <- filtered1
      numTitle = article3.title.split(" ").length
      numAbstract = article3.textabs.split(" ").length
      numFulltext = article3.fulltext.getOrElse("").split(" ").length
    } yield s"${article3.articleid},${article3.arturl},${article3.year},$numTitle,$numAbstract,$numFulltext"

    printToFile(new File("first-stats.csv")) { p =>
      collector1.foreach(p.println)
    }

    val col1Count = collector1.length
    val long2 = System.currentTimeMillis() / 1000
    Logger.info(s"pre-filtering took $long2 secs")

    /*#########################################################################
     * next stats filtering only TITLE, this will then later also be done based
     * on ABSTRACT and FULLTEXT
     ########################################################################*/

    val collector2 = for {
      article4 <- filtered1

      // TODO we will try the REGEX approach afterwards
      georefs = findGeoRefsInTextWithRegex(article4.fulltext.getOrElse(""))
      // georefs = findGeoRefsInText(article4.title)

    } yield (article4, georefs)

    val col2Count = collector2.length

    val collector3 = collector2.flatMap {
      case (article, georefList) => {
        georefList.map(geo => (article, geo))
      }
    }

    val col3Count = collector3.length

    val collector4 = for {
      (article, geo) <- collector3

    } yield s""""${article.articleid}","${article.arturl}","${geo.name_id.toString()}","${geo.name}","${geo.status}","${geo.crd_datum} (${geo.crd_latitude} ${geo.crd_longitude})""""

    val col4Count = collector4.length

    val long3 = System.currentTimeMillis() / 1000
    Logger.info(s"TITLE filtering took $long3 secs")
    
    printToFile(new File("matching-title-stats.csv")) { p =>
      collector4.foreach(p.println)
    }

    /*#########################################################################
     * next stats filtering only ABSTRACT, 
     * this will then later also be done based on FULLTEXT
     ########################################################################*/

    val collector2abs = for {
      article4 <- filtered1

      // TODO we will try the REGEX approach afterwards
      georefs = findGeoRefsInTextWithRegex(article4.fulltext.getOrElse(""))
      // georefs = findGeoRefsInText(article4.textabs)

    } yield (article4, georefs)

    val col2Countabs = collector2abs.length

    val collector3abs = collector2abs.flatMap {
      case (article, georefList) => {
        georefList.map(geo => (article, geo))
      }
    }

    val col3Countabs = collector3abs.length

    val collector4abs = for {
      (article, geo) <- collector3abs

    } yield s""""${article.articleid}","${article.arturl}","${geo.name_id.toString()}","${geo.name}","${geo.status}","${geo.crd_datum} (${geo.crd_latitude} ${geo.crd_longitude})""""

    val col4Countabs = collector4abs.length

    val long4 = System.currentTimeMillis() / 1000
    Logger.info(s"ABSTRACT filtering took $long4 secs")
    
    printToFile(new File("matching-abstract-stats.csv")) { p =>
      collector4abs.foreach(p.println)
    }

    /*#########################################################################
     * next stats filtering only FULLTEXT
     ########################################################################*/

    /*
    val collector2full = for {
      article4 <- filtered1

      // TODO we will try the REGEX approach afterwards
//      georefs = findGeoRefsInText(article4.fulltext.getOrElse(""))
      georefs = findGeoRefsInTextWithRegex(article4.fulltext.getOrElse(""))

    } yield (article4, georefs)

    val col2Countfull = collector2full.length

    val collector3full = collector2full.flatMap {
      case (article, georefList) => {
        georefList.map(geo => (article, geo))
      }
    }

    val col3Countfull = collector3full.length

    val collector4full = for {
      (article, geo) <- collector3full

    } yield s""""${article.articleid}","${article.arturl}","${geo.name_id.toString()}","${geo.name}","${geo.status}","${geo.crd_datum} (${geo.crd_latitude} ${geo.crd_longitude})""""

    val col4Countfull = collector4full.length

    val long5 = System.currentTimeMillis() / 1000
    Logger.info(s"FULLTEXT filtering took $long5 secs")
    
    printToFile(new File("matching-fulltext-stats.csv")) { p =>
      collector4full.foreach(p.println)
    }
    */
    /*#########################################################################
     * last one should do it for like TITLE-ABSTRACT together vs ALL-FULLTEXT?
     ########################################################################*/

    /*#########################################################################
     * the same possibly with the glossary?
     ########################################################################*/

    /*#########################################################################
     * conclusion
     ########################################################################*/

    Ok(views.html.index(s"""have $y georefs, $x articles in DB, and $z filtered (no stopwords and fulltext) to work with, 
            co11 $col1Count availbe article objects metrics, 
            ### TITLE co12 $col2Count title metrics (arts only title-based, with georefs),
            co13 $col3Count title metrics, co14 $col4Count title metrics (overall single art-georef elements, only title-based),
            ### Abstract  co12abs $col2Countabs abstract metrics (arts only abstract-based, with georefs),
            co13abs $col3Countabs abstract metrics, co14abs $col4Countabs abstract metrics (overall single art-georef elements, only abstract-based) """))
          /*  ### Fulltext  co12full $col2Countfull fulltext metrics (arts only fulltext-based, with georefs),
            co13full $col3Countfull fulltext metrics, co14full $col4Countfull fulltext metrics (overall single art-georef elements, only fulltext-based)
            */  

  }

  def findGeoRefsInText(testW: String): List[GeoName] = {
    val filtered = geonames.filter{ georef =>
      testW.toLowerCase().contains(georef.name.toLowerCase())
    }
    filtered
  }

  def findGeoRefsInTextWithRegex(testW: String): List[GeoName] = {
    val filtered = geonames.filter{ georef =>
      val GeoWord = georef.name.toLowerCase().r
      // testW.toLowerCase().contains(georef.name.toLowerCase())
      GeoWord.pattern.matcher(testW.toLowerCase()).matches
    }
    filtered
  }
  
  def findGeoRefsInTextWithRegexSingleDict(testW: String): List[GeoName] = {
    // incoming title. abstract or fulltext as single string
    // split \\W+ to seq of Strings
    val dict = toSingleDict(testW)
    // for each GeoName filter if geoname regex matches a word from the dict
    val filtered = geonames.filter{ georef =>
      val GeoWord = georef.name.toLowerCase().r
      // actually that's what I thought about last night, we don't want to break down the title/abstract/fulltext, 
      // otherwise we can't match multi word geonames
      val hasMatches = dict.filter { textWord =>
        // if matches -> boolean true, means it's taken, therefore hasMatches grows
        GeoWord.pattern.matcher(textWord.toLowerCase()).matches
      }
      // if hasMatches is greater 0 means, the geoname single dict regex matching found a match,
      // therefore the Geoname can be taken upstream
      hasMatches.length > 0
    }
    filtered
  }

  def toSingleDict(plaintext: String): Seq[String] = {
    val words = plaintext.split("\\W+").toSeq
    // words.distinct.zipWithIndex.toMap
    words.distinct
  }

}

package controllers

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import play.api._
import play.api.Logger._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.Future
import models.AbstractRoyal
import models.RawRoyalPage
import models.MetadataDB
import models.GeoName

object RoyalTest extends Controller {

  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  import play.api.Play.current
  import play.api.libs.ws._
  import scala.collection.JavaConverters._

  // New Zealand Journal of Marine and Freshwater Research
  // http://www.tandfonline.com/loi/tnzm20
  // volumes 1 - 49, each 4 issues, 1967 - 2015
  // http://www.tandfonline.com/toc/tnzm20/49/1

  val url = "http://www.tandfonline.com"
  val tnzm20BaseUrl = "/toc/tnzm20"
  val tnzm20Range = 1 to 49


  // New Zealand Journal of Geology and Geophysics
  // http://www.tandfonline.com/loi/tnzg20
  // volumes 1 - 49, each 4 issues, 1958 -2015
  // http://www.tandfonline.com/toc/tnzg20/33/4

  val tnzg20BaseUrl = "toc/tnzg20"
  val tnzg20Range = 1 to 58

  val issueRange = 1 to 4

  // params get
  val sessAppend = "?cookieSet=1"

  def getRoyalfromweb = Action {

    import org.jsoup.Jsoup
    import org.jsoup.nodes.Document
    import org.jsoup.select.Elements

    tnzm20Range.map { issue =>

      println(s"map $issue")

      issueRange.map { num =>


        val goUrl = s"${url}${tnzm20BaseUrl}/$issue/$num$sessAppend"
        println(s"map $goUrl")



        val futureResult: Future[String] = WS.url(goUrl)
          .withFollowRedirects(true)
          .withRequestTimeout(10000)
          .withQueryString("cookieSet" -> "1")
          .get()
          .map {
          response => {
            // val matcher = new scala.util.matching.Regex(".*volume_id=(\\d{1,2})\">Vol (\\d{1,2}) (\\d{4}).*", "url", "volnum", "year")
            // val thingy = for (matcher(url, volnum, year) <- matcher findAllIn response.body) yield (url, volnum, year)
            // val tlist = thingy.toList

            val doc: Document = Jsoup.parse(response.body)
            val mydata: Elements = doc.select("div.article.original.as2")
            // for element in mydata => element.select("a.entryTitle")
            // .attr("href")

            val iter = mydata.iterator()
            while (iter.hasNext) {
              val elem = iter.next()
              val ref = elem.select("a.entryTitle")
              val link = ref.attr("href")
              val newUrl = s"${url}${link}?$sessAppend"
              println(s"offering newUrl")
              // Logger.debug(s"$jurl $voln $year")
              // createNewQueries(jurl, voln, year)
            }

            // for ((jurl, voln, year) <- tlist) {
            // Logger.debug(s"$jurl $voln $year")
            // createNewQueries(jurl, voln, year)
            //}

            s"articles found: ${mydata.size()} in issue $goUrl"
          }
        }

        futureResult.onComplete { case msg =>
          println(msg.getOrElse("failed"))
        }
        futureResult.onFailure { case msg =>
          println(msg.getLocalizedMessage)
        }
      }
    }

    Ok(views.html.index("running ..."))

  }

  def loadFromFSMarine = Action {

    tnzm20Range.map { countMarine =>

      issueRange.map { issue =>

        println(s"trying $countMarine / $issue")
        val source = scala.io.Source.fromFile(s"/home/akmoch/dev/build/lucene-hydroabstracts-scala/misc/royal-files/issues-html/marine-${countMarine}-${issue}.html")
        val lines = try source.mkString finally source.close()

        val doc: Document = Jsoup.parse(lines)
        val mydata: Elements = doc.select("div.article.original.as2")

        val iter = mydata.iterator()
        while (iter.hasNext) {
          val elem = iter.next()
          val ref = elem.select("a.entryTitle")
          val link = ref.attr("href")
          val newUrl = s"${url}${link}?$sessAppend"
          println(s"offering $newUrl")
          // Logger.debug(s"$jurl $voln $year")
          // createNewQueries(jurl, voln, year)
        }
      }
    }

    Ok(views.html.index("load from royal files folder"))
  }

  def loadFromFSGeol = Action {

    tnzg20Range.map { countGeol =>

      issueRange.map { issue =>

        println(s"trying $countGeol / $issue")
        val source = scala.io.Source.fromFile(s"/home/akmoch/dev/build/lucene-hydroabstracts-scala/misc/royal-files/issues-html/geol-${countGeol}-${issue}.html")
        val lines = try source.mkString finally source.close()

        val doc: Document = Jsoup.parse(lines)
        val mydata: Elements = doc.select("div.article.original.as2")

        val iter = mydata.iterator()
        while (iter.hasNext) {
          val elem = iter.next()
          val ref = elem.select("a.entryTitle")
          val link = ref.attr("href")
          val newUrl = s"${url}${link}"
          println(s"offering $newUrl")
          // Logger.debug(s"$jurl $voln $year")
          // createNewQueries(jurl, voln, year)
        }
      }
    }

    Ok(views.html.index("load from royal files folder geology"))
  }

  def royalAbsLoader = Action {

    var counter = 0
    import sys.process._
    val path = "/home/akmoch/dev/build/lucene-hydroabstracts-scala/misc/royal-files/full-art/"

    import scala.language.postfixOps

    val contents = Process("ls -1 /home/akmoch/dev/build/lucene-hydroabstracts-scala/misc/royal-files/full-art/").lineStream

    contents.foreach { line =>

      val html = scala.io.Source.fromFile(s"${path}${line}").mkString
      RawRoyalPage.insert(RawRoyalPage(counter, html, line, "1", "2000"))
      counter = counter + 1
    }

    Ok(views.html.index(s"file system source"))

  }

  def royalParser1 = Action {

    import org.jsoup.Jsoup
    import org.jsoup.nodes.Document
    import org.jsoup.select.Elements

    val artList = RawRoyalPage.getAllWithParser

    for (article <- artList) {

      try {

        // dirty preps
        var articleid = article.articleid
        var authortitle = ""
        var textabs = ""
        var author = ""
        var title = ""
        var year = 0l
        var arturl = ""
        var fulltext = ""

        val matcher = new scala.util.matching.Regex(".*\\((\\d{4})\\).*", "year")

        var baseurl = "http://www.tandfonline.com"
        var fulltexturl = "http://www.tandfonline.com"

        val doc: Document = Jsoup.parse(article.fulltext)

        val links: Elements = doc.select("a.pdf")
        val iter = links.iterator()
        while (iter.hasNext) {
          val elem = iter.next()
          if (elem.hasText && elem.text().equalsIgnoreCase("Download full text")) {
            // logger.debug(s"${elem.attr("href")} ${elem.text()}")
            fulltexturl = baseurl + elem.attr("href")
          }
        }

        val shortUrl = doc.select("a.pdf").first.attr("href")
        arturl = baseurl + shortUrl
        val title1 = doc.select("div.description").select("h1").text.trim

        // <meta name="dc.Creator" content=" D. M.   Garner ">
        val metas: Elements = doc.select("meta")
        val miter = metas.iterator()
        while (miter.hasNext) {
          val elem = miter.next()
          if (elem.hasAttr("property") && elem.attr("property").equalsIgnoreCase("og:description")) {
            // logger.debug(s"${elem.attr("content")}")
            authortitle = elem.attr("content")
            val thingy = for (matcher(year) <- matcher findAllIn authortitle) yield (year)
            if (thingy.hasNext) {
              year = thingy.next().toLong
            }
          }
          if (elem.hasAttr("name") && elem.attr("name").equalsIgnoreCase("dc.Title")) {
            // logger.debug(s"${elem.attr("content")}")
            title = elem.attr("content").trim
            if (!title1.equalsIgnoreCase(title)) {
              // logger.info(s"diff title: $title || $title1 ")
            }
          }
          if (elem.hasAttr("name") && elem.attr("name").equalsIgnoreCase("dc.Creator")) {
            // logger.debug(s"${elem.attr("content")}")
            author = author + ", " + elem.attr("content")

          }
          if (elem.hasAttr("name") && elem.attr("name").equalsIgnoreCase("dc.Description")) {
            // logger.debug(s"${elem.attr("content")}")
            textabs = elem.attr("content")
          }
        }

        val abs = AbstractRoyal(
          articleid,
          authortitle,
          textabs,
          author,
          title,
          year,
          arturl,
          Some(fulltext))

        AbstractRoyal.insertFull(abs)
        // AbstractRoyal.insertF(abs)

      } catch {
        case e: IllegalArgumentException => Logger.error("illegal arg. exception")
        case e: IndexOutOfBoundsException => Logger.error("illegal state exception")
        case e: NoSuchElementException => Logger.error("NoSuchElementException")
        case e: java.io.IOException => Logger.error("IO exception")
      }
    }

    Ok(views.html.index(s"royal parsing and absract insert"))
  }

  def royalParse2 = Action {

    import org.jsoup.Jsoup
    import org.jsoup.nodes.Document
    import org.jsoup.select.Elements

    val artList = RawRoyalPage.getAllWithParser

    val arts = AbstractRoyal.getAllWithParser

    var counter = 0
    for (article <- artList) {

      try {

        // dirty preps
        var articleid = article.articleid
        var authortitle = ""
        var textabs = ""
        var author = ""
        var title = ""
        var year = 0l
        var arturl = ""
        var fulltext = ""

        val matcher = new scala.util.matching.Regex(".*\\((\\d{4})\\).*", "year")

        var baseurl = "http://www.tandfonline.com"
        var fulltexturl = "http://www.tandfonline.com"

        val doc: Document = Jsoup.parse(article.fulltext)

        val links: Elements = doc.select("a.pdf")
        val iter = links.iterator()
        while (iter.hasNext) {
          val elem = iter.next()
          if (elem.hasText && elem.text().equalsIgnoreCase("Download full text")) {
            // logger.debug(s"${elem.attr("href")} ${elem.text()}")
            fulltexturl = baseurl + elem.attr("href")
          }
        }

        val shortUrl = doc.select("a.pdf").first.attr("href")
        arturl = baseurl + shortUrl
        val title1 = doc.select("div.description").select("h1").text.trim

        // <meta name="dc.Creator" content=" D. M.   Garner ">
        val metas: Elements = doc.select("meta")
        val miter = metas.iterator()
        while (miter.hasNext) {
          val elem = miter.next()
          if (elem.hasAttr("property") && elem.attr("property").equalsIgnoreCase("og:description")) {
            // logger.debug(s"${elem.attr("content")}")
            authortitle = elem.attr("content")
            val thingy = for (matcher(year) <- matcher findAllIn authortitle) yield (year)
            if (thingy.hasNext) {
              year = thingy.next().toLong
            }
          }
          if (elem.hasAttr("name") && elem.attr("name").equalsIgnoreCase("dc.Title")) {
            // logger.debug(s"${elem.attr("content")}")
            title = elem.attr("content").trim
            if (!title1.equalsIgnoreCase(title)) {
              logger.info(s"diff title: $title || $title1 ")
            }
          }
          if (elem.hasAttr("name") && elem.attr("name").equalsIgnoreCase("dc.Creator")) {
            // logger.debug(s"${elem.attr("content")}")
            author = author + ", " + elem.attr("content")

          }
          if (elem.hasAttr("name") && elem.attr("name").equalsIgnoreCase("dc.Description")) {
            // logger.debug(s"${elem.attr("content")}")
            textabs = elem.attr("content")
          }
        }


        val yesDoIt = arts.filter { article =>
          article.authortitle.equals(authortitle)
        }
        if (yesDoIt.length == 1) {
          val matchArt = yesDoIt.head
          val updatedArticle = AbstractRoyal(
            matchArt.articleid,
            matchArt.authortitle,
            matchArt.textabs.replaceFirst("Summary ", ""),
            author,
            title,
            matchArt.year,
            matchArt.arturl,
            matchArt.fulltext)
          // logger.info(s"found article ${title} with ${author} ")
          counter = counter + 1
          AbstractRoyal.updateWithFullByID(updatedArticle)
        } else {
          logger.warn(s"no found article")
        }

      } catch {
        case e: IllegalArgumentException => Logger.error("illegal arg. exception")
        case e: IndexOutOfBoundsException => Logger.error("illegal state exception")
        case e: NoSuchElementException => Logger.error("NoSuchElementException")
        case e: java.io.IOException => Logger.error("IO exception")
      }
    }
    Ok(views.html.index(s"royal parsing and update for ${counter} articles"))
  }

  def pdfLoadList = Action {

    val arts = AbstractRoyal.getAllWithParser
    arts.map(art => logger.info(art.arturl))
    Ok(views.html.index("downloading royal pdfs."))
  }

  def royalFullTextLoader2 = Action {
    var counter = 0
    val arts = AbstractRoyal.getAllWithParserUpdated
    import sys.process._
    val path = "/home/akmoch/dev/build/lucene-hydroabstracts-scala/misc/royal-files/tesseracts-geol/"

    import scala.language.postfixOps

    val contents = Process("ls -1 /home/akmoch/dev/build/lucene-hydroabstracts-scala/misc/royal-files/tesseracts-geol/").lineStream

    contents.foreach { line =>
      val artDoi = line.replace("-out-300.txt", "").replace("geo-abs-","")

      val yesDoIt = arts.filter { article =>
        article.arturl.endsWith(artDoi)
      }
      if (yesDoIt.length == 1) {
        val matchArt = yesDoIt.head
        val fulltextpiece = scala.io.Source.fromFile(s"${path}${line}").mkString
        val updatedArticle = AbstractRoyal(
          matchArt.articleid,
          matchArt.authortitle,
          matchArt.textabs,
          matchArt.author,
          matchArt.title,
          matchArt.year,
          matchArt.arturl,
          Some(fulltextpiece))
        // logger.info(s"found article ${line} with ${matchArt.arturl} ")
        counter = counter + 1
        // AbstractRoyal.updateWithFullByID(updatedArticle)
      } else {
        logger.warn(s"no found article for ${line}")
      }
    }

    Ok(views.html.index(s"royal full text file system sourced ${counter}"))

  }

  def geolAbsLoader = Action {

    var counter = 0
    val startNum = 10000
    import sys.process._
    val path = "/home/akmoch/dev/build/lucene-hydroabstracts-scala/misc/royal-files/full-art-geol/"

    import scala.language.postfixOps

    val contents = Process("ls -1 /home/akmoch/dev/build/lucene-hydroabstracts-scala/misc/royal-files/full-art-geol/").lineStream

    contents.foreach { line =>

      val htmlText = scala.io.Source.fromFile(s"${path}${line}").mkString

      try {

        // dirty preps
        val artIdent = line.replace("geo-abs-","").replace(".html","")

        var articleid = counter + startNum
        val journal = "New Zealand Journal of Geology and Geophysics"
        var authortitle = ""
        var textabs = ""
        var author = ""
        var title = ""
        var year = 0l
        var arturl = s"http://www.tandfonline.com/doi/abs/10.1080/${artIdent}"
        var fulltext = ""

        val matcher = new scala.util.matching.Regex(".*\\((\\d{4})\\).*", "year")

        var baseurl = "http://www.tandfonline.com"
        var fulltexturl = "http://www.tandfonline.com"

        val doc: Document = Jsoup.parse(htmlText)

        val links: Elements = doc.select("a.pdf")
        val iter = links.iterator()
        while (iter.hasNext) {
          val elem = iter.next()
          if (elem.hasText && elem.text().equalsIgnoreCase("Download full text")) {
            // logger.debug(s"${elem.attr("href")} ${elem.text()}")
            fulltexturl = baseurl + elem.attr("href")
          }
        }

        // caused thingy
        val shortUrl = doc.select("a.pdf").first.attr("href")
        // arturl = baseurl + shortUrl
        val title1 = doc.select("div.description").select("h1").text.trim

        // <meta name="dc.Creator" content=" D. M.   Garner ">
        val metas: Elements = doc.select("meta")
        val miter = metas.iterator()
        while (miter.hasNext) {
          val elem = miter.next()
          if (elem.hasAttr("property") && elem.attr("property").equalsIgnoreCase("og:description")) {
            // logger.debug(s"${elem.attr("content")}")
            authortitle = elem.attr("content")
            val thingy = for (matcher(year) <- matcher findAllIn authortitle) yield (year)
            if (thingy.hasNext) {
              year = thingy.next().toLong
            }
          }
          if (elem.hasAttr("name") && elem.attr("name").equalsIgnoreCase("dc.Title")) {
            logger.debug(s"${elem.attr("content")} count $counter")
            title = elem.attr("content").trim
            if (!title1.equalsIgnoreCase(title)) {
              // logger.info(s"diff title: $title || $title1 ")
            }
          }
          if (elem.hasAttr("name") && elem.attr("name").equalsIgnoreCase("dc.Creator")) {
            logger.debug(s"${elem.attr("content")}")
            author = author + ", " + elem.attr("content")

          }
          if (elem.hasAttr("name") && elem.attr("name").equalsIgnoreCase("dc.Description")) {
            // logger.debug(s"${elem.attr("content")}")
            textabs = elem.attr("content")
          }
        }

        val abs = AbstractRoyal(
          articleid,
          authortitle,
          textabs,
          author,
          title,
          year,
          arturl,
          Some(fulltext))

        counter = counter + 1
        AbstractRoyal.insertFull(abs)
        AbstractRoyal.insertGeologyF(abs)

      } catch {
        case e: IllegalArgumentException => Logger.error("illegal arg. exception")
        case e: IndexOutOfBoundsException => Logger.error("illegal state exception")
        case e: NoSuchElementException => Logger.error("NoSuchElementException")
        case e: java.io.IOException => Logger.error("IO exception")
      }
    }
    Ok(views.html.index(s"file system source geology"))

  }


  def royalFullTextLoaderCassandra = Action {
    var counter = 0
    val arts = AbstractRoyal.getAllWithParserUpdated
    import sys.process._
    val path = "/home/akmoch/dev/build/lucene-hydroabstracts-scala/misc/royal-files/tesseracts-geol/"

    import scala.language.postfixOps

    val contents = Process("ls -1 /home/akmoch/dev/build/lucene-hydroabstracts-scala/misc/royal-files/tesseracts-geol/").lineStream

    contents.foreach { line =>
      val artDoi = line.replace("-out-300.txt", "").replace("geo-abs-","")

      val yesDoIt = arts.filter { article =>
        article.arturl.endsWith(artDoi)
      }
      if (yesDoIt.length == 1) {
        // update the cassandra one
        val casArt = AbstractRoyal.getByIdF(yesDoIt.head.articleid)
        casArt.map { casArticleList =>
          val casArticle = casArticleList.head
          if (casArticleList.length == 1 && casArticle.articleid == yesDoIt.head.articleid) {

            val fulltextpiece = scala.io.Source.fromFile(s"${path}${line}").mkString

            val updatedArticle = AbstractRoyal(
                casArticle.articleid,
                casArticle.authortitle,
                casArticle.textabs,
                casArticle.author,
                casArticle.title,
                casArticle.year,
                casArticle.arturl,
                Some(fulltextpiece))

            // AbstractRoyal.updateF(updatedArticle)
            val retBool = AbstractRoyal.insertGeologyF(updatedArticle)
            logger.info(s"retBool ${retBool} fulltextpiece of ${casArticle.articleid} = ${fulltextpiece.size}")

          } else {
            logger.warn(s"casArticleList.length ${casArticleList.length} casArticle.articleid ${casArticle.articleid} yesDoIt.head.articleid ${yesDoIt.head.articleid}")
          }
        }
        counter = counter + 1
      } else {
        logger.warn(s"no found article for ${line}")
      }
    }

    Ok(views.html.index(s"royal full text file system sourced ${counter}"))

  }
}

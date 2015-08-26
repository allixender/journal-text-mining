package controllers

import play.api._
import play.api.Logger._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.Future
import models.AbstractHydro
import models.RawArticlePage
import models.MetadataDB
import models.GeoName

object JournalTest extends Controller {

  val url = "http://www.hydrologynz.co.nz/journal.php"

  def getallfromweb = Action.async {

    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    val futureResult: Future[String] = WS.url(url).get().map {
      response =>
        {

          // <td><a href="journal.php?volume_id=27">Vol 12 2005</a> </td>

          // val dateP2 = new scala.util.matching.Regex("""(\d\d\d\d)-(\d\d)-(\d\d)""", "year", "month", "day")

          // Logger.debug(response.body)

          val matcher = new scala.util.matching.Regex(".*volume_id=(\\d{1,2})\">Vol (\\d{1,2}) (\\d{4}).*", "url", "volnum", "year")

          val thingy = for (matcher(url, volnum, year) <- matcher findAllIn response.body) yield (url, volnum, year)

          val tlist = thingy.toList

          for ((jurl, voln, year) <- tlist) {

            // Logger.debug(s"$jurl $voln $year")

            createNewQueries(jurl, voln, year)

          }

          "volumes found: " + tlist.length
        }
    }

    futureResult.map(result => Ok(views.html.index(result)))

  }

  def createNewQueries(jurl: String, voln: String, year: String): Future[String] = {

    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    val newurl = url + "?volume_id=" + jurl

    // Logger.debug(s"checking articles for volume $voln $year $newurl")

    val singleVolumeArticles: Future[String] = WS.url(newurl).get().map {
      response =>
        {
          // <a title="Read the abstract of this article" href="journal.php?article_id=478">Read Abstract</a></a>
          val matcher2 = new scala.util.matching.Regex(".*article_id=(\\d{1,4})\">Read Abstract</a>.*", "articleid")

          val articles = for (matcher2(articleid) <- matcher2 findAllIn response.body) yield (articleid)

          val tlist = articles.toList

          for (articleid <- tlist) {

            // Logger.debug(s"checking $articleid")

            saveArticle(articleid, jurl, voln, year)
            // updateArticle(articleid, jurl, voln, year)

          }

          "articles found: " + articles.size.toString
        }
    }

    singleVolumeArticles

  }

  def saveArticle(articleid: String, jurl: String, voln: String, year: String): Future[String] = {

    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    val newurl = url + "?article_id=" + articleid
    Logger.debug(s"loading $newurl")
    val artid: Long = articleid.toLong

    val futureResult: Future[String] = WS.url(newurl).get().map {

      response =>
        {
          val mytext = response.body
          // Logger.debug(s"saving ... $mytext")
          RawArticlePage.insert(RawArticlePage(0, mytext, articleid, voln, year))

          "inserted"
        }
    }

    futureResult

  }

  def updateArticle(articleid: String, jurl: String, voln: String, year: String): Future[String] = {

    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    val newurl = url + "?article_id=" + articleid
    // Logger.debug(s"loading $newurl")
    val artid: Long = articleid.toLong

    val origRawArtList = RawArticlePage.getAllWithParser

    val futureResult: Future[String] = WS.url(newurl).get().map {

      response =>
        {
          val mytext = response.body
          // Logger.debug(s"checking ... $newurl")

          for (origRaw <- origRawArtList) {
            if (origRaw.fulltext.equalsIgnoreCase(mytext) && origRaw.volnum.equalsIgnoreCase(voln) && origRaw.year.equalsIgnoreCase(year)) {

              // Logger.debug(s"found matching raw article $artid $jurl $voln $year -> oridinal in DB artid ${origRaw.articleid}")
              val newRawArt: RawArticlePage = RawArticlePage(origRaw.articleid, origRaw.fulltext, articleid, origRaw.volnum, origRaw.year)

              val retval = RawArticlePage.updateWithFullByID(newRawArt)

              val artid = newRawArt.articleid

              if (retval) Logger.debug(s"origRaw.articleid ${origRaw.articleid} URL updated with $articleid") else Logger.debug(s"$artid ERROR")
            }
          }

          s"updated"
        }
    }

    futureResult

  }

  def analyse() = Action {

    import org.jsoup.Jsoup
    import org.jsoup.nodes.Document
    import org.jsoup.select.Elements

    val artList = RawArticlePage.getAllWithParser

    val size = artList.length

    for (article <- artList) {

      try {

        val doc: Document = Jsoup.parse(article.fulltext)

        val mydata: Elements = doc.select("h3~p"); // direct a after h3

        val authortitle = mydata.get(0).text()
        val textabs = mydata.get(1).text()
        val arturl: Long = article.url.toLong
        val artid: Long = article.articleid
        val year = article.year.toLong

        AbstractHydro.insert(AbstractHydro(artid, authortitle, textabs, "", "", year, arturl, None))

        Logger.debug(s"$artid : $authortitle $year = article.year.toLong")
      } catch {
        case e: IllegalArgumentException  => Logger.error("illegal arg. exception")
        case e: IndexOutOfBoundsException => Logger.error("illegal state exception")
        case e: java.io.IOException       => Logger.error("IO exception")
      }
    }

    Ok(views.html.index(s"looked at $size articles from DB"))
  }

  def zerParser = Action {
    val match2 = new scala.util.matching.Regex("^((?:[\\w-]+, (?:\\S\\.)+;? )*)(.*)$", "author", "title")

    val tl = AbstractHydro.getAllWithParser

    for (abstr <- tl) {
      val match2(author, title) = abstr.authortitle

      val newAbstr = AbstractHydro(abstr.articleid, abstr.authortitle, abstr.textabs, author, title, abstr.year, abstr.arturl, None)

      Logger.debug(s"old year ${abstr.year} -> new year ${newAbstr.year}")

      val retval = AbstractHydro.updateWithFullByID(newAbstr)
      val artid = newAbstr.articleid
      if (retval) Logger.debug(s"$artid updated") else Logger.debug(s"$artid ERROR")
    }

    Ok(views.html.index(s"fertig"))
  }

  def downloadPdfs = Action {

    val xmlList = MetadataDB.getAllWithParser
    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    for (elem <- xmlList) {
      val xmlText = elem.xml
      val xmlsource = scala.xml.Source.fromString(xmlText)
      val xmlDoc = scala.xml.XML.load(xmlsource)
      val urlNode = xmlDoc \\ "MD_DigitalTransferOptions" \ "onLine" \ "CI_OnlineResource" \ "linkage" \ "URL"

      val newurl = urlNode.text

      val pdfurl = newurl.replace("http://www.hydrologynz.co.nz/journal.php?article_id", "http://www.hydrologynz.co.nz/journal_article.php?article_id")
      val pdfid = pdfurl.replace("http://www.hydrologynz.co.nz/journal_article.php?article_id=", "")
      val pdffile = s"johnz-$pdfid.pdf"
      Logger.debug(pdfurl + " - " + pdffile)

      import org.apache.commons.io.FileUtils._
      // import sys.process._
      // import java.net.URL
      // import java.io.File
      // new URL(pdfurl) #> new File("Output.html") !!
      // val src = scala.io.Source.fromURL(pdfurl)

      try {
        org.apache.commons.io.FileUtils.copyURLToFile(new java.net.URL(pdfurl), new java.io.File(pdffile))
      } catch {
        case ioe: java.io.IOException => Logger.error(s"$pdfurl NOT found ${ioe.getLocalizedMessage()}")
        case e: Exception             => Logger.error(s"$pdfurl ERROR ${e.getLocalizedMessage()}")
      }

      //      val futureResult: Future[String] = WS.url(pdfurl).get().map {
      //
      //        response =>
      //          {
      //            // Logger.debug(s"downloading ${newurl} ")
      //            // scala.io.Source.fromURI(new java.net.URI(newurl))
      //            val pdfid = pdfurl.replaceAll("http://www.hydrologynz.co.nz/journal_article.php?article_id=", "")
      //            Logger.debug("accessing download " + pdfid)
      //            val pdffile = s"johnz-$pdfid.pdf"
      //            val out = new java.io.FileOutputStream(new java.io.File(pdffile))
      //            out.write(response.body.getBytes())
      //            
      //            pdffile
      //          }
      //      }
      //
      //      futureResult.map(message => {
      //        Logger.debug(s"downloaded ${message} ")
      //      })

    }

    Ok(views.html.index(s"download"))
  }

}

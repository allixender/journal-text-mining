package controllers

// import play.api._

import play.api.mvc._
import play.api.Logger
import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.Future

import models.Term

import java.io.File
import java.io.FileReader
import java.util.Date

import scala.io.Source
import au.com.bytecode.opencsv.CSVReader

import com.hp.hpl.jena.sparql._
import com.hp.hpl.jena.query._
import com.hp.hpl.jena.rdf.model.{ Model, ModelFactory, RDFNode }

object JenaSparqlLink extends Controller {

  def querySparqlTest1 = Action {
    
    // val bla = QueryExecutionFactory.sparqlService
    //create a Jena model to query against
    // val model: Model = ...
    val model: Model = ModelFactory.createMemModelMaker().createModel("http://example.org/book")
    //create a node for our variable
    val creator: RDFNode = model.createResource("J.K. Rowling");
    // store the node in a QuerySolutionMap
    val initialBindings: QuerySolutionMap = new QuerySolutionMap()
    initialBindings.add("creator", creator);
    //here's the query
    
    val author = "J.K. Rowling"
    
    val queryString = s"""PREFIX foaf: <http://purl.org/dc/elements/1.1/title>
PREFIX dc:    <http://purl.org/dc/elements/1.1/>
PREFIX :      <http://example.org/book/>
PREFIX ns:    <http://example.org/ns#>
PREFIX vcard: <http://www.w3.org/2001/vcard-rdf/3.0#>
SELECT ?book
      WHERE {
        ?book dc:creator "$author".}
"""
    
    //create the query object
    val query: Query = QueryFactory.create(queryString)
    Logger.debug(query.toString())
    //execute the query over the model, providing the
    //initial bindings for all variables
    // val execQ: QueryExecution = QueryExecutionFactory.create(query, model, initialBindings)
    
    val execQ = QueryExecutionFactory.sparqlService("http://localhost:3030/books/query", query)
    
    val results: ResultSet = execQ.execSelect()
    
    val resMod = results.getResourceModel()
    val resVars = results.getResultVars()
    
    val nsIter = resMod.listNameSpaces()
    
    val nextQ = results.next()
    val resVarList = nextQ.varNames()
    
    Logger.debug(results.toString())
    execQ.close()
    
    Ok(views.html.index(s" querying?"))
  }

  def listAllTerms = Action {
    
		val termsList = Term.all()
    
    val text = for (term <- termsList ) yield {
      s"${term.termid} ${term.preflabel}"
    }
    Ok(views.html.index(text.mkString(" ## \n")))
    
  }
  
  def findTermPref(preflabel: String) = Action {
	  
	  val termsList = Term.findByLabel(preflabel)
			  
			  val text = for (term <- termsList ) yield {
				  s"${term.termid} ${term.preflabel}"
			  }
	  Ok(views.html.index(text.mkString(" ## \n")))
	  
  }
  
  def findTermId(termid: Long) = Action {
	  
	  val termsList = Term.getFullByID(termid)
			  
			  val text = for (term <- termsList ) yield {
				  s"${term.termid} ${term.preflabel}"
			  }
	  Ok(views.html.index(text.mkString(" ## \n")))
	  
  }
  
  def loadVocab(vocab: String) = Action {
    
    val vocabulary = vocab match {
      case "glossary" => loadGlossary("misc/glossary_esc.csv")
      case "papawai" => loadPapawai("misc/Papawai.csv")
      case "catchments" => loadCatchments("misc/catchments.csv")
      case "ngmp" => loadNgmpParameters("misc/ngmp-params.csv")
    }
    
    Ok(views.html.index(s"getting started $vocab \n ### ${vocabulary.mkString(" ## ")}"))
    
  }
  
  def querySparql(vocab: String, term: String) = Action {
    
    Ok(views.html.index(s"querying for $term in $vocab"))
  }
  
  
  def loadGlossary(filename: String) = {
    
    import scala.collection.JavaConversions._

    var x = 0
    val reader = new CSVReader(new FileReader(filename))
    
    val readintry = for (row <- reader.readAll) yield {
      x=x+1
      
      val glossid = row(0)
      val preflabel = row(1)
      val term_en_description = row(2)
      val anotherRef = row(3)
      
      val newTerm = Term(0, "glossary", preflabel, Some(preflabel), None, None, None, Some("["+glossid+"]: "+term_en_description+" (in "+anotherRef+")"), None, None, None, None, None, None, None, None, None, None, None, None, None, Some(new Date()) )
      val retVal = Term.create(newTerm)
      Logger.debug(s"$retVal inserted for ${newTerm.term_en_description}")
      
      s"${newTerm.term_en_description}"

    }
    Logger.debug(s"$x glossary items taken")
    readintry.toList
  }
  
  def loadPapawai(filename: String) = {
    
    import scala.collection.JavaConversions._

    var x = 0
    val reader = new CSVReader(new FileReader(filename))
    
    val readintry = for (row <- reader.readAll) yield {
      x=x+1
      
	    val collection = "papawai"
	    val preflabel = row(0)
	    val term_en = Some(row(1))
			val term_en_origin = Some(row(2))
			val term_en_related = Some(row(3))
			val term_mi = Some(row(4))
			val term_en_description = Some(row(5))
			val term_mi_description_translation = Some(row(6))
			val term_mi_origin = Some(row(7))
			val term_mi_related = Some(row(8))
			val bib_citation = Some(row(9))
			val subjects = Some(row(10))
			val doc_type = Some(row(11))
			val ucd_number = Some(row(12))
			val nzms260_map_no = Some(row(13))
			val ext_links = Some(row(14))
			val notes = Some(row(15))
			val rec_loaded_date = Some(row(16))
			val rec_loaded_by = Some(row(17))
			val rec_reviewed_by = Some(row(18))
			val last_edit = Some(new Date())
      
      val newTerm = Term(0, collection, preflabel, term_en, term_en_origin, term_en_related, term_mi, term_en_description, 
          term_mi_description_translation, term_mi_origin, term_mi_related, bib_citation, subjects, doc_type, ucd_number, 
          nzms260_map_no, ext_links, notes, rec_loaded_date, rec_loaded_by, rec_reviewed_by, last_edit )
      
      val retVal = Term.create(newTerm)
      Logger.debug(s"$retVal inserted for ${newTerm.term_en_description}")
      
      s"[$retVal]: ${newTerm.preflabel} (with ${newTerm.term_mi})"
    }
    Logger.debug(s"$x papawai terms taken")
    readintry.toList
  }
  
  def loadCatchments(filename: String) = {
    
    import scala.collection.JavaConversions._

    var x = 0
    val reader = new CSVReader(new FileReader(filename))
    
    val readintry = for (row <- reader.readAll) yield {
      x=x+1
      row.mkString(", ")
    }
    readintry.toList
  }
  
  def loadNgmpParameters(filename: String) = {
    
    import scala.collection.JavaConversions._

    var x = 0
    val reader = new CSVReader(new FileReader(filename))
    
    // PARAM_TYPE,PARAM_TYPE_DISP_NAME,PARAM_TYPE_UNIT_ABRV,Flag,Comment
    val readintry = for (row <- reader.readAll) yield {
      x=x+1
      val paramtype = row(0)
      val paramdispname = row(1)
      val paramunitabrv = row(2)
      
      val newTerm = Term(paramtype.toLong, "ngmp", paramdispname, Some(paramdispname), None, None, None, Some("["+paramtype+"]: "+paramdispname+" (in "+paramunitabrv+")"), None, None, None, None, None, None, None, None, None, None, None, None, None, Some(new Date()) )
      val retVal = Term.insertWithID(newTerm)
      Logger.debug(s"$retVal inserted for ${newTerm.term_en_description}")
      
      s"[$paramtype]: $paramdispname (in $paramunitabrv)"
    }
    Logger.debug(s"$x ngmp params taken")
    readintry.toList
  }
  
  def exportAsRDF(vocab: String) = Action {
    
    import org.joda.time.format.ISODateTimeFormat
    import org.joda.time.DateTime
    
    val vocabulary = Term.findByCollection(vocab)
    
    val rdfBuilder = new StringBuilder()
    val fmt = ISODateTimeFormat.dateTime()
    val dt = new DateTime()
    val modifiedDate = dt.minusWeeks(1)
    
    val collectionTitle = vocab match {
      case "ngmp" => "National Groundwater Monitoring Programme, observed properties"
      case "glossary" => "NZ HS Freshwater Database Glossary"
      case "papawai" => "English / Maori Groundwater Terms"
    }
    
    rdfBuilder
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
						+ "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "
						+ "		xmlns:dc=\"http://purl.org/dc/elements/1.1/\" \n"
						+ "		xmlns:dcterms=\"http://purl.org/dc/terms/\" " 
				//		+ "		xmlns:fn=\"http://www.w3.org/2005/02/xpath-functions\" \n"
						+ "		xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" "
						+ "		xmlns:gml=\"http://www.opengis.net/gml\" \n"
						+ "		xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" "
						+ "		xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" \n"
				//		+ "		xmlns:xdt=\"http://www.w3.org/2005/02/xpath-datatypes\" "
						+ "		xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n"
						+ "  <!-- Collection -->\n"
						+ "  <skos:Collection rdf:about=\"http://vocab.smart-project.info/collection/" + vocab + "\">\n"
						+ "    <rdfs:label>" + collectionTitle + "</rdfs:label>\n"
						+ "    <dc:title>" + collectionTitle + "</dc:title>\n"
						+ "    <dc:description>SMART project and GNS collected geoscience terms</dc:description>\n"
						+ "    <dc:creator>\n" + "      <foaf:Organization>\n"
						+ "        <foaf:name>GNS</foaf:name>\n"
						+ "      </foaf:Organization>\n"
						+ "    </dc:creator>\n"
						+ "    <dc:rights>CC-SA-BY-NC 3.0 NZ</dc:rights>\n"
						+ "    <dcterms:issued>" + fmt.print(dt)
						+ "</dcterms:issued>\n" + "    <dcterms:modified>"
						+ fmt.print(modifiedDate) + "</dcterms:modified>\n")
    
		for (term <- vocabulary) {
			rdfBuilder.append("<skos:member>http://vocab.smart-project.info/" + vocab + "/" + term.termid.toString() + "</skos:member>\n");
		}
		rdfBuilder.append("  </skos:Collection>\n" + "  <!-- Collection -->\n");
		
		for (term <- vocabulary) {
			
		  rdfBuilder.append("<skos:Concept rdf:about=\"http://vocab.smart-project.info/" + vocab + "/"
								+ term.termid.toString() + "\">\n")
		  
			if (term.term_en != null && term.term_en.isDefined && !term.term_en.isEmpty) {
				rdfBuilder.append("    <skos:label>"
						+ xml.Utility.escape(term.term_en.get) + "</skos:label>\n");
				rdfBuilder.append("    <dc:title>"
						+ xml.Utility.escape(term.term_en.get) + "</dc:title>\n");
				rdfBuilder.append("    <skos:prefLabel xml:lang=\"en\">"
						+ xml.Utility.escape(term.term_en.get) + "</skos:prefLabel>\n");
			}
		  
		  if (term.term_en_description != null && term.term_en_description.isDefined && !term.term_en_description.isEmpty) {
				rdfBuilder.append("    <skos:definition xml:lang=\"en\">"
						+ xml.Utility.escape(term.term_en_description.get) + "</skos:definition>\n");
				rdfBuilder.append("    <dc:description>"
						+ xml.Utility.escape(term.term_en_description.get) + "</dc:description>\n");
			}
		  
		  if (term.last_edit != null && term.last_edit.isDefined ) {
				rdfBuilder.append("    <dc:modified>"
						+ fmt.print(new DateTime(term.last_edit.get))
						+ "</dc:modified>\n");
			}
		  
      rdfBuilder.append("    <skos:inCollection rdf:resource=\"http://vocab.smart-project.info/collection/" + vocab + "\"/>\n</skos:Concept>\n")
		}
		rdfBuilder.append("</rdf:RDF>")
		
    // Ok(views.html.index(rdfBuilder.toString()))
		Ok(rdfBuilder.toString()).as("application/rdf+xml")
  }
  
}
package models

import anorm.{ RowParser, SQL }
import anorm.SqlParser.{ get, double, long, str, date }
import anorm.ResultSetParser
import anorm.SqlQuery
import anorm._
import org.joda.time.DateTime
import java.util.Date

import play.api.Play.current
import play.api.db.DB

// http://vocab.getty.edu/tgn/
// linz gazetteer
// landcare niwa eodp vocab 


case class Term (
    termid: Long,  // dc terms : identifier  skos:Concept
    collection: String,   /// dc terms: isPartOf , skos:inScheme  think of skos:conceptScheme and skos:Collection
    preflabel: String,   // dc terms : subject  skos:preflabel
  	term_en: Option[String],  // dc terms : title   skos:label
  	term_en_origin: Option[String],  // terms : source   skos:historyNote
  	term_en_related: Option[String], // dc terms : relation  skos:related
  	term_mi: Option[String],   // dc terms : alternative   skos:altLabel
  	term_en_description: Option[String], // dc description : description, skos:definition
  	term_mi_description_translation: Option[String], // dc terms : instructionalMethod   skos:definition
  	term_mi_origin: Option[String],  // dc terms : source, language=mi   skos:historyNote
  	term_mi_related: Option[String], // dc terms : relation language=mi  skos:related
  	bib_citation: Option[String], //  dc terms : bibliographicCitation
  	subjects: Option[String],  // dc terms : rights, terms : provenance
  	doc_type: Option[String],  // dc terms : type (terms: conformsTo?)
  	ucd_number: Option[String], // dc terms : hasVersion
  	nzms260_map_no: Option[String],  // dc terms : isReferencedBy
  	ext_links: Option[String], // dc terms : references
  	notes: Option[String],  // dc terms : abstract  skos:note or editorialNote, skos: changeNote
  	rec_loaded_date: Option[String],  // dc terms : available
  	rec_loaded_by: Option[String], // dc terms : publisher 
  	rec_reviewed_by: Option[String],  // dc terms : contributor
  	last_edit: Option[Date] )   // dc terms : modified

object Term {
  
  val getBasicQuery: SqlQuery = SQL("select termid, collection, preflabel, term_en, term_en_description from terms order by termid asc")
  
  val basicTermRowParser: RowParser[Term] = {
    long("termid") ~
      str("collection") ~
      str("preflabel") ~ 
      get[Option[String]]("term_en") ~
    	get[Option[String]]("term_en_description") map {
        case termid ~ collection ~ preflabel ~ term_en ~ term_en_description =>
          Term(termid, collection, preflabel, term_en, None, None, None, term_en_description, None, None, None, None, None, None, None, None, None, None, None, None, None, None )
      }
  }
  val basicTermParser: ResultSetParser[List[Term]] = {
    // import as suggested
    import scala.language.postfixOps
    basicTermRowParser *
  }

  def getBasicWithParser: List[Term] = DB.withConnection {
    implicit connection =>
      getBasicQuery.as(basicTermParser)
  }
  
  // ---------------------
  val getAllQuery: SqlQuery = SQL("select * from terms order by termid asc")
  
  val fullTermRowParser: RowParser[Term] = {
  	  long("termid") ~
  	  str("collection") ~
      str("preflabel") ~ 
  	  get[Option[String]]("term_en")~
  	  get[Option[String]]("term_en_origin")~
  	  get[Option[String]]("term_en_related")~
  	  get[Option[String]]("term_mi")~
  	  get[Option[String]]("term_en_description")~
  	  get[Option[String]]("term_mi_description_translation")~
  	  get[Option[String]]("term_mi_origin")~
  	  get[Option[String]]("term_mi_related")~
  	  get[Option[String]]("bib_citation")~
  	  get[Option[String]]("subjects")~
  	  get[Option[String]]("doc_type")~
  	  get[Option[String]]("ucd_number")~
  	  get[Option[String]]("nzms260_map_no")~
  	  get[Option[String]]("ext_links")~
  	  get[Option[String]]("notes")~
  	  get[Option[String]]("rec_loaded_date")~
  	  get[Option[String]]("rec_loaded_by")~
  	  get[Option[String]]("rec_reviewed_by") ~
  		get[Option[Date]]("last_edit") map {
    case termid~
	    collection~
	    prefLabel~
	    term_en~
			term_en_origin~
			term_en_related~
			term_mi~
			term_en_description~
			term_mi_description_translation~
			term_mi_origin~
			term_mi_related~
			bib_citation~
			subjects~
			doc_type~
			ucd_number~
			nzms260_map_no~
			ext_links~
			notes~
			rec_loaded_date~
			rec_loaded_by~
			rec_reviewed_by~
			last_edit => Term(termid, 
	    			collection,
	    			prefLabel,
	    			term_en,
	    			term_en_origin,
	    			term_en_related,
	    			term_mi,
	    			term_en_description,
	    			term_mi_description_translation,
	    			term_mi_origin,
	    			term_mi_related,
	    			bib_citation,
	    			subjects,
	    			doc_type,
	    			ucd_number,
	    			nzms260_map_no,
	    			ext_links,
	    			notes,
	    			rec_loaded_date,
	    			rec_loaded_by,
	    			rec_reviewed_by,
	    			last_edit
	    	)
	  }
  }
  
  def all(): List[Term] = DB.withConnection { implicit c =>
    import scala.language.postfixOps
    getAllQuery.as(fullTermRowParser *)
  }
  
  def findByLabel(preflabel: String): List[Term] = DB.withConnection { implicit c =>
    import scala.language.postfixOps
      SQL("select * from terms where preflabel = {preflabel}").on(
          'preflabel -> preflabel
      ).as(fullTermRowParser *)
  }
  
  def findByCollection(vocab: String): List[Term] = DB.withConnection { implicit c =>
  import scala.language.postfixOps
  SQL("select * from terms where collection = {vocab}").on(
		  'vocab -> vocab
		  ).as(fullTermRowParser *)
  }
  
  def getFullByID(termid: Long): List[Term] = DB.withConnection { implicit c =>
    import scala.language.postfixOps
      SQL("select * from terms where termid = {termid}").on(
          'termid -> termid
      ).as(fullTermRowParser *)
  }
  
  def create(theTerm: Term) : Int = {
    import scala.language.postfixOps
    DB.withConnection { implicit c =>
      SQL("""insert into terms (
    			collection,
    		  preflabel,
    			term_en,
    			term_en_origin,
    			term_en_related,
    			term_mi,
    			term_en_description,
    			term_mi_description_translation,
    			term_mi_origin,
    			term_mi_related,
    			bib_citation,
    			subjects,
    			doc_type,
    			ucd_number,
    			nzms260_map_no,
    			ext_links,
    			notes,
    			rec_loaded_date,
    			rec_loaded_by,
    			rec_reviewed_by, 
    		  last_edit) 
        values (
	  			{collection},
    		  {preflabel},
	  			{term_en},
    			{term_en_origin},
    			{term_en_related},
    			{term_mi},
    			{term_en_description},
    			{term_mi_description_translation},
    			{term_mi_origin},
    			{term_mi_related},
    			{bib_citation},
    			{subjects},
    			{doc_type},
    			{ucd_number},
    			{nzms260_map_no},
    			{ext_links},
    			{notes},
    			{rec_loaded_date},
    			{rec_loaded_by},
	  			{rec_reviewed_by},
					{last_edit})""").on(
		    		'collection -> theTerm.collection,
		    		'preflabel -> theTerm.preflabel,
		    		'term_en -> theTerm.term_en,
						'term_en_origin -> theTerm.term_en_origin,
						'term_en_related -> theTerm.term_en_related,
						'term_mi -> theTerm.term_mi,
						'term_en_description -> theTerm.term_en_description,
						'term_mi_description_translation -> theTerm.term_mi_description_translation,
						'term_mi_origin -> theTerm.term_mi_origin,
						'term_mi_related -> theTerm.term_mi_related,
						'bib_citation -> theTerm.bib_citation,
						'subjects -> theTerm.subjects,
						'doc_type -> theTerm.doc_type,
						'ucd_number -> theTerm.ucd_number,
						'nzms260_map_no -> theTerm.nzms260_map_no,
						'ext_links -> theTerm.ext_links,
						'notes -> theTerm.notes,
						'rec_loaded_date -> theTerm.rec_loaded_date,
						'rec_loaded_by -> theTerm.rec_loaded_by,
	         	'rec_reviewed_by -> theTerm.rec_reviewed_by,
	         	'last_edit -> new Date()
      ).executeUpdate()
    }
  }
  def insertWithID(theTerm: Term) : Int = {
		  import scala.language.postfixOps
		  DB.withConnection { implicit c =>
		  SQL("""insert into terms (
		      termid,
				  collection,
				  preflabel,
				  term_en,
				  term_en_origin,
				  term_en_related,
				  term_mi,
				  term_en_description,
				  term_mi_description_translation,
				  term_mi_origin,
				  term_mi_related,
				  bib_citation,
				  subjects,
				  doc_type,
				  ucd_number,
				  nzms260_map_no,
				  ext_links,
				  notes,
				  rec_loaded_date,
				  rec_loaded_by,
				  rec_reviewed_by, 
				  last_edit) 
		    values (
				  {termid},
				  {collection},
				  {preflabel},
				  {term_en},
				  {term_en_origin},
				  {term_en_related},
				  {term_mi},
				  {term_en_description},
				  {term_mi_description_translation},
				  {term_mi_origin},
				  {term_mi_related},
				  {bib_citation},
				  {subjects},
				  {doc_type},
				  {ucd_number},
				  {nzms260_map_no},
				  {ext_links},
				  {notes},
				  {rec_loaded_date},
				  {rec_loaded_by},
				  {rec_reviewed_by},
				  {last_edit})""").on(
						  'termid -> theTerm.termid,
						  'collection -> theTerm.collection,
						  'preflabel -> theTerm.preflabel,
						  'term_en -> theTerm.term_en,
						  'term_en_origin -> theTerm.term_en_origin,
						  'term_en_related -> theTerm.term_en_related,
						  'term_mi -> theTerm.term_mi,
						  'term_en_description -> theTerm.term_en_description,
						  'term_mi_description_translation -> theTerm.term_mi_description_translation,
						  'term_mi_origin -> theTerm.term_mi_origin,
						  'term_mi_related -> theTerm.term_mi_related,
						  'bib_citation -> theTerm.bib_citation,
						  'subjects -> theTerm.subjects,
						  'doc_type -> theTerm.doc_type,
						  'ucd_number -> theTerm.ucd_number,
						  'nzms260_map_no -> theTerm.nzms260_map_no,
						  'ext_links -> theTerm.ext_links,
						  'notes -> theTerm.notes,
						  'rec_loaded_date -> theTerm.rec_loaded_date,
						  'rec_loaded_by -> theTerm.rec_loaded_by,
						  'rec_reviewed_by -> theTerm.rec_reviewed_by,
						  'last_edit -> new Date()
						  ).executeUpdate()
		  }
  }
  
  def update(theTerm: Term, termid: Long) : Int = {
    import scala.language.postfixOps
    DB.withConnection { implicit c =>
      SQL("""update terms set 
  			  collection = {collection},
    		  preflabel = {preflabel},
    			term_en = {term_en},
    			term_en_origin = {term_en_origin},
    			term_en_related = {term_en_related},
    			term_mi = {term_mi},
    			term_en_description = {term_en_description},
    			term_mi_description_translation = {term_mi_description_translation},
    			term_mi_origin = {term_mi_origin},
    			term_mi_related = {term_mi_related},
    			bib_citation = {bib_citation},
    			subjects = {subjects},
    			doc_type = {doc_type},
    			ucd_number = {ucd_number},
    			nzms260_map_no = {nzms260_map_no},
    			ext_links = {ext_links},
    			notes = {notes},
    			rec_loaded_date = {rec_loaded_date},
    			rec_loaded_by = {rec_loaded_by},
    			rec_reviewed_by = {rec_reviewed_by},
    		  last_edit = {last_edit}
			  where termid={termid} """).on(
			    	'preflabel -> theTerm.preflabel,
			    	'collection -> theTerm.collection,
			    	'term_en -> theTerm.term_en,
						'term_en_origin -> theTerm.term_en_origin,
						'term_en_related -> theTerm.term_en_related,
						'term_mi -> theTerm.term_mi,
						'term_en_description -> theTerm.term_en_description,
						'term_mi_description_translation -> theTerm.term_mi_description_translation,
						'term_mi_origin -> theTerm.term_mi_origin,
						'term_mi_related -> theTerm.term_mi_related,
						'bib_citation -> theTerm.bib_citation,
						'subjects -> theTerm.subjects,
						'doc_type -> theTerm.doc_type,
						'ucd_number -> theTerm.ucd_number,
						'nzms260_map_no -> theTerm.nzms260_map_no,
						'ext_links -> theTerm.ext_links,
						'notes -> theTerm.notes,
						'rec_loaded_date -> theTerm.rec_loaded_date,
						'rec_loaded_by -> theTerm.rec_loaded_by,
			      'rec_reviewed_by -> theTerm.rec_reviewed_by,
			      'last_edit -> new Date(),
			      'termid -> termid
      ).executeUpdate()
    }
  }
  
  def delete(termid: Long) : Int = {
    import scala.language.postfixOps
    DB.withConnection { implicit c =>
      SQL("delete from terms where termid = {termid}").on(
          'termid -> termid
      ).executeUpdate()
      
    }
  }
  
}
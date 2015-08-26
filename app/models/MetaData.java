package models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import models.OGCConstants.GmdTopicCategory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.xmlbeans.XmlOptions;
import org.isotc211.x2005.gco.CodeListValueType;
import org.isotc211.x2005.gmd.CIAddressType;
import org.isotc211.x2005.gmd.CICitationType;
import org.isotc211.x2005.gmd.CIContactType;
import org.isotc211.x2005.gmd.CIOnlineResourceType;
import org.isotc211.x2005.gmd.CIResponsiblePartyType;
import org.isotc211.x2005.gmd.EXExtentType;
import org.isotc211.x2005.gmd.EXGeographicBoundingBoxDocument;
import org.isotc211.x2005.gmd.EXGeographicBoundingBoxType;
import org.isotc211.x2005.gmd.MDDataIdentificationDocument;
import org.isotc211.x2005.gmd.MDDataIdentificationType;
import org.isotc211.x2005.gmd.MDDistributionType;
import org.isotc211.x2005.gmd.MDKeywordsType;
import org.isotc211.x2005.gmd.MDMetadataDocument;
import org.isotc211.x2005.gmd.MDMetadataType;
import org.isotc211.x2005.gmd.MDTopicCategoryCodeType;

import play.Logger;


public class MetaData {

	public MetaData(long articleid, String author, String title, long year, String textabs) {
		this.contactName = author;
		this.contactOrg = "NZ Hydrological Society - Publications - Journal";
		this.datetimestamp = new Date();
		this.title = title;
		this.topiccategory = "inlandWaters";
		this.keywords = "hydrology, New Zealand, Journal of Hydrology";
		this.datainfolevel = "dataset";
		this.mapExtentCoordinates = "BBOX ( 178.73954 E -47.93848 S 165.39060 W -34.02613 N )";
		this.gmd_identificationInfo_MD_DataIdentification_extent_BBOX_westBoundLongitude = 165.39060;
		this.gmd_identificationInfo_MD_DataIdentification_extent_BBOX_eastBoundLongitude = 178.73954;
		this.gmd_identificationInfo_MD_DataIdentification_extent_BBOX_southBoundLatitude = -47.93848;
		this.gmd_identificationInfo_MD_DataIdentification_extent_BBOX_northBoundLatitude = -34.02613;
		
		this.gmd_identificationInfo_MD_DataIdentification_MD_Resolution_scaleDenominator = 50000;
		
		this.gmd_identificationInfo_MD_DataIdentification_CI_Citation_gmd_title = title;
		
		this.isCreateNewRecordRequest = "true";
		this.hostingcatalogueUrl = "http://portal.smart-project.info/mycsw/csw";
		
		this.gmd_identificationInfo_MD_DataIdentification_abstract = textabs;
		this.gmd_identificationInfo_MD_DataIdentification_TopicCategoryCode = this.topiccategory;
		this.gmd_identificationInfo_MD_DataIdentification_extent_Description = "derive from abstract";
		
		this.gmd_referenceSystemInfo = "urn:ogc:def:crs:EPSG::4326";
		this.gmd_identificationInfo_MD_DataIdentification_CI_Citation_CI_Date = Long.toString(year);
		
		this.gmd_identificationInfo_MD_DataIdentification_CI_Citation_CI_Date_dateType = "creation";
		this.gmd_dataQualityInfo_LI_Lineage_statement = ""; // will be filled generically
		
		this.gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_ci_individualName = author;
		this.gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_CI_Contact_ci_telephone = "+64 6 357 1605";
		this.gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_CI_Contact_ci_email = "admin@hydrologynz.org.nz";
		this.gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_ci_pointOfContact = "author";
		this.gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_ci_organisationName = "NZ Hydrological Society - Publications - Journal";
		this.gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_CI_Contact_ci_onlineLinkage = "http://www.hydrologynz.org.nz/index.php/nzhs-publications/nzhs-journal";
		
		this.gmd_Contact_CI_ResponsibleParty_ci_individualName = author;
		this.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_telephone = "+64 6 357 1605";
		this.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_email = "admin@hydrologynz.org.nz";
		this.gmd_Contact_CI_ResponsibleParty_ci_pointOfContact = "author";
		this.gmd_Contact_CI_ResponsibleParty_ci_organisationName = "NZ Hydrological Society - Publications - Journal";
		this.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_onlineLinkage = "http://www.hydrologynz.org.nz/index.php/nzhs-publications/nzhs-journal";
		
		this.gmd_metadataConstraints_MD_LegalConstraints_useLimitation = "The Journal of Hydrology (New Zealand) (ISSN 0022-1708.) - We have loaded all abstracts to Volume 51 and papers for free viewing up to Volume 46 as at 12 October 2011.";
		this.gmd_identificationInfo_MD_DataIdentification_MD_Constraints = "The Journal of Hydrology (New Zealand) (ISSN 0022-1708.) - We have loaded all abstracts to Volume 51 and papers for free viewing up to Volume 46 as at 12 October 2011.";
		
		this.gmd_distributionInfo_MD_Distribution_formatName = "Journal Article in PDF";
		this.gmd_distributionInfo_MD_Distribution_formatVersion = "1.3 scanned / 1.6 electronic articles";
		
		this.gmd_distributionInfo_MD_Distribution_MD_DigitalTransferOptions_CI_OnlineResource_linkage = "http://www.hydrologynz.co.nz/journal.php?article_id=" + articleid;
		
		this.gmd_metadataStandardName = "ISO 19115:2003/19139";
		this.gmd_metadataStandardVersion = "1.0";
	}
	
	private final static String isoCodeList = "http://www.isotc211.org/2005/resources/codeList.xml";
	private final static String anzlicCodeList = "http://asdd.ga.gov.au/asdd/profileinfo/gmxCodelists.xml";

	
	public String recordID;

	public String timestamp;

	public Date datetimestamp;

	public String contactName;

	public String contactOrg;

	public String title;

	public String topiccategory;

	public String keywords;

	public String datainfolevel;

	public String mapExtentCoordinates;

	public String resTemporalExtent;

	public String isSmartInternal;

	public String maxRecords;

	public String isCreateNewRecordRequest;

	public String hostingcatalogueUrl;
	
	/*
	 * from here the gmd:MD_Metadata XML representation elements
	 */

	// gmd:fileIdentifier/gco:CharacterString
	public String gmd_fileIdentifier;

	// gmd:language/gmd:language/codeList="./resources/codeList.xml#LanguageCode"
	public String gmd_languageCode; // eng, mao

	// gmd:characterSet/gmd:MD_CharacterSetCode/codeList="./resources/codeList.xml#MD_CharacterSetCode"
	public String gmd_characterSetCode; // utf8

	// gmd:hierarchyLevel/gmd:MD_ScopeCode
	// codeList="./resources/codeList.xml#MD_ScopeCode" codeListValue="dataset"
	public String gmd_hierarchyLevel; // dataset, service, request?

	// gmd:hierarchyLevelName/gco:CharacterString
	public String gmd_hierarchyLevelName; // dataset

	// gmd:contact/gmd:CI_ResponsibleParty/gmd:individualName/gco:CharacterString
	public String gmd_Contact_CI_ResponsibleParty_ci_individualName; // Alex
																		// Kmoch

	// gmd:contact/gmd:CI_ResponsibleParty/gmd:organisationName/gco:CharacterString
	public String gmd_Contact_CI_ResponsibleParty_ci_organisationName; // GNS
																		// Science

	// gmd:contact/gmd:CI_ResponsibleParty/positionName/gco:CharacterString
	public String gmd_Contact_CI_ResponsibleParty_ci_positionName; // Student

	// gmd:contact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice/gco:CharacterString
	public String gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_telephone; // +64(0)21993574

	// gmd:contact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile/gco:CharacterString
	public String gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_facsimile; // +64(0)21993500

	// gmd:contact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint/gco:CharacterString
	public String gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_deliveryPoint; // eg
																				// street
																				// po
																				// box

	// gmd:contact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city/gco:CharacterString
	public String gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_city;

	// gmd:contact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode/gco:CharacterString
	public String gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_postalCode;

	// gmd:contact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country/gco:CharacterString
	public String gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_country;

	// gmd:contact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString
	public String gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_email; // a.kmoch@gns.cri.nz

	// gmd:contact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL
	public String gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_onlineLinkage;

	// gmd:contact/gmd:CI_ResponsibleParty/gmd:role/gmd:CI_RoleCode/codeList="./resources/codeList.xml#CI_RoleCode"
	// codeListValue="pointOfContact"
	public String gmd_Contact_CI_ResponsibleParty_ci_pointOfContact; // pointOfContact,
																		// resourceProvider?

	// gmd:dateStamp/gco:Date
	public Date gmd_dateStamp; // of metadata entry

	// gmd:metadataStandardName/gco:CharacterString "ISO 19115:2003/19139"
	public String gmd_metadataStandardName; // ISO 19115:2003/19139

	// gmd:metadataStandardVersion/gco:CharacterString "1.0"
	public String gmd_metadataStandardVersion; // 1.0

	// gmd:referenceSystemInfo/gmd:MD_ReferenceSystem/gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code/gco:CharacterString
	public String gmd_referenceSystemInfo; // urn:ogc:def:crs:EPSG::2193

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_CI_Citation_gmd_title; // nz_aquifers_(white_2001)

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date/gco:Date
	public String gmd_identificationInfo_MD_DataIdentification_CI_Citation_CI_Date; // 2012-11-22

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode/codeList="./resources/codeList.xml#CI_DateTypeCode"
	// codeListValue="revision
	public String gmd_identificationInfo_MD_DataIdentification_CI_Citation_CI_Date_dateType; // revision

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:abstract/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_abstract; // the
																			// actual
																			// abstract
																			// dataset
																			// description

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:status/gmd:MD_ProgressCode/codeList="./resources/codeList.xml#MD_ProgressCode"
	// codeListValue="completed">completed
	public String gmd_identificationInfo_MD_DataIdentification_status; // eg
																		// progresscode
																		// codelist
																		// completed

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:purpose/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_purpose; // intention
																		// what
																		// to do
																		// with
																		// the
																		// dataset

	// TODO check here recurse another CI_responsible party as pointofcontact,
	// could be another then the upper one (upper one for the actual dataset,
	// this here is technical catalogue and metadata contact)
	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointofcontact/gmd:CI_ResponsibleParty/gmd:individualName/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_ci_individualName; // Alex
																										// Kmoch

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointofcontact/gmd:CI_ResponsibleParty/gmd:organisationName/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_ci_organisationName; // GNS
																										// Science

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointofcontact/gmd:CI_ResponsibleParty/positionName/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_ci_positionName; // Student

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointofcontact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:voice/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_CI_Contact_ci_telephone; // +64(0)21993574

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointofcontact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:phone/gmd:CI_Telephone/gmd:facsimile/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_CI_Contact_ci_facsimile; // +64(0)21993574

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointofcontact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:deliveryPoint/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_CI_Contact_ci_deliveryPoint; // eg
																												// street
																												// po
																												// box

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointofcontact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:city/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_CI_Contact_ci_city;

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointofcontact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:postalCode/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_CI_Contact_ci_postalCode;

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointofcontact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_CI_Contact_ci_country;

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointofcontact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_CI_Contact_ci_email; // a.kmoch@gns.cri.nz

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointofcontact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:onlineResource/gmd:CI_OnlineResource/gmd:linkage/gmd:URL
	public String gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_CI_Contact_ci_onlineLinkage;

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:pointofcontact/gmd:CI_ResponsibleParty/gmd:role/gmd:CI_RoleCode/codeList="./resources/codeList.xml#CI_RoleCode"
	// codeListValue="pointOfContact"
	public String gmd_identificationInfo_MD_DataIdentification_CI_ResponsibleParty_ci_pointOfContact; // pointOfContact,
																										// resourceProvider?

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString
	public List<String> gmd_identificationInfo_MD_DataIdentification_keywords; // hmm,
																				// many
																				// keyword
																				// single
																				// elements
																				// intended

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/codeList="./resources/codeList.xml#MD_KeywordTypeCode"
	// codeListValue="theme
	public String gmd_identificationInfo_MD_DataIdentification_keywordType; // theme,
																			// or
																			// discipline?

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:title/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_keywords_ThesaurusTitle;

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date/gco:Date
	public String gmd_identificationInfo_MD_DataIdentification_keywords_ThesaurusDate; // 2008-10-29

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:thesaurusName/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:dateType/gmd:CI_DateTypeCode
	// codeList="http://asdd.ga.gov.au/asdd/profileinfo/gmxCodelists.xml#CI_DateTypeCode"
	// codeListValue="revision
	public String gmd_identificationInfo_MD_DataIdentification_keywords_ThesaurusDateType; // revision,
																							// publication

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:resourceConstraints/gmd:MD_Constraints/gmd:useLimitation/gco:CharacterString
	public String gmd_identificationInfo_MD_DataIdentification_MD_Constraints; // CC
																				// License

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:spatialResolution/gmd:MD_Resolution/gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator/gco:Integer>50000
	public Integer gmd_identificationInfo_MD_DataIdentification_MD_Resolution_scaleDenominator; // 50000
																								// (:1)

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:language/gmd:LanguageCode/codeList="./resources/codeList.xml#LanguageCode"
	// codeListValue="eng">eng
	public String gmd_identificationInfo_MD_DataIdentification_language; // eng

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:characterSet/gco:nilReason="inapplicable
	public String gmd_identificationInfo_MD_DataIdentification_characterSet; // utf8?

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:topicCategory/gmd:MD_TopicCategoryCode
	// eg geoscientificInformation isolist?
	public String gmd_identificationInfo_MD_DataIdentification_TopicCategoryCode; // geoscientificInformation

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:description/gco:CharacterString>regional
	// specific description
	public String gmd_identificationInfo_MD_DataIdentification_extent_Description;

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:westBoundLongitude/gco:Decimal
	public Double gmd_identificationInfo_MD_DataIdentification_extent_BBOX_westBoundLongitude;

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:eastBoundLongitude/gco:Decimal
	public Double gmd_identificationInfo_MD_DataIdentification_extent_BBOX_eastBoundLongitude;

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:southBoundLongitude/gco:Decimal
	public Double gmd_identificationInfo_MD_DataIdentification_extent_BBOX_southBoundLatitude;

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:northBoundLongitude/gco:Decimal
	public Double gmd_identificationInfo_MD_DataIdentification_extent_BBOX_northBoundLatitude;

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:temporalElement
	// gco:nilReason="inapplicable"/>
	public Boolean gmd_identificationInfo_MD_DataIdentification_extent_temporalElement;

	// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:verticalElement
	// gco:nilReason="inapplicable"/>
	public Boolean gmd_identificationInfo_MD_DataIdentification_extent_verticalElement;

	// gmd:distributionInfo/gmd:MD_Distribution/gmd:distributionFormat/gmd:MD_Format/gmd:name/gco:CharacterString
	// // xls
	// only if not really a service, but rather a file/report
	public String gmd_distributionInfo_MD_Distribution_formatName;

	// gmd:distributionInfo/gmd:MD_Distribution/gmd:distributionFormat/gmd:MD_Format/gmd:version/gco:CharacterString
	// // excel97
	public String gmd_distributionInfo_MD_Distribution_formatVersion;

	// gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:linkage/gmd:URL
	// http://portal.smart-project.info/geoserver/horowhenua_ws/wms?SERVICE=WMS
	// or just: http://data.linz.govt.nz/layer/767/
	public String gmd_distributionInfo_MD_Distribution_MD_DigitalTransferOptions_CI_OnlineResource_linkage;

	// gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:protocol/gco:CharacterString
	// with WMS add protocol n stuff: OGC:WMS-1.1.1-http-get-map
	public String gmd_distributionInfo_MD_Distribution_MD_DigitalTransferOptions_CI_OnlineResource_protocol;

	// gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:name/gco:CharacterString
	// wms layer dataset name: horowhenua_ws:nz_aquifers
	public String gmd_distributionInfo_MD_Distribution_MD_DigitalTransferOptions_CI_OnlineResource_name;

	// gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:description/gco:CharacterString
	// wms layer speaking name or desription: nz_aquifers_(white_2001)
	public String gmd_distributionInfo_MD_Distribution_MD_DigitalTransferOptions_CI_OnlineResource_description;

	// gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:scope/gmd:DQ_Scope/gmd:level/gmd:MD_ScopeCode
	// codeList="./resources/codeList.xml#MD_ScopeCode" codeListValue="dataset"
	public String gmd_dataQualityInfo_DQ_Scope_level; // dataset

	// gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:statement/gco:CharacterString
	// can be gco:nilReason="missing"
	public String gmd_dataQualityInfo_LI_Lineage_statement;

	// gmd:metadataConstraints/gmd:MD_LegalConstraints/gmd:useLimitation/gco:CharacterString
	public String gmd_metadataConstraints_MD_LegalConstraints_useLimitation; // Released
																				// under
																				// Creative
																				// Commons
																				// By
	public String getMDRecordXml () {
		
		XmlOptions xmlOptions = XmlOptionsHelper.getInstance().getXmlOptions();
		
		MetaData meta = this;
		
		org.isotc211.x2005.gmd.MDMetadataDocument xbMetaDoc = MDMetadataDocument.Factory
				.newInstance(xmlOptions);
		MDMetadataType xbMetaType = xbMetaDoc.addNewMDMetadata();

		// creates 067e6162-3b6f-4ae2-a171-2470b63dff00, pretty similar length
		// etc as geonetwork
		UUID uniqueID = UUID.randomUUID();
		xbMetaType.addNewFileIdentifier().setCharacterString(
				StringEscapeUtils.escapeXml(uniqueID.toString()));

		// based on the metadata std the different codelists should be used
		String codeList = isoCodeList;

		if (meta.gmd_metadataStandardName.contains("ISO 19115:2003/19139")) {
			xbMetaType.addNewMetadataStandardName().setCharacterString(
					"ISO 19115:2003/19139");
			xbMetaType.addNewMetadataStandardVersion()
					.setCharacterString("1.0");
			codeList = isoCodeList;
		} else if (meta.gmd_metadataStandardName
				.contains("ANZLIC Metadata Profile")) {
			xbMetaType.addNewMetadataStandardName().setCharacterString(
					"ANZLIC Metadata Profile");
			xbMetaType.addNewMetadataStandardVersion()
					.setCharacterString("1.1");
			codeList = anzlicCodeList;
		}

		// here all the checking and parsing and creating the new record
		// and then eventually
		xbMetaType.addNewLanguage().setCharacterString("eng");
		xbMetaType.addNewCharacterSet().setMDCharacterSetCode(
				createCodeListValue(codeList, "utf8", "MD_CharacterSetCode"));

		if (meta.datainfolevel == null || meta.datainfolevel.isEmpty()) {
			meta.datainfolevel = "dataset";
		}
		meta.gmd_hierarchyLevel = meta.datainfolevel;
		meta.gmd_hierarchyLevelName = meta.datainfolevel;

		xbMetaType.addNewHierarchyLevel().setMDScopeCode(
				createCodeListValue(codeList, meta.gmd_hierarchyLevel,
						"MD_ScopeCode"));
		xbMetaType.addNewHierarchyLevelName().setCharacterString(
				meta.gmd_hierarchyLevelName);

		CIResponsiblePartyType responsibleParty = xbMetaType.addNewContact()
				.addNewCIResponsibleParty();
		responsibleParty
				.addNewIndividualName()
				.setCharacterString(
						StringEscapeUtils
								.escapeXml(meta.gmd_Contact_CI_ResponsibleParty_ci_individualName));

		if (meta.gmd_Contact_CI_ResponsibleParty_ci_organisationName != null
				&& !meta.gmd_Contact_CI_ResponsibleParty_ci_organisationName
						.isEmpty()) {
			responsibleParty
					.addNewOrganisationName()
					.setCharacterString(
							StringEscapeUtils
									.escapeXml(meta.gmd_Contact_CI_ResponsibleParty_ci_organisationName));
		}
		if (meta.gmd_Contact_CI_ResponsibleParty_ci_positionName != null
				&& !meta.gmd_Contact_CI_ResponsibleParty_ci_positionName
						.isEmpty()) {
			responsibleParty
					.addNewOrganisationName()
					.setCharacterString(
							StringEscapeUtils
									.escapeXml(meta.gmd_Contact_CI_ResponsibleParty_ci_positionName));
		}

		CIContactType ciContact = responsibleParty.addNewContactInfo()
				.addNewCIContact();
		if (meta.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_telephone != null
				&& !meta.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_telephone
						.isEmpty()) {
			ciContact
					.addNewPhone()
					.addNewCITelephone()
					.addNewVoice()
					.setCharacterString(
							StringEscapeUtils
									.escapeXml(meta.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_telephone));
		}

		ciContact
				.addNewOnlineResource()
				.addNewCIOnlineResource()
				.addNewLinkage()
				.setURL(StringEscapeUtils
						.escapeXml(meta.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_onlineLinkage));

		CIAddressType ciAddress = ciContact.addNewAddress().addNewCIAddress();
		ciAddress
				.addNewElectronicMailAddress()
				.setCharacterString(
						StringEscapeUtils
								.escapeXml(meta.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_email));

		responsibleParty
				.addNewRole()
				.setCIRoleCode(
						createCodeListValue(
								codeList,
								StringEscapeUtils
										.escapeXml(meta.gmd_Contact_CI_ResponsibleParty_ci_pointOfContact),
								"CI_RoleCode"));

		Calendar calendar = new GregorianCalendar();

		xbMetaType.addNewDateStamp().setDateTime(calendar);

		xbMetaType
				.addNewReferenceSystemInfo()
				.addNewMDReferenceSystem()
				.addNewReferenceSystemIdentifier()
				.addNewRSIdentifier()
				.addNewCode()
				.setCharacterString(
						StringEscapeUtils
								.escapeXml(meta.gmd_referenceSystemInfo));

		xbMetaType.addNewIdentificationInfo().addNewAbstractMDIdentification();
		QName mdDataIdentType = new QName("http://www.isotc211.org/2005/gmd",
				"MD_DataIdentification", "gmd");
		xbMetaType.getIdentificationInfoArray(0).substitute(mdDataIdentType,
				MDDataIdentificationType.type);

		MDDataIdentificationDocument dataIdentDoc = MDDataIdentificationDocument.Factory
				.newInstance(xmlOptions);
		MDDataIdentificationType dataIdent = dataIdentDoc
				.addNewMDDataIdentification();
		CICitationType dataCi = dataIdent.addNewCitation().addNewCICitation();
		dataCi.addNewTitle()
				.setCharacterString(
						StringEscapeUtils
								.escapeXml(meta.gmd_identificationInfo_MD_DataIdentification_CI_Citation_gmd_title));

		SimpleDateFormat sdf4parse = new SimpleDateFormat("yyyy");
		try {
			calendar.setTime(sdf4parse
					.parse(meta.gmd_identificationInfo_MD_DataIdentification_CI_Citation_CI_Date));
		} catch (ParseException e) {
			// e.printStackTrace();
			// if parsing fails we take NOW
		}

		dataCi.addNewDate().addNewCIDate().addNewDate().setDateTime(calendar);
		
		dataCi.getDateArray(0)
				.getCIDate()
				.addNewDateType()
				.setCIDateTypeCode(
						createCodeListValue(
								codeList,
								StringEscapeUtils
										.escapeXml(meta.gmd_identificationInfo_MD_DataIdentification_CI_Citation_CI_Date_dateType),
								"CI_DateTypeCode"));

		// abstract
		dataIdent
				.addNewAbstract()
				.setCharacterString(
						StringEscapeUtils
								.escapeXml(meta.gmd_identificationInfo_MD_DataIdentification_abstract));

		// responsibleParty (same as above, wonder if that works)
		dataIdent.addNewPointOfContact()
				.setCIResponsibleParty(responsibleParty);

		// keywords
		MDKeywordsType keywordsType = dataIdent.addNewDescriptiveKeywords()
				.addNewMDKeywords();

		if (meta.keywords != null && meta.keywords.length() > 0) {
			String[] keywords = StringEscapeUtils.escapeXml(meta.keywords)
					.split(",");

			for (String keyword : keywords) {
				keywordsType.addNewKeyword().setCharacterString(keyword);
			}

			keywordsType.addNewType()
					.setMDKeywordTypeCode(
							createCodeListValue(codeList, "theme",
									"MD_KeywordTypeCode"));
			// here could go thesaurus
		}

		// constarints CC License of resource (vs metadata use)
		dataIdent
				.addNewResourceConstraints()
				.addNewMDConstraints()
				.addNewUseLimitation()
				.setCharacterString(
						StringEscapeUtils
								.escapeXml(meta.gmd_metadataConstraints_MD_LegalConstraints_useLimitation));

		// scale denominator
		if (meta.gmd_identificationInfo_MD_DataIdentification_MD_Resolution_scaleDenominator != null && meta.gmd_identificationInfo_MD_DataIdentification_MD_Resolution_scaleDenominator > 0) {
			dataIdent
					.addNewSpatialResolution()
					.addNewMDResolution()
					.addNewEquivalentScale()
					.addNewMDRepresentativeFraction()
					.addNewDenominator()
					.setInteger(
							BigInteger
									.valueOf(meta.gmd_identificationInfo_MD_DataIdentification_MD_Resolution_scaleDenominator));
		}

		// characterset and language
		dataIdent.addNewLanguage().setCharacterString("eng");
		dataIdent.addNewCharacterSet().setMDCharacterSetCode(
				createCodeListValue(codeList, "utf8", "MD_CharacterSetCode"));

		// topiccategory
		GmdTopicCategory check = OGCConstants.GmdTopicCategory
				.valueOf(meta.gmd_identificationInfo_MD_DataIdentification_TopicCategoryCode);
		MDTopicCategoryCodeType.Enum topic = MDTopicCategoryCodeType.INLAND_WATERS;

		switch (check) {
		case environment:
			topic = MDTopicCategoryCodeType.ENVIRONMENT;
			break;
		case inlandWaters:
			topic = MDTopicCategoryCodeType.INLAND_WATERS;
			break;
		case geoscientificInformation:
			topic = MDTopicCategoryCodeType.GEOSCIENTIFIC_INFORMATION;
			break;
		case farming:
			topic = MDTopicCategoryCodeType.FARMING;
			break;
		case biota:
			topic = MDTopicCategoryCodeType.BIOTA;
			break;
		case boundaries:
			topic = MDTopicCategoryCodeType.BOUNDARIES;
			break;
		case climatologyMeteorologyAtmosphere:
			topic = MDTopicCategoryCodeType.CLIMATOLOGY_METEOROLOGY_ATMOSPHERE;
			break;
		case oceans:
			topic = MDTopicCategoryCodeType.OCEANS;
			break;
		case imageryBaseMapsEarthCover:
			topic = MDTopicCategoryCodeType.IMAGERY_BASE_MAPS_EARTH_COVER;
			break;
		case location:
			topic = MDTopicCategoryCodeType.LOCATION;
			break;
		case elevation:
			topic = MDTopicCategoryCodeType.ELEVATION;
			break;
		case economy:
			topic = MDTopicCategoryCodeType.ECONOMY;
			break;
		case health:
			topic = MDTopicCategoryCodeType.HEALTH;
			break;
		case society:
			topic = MDTopicCategoryCodeType.SOCIETY;
			break;
		case planningCadastre:
			topic = MDTopicCategoryCodeType.PLANNING_CADASTRE;
			break;
		case structure:
			topic = MDTopicCategoryCodeType.STRUCTURE;
			break;
		case transportation:
			topic = MDTopicCategoryCodeType.TRANSPORTATION;
			break;
		case utilitiesCommunication:
			topic = MDTopicCategoryCodeType.UTILITIES_COMMUNICATION;
			break;
		case intelligenceMilitary:
			topic = MDTopicCategoryCodeType.INTELLIGENCE_MILITARY;
			break;
		}
		dataIdent.addNewTopicCategory().setMDTopicCategoryCode(topic);

		// extent, bounding box later, linz gazetteer
		// http://wfs.data.linz.govt.nz/a8fb9bcd52684b7abe14dd4664ce9df9/v/x1681/wfs?service=WFS&request=GetFeature&typeName=v:x1681&MAXFEATURES=3
		if (meta.gmd_identificationInfo_MD_DataIdentification_extent_Description != null
				&& !meta.gmd_identificationInfo_MD_DataIdentification_extent_Description
						.isEmpty()) {
			EXExtentType extent = dataIdent
					.addNewExtent()
					.addNewEXExtent();
			
			extent.addNewDescription()
					.setCharacterString(
							StringEscapeUtils
									.escapeXml(meta.gmd_identificationInfo_MD_DataIdentification_extent_Description));
			
			if (meta.mapExtentCoordinates != null && !meta.mapExtentCoordinates.isEmpty()) {
				
				// gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:westBoundLongitude/gco:Decimal
				org.isotc211.x2005.gmd.EXGeographicBoundingBoxDocument geobboxdoc = EXGeographicBoundingBoxDocument.Factory.newInstance(xmlOptions);
				EXGeographicBoundingBoxType geobbox = geobboxdoc.addNewEXGeographicBoundingBox();
				
				geobbox.addNewEastBoundLongitude().setDecimal(BigDecimal.valueOf(meta.gmd_identificationInfo_MD_DataIdentification_extent_BBOX_eastBoundLongitude));
				geobbox.addNewWestBoundLongitude().setDecimal(BigDecimal.valueOf(meta.gmd_identificationInfo_MD_DataIdentification_extent_BBOX_westBoundLongitude));
				geobbox.addNewNorthBoundLatitude().setDecimal(BigDecimal.valueOf(meta.gmd_identificationInfo_MD_DataIdentification_extent_BBOX_northBoundLatitude));
				geobbox.addNewSouthBoundLatitude().setDecimal(BigDecimal.valueOf(meta.gmd_identificationInfo_MD_DataIdentification_extent_BBOX_southBoundLatitude));
				
				extent.addNewGeographicElement().set(geobboxdoc);
				/*
				 * (EXGeographicBoundingBoxType) extent.addNewGeographicElement()
						.addNewAbstractEXGeographicExtent()
						.substitute(new QName(OGCConstants.NS_GMD, "EXGeographicBoundingBoxType", OGCConstants.NS_GMD_PREFIX), EXGeographicBoundingBoxType.type);
				 */
			}
		}
		

		// putting it back into the main record
		xbMetaType.getIdentificationInfoArray(0).set(dataIdentDoc);

		MDDistributionType distInfo = xbMetaType.addNewDistributionInfo()
				.addNewMDDistribution();

		if (meta.gmd_distributionInfo_MD_Distribution_formatName != null
					&& !meta.gmd_distributionInfo_MD_Distribution_formatName
							.isEmpty()) {
				
			
			distInfo.addNewDistributionFormat()
						.addNewMDFormat()
						.addNewName()
						.setCharacterString(
								StringEscapeUtils
										.escapeXml(meta.gmd_distributionInfo_MD_Distribution_formatName));

			distInfo.getDistributionFormatArray(0)
						.getMDFormat()
						.addNewVersion()
						.setCharacterString(
								StringEscapeUtils
										.escapeXml(meta.gmd_distributionInfo_MD_Distribution_formatVersion));

		}

		CIOnlineResourceType transOpts = distInfo.addNewTransferOptions()
				.addNewMDDigitalTransferOptions().addNewOnLine()
				.addNewCIOnlineResource();
		if (meta.gmd_distributionInfo_MD_Distribution_MD_DigitalTransferOptions_CI_OnlineResource_linkage != null
				&& !meta.gmd_distributionInfo_MD_Distribution_MD_DigitalTransferOptions_CI_OnlineResource_linkage
						.isEmpty()) {

			transOpts
					.addNewLinkage()
					.setURL(StringEscapeUtils
							.escapeXml(meta.gmd_distributionInfo_MD_Distribution_MD_DigitalTransferOptions_CI_OnlineResource_linkage));
			transOpts.addNewProtocol().setCharacterString("Web address (URL)");

		} else {
			if (meta.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_onlineLinkage != null
					&& !meta.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_onlineLinkage
							.isEmpty()) {
				transOpts
						.addNewLinkage()
						.setURL(StringEscapeUtils
								.escapeXml(meta.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_onlineLinkage));
				transOpts.addNewProtocol().setCharacterString(
						"Web address (URL)");
			} else {
				transOpts.addNewLinkage().setURL(
						"http://portal.smart-project.info");
				transOpts.addNewProtocol().setCharacterString(
						"Related link (URL)");
			}
		}
		// transOpts.addNewName().setCharacterString(meta.gmd_distributionInfo_MD_Distribution_MD_DigitalTransferOptions_CI_OnlineResource_name);
		// transOpts.addNewDescription().setCharacterString(meta.gmd_distributionInfo_MD_Distribution_MD_DigitalTransferOptions_CI_OnlineResource_description);

		// gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:scope/gmd:DQ_Scope/gmd:level/gmd:MD_ScopeCode
		// codeList="./resources/codeList.xml#MD_ScopeCode"
		// codeListValue="dataset"
		xbMetaType
				.addNewDataQualityInfo()
				.addNewDQDataQuality()
				.addNewScope()
				.addNewDQScope()
				.addNewLevel()
				.setMDScopeCode(
						createCodeListValue(codeList, StringEscapeUtils
								.escapeXml(meta.gmd_hierarchyLevelName),
								"MD_ScopeCode"));

		// gmd:dataQualityInfo/gmd:DQ_DataQuality/gmd:lineage/gmd:LI_Lineage/gmd:statement/gco:CharacterString
		// can be gco:nilReason="missing"
		if (meta.gmd_dataQualityInfo_LI_Lineage_statement != null
				&& !meta.gmd_dataQualityInfo_LI_Lineage_statement.isEmpty()) {
			xbMetaType
					.getDataQualityInfoArray(0)
					.getDQDataQuality()
					.addNewLineage()
					.addNewLILineage()
					.addNewStatement()
					.setCharacterString(
							StringEscapeUtils
									.escapeXml(meta.gmd_dataQualityInfo_LI_Lineage_statement));
		} else {
			StringBuilder altLineage = new StringBuilder();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			altLineage.append("This metadata record has been created in the SMART project"
					+ "based on the publicly accessible abstracts from the Journal of Hydrology New Zealand"
					+ " on " + sdf.format(calendar.getTime()) + ".");

			altLineage
					.append(" No further information regarding the lineage of the dataset was provided so far.");

			xbMetaType.getDataQualityInfoArray(0).getDQDataQuality()
					.addNewLineage().addNewLILineage().addNewStatement()
					.setCharacterString(altLineage.toString());
		}

		// gmd:metadataConstraints/gmd:MD_LegalConstraints/gmd:useLimitation/gco:CharacterString
		// beware it's only coming as MD_Constraints!
		xbMetaType
				.addNewMetadataConstraints()
				.addNewMDConstraints()
				.addNewUseLimitation()
				.setCharacterString(
						StringEscapeUtils
								.escapeXml(meta.gmd_metadataConstraints_MD_LegalConstraints_useLimitation));
		
		if (this.validate() == false) {
			Logger.debug("validation errors");
			return "validation errors";
		} else {
			return xbMetaDoc.xmlText(xmlOptions);
		}
	}
	
	// validation to request required fields
	public boolean validate() {

		List<String> validationList = new ArrayList<String>();

		if (this.isCreateNewRecordRequest != null
				&& !this.isCreateNewRecordRequest.isEmpty()) {

			// title
			if (this.gmd_identificationInfo_MD_DataIdentification_CI_Citation_gmd_title == null
					|| this.gmd_identificationInfo_MD_DataIdentification_CI_Citation_gmd_title
							.isEmpty()) {
				String valerr = new String(
						"gmd_identificationInfo_MD_DataIdentification_CI_Citation_gmd_title " + 
						" Please enter a title.");
				validationList.add(valerr);
			}

			// abstract
			if (this.gmd_identificationInfo_MD_DataIdentification_abstract == null
					|| this.gmd_identificationInfo_MD_DataIdentification_abstract
							.isEmpty()) {
				String valerr = new String(
						"gmd_identificationInfo_MD_DataIdentification_abstract " + 
						" Please describe the dataset with a short abstract.");
				validationList.add(valerr);
			}

			// email
			if (this.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_email == null
					|| this.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_email
							.isEmpty()) {
				String valerr = new String(
						"gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_email " + 
						" Please enter a valid contact email address.");
				validationList.add(valerr);
			}

			// (org) url
			if (this.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_onlineLinkage == null
					|| this.gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_onlineLinkage
							.isEmpty()) {
				String valerr = new String(
						"gmd_Contact_CI_ResponsibleParty_CI_Contact_ci_onlineLinkage " + 
						" Please provide a link (URL) for your or the data provider organisation.");
				validationList.add(valerr);
			}

			// keywords
			if (this.keywords == null || this.keywords.isEmpty()) {
				String valerr = new String("keywords " + 
						" Please provide keywords for the dataset.");
				validationList.add(valerr);
			}

			// date and type

			// contact name
			if (this.gmd_Contact_CI_ResponsibleParty_ci_individualName == null
					|| this.gmd_Contact_CI_ResponsibleParty_ci_individualName
							.isEmpty()) {
				String valerr = new String(
						"gmd_Contact_CI_ResponsibleParty_ci_individualName " + 
						" Please enter your or another appropriate contact name.");
				validationList.add(valerr);
			}
		}
		if (validationList.size() == 0) {
			return true;
		} else {
			for (String valerr : validationList) {
				Logger.error("VALDIATION ERR " + this.title + " ### " + gmd_distributionInfo_MD_Distribution_MD_DigitalTransferOptions_CI_OnlineResource_linkage  + " ### " + valerr);
			}
			return false;
		}
	}
	
	public static CodeListValueType createCodeListValue(String codeList,
			String value, String type) {
		CodeListValueType codeListValue = CodeListValueType.Factory
				.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
		// codeListValue.setCodeSpace(codeList);
		codeListValue.setCodeList(codeList + "#" + type);
		codeListValue.setCodeListValue(value);

		return codeListValue;
	}
}
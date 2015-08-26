/**
 * This file contains collected constants from 
 * https://svn.52north.org/svn/swe/main/SOS/Service/branches/52n-sos-400-refactored
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * ... which is free software and can be redistributed and/or modified under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 */

package models;

import javax.xml.namespace.QName;

public class OGCConstants {

	// CSW GMD stuff
	public static enum GmdTopicCategory {
		environment, inlandWaters, geoscientificInformation, farming, biota, boundaries, climatologyMeteorologyAtmosphere, oceans, imageryBaseMapsEarthCover, location, elevation, economy, health, society, planningCadastre, structure, transportation, utilitiesCommunication, intelligenceMilitary;
	}

	// attribute names
	public static final String AN_HREF = "href";
	public static final String AN_TITLE = "title";
	public static final String AN_SCHEMA_LOCATION = "schemaLocation";
	// namespaces and schema locations
	public static final String NS_XLINK = "http://www.w3.org/1999/xlink";
	public static final String NS_XLINK_PREFIX = "xlink";
	public static final String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String NS_XSI_PREFIX = "xsi";
	public static final String SCHEMA_LOCATION_XLINK = "http://www.w3.org/1999/xlink.xsd";
	public static final String NS_XS = "http://www.w3.org/2001/XMLSchema";
	public static final String NS_XS_PREFIX = "xs";
	public static final QName QN_SCHEMA_LOCATION = new QName(NS_XSI,
			AN_SCHEMA_LOCATION);
	public static final QName QN_SCHEMA_LOCATION_PREFIXED = new QName(NS_XSI,
			AN_SCHEMA_LOCATION, NS_XSI_PREFIX);

	public static final String NS_OGC = "http://www.opengis.net/ogc";

	public static final String NS_OGC_PREFIX = "ogc";

	public static final String SCHEMA_LOCATION_OGC = "http://schemas.opengis.net/sos/1.0.0/ogc4sos.xsd";

	public static final String UNKNOWN = "http://www.opengis.net/def/nil/OGC/0/unknown";

	/** Constant for prefixes of FOIs */
	public static final String URN_FOI_PREFIX = "urn:ogc:def:object:feature:";

	public static final String URN_IDENTIFIER_IDENTIFICATION = "urn:ogc:def:identifier:OGC::identification";

	public static final String URN_OFFERING_ID = "urn:ogc:def:identifier:OGC:offeringID";

	/** Constant for prefixes of procedures */
	public static final String URN_PHENOMENON_PREFIX = "urn:ogc:def:phenomenon:OGC:1.0.30:";

	/** Constant for prefixes of procedures */
	public static final String URN_PROCEDURE_PREFIX = "urn:ogc:object:feature:Sensor:IFGI:";

	public static final String URN_PROPERTY_NAME_LOCATION = "urn:ogc:data:location";

	public static final String URN_PROPERTY_NAME_SAMPLING_GEOMETRY = "urn:ogc:data:samplingGeometry";

	public static final String URN_PROPERTY_NAME_SPATIAL_VALUE = "urn:ogc:data:spatialValue";

	public static final String URN_UNIQUE_IDENTIFIER = "urn:ogc:def:identifier:OGC:uniqueID";

	public static final String URN_UNIQUE_IDENTIFIER_END = "uniqueID";

	public static final String URN_UNIQUE_IDENTIFIER_START = "urn:ogc:def:identifier:OGC:";

	// //////////////////////////////
	// namespaces and schema locations
	public static final String SCHEMA_LOCATION_OM = "http://schemas.opengis.net/om/1.0.0/om.xsd";
	public static final String SCHEMA_LOCATION_OM_CONSTRAINT = "http://schemas.opengis.net/om/1.0.0/extensions/observationSpecialization_constraint.xsd";
	public static final String SCHEMA_LOCATION_OM_2 = "http://schemas.opengis.net/om/2.0/observation.xsd";
	public static final String SCHEMA_LOCATION_OM_2_OM_OBSERVATION = SCHEMA_LOCATION_OM_2
			+ "#OM_Observation";
	public static final String NS_OM = "http://www.opengis.net/om/1.0";
	public static final String NS_OM_2 = "http://www.opengis.net/om/2.0";
	public static final String NS_OM_PREFIX = "om";
	public static final String NS_GMD = "http://www.isotc211.org/2005/gmd";
	public static final String NS_GMD_PREFIX = "gmd";
	public static final String NS_CSW = "http://www.opengis.net/cat/csw/2.0.2";
	public static final String NS_CSW_PREFIX = "csw";

	public static final String NS_GTS = "http://www.isotc211.org/2005/gts";
	public static final String NS_GTS_PREFIX = "gts";

	public static final String NS_GCO = "http://www.isotc211.org/2005/gco";
	public static final String NS_GCO_PREFIX = "gco";

	public static final String NS_C3ALPS = "http://portal.c3alps.eu/namespace";
	public static final String NS_C3ALPS_PREFIX = "c3alps";

	public static final String NS_GEONET = "http://www.fao.org/geonetwork";
	public static final String NS_GEONET_PREFIX = "geonet";

	public static final String NS_SRV = "http://www.isotc211.org/2005/srv";
	public static final String NS_SRV_PREFIX = "srv";

	public static final String NS_GMX = "http://www.isotc211.org/2005/gmx";
	public static final String NS_GMX_PREFIX = "gmx";

	public static final String NS_WV = "http://www.n52.org/wv";

	public static final String NS_FES_2 = "http://www.opengis.net/fes/2.0";
	public static final String NS_FES_2_PREFIX = "fes";

	public static final String SCHEMA_LOCATION_WMC_10 = "http://www.opengis.net/context http://schemas.opengis.net/context/1.0.0/context.xsd";
	public static final String SCHEMA_LOCATION_WMC_11 = "http://www.opengis.net/context http://schemas.opengis.net/context/1.1.0/context.xsd";
	public static final String NS_WMC = "http://www.opengis.net/context";
	public static final String NS_WMC_PREFIX = "wmc";

	public static final String NS_WMS = "http://www.opengis.net/wms";
	public static final String NS_WMS_PREFIX = "wms";

	public static final String SCHEMA_LOCATION_SLD_10 = "http://schemas.opengis.net/sld/1.0.0/context.xsd";
	public static final String SCHEMA_LOCATION_SLD_11 = "http://schemas.opengis.net/sld/1.1.0/context.xsd";
	public static final String NS_SLD = "http://www.opengis.net/sld";
	public static final String NS_SLD_PREFIX = "sld";

	public static final String SCHEMA_LOCATION_SE = "http://schemas.opengis.net/se/1.1.0/common.xsd";
	public static final String NS_SE = "http://www.opengis.net/se";
	public static final String NS_SE_PREFIX = "se";

	public static final String NS_OL = "http://openlayers.org/context";
	public static final String NS_OL_PREFIX = "ol";

	/* namespaces and schema locations */
	public static final String NS_GML = "http://www.opengis.net/gml";

	public static final String NS_GML_32 = "http://www.opengis.net/gml/3.2";

	public static final String NS_GML_PREFIX = "gml";

	public static final String NS_GML_32_PREFIX = "gml32";

	public static final String SCHEMA_LOCATION_GML = "http://schemas.opengis.net/gml/3.1.1/base/gml.xsd";

	public static final String SCHEMA_LOCATION_GML_32 = "http://schemas.opengis.net/gml/3.2.1/gml.xsd";

	public static final String GML_ID_ATT = "id";

	// //////////////////////////////////////////////////////////////////////
	// other
	public static final String AN_ID = "id";
	public static final String CONTENT_TYPE_OM = "text/xml;subtype=\"om/1.0.0\"";
	public static final String CONTENT_TYPE_OM_2 = "text/xml;subtype=\"om/2.0.0\"";
	public static final String RESPONSE_FORMAT_OM = "http://www.opengis.net/om/1.0.0";
	public static final String RESPONSE_FORMAT_OM_2 = "http://www.opengis.net/om/2.0";
	// ///////////////////////////////////////////////////////////////////
	// names of elements in O&M documents
	public static final String EN_ASCII_BLOCK = "AsciiBlock";
	public static final String EN_ABSTRACT_DATA_GROUP = "_DataGroup";
	public static final String EN_ABSTRACT_DATA_QUALITY = "AbstractDQ_Element";
	public static final String EN_BOUNDED_BY = "boundedBy";
	public static final String EN_CATEGORY_OBSERVATION = "CategoryObservation";
	public static final String EN_COUNT_OBSERVATION = "CountObservation";
	public static final String EN_TEXT_OBSERVATION = "TextObservation";
	public static final String EN_TRUTH_OBSERVATION = "TruthObservation";
	public static final String EN_GEOMETRY_OBSERVATION = "GeometryObservation";
	public static final String EN_COMMON_OBSERVATION = "CommonObservation";
	public static final String EN_COMPOSITE_PHENOMENON = "CompositePhenomenon";
	public static final String EN_DATA_GROUP = "DataGroup";
	public static final String EN_DQ_QUAN_ATTR_ACC = "DQ_QuantitativeAttributeAccuracy";
	public static final String EN_DQ_NON_QUAN_ATTR_ACC = "DQ_NonQuantitativeAttributeAccuracy";
	public static final String EN_DQ_COMPL_COMM = "DQ_CompletenessCommission";
	public static final String EN_DQ_COMPL_OM = "DQ_CompletenessOmission";
	public static final String EN_FEATURE = "Feature";
	public static final String EN_FEATURE_COLLECTION = "FeatureCollection";
	public static final String EN_GEOREF_FEATURE = "GeoReferenceableFeature";
	public static final String EN_MEMBER = "member";
	public static final String EN_MEASUREMENT = "Measurement";
	public static final String EN_OBSERVED_PROPERTY = "observedProperty";
	public static final String EN_OBSERVATION_COLLECTION = "ObservationCollection";
	public static final String EN_OBSERVATION = "Observation";
	public static final String EN_PHENOMENON = "Phenomenon";
	public static final String EN_COMPOSITE_SURFACE = "CompositeSurface";
	public static final String EN_RESULT = "result";
	public static final String EN_WV_STATION = "WVStation";
	public static final String EN_TEMPORAL_OPS = "temporalOps";
	public static final String EN_PROCEDURE = "procedure";
	public static final String EN_FEATURE_OF_INTEREST = "featureOfInterest";
	// /////////////////////////////////////////////////////////////////////////////////
	// other constants
	public static final String PHEN_SAMPLING_TIME = "http://www.opengis.net/def/property/OGC/0/SamplingTime";
	public static final String PHENOMENON_TIME = "http://www.opengis.net/def/property/OGC/0/PhenomenonTime";
	public static final String PHENOMENON_TIME_NAME = "phenomenonTime";
	public static final String PHEN_UOM_ISO8601 = "http://www.opengis.net/def/uom/ISO-8601/0/Gregorian";
	public static final String PHEN_FEATURE_OF_INTEREST = "http://www.opengis.net/def/property/OGC/0/FeatureOfInterest";
	public static final String EN_ABSTRACT_DATA_RECORD = "AbstractDataRecord";
	public static final String EN_SIMPLE_DATA_RECORD = "SimpleDataRecord";
	public static final String ATTR_SRS_NAME = "srsName";
	// observation types
	public static final String OBS_TYPE_MEASUREMENT = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement";
	public static final String OBS_TYPE_CATEGORY_OBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CategoryObservation";
	public static final String OBS_TYPE_COMPLEX_OBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_ComplexObservation";
	public static final String OBS_TYPE_COUNT_OBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_CountObservation";
	public static final String OBS_TYPE_GEOMETRY_OBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_GeometryObservation";
	// no Definition in O&M and not in Lightweight Profile
	public static final String OBS_TYPE_TEXT_OBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TextObservation";
	public static final String OBS_TYPE_TRUTH_OBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_TruthObservation";
	public static final String OBS_TYPE_OBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Observation";
	public static final String OBS_TYPE_UNKNOWN = UNKNOWN;
	public static final String OBS_TYPE_SWE_ARRAY_OBSERVATION = "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_SWEArrayObservation";
	public static final String OBS_RESULT_TYPE_OBSERVATION = "http://www.opengis.net/sensorML/2.0/DataArray";
	public static final String SAMPLING_FEAT_TYPE_UNKNOWN = "http://www.opengis.net/def/samplingFeatureType/unknown";
	// ////////////////////////////////////////////////////////
	// resultModel constants; not possible to use enum because of
	public static final QName RESULT_MODEL_MEASUREMENT = new QName(NS_OM,
			EN_MEASUREMENT, NS_OM_PREFIX);
	public static final QName RESULT_MODEL_GEOMETRY_OBSERVATION = new QName(
			NS_OM, EN_GEOMETRY_OBSERVATION, NS_OM_PREFIX);
	public static final QName RESULT_MODEL_CATEGORY_OBSERVATION = new QName(
			NS_OM, EN_CATEGORY_OBSERVATION, NS_OM_PREFIX);
	public static final QName RESULT_MODEL_OBSERVATION = new QName(NS_OM,
			EN_OBSERVATION, NS_OM_PREFIX);
	public static final QName RESULT_MODEL_COUNT_OBSERVATION = new QName(NS_OM,
			EN_COUNT_OBSERVATION, NS_OM_PREFIX);
	public static final QName RESULT_MODEL_TRUTH_OBSERVATION = new QName(NS_OM,
			EN_TRUTH_OBSERVATION, NS_OM_PREFIX);
	public static final QName RESULT_MODEL_TEXT_OBSERVATION = new QName(NS_OM,
			EN_TEXT_OBSERVATION, NS_OM_PREFIX);

	public static final String NS_SOS_PREFIX = "sos";
	/**
	 * Constant for the content type of the response
	 */
	public static final String CONTENT_TYPE_XML = "text/xml";
	/**
	 * Constant for the content type of the response
	 */
	public static final String CONTENT_TYPE_ZIP = "application/zip";
	/**
	 * Constant for the content types of the accept formats
	 */

	/**
	 * Constant for the service name of the SOS
	 */
	public static final String SOS = "SOS";

	/**
	 * Constant for actual implementing version Measurement
	 */
	public static final String OBS_ID_PREFIX = "o_";
	/**
	 * Constant for actual implementing version OvservationCollection
	 */
	public static final String OBS_GENERIC_ID_PREFIX = "go_";
	/**
	 * Constant for actual implementing version OvservationCollection
	 */
	public static final String OBS_COL_ID_PREFIX = "oc_";
	/**
	 * Constant for actual implementing version ObservationTemplate
	 */
	public static final String OBS_TEMP_ID_PREFIX = "ot_";

	public static final String NS_SOS_20 = "http://www.opengis.net/sos/2.0";

	public static final String SCHEMA_LOCATION_SOS = "http://schemas.opengis.net/sos/2.0/sos.xsd";

	public static final String SCHEMA_LOCATION_INSERTION_CAPABILITIES = "http://schemas.opengis.net/sos/2.0/sosInsertionCapabilities.xsd#InsertionCapabilities";

	// namespace and schema locations
	public static final String NS_OWS = "http://www.opengis.net/ows/1.1";

	public static final String NS_OWS_PREFIX = "ows";

	public static final String SCHEMA_LOCATION_OWS = "http://schemas.opengis.net/ows/1.1.0/owsAll.xsd";

	public static final String SCHEMA_LOCATION_OWS_EXCEPTIONREPORT = "http://schemas.opengis.net/ows/1.1.0/owsExceptionReport.xsd";

	// exception messages
	public static final String SOAP_REASON_INVALID_PARAMETER_VALUE = "The request contained an invalid parameter value.";

	public static final String SOAP_REASON_INVALID_UPDATE_SEQUENCES = "The value of the updateSequence parameter in the GetCapabilities operation request was greater than the current value of the service metadata updateSequence number.";

	public static final String SOAP_REASON_MISSING_PARAMETER_VALUE = "The request did not include a value for a required parameter and this server does not declare a default value for it.";

	public static final String SOAP_REASON_NO_APPLICABLE_CODE = "A server exception was encountered.";

	public static final String SOAP_REASON_NO_DATA_AVAILABLE = "There are no data available.";

	public static final String SOAP_REASON_OPERATION_NOT_SUPPORTED = "The requested operation is not supported by this server.";

	public static final String SOAP_REASON_OPTION_NOT_SUPPORTED = "The request included/targeted an option that is not supported by this server.";

	public static final String SOAP_REASON_VERSION_NEGOTIATION_FAILED = "The list of versions in the â€˜AcceptVersionsâ€™ parameter value of the GetCapabilities operation request did not include any version supported by this server.";

	public static final String SOAP_REASON_RESPONSE_EXCEEDS_SIZE_LIMIT = "The requested result set exceeds the response size limit of this service and thus cannot be delivered.";

	public static final String SOAP_REASON_INVALID_PROPERTY_OFFERING_COMBINATION = "Observations for the requested combination of observedProperty and offering do not use SWE Common encoded results.";

	public static final String SOAP_REASON_UNKNOWN = "A server exception was encountered.";

	public static final String EN_EXCEPTION = "Exception";

	public static final String EN_EXCEPTION_CODE = "exceptionCode";

	public static final String EN_LOCATOR = "locator";

	public static final String EN_EXCEPTION_TEXT = "ExceptionText";

	public static final QName QN_EXCEPTION = new QName(NS_OWS, EN_EXCEPTION,
			NS_OWS_PREFIX);
	public static final QName QN_EXCEPTION_TEXT = new QName(NS_OWS,
			EN_EXCEPTION_TEXT, NS_OWS_PREFIX);
	// QName QN_NO_APPLICABLE_CODE = new QName(NS_OWS,
	// OwsExceptionCode.NoApplicableCode.name(), NS_OWS_PREFIX);

	/* element names used in GML */

	public static final String EN_TIME_INSTANT = "TimeInstant";

	public static final String EN_TIME_PERIOD = "TimePeriod";

	public static final String EN_TIME_BEGIN = "beginTime";

	public static final String EN_TIME_END = "endTime";

	public static final String EN_TIME_POSITION = "timePosition";

	public static final String EN_BEGIN_POSITION = "beginPosition";

	public static final String EN_END_POSITION = "endPosition";

	public static final String GML_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

	// nil values from GML 3.2 Section 8.2.3.1
	public static final String NIL_INAPPLICABLE = "urn:ogc:def:nil:OGC:inapplicable";

	public static final String NIL_MISSING = "urn:ogc:def:nil:OGC:missing";

	public static final String NIL_TEMPLATE = "urn:ogc:def:nil:OGC:template";

	public static final String NIL_UNKNOWN = "urn:ogc:def:nil:OGC:unknown";

	public static final String NIL_WITHHELD = "urn:ogc:def:nil:OGC:withheld";

	public static final String EN_ENVELOPE = "Envelope";

	public static final String EN_ABSTRACT_TIME_OBJECT = "_TimeObject";

	public static final String EN_ABSTRACT_TIME_OBJECT_32 = "AbstractTimeObject";

	public static final String EN_ABSTRACT_ENCODING = "_Encoding";

	public static final String EN_ABSTRACT_OBSERVATION = "AbstractObservation";

	public static final String EN_ABSTRACT_FEATURE = "_Feature";

	public static final String EN_ABSTRACT_FEATURE_32 = "AbstractFeature";

	public static final String EN_ABSTRACT_FEATURE_COLLECTION = "_FeatureCollection";

	public static final String EN_ABSTRACT_GEOMETRY = "_Geometry";

	public static final String EN_ABSTRACT_SURFACE = "_Surface";

	public static final String EN_ABSTRACT_TIME_GEOM_PRIM = "_TimeGeometricPrimitive";

	public static final String EN_ABSTRACT_RING = "_Ring";

	public static final String EN_LINEAR_RING = "LinearRing";

	public static final String EN_POINT = "Point";

	public static final String EN_POLYGON = "Polygon";

	public static final String EN_LOWER_CORNER = "lowerCorner";

	public static final String EN_UPPER_CORNER = "upperCorner";

	/* attribute names in GML */

	/* QNames for elements */

	// namespaces and schema lcations
	public static final String NS_SA = "http://www.opengis.net/sampling/1.0";

	public static final String NS_SA_PREFIX = "sa";

	public static final String NS_SF = "http://www.opengis.net/sampling/2.0";

	public static final String NS_SF_PREFIX = "sf";

	public static final String NS_SAMS = "http://www.opengis.net/samplingSpatial/2.0";

	public static final String NS_SAMS_PREFIX = "sams";

	public static final String SCHEMA_LOCATION_SA = "http://schemas.opengis.net/sampling/1.0.0/sampling.xsd";

	public static final String SCHEMA_LOCATION_SF = "http://schemas.opengis.net/sampling/2.0/samplingFeature.xsd";

	public static final String SCHEMA_LOCATION_SAMS = "http://schemas.opengis.net/samplingSpatial/2.0/spatialSamplingFeature.xsd";

	// feature types
	public static final String SAMPLING_FEAT_TYPE_SF_SAMPLING_FEATURE = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingFeature";

	public static final String SAMPLING_FEAT_TYPE_SF_SPATIAL_SAMPLING_FEATURE = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SpatialSamplingFeature";

	public static final String SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint";

	public static final String SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingCurve";

	public static final String SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingSurface";

	public static final String SAMPLING_FEAT_TYPE_SF_SAMPLING_SOLID = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingSolid";

	public static final String SAMPLING_FEAT_TYPE_SF_SAMPLING_SPECIMEN = "http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingSpecimen";

	// element names
	public static final String EN_SAMPLINGPOINT = "SamplingPoint";

	public static final String EN_SAMPLINGSURFACE = "SamplingSurface";

	public static final String EN_SAMPLINGCURVE = "SamplingCurve";

	public static final String FT_SAMPLINGPOINT = NS_SA_PREFIX + ":"
			+ EN_SAMPLINGPOINT;

	public static final String FT_SAMPLINGSURFACE = NS_SA_PREFIX + ":"
			+ EN_SAMPLINGSURFACE;

	public static final String FT_SAMPLINGCURVE = NS_SA_PREFIX + ":"
			+ EN_SAMPLINGCURVE;

	// namespaces and schema locations
	public static final String NS_SML = "http://www.opengis.net/sensorML/1.0.1";

	public static final String NS_SML_PREFIX = "sml";

	public static final String SCHEMA_LOCATION_SML = "http://schemas.opengis.net/sensorML/1.0.1/sensorML.xsd";

	public static final String SENSORML_OUTPUT_FORMAT_MIME_TYPE = "text/xml;subtype=\"sensorML/1.0.1\"";

	public static final String SENSORML_OUTPUT_FORMAT_URL = NS_SML;

	public static final String SENSORML_CONTENT_TYPE = "text/xml;subtype=\"sensorML/1.0.1\"";

	public static final String EN_SYSTEM = "System";

	public static final String EN_PROCESS_MODEL = "ProcessModel";

	public static final String EN_COMPONENT = "Component";

	public static final String EN_ABSTRACT_PROCESS = "AbstractProcess";

	public static final String VERSION_V101 = "1.0.1";

	/**
	 * Name of a SensorML element describing the offering, a procedure/sensor is
	 * related to, or should be inserted into
	 */
	public static final String ELEMENT_NAME_OFFERING = "offering";

	/**
	 * name of System capabilities containing parent procedures
	 */
	public static final String ELEMENT_NAME_PARENT_PROCEDURES = "parentProcedures";

	public static final String PARENT_PROCEDURES_FIELD_DEFINITION = "http://www.opengis.net/def/procedure/identifier";

	public static final String PARENT_PROCEDURES_FIELD_NAME = "ParentProcedureID";

	/**
	 * name of System capabilities containing featureOfInterest
	 */
	public static final String ELEMENT_NAME_FEATURE_OF_INTEREST = "featureOfInterest";

	/**
	 * name of System components containing child procedures
	 */
	public static final String ELEMENT_NAME_CHILD_PROCEDURES = "childProcedure";

	public static final String FEATURE_OF_INTEREST_FIELD_DEFINITION = "http://www.opengis.net/def/featureOfInterest/identifier";

	public static final String FEATURE_OF_INTEREST_FIELD_NAME = "FeatureOfInterestID";

	// SWE
	// namespaces and schema locations
	public static final String NS_SWE = "http://www.opengis.net/swe/1.0.1";

	public static final String NS_SWE_20 = "http://www.opengis.net/swe/2.0";

	public static final String NS_SWE_PREFIX = "swe";

	public static final String NS_SWES_20 = "http://www.opengis.net/swes/2.0";

	public static final String NS_SWES_PREFIX = "swes";

	public static final String SCHEMA_LOCATION_SWE = "http://schemas.opengis.net/sweCommon/1.0.1/swe.xsd";

	public static final String SCHEMA_LOCATION_SWE_200 = "http://schemas.opengis.net/sweCommon/2.0/swe.xsd";

	public static final String SCHEMA_LOCATION_SWES_200 = "http://schemas.opengis.net/swes/2.0/swes.xsd";

	// element names
	public static final String EN_ABSTRACT_OFFERING = "AbstractOffering";

	public static final String EN_BOOLEAN = "Boolean";

	public static final String EN_CATEGORY = "Category";

	public static final String EN_COUNT = "Count";

	public static final String EN_DATA_ARRAY = "DataArray";

	public static final String EN_DATA_RECORD = "DataRecord";

	public static final String EN_DELETE_SENSOR = "DeleteSensor";

	public static final String EN_DELETE_SENSOR_RESPONSE = "DeleteSensorResponse";

	public static final String EN_DESCRIBE_SENSOR = "DescribeSensor";

	public static final String EN_DESCRIBE_SENSOR_RESPONSE = "DescribeSensorResponse";

	public static final String EN_INSERT_SENSOR = "InsertSensor";

	public static final String EN_INSERT_SENSOR_RESPONSE = "InsertSensorResponse";

	public static final String EN_INSERTION_METADATA = "InsertionMetadata";

	public static final String EN_METADATA = "metadata";

	public static final String EN_OFFERING = "offering";

	public static final String EN_QUANTITY = "Quantity";

	public static final String EN_SIMPLEDATARECORD = "SimpleDataRecord";

	public static final String EN_TEXT = "Text";

	public static final String EN_TEXT_ENCODING = "TextEncoding";

	public static final String EN_TIME = "Time";

	public static final String EN_TIME_RANGE = "TimeRange";

	public static final String EN_UPDATE_SENSOR_DESCRIPTION = "UpdateSensorDescription";

	public static final String EN_UPDATE_SENSOR_DESCRIPTION_RESPONSE = "UpdateSensorDescriptionResponse";

	// QNames for elements
	public static final QName QN_ABSTRACT_OFFERING = new QName(NS_SWES_20,
			EN_ABSTRACT_OFFERING, NS_SWES_PREFIX);

	public static final QName QN_BOOLEAN_SWE_101 = new QName(NS_SWE,
			EN_BOOLEAN, NS_SWE_PREFIX);

	public static final QName QN_BOOLEAN_SWE_200 = new QName(NS_SWE_20,
			EN_BOOLEAN, NS_SWE_PREFIX);

	public static final QName QN_CATEGORY_SWE_101 = new QName(NS_SWE,
			EN_CATEGORY, NS_SWE_PREFIX);

	public static final QName QN_CATEGORY_SWE_200 = new QName(NS_SWE_20,
			EN_CATEGORY, NS_SWE_PREFIX);

	public static final QName QN_COUNT_SWE_101 = new QName(NS_SWE, EN_COUNT,
			NS_SWE_PREFIX);

	public static final QName QN_COUNT_SWE_200 = new QName(NS_SWE_20, EN_COUNT,
			NS_SWE_PREFIX);

	public static final QName QN_DATA_ARRAY_SWE_200 = new QName(NS_SWE,
			EN_DATA_ARRAY, NS_SWE_PREFIX);

	public static final QName QN_DATA_RECORD_SWE_200 = new QName(NS_SWE,
			EN_DATA_RECORD, NS_SWE_PREFIX);

	public static final QName QN_DELETE_SENSOR = new QName(NS_SWES_20,
			EN_DELETE_SENSOR, NS_SWES_PREFIX);

	public static final QName QN_DELETE_SENSOR_RESPONSE = new QName(NS_SWES_20,
			EN_DELETE_SENSOR_RESPONSE, NS_SWES_PREFIX);

	public static final QName QN_DESCRIBE_SENSOR = new QName(NS_SWES_20,
			EN_DESCRIBE_SENSOR, NS_SWES_PREFIX);

	public static final QName QN_DESCRIBE_SENSOR_RESPONSE = new QName(
			NS_SWES_20, EN_DESCRIBE_SENSOR_RESPONSE, NS_SWES_PREFIX);

	public static final QName QN_INSERT_SENSOR = new QName(NS_SWES_20,
			EN_INSERT_SENSOR, NS_SWES_PREFIX);

	public static final QName QN_INSERT_SENSOR_RESPONSE = new QName(NS_SWES_20,
			EN_INSERT_SENSOR_RESPONSE, NS_SWES_PREFIX);

	public static final QName QN_INSERTION_METADATA = new QName(NS_SWES_20,
			EN_INSERTION_METADATA, NS_SWES_PREFIX);

	public static final QName QN_METADATA = new QName(NS_SWES_20, EN_METADATA,
			NS_SWES_PREFIX);

	public static final QName QN_OFFERING = new QName(NS_SWES_20, EN_OFFERING,
			NS_SWES_PREFIX);

	public static final QName QN_QUANTITY_SWE_101 = new QName(NS_SWE,
			EN_QUANTITY, NS_SWE_PREFIX);

	public static final QName QN_QUANTITY_SWE_200 = new QName(NS_SWE_20,
			EN_QUANTITY, NS_SWE_PREFIX);

	public static final QName QN_SIMPLEDATARECORD_SWE_101 = new QName(NS_SWE,
			EN_SIMPLEDATARECORD, NS_SWE_PREFIX);
	public static final QName QN_DATA_RECORD_SWE_101 = new QName(NS_SWE,
			EN_DATA_RECORD, NS_SWE_PREFIX);

	public static final QName QN_TEXT_ENCODING_SWE_101 = new QName(NS_SWE,
			EN_TEXT_ENCODING, NS_SWE_PREFIX);

	public static final QName QN_TEXT_ENCODING_SWE_200 = new QName(NS_SWE_20,
			EN_TEXT_ENCODING, NS_SWE_PREFIX);

	public static final QName QN_TEXT_SWE_101 = new QName(NS_SWE, EN_TEXT,
			NS_SWE_PREFIX);

	public static final QName QN_TEXT_SWE_200 = new QName(NS_SWE_20, EN_TEXT,
			NS_SWE_PREFIX);

	public static final QName QN_TIME_RANGE_SWE_101 = new QName(NS_SWE,
			EN_TIME_RANGE, NS_SWE_PREFIX);

	public static final QName QN_TIME_RANGE_SWE_200 = new QName(NS_SWE_20,
			EN_TIME_RANGE, NS_SWE_PREFIX);

	public static final QName QN_TIME_SWE_101 = new QName(NS_SWE, EN_TIME,
			NS_SWE_PREFIX);
	public static final QName QN_ENVELOPE_SWE_101 = new QName(NS_SWE,
			EN_ENVELOPE, NS_SWE_PREFIX);

	public static final QName QN_TIME_SWE_200 = new QName(NS_SWE_20, EN_TIME,
			NS_SWE_PREFIX);

	public static final QName QN_UPDATE_SENSOR_DESCRIPTION = new QName(
			NS_SWES_20, EN_UPDATE_SENSOR_DESCRIPTION, NS_SWES_PREFIX);

	public static final QName QN_UPDATE_SENSOR_DESCRIPTION_RESPONSE = new QName(
			NS_SWES_20, EN_UPDATE_SENSOR_DESCRIPTION_RESPONSE, NS_SWES_PREFIX);

	public static final String SOAP_REASON_INVALID_REQUEST = "The request did not conform to its XML Schema definition.";

	public static final String SOAP_REASON_REQUEST_EXTENSION_NOT_SUPPORTED = "";

	// SOAP CONSTANTS
	// SOS core
	public static final String REQ_ACTION_GET_CAPABILITIES = "http://www.opengis.net/def/serviceOperation/sos/core/2.0/GetCapabilities";
	public static final String RESP_ACTION_GET_CAPABILITIES = "http://www.opengis.net/def/serviceOperation/sos/core/2.0/GetCapabilitiesResponse";

	public static final String REQ_ACTION_DESCRIBE_SENSOR = "http://www.opengis.net/swes/2.0/DescribeSensor";
	public static final String RESP_ACTION_DESCRIBE_SENSOR = "http://www.opengis.net/swes/2.0/DescribeSensorResponse";

	public static final String REQ_ACTION_GET_OBSERVATION = "http://www.opengis.net/def/serviceOperation/sos/core/2.0/GetObservation";
	public static final String RESP_ACTION_GET_OBSERVATION = "http://www.opengis.net/def/serviceOperation/sos/core/2.0/GetObservationResponse";

	// SOS transactional
	public static final String REQ_ACTION_INSERT_OBSERVATION = "http://www.opengis.net/def/serviceOperation/sos/obsInsertion/2.0/InsertObservation";
	public static final String RESP_ACTION_INSERT_OBSERVATION = "http://www.opengis.net/def/serviceOperation/sos/obsInsertion/2.0/InsertObservationResponse";

	public static final String REQ_ACTION_UPDATE_SENSOR_DESCRIPTION = "http://www.opengis.net/swes/2.0/UpdateSensorDescription";
	public static final String RESP_ACTION_UPDATE_SENSOR_DESCRIPTION = "http://www.opengis.net/swes/2.0/UpdateSensorDescriptionResponse";

	public static final String REQ_ACTION_INSERT_SENSOR = "http://www.opengis.net/swes/2.0/InsertSensor";
	public static final String RESP_ACTION_INSERT_SENSOR = "http://www.opengis.net/swes/2.0/InsertSensorResponse";

	public static final String REQ_ACTION_DELETE_SENSOR = "http://www.opengis.net/swes/2.0/DeleteSensor";
	public static final String RESP_ACTION_DELETE_SENSOR = "http://www.opengis.net/swes/2.0/DeleteSensorResponse";

	// SOS enhanced
	public static final String REQ_ACTION_GET_FEATURE_OF_INTEREST = "http://www.opengis.net/def/serviceOperation/sos/foiRetrieval/2.0/GetFeatureOfInterest";
	public static final String RESP_ACTION_GET_FEATURE_OF_INTEREST = "http://www.opengis.net/def/serviceOperation/sos/foiRetrieval/2.0/GetFeatureOfInterestResponse";

	public static final String REQ_ACTION_GET_OBSERVATION_BY_ID = "http://www.opengis.net/def/serviceOperation/sos/obsByIdRetrieval/2.0/GetObservationById";
	public static final String RESP_ACTION_GET_OBSERVATION_BY_ID = "http://www.opengis.net/def/serviceOperation/sos/obsByIdRetrieval/2.0/GetObservationByIdResponse";

	// SOS result handling
	public static final String REQ_ACTION_GET_RESULT_TEMPLATE = "http://www.opengis.net/def/serviceOperation/sos/resultRetrieval/2.0/GetResultTemplate";
	public static final String RESP_ACTION_GET_RESULT_TEMPLATE = "http://www.opengis.net/def/serviceOperation/sos/resultRetrieval/2.0/GetResultTemplateResponse";

	public static final String REQ_ACTION_INSERT_RESULT_TEMPLATE = "http://www.opengis.net/def/serviceOperation/sos/resultInsertion/2.0/InsertResultTemplate";
	public static final String RESP_ACTION_INSERT_RESULT_TEMPLATE = "http://www.opengis.net/def/serviceOperation/sos/resultInsertion/2.0/InsertResultTemplateResponse";

	public static final String REQ_ACTION_GET_RESULT = "http://www.opengis.net/def/serviceOperation/sos/resultRetrieval/2.0/GetResult";
	public static final String RESP_ACTION_GET_RESULT = "http://www.opengis.net/def/serviceOperation/sos/resultRetrieval/2.0/GetResultResponse";

	public static final String REQ_ACTION_INSERT_RESULT = "http://www.opengis.net/def/serviceOperation/sos/resultInsertion/2.0/InsertResultTemplate";
	public static final String RESP_ACTION_INSERT_RESULT = "http://www.opengis.net/def/serviceOperation/sos/resultInsertion/2.0/InsertResultTemplateResponse";

}

/**
 * This file contains collected statements from 
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

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.events.XMLEvent;

import org.apache.xmlbeans.XmlOptions;

public class XmlOptionsHelper {

    private static class Holder {
        static final XmlOptionsHelper xmlOptionsHelper = new XmlOptionsHelper();
    }

    public static XmlOptionsHelper getInstance() {
        return Holder.xmlOptionsHelper;
    }

    private XmlOptions xmlOptions;

    private String characterEncoding = "UTF-8";
    private boolean prettyPrint = true;

    private XmlOptionsHelper() {

        xmlOptions = new XmlOptions();
        Map<String, String> prefixes = getPrefixMap();
        xmlOptions.setSaveSuggestedPrefixes(prefixes);
        xmlOptions.setSaveImplicitNamespaces(prefixes);
        xmlOptions.setSaveAggressiveNamespaces();
        if (prettyPrint) {
            xmlOptions.setSavePrettyPrint();
        }
        xmlOptions.setSaveNamespacesFirst();
        xmlOptions.setCharacterEncoding(characterEncoding);
        // this.xmlOptions = xmlOptions;
    }

    // TODO: To be used by other encoders to have common prefixes
    public static Map<String, String> getPrefixMap() {
        Map<String, String> prefixMap = new HashMap<String, String>();
        prefixMap.put(OGCConstants.NS_OGC, OGCConstants.NS_OGC_PREFIX);
        prefixMap.put(OGCConstants.NS_OWS, OGCConstants.NS_OWS_PREFIX);
        prefixMap.put(OGCConstants.NS_GTS, OGCConstants.NS_GTS_PREFIX);
        prefixMap.put(OGCConstants.NS_GCO, OGCConstants.NS_GCO_PREFIX);
        prefixMap.put(OGCConstants.NS_C3ALPS, OGCConstants.NS_C3ALPS_PREFIX);
        prefixMap.put(OGCConstants.NS_SRV, OGCConstants.NS_SRV_PREFIX);
        prefixMap.put(OGCConstants.NS_GMX, OGCConstants.NS_GMX_PREFIX);
        prefixMap.put(OGCConstants.NS_GMD, OGCConstants.NS_GMD_PREFIX);
        prefixMap.put(OGCConstants.NS_CSW, OGCConstants.NS_CSW_PREFIX);
        prefixMap.put(OGCConstants.NS_XLINK, OGCConstants.NS_XLINK_PREFIX);
        prefixMap.put(OGCConstants.NS_XSI, OGCConstants.NS_XSI_PREFIX);
        prefixMap.put(OGCConstants.NS_XS, OGCConstants.NS_XS_PREFIX);
        prefixMap.put(OGCConstants.NS_GML, OGCConstants.NS_GML_PREFIX);
        prefixMap.put(OGCConstants.NS_GML_32, OGCConstants.NS_GML_32_PREFIX);
        prefixMap.put(OGCConstants.NS_WMC, OGCConstants.NS_WMC_PREFIX);
        prefixMap.put(OGCConstants.NS_OL, OGCConstants.NS_OL_PREFIX);
        prefixMap.put(OGCConstants.NS_WMS, OGCConstants.NS_WMS_PREFIX);
        prefixMap.put(OGCConstants.NS_WMC, OGCConstants.NS_WMC_PREFIX);
        prefixMap.put(OGCConstants.NS_SLD, OGCConstants.NS_SLD_PREFIX);
        prefixMap.put(OGCConstants.NS_SWE, OGCConstants.NS_SWE_PREFIX);
        prefixMap.put(OGCConstants.NS_SWES_20, OGCConstants.NS_SWES_PREFIX);
        prefixMap.put(OGCConstants.NS_SOS_20, OGCConstants.NS_SOS_PREFIX);
        prefixMap.put(OGCConstants.NS_SML, OGCConstants.NS_SML_PREFIX);

        return prefixMap;
    }

    public XmlOptions getXmlOptions() {
        return xmlOptions;
    }

    public final static String getEventTypeString(int eventType) {
        switch (eventType) {
        case XMLEvent.START_ELEMENT:
            return "START_ELEMENT";

        case XMLEvent.END_ELEMENT:
            return "END_ELEMENT";

        case XMLEvent.PROCESSING_INSTRUCTION:
            return "PROCESSING_INSTRUCTION";

        case XMLEvent.CHARACTERS:
            return "CHARACTERS";

        case XMLEvent.COMMENT:
            return "COMMENT";

        case XMLEvent.START_DOCUMENT:
            return "START_DOCUMENT";

        case XMLEvent.END_DOCUMENT:
            return "END_DOCUMENT";

        case XMLEvent.ENTITY_REFERENCE:
            return "ENTITY_REFERENCE";

        case XMLEvent.ATTRIBUTE:
            return "ATTRIBUTE";

        case XMLEvent.DTD:
            return "DTD";

        case XMLEvent.CDATA:
            return "CDATA";

        case XMLEvent.SPACE:
            return "SPACE";
        }
        return "UNKNOWN_EVENT_TYPE," + eventType;
    }

    public String getXPathNamespaces() {
        StringBuilder sb = new StringBuilder();
        Map<String, String> prefixMap = getPrefixMap();
        for (String k : prefixMap.keySet()) {
            String s = "declare namespace <NSP>='<NS>'; ";
            sb.append(s.replace("<NS>", k).replace("<NSP>", prefixMap.get(k)));
        }
        return sb.toString();
    }

}

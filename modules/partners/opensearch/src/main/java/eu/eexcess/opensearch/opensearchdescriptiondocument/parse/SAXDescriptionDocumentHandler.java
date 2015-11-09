package eu.eexcess.opensearch.opensearchdescriptiondocument.parse;

import java.util.Arrays;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eu.eexcess.opensearch.opensearchdescriptiondocument.OpensearchDescription;
import eu.eexcess.opensearch.opensearchdescriptiondocument.OpensearchDescription.SyndicationRight;
import eu.eexcess.opensearch.opensearchdescriptiondocument.documentfields.Image;
import eu.eexcess.opensearch.opensearchdescriptiondocument.documentfields.Query;
import eu.eexcess.opensearch.opensearchdescriptiondocument.documentfields.Url;

/**
 * Class handles tags, attributes and tag-values of an <a
 * href="http://www.opensearch.org/Specifications/OpenSearch/1.1/Draft_5"
 * >OpenSearch Description Document</a>. Dissection is case sensitive.
 * 
 * @author Raoul Rubien
 */
public class SAXDescriptionDocumentHandler extends DefaultHandler {

    private static final Logger LOGGER = Logger.getLogger(SAXDescriptionDocumentHandler.class.getName());

    private OpensearchDescription document = new OpensearchDescription();
    private Image currentImage = null;
    private StringBuilder currentNodeValue = null;

    /**
     * Read and store node attributes of the current node.
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        switch (qName) {

        case XMLEntityNames.OpenSearchDescription.NODE:
            readAttributesFromOpenSearchDescriptionNode(uri, localName, qName, attributes);
            break;

        case XMLEntityNames.Contact.NODE:
        case XMLEntityNames.ShortName.NODE:
        case XMLEntityNames.Description.NODE:
        case XMLEntityNames.Developer.NODE:
        case XMLEntityNames.Attribution.NODE:
        case XMLEntityNames.InputEncoding.NODE:
        case XMLEntityNames.OutputEncoding.NODE:
        case XMLEntityNames.Language.NODE:
        case XMLEntityNames.LongName.NODE:
        case XMLEntityNames.SyndicationRight.NODE:
        case XMLEntityNames.AdultContent.NODE:
        case XMLEntityNames.Tags.NODE:
            // this nodes have no attributes
            break;

        case XMLEntityNames.Query.NODE:
            readAttributesFromQueryNode(uri, localName, qName, attributes);
            break;

        case XMLEntityNames.Image.NODE:
            readAttributesFromImageNode(uri, localName, qName, attributes);
            break;

        case XMLEntityNames.Url.NODE:
            readAttributesFromUrlNode(uri, localName, qName, attributes);
            break;

        default:
            LOGGER.warning("failed to read attributes from node [" + qName + "]");
            break;
        }
    }

    /**
     * Store the temporarily node value to the current node.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String nodeValue = null;
        if (currentNodeValue != null) {
            nodeValue = currentNodeValue.toString().trim().replaceAll("[\\r\\n\\t]", "");
        }

        switch (qName) {
        case XMLEntityNames.OpenSearchDescription.NODE:
        case XMLEntityNames.Query.NODE:
        case XMLEntityNames.Url.NODE:
            // this nodes have no values
            break;

        case XMLEntityNames.SyndicationRight.NODE:
            document.syndicationRight = translateSyndicationRight(nodeValue);
            currentNodeValue = null;
            break;

        case XMLEntityNames.Contact.NODE:
            document.contact = nodeValue;
            currentNodeValue = null;
            break;

        case XMLEntityNames.ShortName.NODE:
            document.shortName = nodeValue;
            currentNodeValue = null;
            break;

        case XMLEntityNames.LongName.NODE:
            document.longName = nodeValue;
            currentNodeValue = null;
            break;

        case XMLEntityNames.Description.NODE:
            document.description = nodeValue;
            currentNodeValue = null;
            break;

        case XMLEntityNames.Developer.NODE:
            document.developer = nodeValue;
            currentNodeValue = null;
            break;

        case XMLEntityNames.Image.NODE:
            if (currentImage != null) {
                currentImage.url = nodeValue;
                document.images.add(currentImage);
            }
            currentNodeValue = null;
            currentImage = null;
            break;

        case XMLEntityNames.Attribution.NODE:
            document.attribution = nodeValue;
            currentNodeValue = null;
            break;

        case XMLEntityNames.InputEncoding.NODE:
            document.inputEncodings.add(nodeValue);
            currentNodeValue = null;
            break;

        case XMLEntityNames.OutputEncoding.NODE:
            document.outputEncodings.add(nodeValue);
            currentNodeValue = null;
            break;

        case XMLEntityNames.Language.NODE:
            document.languages.add(nodeValue);
            currentNodeValue = null;
            break;

        case XMLEntityNames.AdultContent.NODE:
            document.adultContent = translateBoolean(nodeValue);
            currentNodeValue = null;
            break;

        case XMLEntityNames.Tags.NODE:
            document.tags.addAll(Arrays.asList(StringUtils.split(nodeValue, " ")));
            currentNodeValue = null;
            break;

        default:
            LOGGER.warning("failed to read value from node [" + qName + "]");
            break;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        if (document.languages.size() <= 0) {
            document.languages.add("*");
        }
    }

    /**
     * Interprete a boolean-string regarding <a href=
     * "http://www.opensearch.org/Specifications/OpenSearch/1.1#The_.22AdultContent.22_element"
     * >OpenSearch specification</a>.
     * 
     * @param booleanValue
     *            input string
     * @return interpreted value
     */
    private boolean translateBoolean(String booleanValue) {

        if (booleanValue.toLowerCase().compareTo("false") == 0 || booleanValue.toLowerCase().compareTo("0") == 0 || booleanValue.toLowerCase().compareTo("no") == 0) {
            return false;
        }
        return true;
    }

    /**
     * Store the current node value temporarily.
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        if (null == currentNodeValue) {
            currentNodeValue = new StringBuilder();
        }
        currentNodeValue.append(ch, start, length);
    }

    /**
     * reads known attributed for the current node
     * 
     * @param attributes
     *            the attributes
     */
    private void readAttributesFromUrlNode(String uri, String localName, String qName, Attributes attributes) {

        Url currentSearchLink = new Url();
        int templateIndex = attributes.getIndex(XMLEntityNames.Url.Attribute.TEMPLATE);
        int typeIndex = attributes.getIndex(XMLEntityNames.Url.Attribute.TYPE);
        int relIndex = attributes.getIndex(XMLEntityNames.Url.Attribute.REL);
        int indexOffsetIndex = attributes.getIndex(XMLEntityNames.Url.Attribute.INDEXOFFSET);
        int pageOffsetIndex = attributes.getIndex(XMLEntityNames.Url.Attribute.PAGEOFFSET);

        currentSearchLink.template = (templateIndex >= 0) ? attributes.getValue(templateIndex) : "";

        currentSearchLink.type = (typeIndex >= 0) ? attributes.getValue(typeIndex) : "";

        String relValue = (relIndex >= 0) ? attributes.getValue(relIndex) : "-1";
        currentSearchLink.rel = translateRelValue(relValue);

        String indexOffsetValue = (indexOffsetIndex >= 0) ? attributes.getValue(indexOffsetIndex) : "-1";
        try {
            currentSearchLink.indexOffset = Integer.parseInt(indexOffsetValue);
        } catch (NumberFormatException e) {
            LOGGER.warning("failed to parse integer from [" + indexOffsetValue + "]");
        }

        String pageOffsetValue = (pageOffsetIndex >= 0) ? attributes.getValue(pageOffsetIndex) : "-1";
        try {
            currentSearchLink.pageOffset = Integer.parseInt(pageOffsetValue);
        } catch (NumberFormatException e) {
            LOGGER.warning("failed to parse integer from [" + pageOffsetIndex + "]");
        }

        document.searchLinks.add(currentSearchLink);

    }

    /**
     * Interprete a url rel value regarding <a href=
     * "http://www.opensearch.org/Specifications/OpenSearch/1.1#Url_rel_values"
     * >OpenSearch specification</a>.
     * 
     * @param stringRepresentation
     * @return Enum representing the value.
     */
    private Url.UrlRel translateRelValue(String stringRepresentation) {
        if (stringRepresentation.compareTo(XMLEntityNames.Url.AttributeValue.RELCOLLECTION) == 0) {
            return Url.UrlRel.COLLECTION;
        } else if (stringRepresentation.compareTo(XMLEntityNames.Url.AttributeValue.RELRESULTS) == 0) {
            return Url.UrlRel.RESULTS;
        } else if (stringRepresentation.compareTo(XMLEntityNames.Url.AttributeValue.RELSELF) == 0) {
            return Url.UrlRel.SELF;
        } else if (stringRepresentation.compareTo(XMLEntityNames.Url.AttributeValue.RELSUGGESTIONS) == 0) {
            return Url.UrlRel.SUGGESTIONS;
        } else {
            return Url.UrlRel.UNDEFINED;
        }
    }

    /**
     * Interprete a syndication right value regarding <a href=
     * "http://www.opensearch.org/Specifications/OpenSearch/1.1#The_.22SyndicationRight.22_element"
     * >OpenSearch specification</a>.
     * 
     * @param stringRepresentation
     * @return Enum representing the value.
     */
    private SyndicationRight translateSyndicationRight(String stringRepresentation) {
        if (stringRepresentation.compareTo(XMLEntityNames.SyndicationRight.NodeValue.CLOSED) == 0) {
            return SyndicationRight.CLOSED;
        } else if (stringRepresentation.compareTo(XMLEntityNames.SyndicationRight.NodeValue.LIMITED) == 0) {
            return SyndicationRight.LIMITED;
        } else if (stringRepresentation.compareTo(XMLEntityNames.SyndicationRight.NodeValue.OPEN) == 0) {
            return SyndicationRight.OPEN;
        } else if (stringRepresentation.compareTo(XMLEntityNames.SyndicationRight.NodeValue.PRIVATE) == 0) {
            return SyndicationRight.PRIVATE;
        } else {
            return SyndicationRight.UNDEFINED;
        }
    }

    /**
     * Store known attributes of an opensearchdescription node.
     * 
     * @param attributes
     *            fields to read from
     */
    private void readAttributesFromOpenSearchDescriptionNode(String uri, String localName, String qName, Attributes attributes) {
        int namespaceIndex = attributes.getIndex(XMLEntityNames.OpenSearchDescription.Attribute.NAMESPACE);
        document.xmlns = (namespaceIndex >= 0) ? attributes.getValue(namespaceIndex) : "";
    }

    /**
     * Store known attributes of an image node.
     * 
     * @param attributes
     *            fields to read from
     */
    private void readAttributesFromImageNode(String uri, String localName, String qName, Attributes attributes) {

        currentImage = new Image();
        int widthIndex = attributes.getIndex(XMLEntityNames.Image.Attribute.WIDTH);
        int heightIndex = attributes.getIndex(XMLEntityNames.Image.Attribute.HEIGHT);
        int imageType = attributes.getIndex(XMLEntityNames.Image.Attribute.TYPE);

        String widthIndexValue = (widthIndex >= 0) ? attributes.getValue(widthIndex) : "-1";
        try {
            currentImage.width = Integer.parseInt(widthIndexValue);
        } catch (NumberFormatException e) {
            LOGGER.warning("failed to parse int from [" + widthIndexValue + "]");
        }

        String heightIndexValue = (heightIndex >= 0) ? attributes.getValue(heightIndex) : "-1";
        try {
            currentImage.height = Integer.parseInt(heightIndexValue);
        } catch (NumberFormatException e) {
            LOGGER.warning("failed to parse from int [" + heightIndexValue + "]");
        }

        currentImage.type = (imageType >= 0) ? attributes.getValue(imageType) : "";
    }

    /**
     * Store known attributes of a query node.
     * 
     * @param attributes
     *            fields to read from
     */
    private void readAttributesFromQueryNode(String uri, String localName, String qName, Attributes attributes) {
        Query currentQuery = new Query();
        int countIndex = attributes.getIndex(XMLEntityNames.Query.Attribute.COUNT);
        int inputEncodingIndex = attributes.getIndex(XMLEntityNames.Query.Attribute.INPUTENCODING);
        int languageIndex = attributes.getIndex(XMLEntityNames.Query.Attribute.LANGUAGE);
        int outputEncodingIndex = attributes.getIndex(XMLEntityNames.Query.Attribute.OUTPUTENCODING);
        int roleIndex = attributes.getIndex(XMLEntityNames.Query.Attribute.ROLE);
        int searchTermsIndex = attributes.getIndex(XMLEntityNames.Query.Attribute.SEARCHTERMS);
        int startIndexIndex = attributes.getIndex(XMLEntityNames.Query.Attribute.STARTINDEX);
        int startPageIndex = attributes.getIndex(XMLEntityNames.Query.Attribute.STARTPAGE);
        int titleIndex = attributes.getIndex(XMLEntityNames.Query.Attribute.TITLE);
        int totalResultsIndex = attributes.getIndex(XMLEntityNames.Query.Attribute.TOTALRESULTS);

        currentQuery.inputEncoding = (inputEncodingIndex >= 0) ? attributes.getValue(inputEncodingIndex) : "";
        currentQuery.language = (languageIndex >= 0) ? attributes.getValue(languageIndex) : "";
        currentQuery.outputEncoding = (outputEncodingIndex >= 0) ? attributes.getValue(outputEncodingIndex) : "";
        currentQuery.role = (roleIndex >= 0) ? attributes.getValue(roleIndex) : "";
        currentQuery.searchTerms = (searchTermsIndex >= 0) ? attributes.getValue(searchTermsIndex) : "";

        String startIndexValue = (startIndexIndex >= 0) ? attributes.getValue(startIndexIndex) : "-1";
        try {
            currentQuery.startIndex = Integer.parseInt(startIndexValue);
        } catch (NumberFormatException e) {
            LOGGER.warning("failed to parse int from [" + startIndexValue + "]");
        }

        String startPageIndexValue = (startPageIndex >= 0) ? attributes.getValue(startPageIndex) : "-1";
        try {
            currentQuery.startPage = Integer.parseInt(startPageIndexValue);
        } catch (NumberFormatException e) {
            LOGGER.warning("failed to parse int from [" + startPageIndexValue + "]");
        }

        String countValue = (countIndex >= 0) ? attributes.getValue(countIndex) : "-1";
        try {
            currentQuery.count = Integer.parseInt(countValue);
        } catch (NumberFormatException e) {
            LOGGER.warning("failed to parse int from [" + countValue + "]: " + e.getMessage());
        }

        currentQuery.title = (titleIndex >= 0) ? attributes.getValue(titleIndex) : "";

        currentQuery.totalResults = -1;
        try {
            currentQuery.totalResults = Integer.parseInt((totalResultsIndex >= 0) ? attributes.getValue(totalResultsIndex) : "-1");
        } catch (Exception e) {
        }

        document.queries.add(currentQuery);
    }

    /**
     * @return the parsed document
     */
    public OpensearchDescription getDocument() {
        return document;
    }
}

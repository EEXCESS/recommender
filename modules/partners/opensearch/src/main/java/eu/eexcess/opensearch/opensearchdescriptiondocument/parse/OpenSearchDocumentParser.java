package eu.eexcess.opensearch.opensearchdescriptiondocument.parse;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import eu.eexcess.opensearch.opensearchdescriptiondocument.OpensearchDescription;

/**
 * Parses an XML OpenSearch Description Document.
 * 
 * @author Raoul Rubien
 */
public class OpenSearchDocumentParser {

    Logger logger = Logger.getLogger(OpenSearchDocumentParser.class.getName());

    /**
     * 
     * @param xmlDocumentDescription
     *            input xml document
     * @return parsed document
     */
    public OpensearchDescription toDescriptionDocument(String xmlDocumentDescription) {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXDescriptionDocumentHandler handler = new SAXDescriptionDocumentHandler();
        try {
            SAXParser parser = parserFactory.newSAXParser();
            XMLReader xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(handler);
            InputSource source = new InputSource(new StringReader(xmlDocumentDescription));
            xmlReader.parse(source);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.log(Level.WARNING, "failed to parse from xml", e);
        }

        return handler.getDocument();
    }
}

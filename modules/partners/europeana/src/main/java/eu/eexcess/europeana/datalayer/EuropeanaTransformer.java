/* Copyright (C) 2014
"JOANNEUM RESEARCH Forschungsgesellschaft mbH" 
 Graz, Austria, digital-iis@joanneum.at.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package eu.eexcess.europeana.datalayer;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.query.QuerySolution;

import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.partnerdata.api.EEXCESSDataTransformationException;
import eu.eexcess.partnerdata.api.ITransformer;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.PartnerdataTracer;
import eu.eexcess.partnerdata.reference.Transformer;

public class EuropeanaTransformer extends Transformer implements ITransformer {
    private static final Logger LOGGER = Logger.getLogger(EuropeanaTransformer.class.getName());

    @Override
    protected Result postProcessResult(Document orgPartnerResult, Result result, QuerySolution querySol) {
        result.documentBadge.uri = "http://europeana.eu/resolve/record" + result.documentBadge.id;
        if (result.mediaType == null || result.mediaType.trim().isEmpty() || result.mediaType.equalsIgnoreCase(EEXCESS_FACETS_VALUE_UNKNOWN))
            result.mediaType = EEXCESS_MEDIATYPE_TEXT;
        return result;
    }

    @Override
    protected ResultList postProcessResults(Document orgPartnerResult, ResultList resultList) {
        resultList.totalResults = Integer.parseInt(getValueWithXPath("/o/totalResults", orgPartnerResult));
        return resultList;
    }

    @Override
    public Document preProcessTransform(Document input, PartnerdataLogger logger) throws EEXCESSDataTransformationException {
        PartnerdataTracer.dumpFile(this.getClass(), partnerConfig, input, "before-transform-before-process", logger);

        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes;
        try {
            NodeList itemsRootNode = (NodeList) xPath.evaluate("/o/items", input.getDocumentElement(), XPathConstants.NODESET);
            nodes = (NodeList) xPath.evaluate("/o/items/e", input.getDocumentElement(), XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                Element e = (Element) nodes.item(i);
                NodeList itemFields = e.getChildNodes();
                for (int j = 0; j < itemFields.getLength(); j++) {
                    Node field = itemFields.item(j);
                    if (field.getNodeName().equalsIgnoreCase("edmIsShownAt")) {
                        if (!field.hasChildNodes()) {
                            itemsRootNode.item(0).removeChild(e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error transforming Europeana result", e);
        }
        PartnerdataTracer.dumpFile(this.getClass(), partnerConfig, input, "before-transform-done-process", logger);
        return input;
    }

}
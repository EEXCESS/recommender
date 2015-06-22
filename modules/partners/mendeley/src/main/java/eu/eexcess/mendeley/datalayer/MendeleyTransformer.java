/* Copyright (C) 2014
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

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
package eu.eexcess.mendeley.datalayer;

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
import eu.eexcess.partnerdata.reference.XMLTools;

public class MendeleyTransformer extends Transformer implements ITransformer{
	
	@Override
	protected ResultList postProcessResults(Document orgPartnerResult, ResultList resultList) {
        resultList.totalResults = resultList.results.size();
		return resultList;
	}

	@Override
	protected Result postProcessResult(Document orgPartnerResult, Result result, QuerySolution querySol) {
		result.licence = "https://creativecommons.org/licenses/by/3.0/legalcode";
		return result;
	}

	@Override
	public Document preProcessTransformDetail(Document input, PartnerdataLogger logger)  throws EEXCESSDataTransformationException{
		PartnerdataTracer.dumpFile(this.getClass(), partnerConfig, input, "before-transform-before-process", logger); 

		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		try {
			NodeList itemsRootNode = (NodeList)xPath.evaluate("/o",
					input.getDocumentElement(), XPathConstants.NODESET);
			nodes = (NodeList)xPath.evaluate("/o/authors/e",
					input.getDocumentElement(), XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength();i++) {
			    Element e = (Element) nodes.item(i);
			    NodeList itemFields = e.getChildNodes();
				String firstName="";
				String lastName="";
			    for (int j = 0; j < itemFields.getLength(); j++) {
					Node field = itemFields.item(j);
					if (field.getNodeName().equalsIgnoreCase("first_name"))
					{
						if (field.getNodeValue() != null)
							firstName = field.getNodeValue();
						else 
							if (field.hasChildNodes())
								firstName = field.getChildNodes().item(0).getNodeValue();
					}
					if (field.getNodeName().equalsIgnoreCase("last_name"))
					{
						if (field.getNodeValue() != null)
							lastName = field.getNodeValue();
						else 
							if (field.hasChildNodes())
								lastName = field.getChildNodes().item(0).getNodeValue();
					}
				}
			    Element authorString = input.createElement("authorsString");
			    authorString.appendChild(input.createTextNode(firstName + " " + lastName));
				itemsRootNode.item(0).appendChild(authorString);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		PartnerdataTracer.dumpFile(this.getClass(), partnerConfig, input, "before-transform-done-process", logger); 
		return input;
	}

}
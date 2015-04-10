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
package eu.eexcess.kimportal.datalayer;

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
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerdata.reference.PartnerdataTracer;
import eu.eexcess.partnerdata.reference.Transformer;

public class KIMPortalTransformer extends Transformer{

	@Override
	protected Result postProcessResult(Document orgPartnerResult, Result result, QuerySolution querySol) {
//		result.uri = "http://www.kim.bl.openinteractive.ch/sammlungen#"+ result.uri;
		if (result.previewImage != null && !result.previewImage.isEmpty())
			result.previewImage = result.previewImage.replace("kgapi.bl.ch/edm/", "kgapi.bl.ch/");
		return result;
	}
	
	

	@Override
	public Document preProcessTransform(Document input, PartnerdataLogger logger)
			throws EEXCESSDataTransformationException {
		PartnerdataTracer.dumpFile(this.getClass(), partnerConfig, input, "before-transform-before-process", logger); 

		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;
		try {
			NodeList itemsRootNode = (NodeList)xPath.evaluate("/response/result",input.getDocumentElement(), XPathConstants.NODESET);
			nodes = (NodeList)xPath.evaluate("/response/result/doc",input.getDocumentElement(), XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength();i++) {
			    Element e = (Element) nodes.item(i);
			    NodeList itemFields = e.getChildNodes();
			    for (int j = 0; j < itemFields.getLength(); j++) {
					Node field = itemFields.item(j);
					if (field.getNodeName().equalsIgnoreCase("str"))
					{
						if (field.hasAttributes() ){
							if (field.getAttributes().getNamedItem("name") != null)
							{
								if (field.getAttributes().getNamedItem("name").getNodeValue().equalsIgnoreCase("uuid"))
								{
									Element eexcessURI = input.createElement("eexcessURI");
									
									
									
									if (field.getNodeType() == Node.ELEMENT_NODE) {
										NodeList fieldChilds = field.getChildNodes();
										if(fieldChilds != null && fieldChilds.getLength() > 0) {
											for(int k = 0 ; k < fieldChilds.getLength();k++) {
												Node fieldChild = fieldChilds.item(k);
													eexcessURI.appendChild(input.createTextNode("http://www.kim.bl.openinteractive.ch/sammlungen#" + fieldChild.getNodeValue()));
													field.getParentNode().appendChild(eexcessURI);
											}
										}
									}
									
									
									
									
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		PartnerdataTracer.dumpFile(this.getClass(), partnerConfig, input, "before-transform-done-process", logger); 
		return input;
	}



	@Override
	protected ResultList postProcessResults(Document orgPartnerResult, ResultList resultList) {
		resultList.totalResults = Integer.parseInt(getAttributeWithXPath("/response/result/@numFound", orgPartnerResult));
		return resultList;
	}

}

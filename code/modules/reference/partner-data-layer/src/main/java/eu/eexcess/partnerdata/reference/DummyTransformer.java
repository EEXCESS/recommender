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
package eu.eexcess.partnerdata.reference;

import javax.xml.transform.TransformerConfigurationException;

import org.w3c.dom.Document;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.partnerdata.api.ITransformer;

public class DummyTransformer extends Transformer implements ITransformer{
	
	@Override
	public void init(PartnerConfiguration partnerConfig) throws TransformerConfigurationException {
	}
	
	public Document transform(Document input) {return input;};

	public Document transformResultObject(Document input){return input;}
	
	public ResultList toResultList(Document input){
		ResultList ret =  new ResultList();
		ret.resultsRDF = XMLTools.getStringFromDocument(input);
		return ret;
	}

	@Override
	protected String createSPARQLqueryForToResultList() {
		return null;
	}




}
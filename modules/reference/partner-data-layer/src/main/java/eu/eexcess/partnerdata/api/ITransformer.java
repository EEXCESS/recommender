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
package eu.eexcess.partnerdata.api;

import javax.xml.transform.TransformerConfigurationException;

import org.w3c.dom.Document;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;

public interface ITransformer {

	public void init(PartnerConfiguration partnerConfig) throws TransformerConfigurationException;
	
	public Document transform(Document input, PartnerdataLogger logger) throws EEXCESSDataTransformationException;

	public ResultList toResultList(Document nativeResults, Document input, PartnerdataLogger logger);

	public boolean hasEEXCESSRDFResponseResults(Document searchResultsEexcess);

	//public Document transformResultObject(Document input) throws EEXCESSDataTransformationException;
	
	//public void setResultListTransformation(String filename);
	
	//public void setResultObjectTransformation(String filename);
	
	
}

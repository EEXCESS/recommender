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
package eu.eexcess.kimcollect.datalayer;

import org.w3c.dom.Document;

import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.partnerdata.reference.Transformer;

public class KIMCollectTransformer extends Transformer{

	@Override
	protected ResultList postProcessResults(Document orgPartnerResult, ResultList resultList) {
		resultList.totalResults = Integer.parseInt(getRootAttribute("countHits", orgPartnerResult, resultList));
		return resultList;
	}

}

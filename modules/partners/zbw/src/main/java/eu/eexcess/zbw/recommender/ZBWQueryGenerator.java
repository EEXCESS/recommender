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
package eu.eexcess.zbw.recommender;

import java.net.URLEncoder;

import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;
@Deprecated
public class ZBWQueryGenerator implements QueryGeneratorApi {

	
    @Override
    public String toQuery(SecureUserProfile userProfile) {
        StringBuilder builder = new StringBuilder();
        for (ContextKeyword context : userProfile.getContextKeywords()) {
            if (builder.length() > 0) { 
            	builder.append(" OR "); 
            }
            builder.append(context.getText());
        }
        return URLEncoder.encode( "\""+builder.toString()+"\"");
    }

	@Override
	public String toDetailQuery(DocumentBadge document) {
		if (document.uri.contains("#"))
			return document.uri.substring(document.uri.lastIndexOf("#")+1);
		return null;
	}

}

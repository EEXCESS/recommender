/*
Copyright (C) 2015
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
package eu.eexcess.partnerrecommender.reference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;

/**
 * Generates Querys for culture.web (http://culture.joanneum.at) out of SecureUserProfile Context field
 * 
 * @author orgel
 *
 */
public class CultureWebQueryGenerator implements QueryGeneratorApi {

    private static final String REGEXP = "(?<=\\w)\\s(?=\\w)";

    @Override
    public String toQuery(SecureUserProfile userProfile) {
        StringBuilder builder = new StringBuilder();
        Pattern replace = Pattern.compile(REGEXP);

        for (ContextKeyword context : userProfile.getContextKeywords()) {
            if (context.getExpansion() != null && (context.getExpansion() == ExpansionType.PSEUDORELEVANCEWP || context.getExpansion() == ExpansionType.SERENDIPITY)) {
                if (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
                    String keyword = context.getText();
                    String[] matcher2 = replace.split(keyword);
                    for (int i = 0; i < matcher2.length; i++) {
                    	String temp = matcher2[i].trim();
                    	if (!temp.isEmpty())
                    	{
	    	              if (builder.length() > 0) {
	    	                  builder.append("&");
	    	              }
	    	              builder.append("field.fulltext.query="+temp);
                    	}
                    }
                }
            } else {
                String keyword = context.getText();
                keyword.split(REGEXP);
                String[] matcher2 = replace.split(keyword);
                for (int i = 0; i < matcher2.length; i++) {
                	String temp = matcher2[i].trim();
                	if (!temp.isEmpty())
                	{
	                  if (builder.length() > 0) {
	                	  builder.append("&");
	                  }
	                  builder.append("field.fulltext.query="+temp);
                	}
				}
            }

        }
        return builder.toString();
    }

    @Override
    public String toDetailQuery(DocumentBadge document) {
        return document.id;
    }

}

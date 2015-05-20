/* Copyright (C) 2014 
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensees holding valid Know-Center Commercial licenses may use this file in
accordance with the Know-Center Commercial License Agreement provided with 
the Software or, alternatively, in accordance with the terms contained in
a written agreement between Licensees and Know-Center.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package eu.eexcess.partnerrecommender.reference;

import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;

/**
 * Query generator to create a Lucene search query out of a user profile.
 * 
 * @author rkern@know-center.at
 */
public class LuceneQueryGenerator implements QueryGeneratorApi {

	@Override
	public String toQuery(SecureUserProfile userProfile) {
		StringBuilder result = new StringBuilder();
		boolean expansion= false;
		for (ContextKeyword key : userProfile.contextKeywords) {
			
			if(key.expansion!=null && (key.expansion ==ExpansionType.PSEUDORELEVANCEWP||key.expansion ==ExpansionType.SERENDIPITY))
			{
				if(!expansion){
					expansion=true;
					if(result.length()>0){
						if(key.expansion==ExpansionType.PSEUDORELEVANCEWP)
							result.append(" OR (\""+key.text+"\"");
						else
							result.append(" AND (\""+key.text+"\"");
					}
					else
						result.append("(\""+key.text+"\"");
				}else{
					result.append(" OR \""+key.text+"\"");
				}
			} else{
				if(expansion){
					result.append(") OR \""+key.text+"\"");
					expansion=false;
				}	
				else
					if(result.length()>0)
						result.append(" OR \""+key.text+"\"");
					else
						result.append("\""+key.text+"\"");
			}
		}
		if(expansion)
			result.append(")");
			
		return result.toString();
	}

	@Override
	public String toDetailQuery(DocumentBadge document) {
		// TODO Auto-generated method stub
		return null;
	}

//    @Override
//    public String toQuery(SecureUserProfile userProfile) {
//        StringBuilder builder = new StringBuilder();
//        boolean expanded=false;
//        for (ContextKeyword context : userProfile.contextKeywords) {
//        	if(context.expansion==null || !context.expansion){
//	        	if (builder.length() > 0) { builder.append(' '); }
//	            builder.append('\"');
//	            builder.append(context.text);
//	            builder.append('\"');
//        	}else{
//        		expanded=true;
//        	}
//        }
//        if(expanded){
//        builder.append(" OR (");
//        Boolean first=true;
//        for (ContextKeyword context : userProfile.contextKeywords) {
//        	if(context.expansion!=null && context.expansion){
//        		
//        		if(first)
//        			builder.append("\"");
//        		else
//        			builder.append("\" OR ");	
//	            builder.append(context.text);
//	            builder.append("\"");
//	            first=false;
//        	}
//        }
//        
//        builder.append(')');
//        }
////        boolean isFirst = false;
////        for (String interest : userProfile.interestList) {
////            if (builder.length() > 0) { builder.append(' '); }
////            if (isFirst) { isFirst = false; }
////            else {builder.append(" OR "); }
////            builder.append('\"');
////            builder.append(interest);
////            builder.append("\"^0.2");
////        }
//        return builder.toString();
//    }
    
    

}

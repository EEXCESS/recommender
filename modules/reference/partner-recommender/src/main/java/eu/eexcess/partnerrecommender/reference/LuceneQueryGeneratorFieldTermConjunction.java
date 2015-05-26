package eu.eexcess.partnerrecommender.reference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;
/**
 * Similar to the Lucene query generator but also checks context keywords for terms and transforms them into conjunction query terms.
 * Keyword "New York" ends up as "New AND York" in the query.
 * @author hziak
 *
 */
public class LuceneQueryGeneratorFieldTermConjunction implements
		QueryGeneratorApi {


	private static final String REGEXP = "(?<=\\w)\\s(?=\\w)";

	@Override
	public String toQuery(SecureUserProfile userProfile) {
		StringBuilder result = new StringBuilder();
		boolean expansion= false;
		Pattern replace = Pattern.compile(REGEXP);
		 
		for (ContextKeyword key : userProfile.contextKeywords) {
			String keyword = key.text;
			 Matcher matcher2 = replace.matcher(keyword);
			keyword=matcher2.replaceAll(" AND ");
			
			if(key.expansion!=null && (key.expansion ==ExpansionType.PSEUDORELEVANCEWP||key.expansion ==ExpansionType.SERENDIPITY))
			{
				if(!expansion){
					expansion=true;
					if(result.length()>0){
						if(key.expansion==ExpansionType.PSEUDORELEVANCEWP)
							result.append(" OR (\""+keyword+"\"");
						else
							result.append(" AND (\""+keyword+"\"");
					}
					else
						result.append("(\""+keyword+"\"");
				}else{
					result.append(" OR \""+keyword+"\"");
				}
			} else{
				if(expansion){
					result.append(") OR \""+keyword+"\"");
					expansion=false;
				}	
				else
					if(result.length()>0)
						result.append(" OR \""+keyword+"\"");
					else
						result.append("\""+keyword+"\"");
			}
		}
		if(expansion)
			result.append(")");
			
		return result.toString();
	}

	@Override
	public String toDetailQuery(DocumentBadge document) {
		return document.id;
	}



}

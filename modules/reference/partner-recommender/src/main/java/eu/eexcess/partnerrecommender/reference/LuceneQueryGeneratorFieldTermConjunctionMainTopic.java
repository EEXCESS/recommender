package eu.eexcess.partnerrecommender.reference;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.logging.*;

import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;

/**
 * Similar to the Lucene query generator but also checks context keywords for
 * terms and transforms them into conjunction query terms. Keyword "New York"
 * ends up as "New AND York" in the query.
 * 
 * @author hziak
 *
 */
public class LuceneQueryGeneratorFieldTermConjunctionMainTopic implements QueryGeneratorApi {
	private static final Logger LOGGER = Logger.getLogger(LuceneQueryGeneratorFieldTermConjunctionMainTopic.class.getCanonicalName());
    private static final String REGEXP = "(?<=\\S)\\s+(?=\\S)";

    @Override
    public String toQuery(SecureUserProfile userProfile) {
    	
        StringBuilder result = new StringBuilder();
        boolean expansion = false;
        Pattern replace = Pattern.compile(REGEXP);
        List<ContextKeyword> mainKeywords = new ArrayList<ContextKeyword>();
        List<ContextKeyword> otherKeywords = new ArrayList<ContextKeyword>();
       
        userProfile.contextKeywords.forEach(kw -> {
        	if(kw.getIsMainTopic()){
        		if(result.length()==0){
        			result.append("("+kw.text);
        		}
        		else{
        			result.append(" AND " +kw.text);
        		}
        		mainKeywords.add(kw);
        	}
        	else
        		otherKeywords.add(kw);
        	});
       if(!mainKeywords.isEmpty())
        	result.append(") AND (");
        
        StringBuilder tmpResult= new StringBuilder();
        for (ContextKeyword key : otherKeywords) {
            String keyword = key.text;
            Matcher matcher2 = replace.matcher(keyword);
            if (matcher2.find()) {
                keyword = "(" + matcher2.replaceAll(" AND ") + ")";
            }

            if (key.expansion != null && (key.expansion == ExpansionType.PSEUDORELEVANCEWP || key.expansion == ExpansionType.SERENDIPITY)) {
              if (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
                    expansion = addExpansionTerm(tmpResult, expansion, key, keyword);
                }
            } else {
                expansion = addQueryTerm(tmpResult, expansion, keyword);
            }
        }
        result.append(tmpResult);
        if (expansion)
            result.append(")");

        if(!mainKeywords.isEmpty())
        	result.append(")");
		return result.toString();
    }

    private boolean addQueryTerm(StringBuilder result, boolean exp, String keyword) {
        boolean expansion = exp;
        if (expansion) {
            result.append(") OR " + keyword + "");
            expansion = false;
        } else if (result.length() > 0)
            result.append(" OR " + keyword + "");
        else
            result.append("" + keyword + "");
        return expansion;
    }

    private boolean addExpansionTerm(StringBuilder result, boolean exp, ContextKeyword key, String keyword) {
        boolean expansion = exp;
        if (!expansion) {
            expansion = true;
            if (result.length() > 0) {
                if (key.expansion == ExpansionType.PSEUDORELEVANCEWP)
                    result.append(" OR (" + keyword + "");
                else
                    result.append(" AND (" + keyword + "");
            } else
                result.append("(" + keyword + "");
        } else {
            result.append(" OR " + keyword + "");
        }
        return expansion;
    }

    @Override
    public String toDetailQuery(DocumentBadge document) {
        return document.id;
    }

}

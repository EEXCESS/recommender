package eu.eexcess.wikimedia.querygenerator;

import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.dataformats.userprofile.SpecialFieldsEum;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hziak on 04.02.16.
 */
public class WikiMediaQueryGenerator implements QueryGeneratorApi {
    private static final Logger LOGGER = Logger.getLogger(WikiMediaQueryGenerator.class.getCanonicalName());
    private static final String REGEXP = "(?<=\\w)\\s(?=\\w)";

    @Override public String toQuery(SecureUserProfile userProfile) {
        StringBuilder result = new StringBuilder();
        boolean expansion = false;
        Pattern replace = Pattern.compile(REGEXP);

        for (ContextKeyword key : userProfile.getContextKeywords()) {
            if (key.getType() == null || key.getType().equals(SpecialFieldsEum.Misc)) {
                String keyword = key.getText();
                Matcher matcher2 = replace.matcher(keyword);
                keyword = matcher2.replaceAll("|");

                if (key.getExpansion() != null && (key.getExpansion() == ExpansionType.PSEUDORELEVANCEWP || key.getExpansion() == ExpansionType.SERENDIPITY)) {
                    if (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled() != null && PartnerConfigurationCache.CONFIG.getPartnerConfiguration()
                            .isQueryExpansionEnabled()) {
                        expansion = addExpansionTerm(result, expansion, key, keyword);
                    }
                } else {
                    expansion = addQueryTerm(result, expansion, keyword);
                }
            }
        }

        String resultString = result.toString();
        try {
            resultString = URLEncoder.encode(resultString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, "Could not encode query in UTF-8", e);
        }
        return resultString;
    }

    private boolean addQueryTerm(StringBuilder result, boolean exp, String keyword) {
        boolean expansion = exp;
        if (result.length() > 0)
            result.append("|" + keyword + "");
        else
            result.append("" + keyword + "");
        return expansion;
    }

    private boolean addExpansionTerm(StringBuilder result, boolean exp, ContextKeyword key, String keyword) {
        boolean expansion = exp;
        if (!expansion) {
            expansion = true;
            if (result.length() > 0) {
                if (key.getExpansion() == ExpansionType.PSEUDORELEVANCEWP)
                    result.append(" OR (" + keyword + "");
                else
                    // result.append(" AND (" + keyword + "");
                    result.append(" AND (" + keyword + "");
            } else
                result.append("(" + keyword + "");
        } else {
            result.append(" OR " + keyword + "");
        }
        return expansion;
    }
    @Override public String toDetailQuery(DocumentBadge document) {
        return null;
    }
}

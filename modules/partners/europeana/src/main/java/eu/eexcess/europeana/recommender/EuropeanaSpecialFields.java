package eu.eexcess.europeana.recommender;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.SpecialFieldsQueryGeneratorApi;
import org.apache.commons.lang.text.StrSubstitutor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hziak on 23.11.15.
 */
public class EuropeanaSpecialFields extends SpecialFieldsQueryGeneratorApi {
    private static final Logger LOGGER = Logger.getLogger(EuropeanaSpecialFields.class.getCanonicalName());

    public String toQuery(SecureUserProfile userProfile) {
        StringBuilder builder = new StringBuilder();
        userProfile.getContextKeywords().forEach((ContextKeyword keyword) -> {
            if (keyword.getType() != null) {
                switch (keyword.getType()) {
                case Person:
                    if (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameWho() != null)
                        builder.append(mapValues(keyword.getText(), PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameWho()));
                    break;
                case Location:
                    if (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameWhere() != null)
                        builder.append(mapValues(keyword.getText(), PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameWhere()));
                    break;
                case Organization:
                    if (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameWhat() != null)
                        builder.append(mapValues(keyword.getText(), PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameWhat()));
                    break;
                }
            }
        });
        if (userProfile.getTimeRange() != null) {
            if (userProfile.getTimeRange().getStart() != null && userProfile.getTimeRange().getEnd() == null) {
                builder.append(mapTime(PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameFrom(), userProfile.getTimeRange().getStart(), null));
            }
            if (userProfile.getTimeRange().getEnd() != null) {

                String mapTime = mapTime(PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameFrom(), userProfile.getTimeRange().getStart(),
                        userProfile.getTimeRange().getEnd());
                //          try {
                //            String encoded = URLEncoder.encode(mapTime, "UTF-8");

                //                } catch (UnsupportedEncodingException e) {
                //                  LOGGER.log(Level.WARNING, "Could not encode time query to UTF-8", e);
                //            }
                builder.append(mapTime);
            }
        }
        return builder.toString();
    }

    @Override protected String mapValues(String text, String fieldName) {
        if (fieldName != null) {
            Map<String, String> valuesMap = new HashMap<String, String>();
            valuesMap.put("field", fieldName);

            try {
                valuesMap.put("query", URLEncoder.encode(text, "UTF-8"));
                return StrSubstitutor.replace(PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getSpecialFieldTemplate(), valuesMap);
            } catch (UnsupportedEncodingException e) {
                LOGGER.log(Level.INFO, "URLencoding Failed \"UTF-8\" not supported", e);
            }
        }
        return "";
    }

    protected String mapTime(String fieldName, String begin, String end) {
        Map<String, String> valuesMap = new HashMap<String, String>();
        if (fieldName != null) {
            String query = "";

            if (begin != null && end == null) {

                try {
                    query = URLEncoder.encode(begin, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    LOGGER.log(Level.WARNING, "Could not encode in UTF-8", e);
                }

                valuesMap.put("field", "&qf=" + fieldName);
            } else if (begin != null && end != null) {
                valuesMap.put("field", fieldName);
                try {
                    query = URLEncoder.encode("[" + begin + " TO " + end + "]", "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    LOGGER.log(Level.WARNING, "Could not encode in UTF-8", e);
                }
            }
            valuesMap.put("query", query);

            return "+" + StrSubstitutor.replace(PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getSpecialFieldTemplate(), valuesMap);
        }
        return "";
    }
}

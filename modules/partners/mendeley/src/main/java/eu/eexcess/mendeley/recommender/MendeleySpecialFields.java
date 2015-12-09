package eu.eexcess.mendeley.recommender;

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
public class MendeleySpecialFields extends SpecialFieldsQueryGeneratorApi {
    private static final Logger logger = Logger.getLogger(MendeleySpecialFields.class.getCanonicalName());

    @Override protected String mapValues(String text, String fieldName) {
        if (fieldName != null) {
            Map<String, String> valuesMap = new HashMap<String, String>();
            valuesMap.put("field", fieldName);
            try {
                valuesMap.put("query", URLEncoder.encode(text, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.log(Level.INFO, "URLencoding Failed \"UTF-8\" not supported", e);
            }
            return StrSubstitutor.replace(PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getSpecialFieldTemplate(), valuesMap);
        }
        return "";
    }
}

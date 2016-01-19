package eu.eexcess.partnerrecommender.api;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;

/**
 * Created by hziak on 23.11.15.
 */
public abstract class SpecialFieldsQueryGeneratorApi {
    /**
     * @param userProfile
     * @return
     */
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
            if (userProfile.getTimeRange().getStart() != null) {
                builder.append(mapValues(userProfile.getTimeRange().getStart(), PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameFrom()));
            }
            if (userProfile.getTimeRange().getEnd() != null) {
                builder.append(mapValues(userProfile.getTimeRange().getEnd(), PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameTo()));
            }
        }
        return builder.toString();
    }

    protected abstract String mapValues(String text, String fieldName);

}

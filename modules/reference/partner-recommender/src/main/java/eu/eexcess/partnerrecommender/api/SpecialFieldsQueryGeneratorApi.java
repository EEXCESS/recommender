package eu.eexcess.partnerrecommender.api;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.dataformats.userprofile.SpecialFieldsEum;

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
        StringBuilder personsString = new StringBuilder();
        StringBuilder locationString = new StringBuilder();
        StringBuilder organizationString = new StringBuilder();
        if (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameWho() != null)
            userProfile.getContextKeywords().stream().filter(keyword -> keyword.getType().equals(SpecialFieldsEum.Person))
                    .forEach(keyword -> appendToBuilder(personsString, keyword));
        if (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameWhere() != null)
            userProfile.getContextKeywords().stream().filter(keyword -> keyword.getType().equals(SpecialFieldsEum.Location))
                    .forEach(keyword -> appendToBuilder(locationString, keyword));
        if (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameWhat() != null)
            userProfile.getContextKeywords().stream().filter(keyword -> keyword.getType().equals(SpecialFieldsEum.Organization))
                    .forEach(keyword -> appendToBuilder(organizationString, keyword));

        if (personsString.length() > 0)
            builder.append(mapValues(personsString.toString(), PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getFieldNameWho()));


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

    private StringBuilder appendToBuilder(StringBuilder builder, ContextKeyword keyword) {
        if (builder.length() > 0)
            return builder.append(" OR " + "("+keyword.getText()+")");
        return builder.append("("+keyword.getText()+")");
    }

    protected abstract String mapValues(String text, String fieldName);

}

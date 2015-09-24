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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;

/**
 * Similar to the Lucene query generator but also checks context keywords for
 * terms and transforms them into conjunction query terms. Keyword "New York"
 * ends up as "New OR York" in the query.
 * 
 * @author hziak@know-center.at
 */
public class LuceneQueryGenerator implements QueryGeneratorApi {

    private static final String REGEXP = "(?<=\\w)\\s(?=\\w)";

    @Override
    public String toQuery(SecureUserProfile userProfile) {
        StringBuilder result = new StringBuilder();
        boolean expansion = false;
        Pattern replace = Pattern.compile(REGEXP);

        for (ContextKeyword key : userProfile.getContextKeywords()) {
            String keyword = key.getText();
            Matcher matcher2 = replace.matcher(keyword);
            keyword = matcher2.replaceAll(" OR ");

            if (key.getExpansion() != null && (key.getExpansion() == ExpansionType.PSEUDORELEVANCEWP || key.getExpansion() == ExpansionType.SERENDIPITY)) {
                if (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled() != null
                        && PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
                    expansion = addExpansionTerm(result, expansion, key, keyword);
                }
            } else {
                expansion = addQueryTerm(result, expansion, keyword);
            }
        }
        if (expansion)
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

    @Override
    public String toDetailQuery(DocumentBadge document) {
        return document.id;
    }

}

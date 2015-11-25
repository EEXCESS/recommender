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
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.QueryGeneratorApi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates OR Querys out of SecureUserProfile Context field
 * 
 * @author hziak
 *
 */
public class OrQueryGeneratorFieldTermConjunction implements QueryGeneratorApi {
    private static final Logger LOGGER = Logger.getLogger(OrQueryGeneratorFieldTermConjunction.class.getName());
    private static final String REGEXP = "(?<=\\w)\\s+(?=\\w)";

    @Override
    public String toQuery(SecureUserProfile userProfile) {
        StringBuilder builder = new StringBuilder();
        Pattern replace = Pattern.compile(REGEXP);

        for (ContextKeyword context : userProfile.getContextKeywords()) {
            if (context.getExpansion() != null && (context.getExpansion() == ExpansionType.PSEUDORELEVANCEWP || context.getExpansion() == ExpansionType.SERENDIPITY)) {
                if (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
                    String keyword = context.getText();
                    Matcher matcher2 = replace.matcher(keyword);
                    keyword = matcher2.replaceAll(" AND ");

                    if (builder.length() > 0) {
                        builder.append(" OR ");
                    }

                    builder.append(keyword);
                }
            } else {
                String keyword = context.getText();
                Matcher matcher2 = replace.matcher(keyword);
                keyword = matcher2.replaceAll(" AND ");

                if (builder.length() > 0) {
                    builder.append(" OR ");
                }

                builder.append(keyword);
            }
        }
        String resultString = builder.toString();
        try {
            resultString = URLEncoder.encode(resultString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, "Could not encode query in UTF-8", e);
        }
        return resultString;
    }

    @Override
    public String toDetailQuery(DocumentBadge document) {
        return document.id;
    }

}

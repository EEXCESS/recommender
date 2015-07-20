/**
 * Copyright (C) 2014
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.eexcess.opensearch.recommender.dataformat;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;

import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.opensearch.opensearch_description_document.OpensearchDescription;

/**
 * Builds an {@link ResultList} from JSON.
 * 
 * @author Raoul Rubien
 *
 */
public class JsonOpensearchResultListBuilder implements OpensearchResultListBuilder {
    private static final Logger LOGGER = Logger.getLogger(JsonOpensearchResultListBuilder.class.getName());
    private JSONArray jsonResponse;
    private OpensearchDescription descriptionDocument;

    /**
     * @param jsonResponse
     *            an OpenSearch json response array i.e. "[search-term,
     *            [shortDesc. array], [desc. array], [links array]]"
     */
    public JsonOpensearchResultListBuilder(JSONArray jsonResponse, OpensearchDescription descriptionDocument) {
        this.jsonResponse = jsonResponse;
        this.descriptionDocument = descriptionDocument;
    }

    /**
     * @return an OpensearchResponse object representing the given
     *         {@code jsonResponse} or null on error
     */
    @Override
    public ResultList build() {

        int shortDescriptonsIdx = 1;
        int descriptonsIdx = 2;
        int urlsIdx = 3;

        int numShortDescriptions = jsonResponse.getJSONArray(shortDescriptonsIdx).length();
        int numDescriptions = jsonResponse.getJSONArray(descriptonsIdx).length();
        int numUrls = jsonResponse.getJSONArray(urlsIdx).length();

        if (numShortDescriptions != numDescriptions || numDescriptions != numUrls) {
            LOGGER.log(Level.WARNING, "warning - unequal response dimensions [" + numShortDescriptions + "][" + numDescriptions + "][" + numUrls
                    + "][#shortDescriptions][#descriptions][#links]");
            return null;
        }

        int maxLength = (numShortDescriptions > numDescriptions) ? numShortDescriptions : numDescriptions;
        maxLength = (numUrls > maxLength) ? numUrls : maxLength;

        JSONArray shortDescriptions = jsonResponse.getJSONArray(shortDescriptonsIdx);
        JSONArray descriptions = jsonResponse.getJSONArray(descriptonsIdx);
        JSONArray urls = jsonResponse.getJSONArray(urlsIdx);

        ResultList response = new ResultList();

        for (int idx = 0; idx < maxLength; idx++) {
            String shortDescription = null;
            try {
                shortDescription = shortDescriptions.getString(idx);
            } catch (JSONException e) {
                LOGGER.log(Level.INFO, "No short description given", e);
                shortDescription = "";
            }

            String description = null;
            try {
                description = descriptions.getString(idx);
            } catch (JSONException e) {
                LOGGER.log(Level.INFO, "No Description given", e);
                description = "";
            }

            String url = null;
            try {
                url = urls.getString(idx);
            } catch (JSONException e) {
                LOGGER.log(Level.INFO, "No URL given", e);
                url = "";
            }

            Result resultEntry = new Result();
            resultEntry.title = shortDescription;
            resultEntry.description = description;
            resultEntry.documentBadge = new DocumentBadge("", url, getProviderName());
            response.results.add(resultEntry);
        }
        response.totalResults = response.results.size();
        return response;
    }

    private String getProviderName() {

        String providerName = null;
        if (descriptionDocument.shortName != null && descriptionDocument.shortName.length() > 0) {
            providerName = descriptionDocument.shortName;

        }
        if (providerName == null && descriptionDocument.longName != null) {
            providerName = descriptionDocument.longName;
        }

        if (providerName == null) {
            providerName = "";
        }

        return providerName;
    }
}

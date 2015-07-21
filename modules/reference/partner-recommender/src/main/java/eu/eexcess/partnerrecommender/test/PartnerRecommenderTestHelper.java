/* Copyright (C) 2014
"JOANNEUM RESEARCH Forschungsgesellschaft mbH" 
 Graz, Austria, digital-iis@joanneum.at.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package eu.eexcess.partnerrecommender.test;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.ResultList;

@SuppressWarnings("deprecation")
public class PartnerRecommenderTestHelper {

    static public StringEntity createParamsForPartnerRecommender(int records, ArrayList<String> keywords) {
        StringEntity params = null;
        String userprofile = "<eexcess-secure-user-profile numResults=\"" + records
                + "\" firstName=\"Hugo\" lastName=\"Boss\" birthDate=\"2013-10-14T05:06:44.550+02:00\">   <contextKeywords>      ";
        for (int i = 0; i < keywords.size(); i++) {
            userprofile += "<contextKeywords><text>" + keywords.get(i) + "</text></contextKeywords>";
        }
        userprofile += " </contextKeywords></eexcess-secure-user-profile>";
        params = new StringEntity(userprofile, "UTF-8");
        return params;
    }

    static public StringEntity createParamsForPartnerRecommenderDetailCall(ArrayList<String> ids, ArrayList<String> uris, String provider) {
        /*
         * <eexcess-document-badges-list> <documentBadge> <id>E1.6882</id>
         * <uri>http
         * ://www.kim.bl.openinteractive.ch/sammlungen#f19e71ca-4dc6-48b8
         * -858c-60a1710066f0</uri> <provider>KIM.Portal</provider>
         * </documentBadge> <documentBadge> <id>E1.6880</id>
         * <uri>http://www.kim.
         * bl.openinteractive.ch/sammlungen#f04ae6c5-45fd-ff40
         * -333c-f3b50dffbe3d</uri> <provider>KIM.Portal</provider>
         * </documentBadge> </eexcess-document-badges-list>
         */
        StringEntity params = null;
        String documentBadges = "<eexcess-document-badges-list>";
        if (ids.size() != uris.size())
            return null;
        for (int i = 0; i < ids.size(); i++) {
            documentBadges += "<documentBadge><id>" + ids.get(i) + "</id><uri>" + uris.get(i) + "</uri><provider>" + provider + "</provider></documentBadge>";
        }
        documentBadges += "</eexcess-document-badges-list>";
        params = new StringEntity(documentBadges, "UTF-8");
        return params;
    }

    static public StringEntity createParamsForPartnerRecommenderDetailCallJSON(ArrayList<String> ids, ArrayList<String> uris, String provider) {
        /*
         * { "documentBadge":[ { "id":"E1.6882", "uri":
         * "http://www.kim.bl.openinteractive.ch/sammlungen#f19e71ca-4dc6-48b8-858c-60a1710066f0"
         * , "provider":"KIM.Portal" }, { "id":"E1.6880", "uri":
         * "http://www.kim.bl.openinteractive.ch/sammlungen#f04ae6c5-45fd-ff40-333c-f3b50dffbe3d"
         * , "provider":"KIM.Portal" } ] }
         */
        StringEntity params = null;
        String documentBadges = "{\"documentBadge\":[";
        if (ids.size() != uris.size())
            return null;
        for (int i = 0; i < ids.size(); i++) {
            if (i != 0)
                documentBadges += ",";
            documentBadges += "{\"id\":\"" + ids.get(i) + "\",\"uri\":\"" + uris.get(i) + "\",\"provider\":\"" + provider + "\"}";
        }
        documentBadges += "]}";
        params = new StringEntity(documentBadges, "UTF-8");
        return params;
    }

    static public ResultList parseResponse(String responseString) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(ResultList.class);

        StringReader reader = new StringReader(responseString);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Object resultListObject = unmarshaller.unmarshal(reader);

        if (resultListObject instanceof ResultList) {
            return (ResultList) resultListObject;

        }
        return null;
    }

    static public DocumentBadgeList parseResponseDetail(String responseString) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(DocumentBadgeList.class);

        StringReader reader = new StringReader(responseString);

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Object resultListObject = unmarshaller.unmarshal(reader);

        if (resultListObject instanceof DocumentBadgeList) {
            return (DocumentBadgeList) resultListObject;

        }
        return null;
    }

    @SuppressWarnings({ "resource" })
    static public ResultList getRecommendations(String deploymentContext, int port, StringEntity params) {

        HttpClient httpClient = new DefaultHttpClient();
        ResultList resultList = null;
        try {

            HttpPost request = new HttpPost("http://localhost:" + port + "/" + deploymentContext + "/partner/recommend/");
            request.addHeader("content-type", "application/xml");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            String responseString = EntityUtils.toString(response.getEntity());

            resultList = PartnerRecommenderTestHelper.parseResponse(responseString);

        } catch (Exception ex) {
            System.out.println(ex);
        } finally {

            httpClient.getConnectionManager().shutdown();
        }

        return resultList;
    }

    @SuppressWarnings({ "resource" })
    static protected DocumentBadgeList getDetails(String deploymentContext, int port, StringEntity params, String contentType) {
        HttpClient httpClient = new DefaultHttpClient();
        DocumentBadgeList decoumentBadges = null;
        try {

            HttpPost request = new HttpPost("http://localhost:" + port + "/" + deploymentContext + "/partner/getDetails/");
            request.addHeader("content-type", contentType);
            request.addHeader("accept", contentType);
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            String responseString = EntityUtils.toString(response.getEntity());

            decoumentBadges = PartnerRecommenderTestHelper.parseResponseDetail(responseString);

        } catch (Exception ex) {
            System.out.println(ex);
        } finally {

            httpClient.getConnectionManager().shutdown();
        }

        return decoumentBadges;
    }

    static public DocumentBadgeList getDetails(String deploymentContext, int port, StringEntity params) {
        return PartnerRecommenderTestHelper.getDetails(deploymentContext, port, params, "application/xml");
    }

    static public DocumentBadgeList getDetailsJSON(String deploymentContext, int port, StringEntity params) {
        return PartnerRecommenderTestHelper.getDetails(deploymentContext, port, params, "application/json");
    }
}

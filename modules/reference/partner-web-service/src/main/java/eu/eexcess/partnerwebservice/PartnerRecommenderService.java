/* Copyright (C) 2014
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

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
package eu.eexcess.partnerwebservice;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.w3c.dom.Document;

import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.Interest;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.PartnerRecommenderApi;
import eu.eexcess.partnerrecommender.reference.PartnerRecommender;

/**
 * Reference implementation of a partner recommender web service.
 * 
 * @author rkern@know-center.at
 */

@Path("/partner")
public class PartnerRecommenderService {
    // private static final String EEXCESS_MIMETYPE =
    // "application/vnd.eexcess.recommendation-results+xml";
    // private static final String EEXCESS_NAMESPACE =
    // "http://eexcess.eu/schema/recommender-results";

    private PartnerRecommenderApi partnerRecommender;

    public PartnerRecommenderService() {
        partnerRecommender = new PartnerRecommender();
    }

    @PostConstruct
    @POST
    @Path("/intialize")
    public void initialize() throws IOException {
        // nothing to be initialzed
    }

    @PreDestroy
    public void destroy() throws IOException {
        // nothing to be done on destruction
    }

    @POST
    @Path("/recommend")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public ResultList recommend(SecureUserProfile userProfile) throws IOException {
        return partnerRecommender.recommend(userProfile);
    }

    /**
     * gets details for the given list of documents
     * 
     * @param documents
     * @return
     * @throws IOException
     */
    @POST
    @Path("/getDetails")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public DocumentBadgeList getDetails(DocumentBadgeList documents) throws IOException {
        return partnerRecommender.getDetails(documents);
    }

    /**
     * Returns the EEXCESS user profile for a given user.
     * 
     * @return
     * @throws IOException
     */
    @POST
    @Path("/getUserProfile")
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Document getUserProfile(String userId) throws IOException {
        return partnerRecommender.getUserProfile(userId);
    }

    /**
     * Creates and outputs a dummy user profile, for testing only.
     * 
     * @return a fake user profile
     */
    @GET
    @Path("/debugDumpProfile")
    @Produces(value = MediaType.APPLICATION_XML)
    public SecureUserProfile debugDumpProfile() {
        SecureUserProfile profile = new SecureUserProfile();
        profile.setFirstName("Hugo");
        profile.setLastName("Boss");
        profile.setBirthDate(new Date(System.currentTimeMillis() - 30 * 365 * 24 * 60 * 60 * 1000));
        profile.getInterestList().add(new Interest("Machine Learning"));
        profile.getInterestList().add(new Interest("Information Retrieval"));
        profile.getInterestList().add(new Interest("Natural Language Processing"));
        profile.getContextKeywords().add(new ContextKeyword("higgs"));
        profile.getContextKeywords().add(new ContextKeyword("boson"));

        profile.getContextKeywords().add(new ContextKeyword("loom"));
        profile.getContextKeywords().add(new ContextKeyword("Economy"));
        profile.getContextKeywords().add(new ContextKeyword("Switzerland"));
        profile.getContextKeywords().add(new ContextKeyword("19th Century"));
        return profile;
    }

    /**
     * Creates and outputs a dummy item recommendation, for testing only.
     * 
     * @return a fake recommended item
     */
    @GET
    @Path("/debugDumpResult")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Result debugDumpRecommendedItem() {
        Result recommendedItem = new Result();
        DocumentBadge db = new DocumentBadge();
        db.id = "/15503/E627F23EF13FA8E6584AF8706A95DB85908413BE";
        db.uri = "http://www.europeana.eu/portal/record/15503/E627F23EF13FA8E6584AF8706A95DB85908413BE.html?utm_source=api&utm_medium=api&utm_campaign=uJrvLB6xd";
        db.provider = "Europeana";
        recommendedItem.title = "Gustaf Gründgens";
        recommendedItem.previewImage = "http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FFS_PM133355alt.jpg&size=LARGE&type=IMAGE";
        recommendedItem.documentBadge = db;
        recommendedItem.date = "1980";
        recommendedItem.language = "de";
        recommendedItem.licence = "Apache2";
        recommendedItem.mediaType = "image";
        recommendedItem.description = "Lorem Ipsum ....";

        return recommendedItem;
    }

    /**
     * Creates and outputs a dummy full recommendation of items, for testing
     * only.
     * 
     * @return a fake recommendation of items
     */
    @GET
    @Path("/debugDumpResultList")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public ResultList debugDumpResultList() {
        ResultList resultList = new ResultList();
        resultList.totalResults = 1;
        resultList.results.add(debugDumpRecommendedItem());
        return resultList;
    }

}

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
package eu.eexcess.federatedrecommenderservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.eexcess.dataformats.userprofile.*;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.spi.resource.Singleton;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerBadgeList;
import eu.eexcess.dataformats.RecommenderStats;
import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.federatedrecommender.FederatedRecommenderCore;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

/**
 * Reference implementation of a partner recommender web service.
 * 
 * @author rkern@know-center.at
 */
@Path("/recommender")
@Singleton
public class FederatedRecommenderService {
    private static final Logger LOGGER = Logger.getLogger(FederatedRecommenderService.class.getName());

    private final FederatedRecommenderCore fRC;

    private final FederatedRecommenderConfiguration federatedRecommenderConfiguration;

    /**
     * Creates a new instance of this class.
     * 
     * @throws FederatedRecommenderException
     */
    public FederatedRecommenderService() throws FederatedRecommenderException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        String eexcessPartnerKeyFile = System.getenv("EEXCESS_FEDERATED_RECOMMENDER_CONFIG_FILE");
        URL resource = null;
        if (eexcessPartnerKeyFile == null) {
            LOGGER.log(Level.INFO, "Config file was not defined in environment EEXCESS_FEDERATED_RECOMMENDER_CONFIG_FILE. Reading file from package resource.");
            resource = getClass().getResource("/federatedRecommenderConfig.json");
        } else {
            LOGGER.log(Level.INFO, "Reading Config file from:" + eexcessPartnerKeyFile);
            try {
                resource = new File(eexcessPartnerKeyFile).toURI().toURL();
            } catch (MalformedURLException e) {
                LOGGER.log(Level.SEVERE, "Environment Variable was malformated:" + eexcessPartnerKeyFile, e);
            }
        }

        try {
            federatedRecommenderConfiguration = mapper.readValue(new File(resource.getFile()), FederatedRecommenderConfiguration.class);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "There was an error parsing the FederationRecommenderConfig File", e);
            throw new FederatedRecommenderException("There was an error parsing the FederationRecommenderConfig File in FederatedRecommenderCore Module", e);
        }
        try {
            fRC = FederatedRecommenderCore.getInstance(federatedRecommenderConfiguration);
        } catch (FederatedRecommenderException e) {
            LOGGER.log(Level.SEVERE, "", e);
            throw new FederatedRecommenderException("Could not get an instance of FederatedRecommenderCore", e);
        }

    }

    @PostConstruct
    public void initialize() throws FederatedRecommenderException {
        LOGGER.log(Level.INFO, "Initialize");
    }

    // Begin Services

    /*
     * OFFERED SERVICES:
     * 
     * (1) Register a new partner -> /register (2) Generate recommendations
     * given a user profile -> /recommend
     */
    /**
     * partners have to register their system with xml or json to let the system
     * query the partners
     * 
     * @param badge
     * @return
     * @throws IOException
     */
    @POST
    @Path("/register")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.WILDCARD })
    public Response registerPartner(PartnerBadge badge) throws IOException {
        LOGGER.log(Level.INFO, "Registering Partner: " + badge.getSystemId());
        String returnString = fRC.registerPartner(badge);
        if ("Partner Key is too short (<20)".equals(returnString))
            return Response.notModified(returnString).build();
        return Response.ok().build();
    }

    /**
     * method to unregister a previously registered partner
     * 
     * @param badge
     * @throws IOException
     */
    @POST
    @Path("/unregister")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public void unregisterPartner(PartnerBadge badge) throws IOException {
        LOGGER.log(Level.INFO, "Unregistering Partner: " + badge.getSystemId());
        fRC.unregisterPartner(badge);
    }

    /**
     * returns the result list for the given query
     * 
     * @param userProfile
     * @return
     * @throws IOException
     */
    @POST
    @Path("/recommend")
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ResultList recommend(SecureUserProfile userProfile) throws IOException {
        ResultList resultList = new ResultList();
        LOGGER.log(Level.INFO,"AgeRange in userProfile " + userProfile.getAgeRange());
        try {
			resultList = fRC.generateFederatedRecommendation(userProfile);
		} catch (FederatedRecommenderException e) {
			LOGGER.log(Level.SEVERE,"Some error occured processing the query",e );
		}
        resultList.queryID = userProfile.getQueryID();
        return resultList;
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
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public DocumentBadgeList getDetails(DocumentBadgeList documents) throws IOException {
        return fRC.getDocumentDetails(documents);
    }

    @GET
    @Path("/getRegisteredPartners")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public PartnerBadgeList getRegisteredPartners() throws IOException {

        PartnerBadgeList partners = new PartnerBadgeList();

        partners.partners = fRC.getPartnerRegister().getPartners();
        partners.totalPartners = partners.partners.size();

        return partners;
    }

    @GET
    @Path("/getRecommenderStats")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public RecommenderStats getRecommenderStats() throws IOException {
        return fRC.getRecommenderStats();
    }

    @GET
    @Path("/getPartnerFavIcon")
    @Produces("image/png")
    public Response getPartnerFavIcon(@QueryParam("partnerId") String partnerId) throws IOException {
        byte[] resourceAsStream = null;
        resourceAsStream = fRC.getPartnerFavIcon(partnerId);
        if (resourceAsStream != null)
            return Response.ok(resourceAsStream).build();
        return Response.serverError().build();
    }

    @GET
    @Path("/getPreviewImage")
    @Produces("image/png")
    public Response getPreviewImage(@QueryParam("type") String type) throws IOException {
        LOGGER.log(Level.INFO, type);
        if (type == null)
            return Response.serverError().build();
        InputStream resourceAsStream = null;
        switch (type) {
        case "other":
            resourceAsStream = this.getClass().getResourceAsStream("/Thumbnails_EECXESS_unknown.png");
            break;
        case "unknown":
            resourceAsStream = this.getClass().getResourceAsStream("/Thumbnails_EECXESS_unknown.png");
            break;
        case "text":
            resourceAsStream = this.getClass().getResourceAsStream("/Thumbnails_EECXESS_text.png");
            break;
        case "audio":
            resourceAsStream = this.getClass().getResourceAsStream("/Thumbnails_EECXESS_audio.png");
            break;
        case "3d":
            resourceAsStream = this.getClass().getResourceAsStream("/Thumbnails_EECXESS_3d.png");
            break;
        case "image":
            resourceAsStream = this.getClass().getResourceAsStream("/Thumbnails_EECXESS_image.png");
            break;
        case "video":
            resourceAsStream = this.getClass().getResourceAsStream("/Thumbnails_EECXESS_video.png");
            break;
        default:
            break;
        }
        if (resourceAsStream != null)
            return Response.ok(resourceAsStream).build();
        return Response.serverError().build();
    }

    // End Services

    // Begin Test Services

    @GET
    @Path("/testRecommend")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ResultList testRecommend(@QueryParam("context") String context) throws IOException {
        SecureUserProfile userProfile = new SecureUserProfile();
        for (String text : Arrays.asList(context)) {
            userProfile.getContextKeywords().add(new ContextKeyword(text, 0.1));
        }
    	ResultList resultList = new ResultList(); 
        try {
			 resultList = fRC.generateFederatedRecommendation(userProfile);
		} catch (FederatedRecommenderException e) {
			LOGGER.log(Level.SEVERE,"Some error occured processing the query",e );
		}
        return resultList;
    }

    @GET
    @Path("/testBadge")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public PartnerBadge testBadge() {

        PartnerBadge pb = new PartnerBadge();
        pb.setSystemId("Europeana");
        pb.setPartnerConnectorEndpoint("http://DIGV536.joanneum.at/eexcess-partner-europeana-1.0-SNAPSHOT/partner/recommend/");
        pb.setDescription("Multi-lingual online collection of millions of digitized items from European museums, libraries, archives and multi-media collections");
        List<String> languageContent = new ArrayList<>(Arrays.asList("de", "en"));
        pb.setLanguageContent(languageContent);
        return pb;
    }

    @GET
    @Path("/testSUP")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SecureUserProfile testSUP() {
        SecureUserProfile secureUserProfile = new SecureUserProfile();
        secureUserProfile.setQueryID("QueryID01234567");
        FeatureVector userVector = new FeatureVector();
        userVector.setText(1.0);
        userVector.setVideo(1.0);
        userVector.setOpenLicence(1.0);
        userVector.setDateExisting(1.0);


        secureUserProfile.setPreferences(userVector);
        secureUserProfile.setAgeRange(2);
        secureUserProfile.setGender("male");
        secureUserProfile.setTimeRange(new TimeRange());
        secureUserProfile.getTimeRange().setStart("1980");
        secureUserProfile.getTimeRange().setEnd("2000");
        List<History> history = new ArrayList<History>();
        history.add(new History(new Date(), "history title", 4, 4, "http://1234.com"));

        Address address = new Address("austria",  "Graz");
        address.setCity("testcity");
        address.setCountry("testcountry");
     
        secureUserProfile.setAddress(address);
        secureUserProfile.setContext(new Context());
        secureUserProfile.getContext().setReason("manual");
        secureUserProfile.getContext().setValue("www.wikipedia.at");
        secureUserProfile.setQueryID("1234COFFEE");
        List<ContextKeyword> contextList = new ArrayList<ContextKeyword>();
        contextList.add(new ContextKeyword("women", 0.5));
        contextList.add(new ContextKeyword("labour", 0.5));
        secureUserProfile.setContextKeywords(contextList);
//        PartnerBadge pB = new PartnerBadge();
//        pB.setSystemId("Europeana");
//        secureUserProfile.getPartnerList().add(pB);

        List<Interest> interestList = new ArrayList<Interest>();
        interestList.add(new Interest("text", 0.1, 0.1, 0.1, "source", "http://dsjkdjas.de"));
        interestList.add(new Interest("text2", 0.2, 0.2, 0.2, "source2", "http://google.de"));
        secureUserProfile.setInterestList(interestList);

        List<PartnerBadge> protectedPartnerList = new ArrayList<PartnerBadge>();
        PartnerBadge badge = new PartnerBadge();
        badge.setSystemId("Wissenmedia");
        badge.setPartnerKey("dsajln22sadjkl!");
        protectedPartnerList.add(badge);

        List<UserCredentials> userCredentials = new ArrayList<UserCredentials>();
        eu.eexcess.dataformats.userprofile.UserCredentials cred = new UserCredentials();
        cred.setLogin("me@partner.x");
        cred.setSecurityToken("sdjalkej21!#");
        cred.setSystemId("Wissenmedia");

        userCredentials.add(cred);
        secureUserProfile.setUserCredentials(userCredentials);

        ContextNamedEntity contextNamedEntitie = new ContextNamedEntity();
        final String dbPediaUrl = "http://dbpedia.url.org";
        ContextNamedEntitiesElement location = new ContextNamedEntitiesElement("graz", 0.1, 0.1, dbPediaUrl);
        contextNamedEntitie.locations.add(location);
        contextNamedEntitie.locations.add(location);
        ContextNamedEntitiesElement misc = new ContextNamedEntitiesElement("something", 0.1, 0.1, dbPediaUrl);
        contextNamedEntitie.misc.add(misc);
        contextNamedEntitie.misc.add(misc);
        ContextNamedEntitiesElement org1 = new ContextNamedEntitiesElement("know-center", 0.1, 0.1, dbPediaUrl);
        contextNamedEntitie.organizations.add(org1);
        ContextNamedEntitiesElement org2 = new ContextNamedEntitiesElement("mendeley", 0.1, 0.1, dbPediaUrl);
        contextNamedEntitie.organizations.add(org2);
        ContextNamedEntitiesElement pers1 = new ContextNamedEntitiesElement("Michael Jackson", 0.1, 0.1, dbPediaUrl);
        contextNamedEntitie.persons.add(pers1);
        ContextNamedEntitiesElement pers2 = new ContextNamedEntitiesElement("Bill Clinton", 0.1, 0.1, dbPediaUrl);
        contextNamedEntitie.persons.add(pers2);
        ContextNamedEntitiesElement top1 = new ContextNamedEntitiesElement("Trees", 0.1, 0.1, dbPediaUrl);
        contextNamedEntitie.topics.add(top1);
        ContextNamedEntitiesElement top2 = new ContextNamedEntitiesElement("Animal", 0.1, 0.1, dbPediaUrl);
        contextNamedEntitie.topics.add(top2);

        Language language1 = new Language("de", 0.1);
        Language language2 = new Language("en", 0.1);

        secureUserProfile.getLanguages().add(language1);
        secureUserProfile.getLanguages().add(language2);

        List<UserLocation> locationsList = new ArrayList<UserLocation>();
        locationsList.add(new UserLocation(33.123123, -127.123123, 4.5, new Date()));
        locationsList.add(new UserLocation(20.123123, -130.123123, 4.5, new Date()));

        return secureUserProfile;
    }

    // End Test Services

}

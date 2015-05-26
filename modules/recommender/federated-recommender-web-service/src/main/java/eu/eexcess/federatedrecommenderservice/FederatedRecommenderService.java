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

import com.sun.jersey.spi.resource.Singleton;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerBadgeList;
import eu.eexcess.dataformats.RecommenderStats;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.*;
import eu.eexcess.federatedrecommender.FederatedRecommenderCore;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reference implementation of a partner recommender web service.
 * 
 * @author rkern@know-center.at
 */
@Path("/recommender")
@Singleton
public class FederatedRecommenderService {
	private static final Logger logger = Logger
			.getLogger(FederatedRecommenderService.class.getName());
	@SuppressWarnings("unused")
	private static final String EEXCESS_MIMETYPE = "application/vnd.eexcess.recommendation-results+json";
	@SuppressWarnings("unused")
	private static final String EEXCESS_NAMESPACE = "http://eexcess.eu/schema/recommender-results";
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
		String eexcessPartnerKeyFile =System.getenv("EEXCESS_FEDERATED_RECOMMENDER_CONFIG_FILE");
		URL resource =null;
		if(eexcessPartnerKeyFile==null){
			logger.log(Level.INFO,"Config file was not defined in environment EEXCESS_FEDERATED_RECOMMENDER_CONFIG_FILE. Reading file from package resource.");	
		 resource = getClass().getResource("/federatedRecommenderConfig.json");
		}else{
			logger.log(Level.INFO,"Reading Config file from:" + eexcessPartnerKeyFile);	
			try {
				resource = new File(eexcessPartnerKeyFile).toURL();
			} catch (MalformedURLException e) {
				logger.log(Level.SEVERE,"Environment Variable was malformated:"+eexcessPartnerKeyFile,e);
			}
		}
		
		
		try {
			federatedRecommenderConfiguration = mapper.readValue(new File(
					resource.getFile()),
					FederatedRecommenderConfiguration.class);
		} catch (JsonParseException e) {
			logger.log(
					Level.SEVERE,
					"There was an error parsing the FederationRecommenderConfig File",
					e);
			throw new FederatedRecommenderException(
					"There was an error parsing the FederationRecommenderConfig File in FederatedRecommenderCore Module",
					e);
		} catch (JsonMappingException e) {
			logger.log(
					Level.SEVERE,
					"There was an error parsing the FederationRecommenderConfig File",
					e);
			throw new FederatedRecommenderException(
					"There was an error parsing the FederationRecommenderConfig File in FederatedRecommenderCore Module",
					e);
		} catch (IOException e) {
			logger.log(
					Level.SEVERE,
					"There was an error reading the FederationRecommenderConfig File",
					e);
			throw new FederatedRecommenderException(
					"There was an error reading the FederationRecommenderConfig File in FederatedRecommenderCore Module",
					e);
		}
		try {
			fRC = FederatedRecommenderCore
					.getInstance(federatedRecommenderConfiguration);
		} catch (FederatedRecommenderException e) {
			logger.log(Level.SEVERE, "", e);
			throw new FederatedRecommenderException(
					"Could not get an instance of FederatedRecommenderCore", e);
		}

	}

	@PostConstruct
	public void initialize() throws Exception {
		logger.log(Level.INFO,"Initialize");
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
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({MediaType.WILDCARD})
	public Response registerPartner(PartnerBadge badge) throws IOException {
		logger.log(Level.INFO,"Registering Partner: "+badge.getSystemId());
		String returnString =fRC.registerPartner(badge);
		if(returnString.contains("Key is too Short"))
			return Response.notModified(returnString).build();
		return Response.ok().build();
	}

	
	@POST
	@Path("/unregister")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void unregisterPartner(PartnerBadge badge) throws IOException {
		logger.log(Level.INFO,"Unregistering Partner: "+badge.getSystemId());
		fRC.unregisterPartner(badge);
	}


	@POST
	@Path("/recommend")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ResultList recommend(SecureUserProfile userProfile)
			throws IOException {
		ResultList resultList = new ResultList();
		resultList = fRC.generateFederatedRecommendation(userProfile);
		resultList.queryID = userProfile.queryID;
		return resultList;
	}

	/**
	 * gets details for the given list of documents
	 * @param documents
	 * @return
	 * @throws IOException
	 */
	@POST
	@Path("/getDetails")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public DocumentBadgeList getDetails(DocumentBadgeList documents)
			throws IOException {
		return fRC.getDocumentDetails(documents);
	}
	
	@GET
	@Path("/getRegisteredPartners")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public PartnerBadgeList getRegisteredPartners() throws IOException {

		/*
		 * PartnerRegister partnerRegister= fRC.getPartnerRegister();
		 * PartnerRegister returnedPartnerRegister= new PartnerRegister(); for
		 * (PartnerBadge partnerBadge: partnerRegister.getPartners()) {
		 * partnerBadge.setEndpoint(""); //clean out the endpoint, user should
		 * perhaps not know that if(partnerBadge.partnerKey==null)
		 * returnedPartnerRegister.addPartner(partnerBadge);
		 * if(partnerBadge.partnerKey.isEmpty())
		 * returnedPartnerRegister.addPartner(partnerBadge); } return
		 * returnedPartnerRegister;
		 */
		PartnerBadgeList partners = new PartnerBadgeList();

		partners.partners = fRC.getPartnerRegister().getPartners();
		partners.totalPartners = partners.partners.size();

		return partners;
	}
	@GET
	@Path("/getRecommenderStats")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public RecommenderStats getRecommenderStats() throws IOException {
		return fRC.getRecommenderStats();
	}
	// End Services

	// Begin Test Services

	/*
	 * TEST METHODS:
	 * 
	 * (1) Test recommend -> /testRecommend?context=Keyword -> Generate
	 * recommendations (2) Test badge -> /testBadge -> Retrieve a sample partner
	 * badge (3) Test secure user profile -> /testSUP -> Retrieve a sample
	 * secure user profile
	 */

	/*
	 * @GET
	 * 
	 * @Path("/testRecommend")
	 * 
	 * @Produces(value = MediaType.APPLICATION_XML) public ResultList
	 * testRecommend(@QueryParam("context") String context) throws IOException {
	 * SecureUserProfile userProfile = new SecureUserProfile();
	 * userProfile.contextList = Arrays.asList(context); return
	 * fRC.generateFederatedRecommendation(userProfile); }
	 */

	@GET
	@Path("/testRecommend")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ResultList testRecommend(@QueryParam("context") String context)
			throws IOException {
		SecureUserProfile userProfile = new SecureUserProfile();
		for (String text : Arrays.asList(context)) {
			userProfile.contextKeywords.add(new ContextKeyword(text, 0.1));
		}
		return fRC.generateFederatedRecommendation(userProfile);
	}

	@GET
	@Path("/testBadge")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
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
		secureUserProfile.firstName = "Max";
		secureUserProfile.lastName = "Musterman";
		secureUserProfile.birthDate = new Date();
		secureUserProfile.gender = "male";

		List<History> history = new ArrayList<History>();
		history.add(new History(new Date(), "history title", 4, 4,
				"http://1234.com"));
		secureUserProfile.history = history;
		Address address = new Address("austria", 8010, "Graz", "nothing",
				"to add");
		address.city = "testcity";
		address.country = "testcountry";
		address.zipCode = 1213345;
		secureUserProfile.address = address;
		secureUserProfile.context = new Context();
		secureUserProfile.context.reason = "manual";
		secureUserProfile.context.value = "www.wikipedia.at";
		secureUserProfile.queryID = "1234COFFEE";
		List<ContextKeyword> contextList = new ArrayList<ContextKeyword>();
		contextList.add(new ContextKeyword("women", 0.5));
		contextList.add(new ContextKeyword("labour", 0.5));
		secureUserProfile.contextKeywords = contextList;
		PartnerBadge pB = new PartnerBadge();
		pB.setSystemId("Europeana");
		 secureUserProfile.partnerList.add(pB);

		List<Interest> interestList = new ArrayList<Interest>();
		interestList.add(new Interest("text", 0.1, 0.1, 0.1, "source",
				"http://dsjkdjas.de"));
		interestList.add(new Interest("text2", 0.2, 0.2, 0.2, "source2",
				"http://google.de"));
		secureUserProfile.interestList = interestList;

		List<PartnerBadge> protectedPartnerList = new ArrayList<PartnerBadge>();
		PartnerBadge badge = new PartnerBadge();
		badge.setSystemId("Wissenmedia");
		badge.partnerKey = "dsajln22sadjkl!";
		protectedPartnerList.add(badge);

		// secureUserProfile.protectedPartnerList = protectedPartnerList;

		List<UserCredentials> UserCredentials = new ArrayList<UserCredentials>();
		eu.eexcess.dataformats.userprofile.UserCredentials cred = new UserCredentials();
		cred.login = "me@partner.x";
		cred.securityToken = "sdjalkej21!#";
		cred.systemId = "Wissenmedia";

		UserCredentials.add(cred);
		secureUserProfile.userCredentials = UserCredentials;

		ContextNamedEntity ContextNamedEntitie = new ContextNamedEntity();
		ContextNamedEntitiesElement location = new ContextNamedEntitiesElement(
				"graz", 0.1, 0.1, "http://dbpedia.url.org");
		ContextNamedEntitie.locations.add(location);
		ContextNamedEntitie.locations.add(location);
		ContextNamedEntitiesElement misc = new ContextNamedEntitiesElement(
				"something", 0.1, 0.1, "http://dbpedia.url.org");
		ContextNamedEntitie.misc.add(misc);
		ContextNamedEntitie.misc.add(misc);
		ContextNamedEntitiesElement org1 = new ContextNamedEntitiesElement(
				"know-center", 0.1, 0.1, "http://dbpedia.url.org");
		ContextNamedEntitie.organizations.add(org1);
		ContextNamedEntitiesElement org2 = new ContextNamedEntitiesElement(
				"mendeley", 0.1, 0.1, "http://dbpedia.url.org");
		ContextNamedEntitie.organizations.add(org2);
		ContextNamedEntitiesElement pers1 = new ContextNamedEntitiesElement(
				"Michael Jackson", 0.1, 0.1, "http://dbpedia.url.org");
		ContextNamedEntitie.persons.add(pers1);
		ContextNamedEntitiesElement pers2 = new ContextNamedEntitiesElement(
				"Bill Clinton", 0.1, 0.1, "http://dbpedia.url.org");
		ContextNamedEntitie.persons.add(pers2);
		ContextNamedEntitiesElement top1 = new ContextNamedEntitiesElement(
				"Trees", 0.1, 0.1, "http://dbpedia.url.org");
		ContextNamedEntitie.topics.add(top1);
		ContextNamedEntitiesElement top2 = new ContextNamedEntitiesElement(
				"Animal", 0.1, 0.1, "http://dbpedia.url.org");
		ContextNamedEntitie.topics.add(top2);

		secureUserProfile.contextNamedEntities = ContextNamedEntitie;
		Language language1 = new Language("de", 0.1);
		Language language2 = new Language("en", 0.1);

		secureUserProfile.languages.add(language1);
		secureUserProfile.languages.add(language2);

		List<UserLocation> locationsList = new ArrayList<UserLocation>();
		locationsList.add(new UserLocation(33.123123, -127.123123, 4.5,
				new Date()));
		locationsList.add(new UserLocation(20.123123, -130.123123, 4.5,
				new Date()));
		secureUserProfile.userLocations = locationsList;

		return secureUserProfile;
	}



	// End Test Services

}

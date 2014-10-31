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

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.spi.resource.Singleton;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerBadgeList;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.Address;
import eu.eexcess.dataformats.userprofile.Context;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ContextNamedEntitiesElement;
import eu.eexcess.dataformats.userprofile.ContextNamedEntity;
import eu.eexcess.dataformats.userprofile.History;
import eu.eexcess.dataformats.userprofile.Interest;
import eu.eexcess.dataformats.userprofile.Language;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.dataformats.userprofile.UserCredentials;
import eu.eexcess.dataformats.userprofile.UserLocation;
import eu.eexcess.federatedrecommender.FederatedRecommenderCore;
import eu.eexcess.federatedrecommender.dataformats.D3GraphDocument;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

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
		URL resource = getClass().getResource(
				"/federatedRecommenderConfig.json");
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
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

		/*
		 * 
		 * TODO: Remove when full registration from partners is implemented.
		 */

		String europeana = null, mendeley = null, zbw = null, kimCollect = null, wissensserver = null;

		if (federatedRecommenderConfiguration.deploymentPlatform
				.equals("localStandalone")) {

			europeana = "http://localhost:8101/eexcess-partner-europeana-1.0-SNAPSHOT/partner/recommend/";

			mendeley = "http://localhost:8103/eexcess-partner-mendeley-1.0-SNAPSHOT/partner/recommend/";

			zbw = "http://localhost:8105/eexcess-partner-zbw-1.0-SNAPSHOT/partner/recommend/";

			kimCollect = "http://localhost:8102/eexcess-partner-kim-collect-1.0-SNAPSHOT/partner/recommend/";

			wissensserver = "http://localhost:8104/eexcess-partner-wissenmedia-1.0-SNAPSHOT/partner/recommend/";

		} else if (federatedRecommenderConfiguration.deploymentPlatform
				.equals("localTomcat")) {

			europeana = "http://localhost:8080/eexcess-partner-europeana-1.0-SNAPSHOT/partner/recommend/";

			mendeley = "http://localhost:8080/eexcess-partner-mendeley-1.0-SNAPSHOT/partner/recommend/";

			zbw = "http://localhost:8080/eexcess-partner-zbw-1.0-SNAPSHOT/partner/recommend/";

			kimCollect = "http://localhost:8080/eexcess-partner-kim-collect-1.0-SNAPSHOT/partner/recommend/";

			wissensserver = "http://localhost:8080/eexcess-partner-wissenmedia-1.0-SNAPSHOT/partner/recommend/";

		}

		else if (federatedRecommenderConfiguration.deploymentPlatform
				.equals("jrDev")) {

			europeana = "http://eexcess-dev.joanneum.at/eexcess-partner-europeana-1.0-SNAPSHOT/partner/recommend/";

			mendeley = "http://eexcess-dev.joanneum.at/eexcess-partner-mendeley-1.0-SNAPSHOT/partner/recommend/";

			zbw = "http://eexcess-dev.joanneum.at/eexcess-partner-zbw-1.0-SNAPSHOT/partner/recommend/";

			kimCollect = "http://eexcess-dev.joanneum.at/eexcess-partner-kim-collect-1.0-SNAPSHOT/partner/recommend/";

			wissensserver = "http://eexcess-dev.joanneum.at/eexcess-partner-wissenmedia-1.0-SNAPSHOT/partner/recommend/";

		}

		else if (federatedRecommenderConfiguration.deploymentPlatform
				.equals("jrStable")) {

			europeana = "http://eexcess.joanneum.at/eexcess-partner-europeana-1.0-SNAPSHOT/partner/recommend/";

			mendeley = "http://eexcess.joanneum.at/eexcess-partner-mendeley-1.0-SNAPSHOT/partner/recommend/";

			zbw = "http://eexcess.joanneum.at/eexcess-partner-zbw-1.0-SNAPSHOT/partner/recommend/";

			kimCollect = "http://eexcess.joanneum.at/eexcess-partner-kim-collect-1.0-SNAPSHOT/partner/recommend/";

			wissensserver = "http://eexcess.joanneum.at/eexcess-partner-wissenmedia-1.0-SNAPSHOT/partner/recommend/";

		}

		else {
			throw new Exception(
					"Problem deploying! Please use an appropriate deployment scenario in the config file (localStandalone, localTomcat, jrDev, jrStable)");
		}

		PartnerBadge badge = new PartnerBadge();
		badge = new PartnerBadge();
		badge.setSystemId("Europeana");
		badge.setEndpoint(europeana);
		badge.setTags(new ArrayList<String>() {
			private static final long serialVersionUID = -7498028779074331771L;
			{
				add("Europe");
				add("Culture");
			}
		});
		fRC.addPartner(badge);

		PartnerBadge badge2 = new PartnerBadge();
		badge2 = new PartnerBadge();
		badge2.setSystemId("Mendeley");
		badge2.setEndpoint(mendeley);
		badge2.setTags(new ArrayList<String>() {
			private static final long serialVersionUID = -4961167430016500063L;

			{
				add("Science");
				add("Journals");
			}
		});
		fRC.addPartner(badge2);

		PartnerBadge badge3 = new PartnerBadge();
		badge3.setSystemId("ZBW");
		badge3.setEndpoint(zbw);
		badge3.setTags(new ArrayList<String>() {
			private static final long serialVersionUID = 7970224907132161869L;
			{
				add("Economy");
				add("Articles");
			}
		});
		fRC.addPartner(badge3);

		PartnerBadge badge4 = new PartnerBadge();
		badge4.setSystemId("KIMCollect");
		badge4.setEndpoint(kimCollect);
		badge4.setTags(new ArrayList<String>() {
			private static final long serialVersionUID = -8047754475051394204L;

			{
				add("Swiss");
				add("Culture");
			}
		});
		fRC.addPartner(badge4);

		PartnerBadge badge5 = new PartnerBadge();
		badge5.setSystemId("Wissenmedia");
		badge5.setEndpoint(wissensserver);
		badge5.setTags(new ArrayList<String>() {
			private static final long serialVersionUID = -993773655268128609L;

			{
				add("Articles");
				add("Culture");
			}
		});
		fRC.addPartner(badge5);

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
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces(MediaType.WILDCARD)
	public Response registerPartner(PartnerBadge badge) throws IOException {
		if (badge.partnerKey != null)
			if (!badge.partnerKey.isEmpty())
				if (badge.partnerKey.length() < 20)
					return Response.serverError().build();
		fRC.addPartner(badge);
		return Response.ok().build();
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
		return fRC.generateSafeModeFederatedRecommendation(userProfile);
	}

	@GET
	@Path("/testBadge")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public PartnerBadge testBadge() {

		PartnerBadge pb = new PartnerBadge();
		pb.setSystemId("Europeana");
		pb.setEndpoint("http://DIGV536.joanneum.at/eexcess-partner-europeana-1.0-SNAPSHOT/partner/recommend/");
		pb.setDescription("Multi-lingual online collection of millions of digitized items from European museums, libraries, archives and multi-media collections");

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
		// secureUserProfile.partnerList.add(pB);

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

	@POST
	@Path("/viewGraph")
	@Consumes(value = MediaType.TEXT_XML)
	@Produces(MediaType.APPLICATION_JSON)
	public Response viewGraph(SecureUserProfile userProfile) throws IOException {
		D3GraphDocument d3GraphDocument;
		try {
			d3GraphDocument = fRC.getGraph(userProfile);
		} catch (FederatedRecommenderException e) {
			return Response.serverError().build();
		}
		return Response.ok(d3GraphDocument).build();
	}

	// End Test Services

}

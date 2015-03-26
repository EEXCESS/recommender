//package eu.eexcess.mendeley.recommender;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URL;
//import java.util.Date;
//
//import net.sf.json.JSONObject;
//
//import org.apache.commons.lang.StringUtils;
//import org.codehaus.jackson.JsonParser;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.w3c.dom.Document;
//
//import com.sun.jersey.api.client.Client;
//
//import eu.eexcess.config.PartnerConfiguration;
//import eu.eexcess.dataformats.result.ResultList;
//import eu.eexcess.dataformats.userprofile.ContextKeyword;
//import eu.eexcess.dataformats.userprofile.Interest;
//import eu.eexcess.dataformats.userprofile.SecureUserProfile;
//import eu.eexcess.partnerdata.api.ITransformer;
//import eu.eexcess.partnerdata.reference.PartnerdataLogger;
//
//public class PartnerConnectorTest {
//	
//    private Client client;
//    private PartnerConfiguration partnerConfiguration;
//	
//	@Before
//    public void setUp() throws Exception {
//        client = Client.create();
//        // client.addFilter(new LoggingFilter());
//        client.setFollowRedirects(false);
//        
//        URL resource = getClass().getResource("/partner-config.json");
//        ObjectMapper mapper = new ObjectMapper();
//    	mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
//    	partnerConfiguration = mapper.readValue(new File(resource.getFile()), PartnerConfiguration.class);
//    }
//
//    @After
//    public void tearDown() {
//        client.destroy();
//    }
//    
//    @Test
//    public void testGetAccessTokenResponse() throws IOException {
//    	PartnerConnector connector = new PartnerConnector();
//    	AccessTokenResponse accessTokenResponse = connector.getAccessToken(client, partnerConfiguration);
//    	assertTrue(StringUtils.isNotBlank(accessTokenResponse.getAccessToken()));
//    	assertEquals("bearer", accessTokenResponse.getTokenType());
//    	assertEquals(3600, accessTokenResponse.getExpiresIn());
//    }
//    
//    @Test
//    public void testAnonymousAccessTokenForOapiDocumentDetails() throws IOException {
//    	PartnerConnector connector = new PartnerConnector();
//    	AccessTokenResponse accessTokenResponse = connector.getAccessToken(client, partnerConfiguration);
//    	
//    	String canonicalId = "bc3b10e6-83f7-3203-ac05-703f58b10088";
//    	JSONObject document = connector.fetchDocumentDetails(client, canonicalId, accessTokenResponse);
//    	assertTrue(document.has("title"));
//    }
//    
//    @Test
//    public void testAnonymousAccessTokenForOapiSearchDocuments() throws Exception {
//        PartnerConnector connector = new PartnerConnector();
//    	AccessTokenResponse accessTokenResponse = connector.getAccessToken(client, partnerConfiguration);
//
//    	SecureUserProfile profile = getUserProfile();
//    	JSONObject searchResults = connector.fetchSearchResults(client, profile, accessTokenResponse, partnerConfiguration);
//        assertTrue(searchResults.getJSONArray("documents").size() > 0);
//    }
//    
//    @Test
//    public void testQueryPartner() throws Exception {    	
//        ITransformer transformer = (ITransformer)Class.forName(partnerConfiguration.transformerClass).newInstance();       
//        transformer.init(partnerConfiguration);
//
//    	SecureUserProfile profile = getUserProfile();
//
//        PartnerConnector connector = new PartnerConnector();
//        Document searchResultsNative = connector.queryPartner(partnerConfiguration, profile);
//        Document searchResultsEexcess = transformer.transform(searchResultsNative, new PartnerdataLogger(partnerConfiguration));
//        
//        ResultList recommendations = transformer.toResultList(searchResultsNative, searchResultsEexcess, new PartnerdataLogger(partnerConfiguration));
//        assertTrue(recommendations.totalResults > 0); 
//    }
//    
//    private SecureUserProfile getUserProfile() {
//    	SecureUserProfile profile = new SecureUserProfile();
//        profile.firstName = "Hugo";
//        profile.lastName = "Boss";
//        profile.birthDate = new Date(System.currentTimeMillis()-30*365*24*60*60*1000);
//        
//        profile.interestList.add(new Interest("Machine Learning"));
//        profile.interestList.add(new Interest("Information Retrieval"));
//        profile.interestList.add(new Interest("Natural Language Processing"));
//        profile.contextKeywords.add(new ContextKeyword("higgs"));
//        profile.contextKeywords.add(new ContextKeyword("boson"));
//        return profile;
//    }
//}

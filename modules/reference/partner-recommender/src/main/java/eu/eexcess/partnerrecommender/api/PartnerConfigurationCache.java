package eu.eexcess.partnerrecommender.api;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.partnerdata.api.ITransformer;
import eu.eexcess.partnerdata.reference.Enrichment;
import eu.eexcess.partnerrecommender.api.keys.PartnerApiKeys;
import eu.eexcess.partnerrecommender.reference.PartnerRegistrationThread;

/**
 * Enum to cache objects that have not to be reloaded every startup
 * 
 * @author hziak
 *
 */
public enum PartnerConfigurationCache {
    CONFIG;
    private final transient Logger LOGGER;
    private boolean intializedFlag;
    private PartnerConfiguration partnerConfiguration;
    private transient PartnerConnectorApi partnerConnector;
    private transient ITransformer transformer;
    private transient Enrichment enricher;
    private transient Client clientJacksonJson;
    private transient Client clientJAXBContext;
    private transient Client clientDefault;
    private transient ObjectMapper objectMapper;
    private transient Map<String, QueryGeneratorApi> queryGeneratorMapping = new HashMap<String, QueryGeneratorApi>();
    private transient QueryGeneratorApi defaultQueryGen;
    private transient Thread regThread;

    private PartnerConfigurationCache() {

        this.LOGGER = Logger.getLogger(PartnerConfigurationCache.class.getName());
        this.objectMapper = new ObjectMapper();
        ClientConfig configDefault = new DefaultClientConfig();
        ClientConfig configJacksonJson = new DefaultClientConfig();
        ClientConfig configJAXBContext = new DefaultClientConfig();
        configJacksonJson.getClasses().add(JacksonJsonProvider.class);
        configJAXBContext.getClasses().add(JAXBContext.class);
        clientJacksonJson = Client.create(configJacksonJson);
        clientJAXBContext = Client.create(configJAXBContext);
        clientDefault = Client.create(configDefault);

        ObjectMapper mapper = new ObjectMapper();
        try {

            /*
             * Read partner configuration file
             */

            URL resource = getClass().getResource("/partner-config.json");
            mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
            partnerConfiguration = mapper.readValue(new File(resource.getFile()), PartnerConfiguration.class);
            /*
             * Read global partner key file
             */
            String eexcessPartnerKeyFile = System.getenv("EEXCESS_PARTNER_KEY_FILE");
            LOGGER.log(Level.INFO, "Reading Api Keys from: " + eexcessPartnerKeyFile);
            PartnerApiKeys partnerKeys = null;
            if (eexcessPartnerKeyFile == null) {
                LOGGER.log(Level.INFO, "Environment variable \"EEXCESS_PARTNER_KEY_FILE\" for " + partnerConfiguration.getSystemId() + " should be set");
            } else {
                partnerKeys = mapper.readValue(new File(eexcessPartnerKeyFile), PartnerApiKeys.class);
            }

            if (partnerKeys != null)
                for (PartnerConfiguration badge : partnerKeys.getPartners()) {
                    setPartnerConfigParamters(badge);
                }

            /*
             * Configure the partner connector
             */

            partnerConnector = (PartnerConnectorApi) Class.forName(partnerConfiguration.getPartnerConnectorClass()).newInstance();

            /*
             * Configure data transformer
             */
            if (!partnerConfiguration.isTransformedNative()) {
                transformer = (ITransformer) Class.forName(partnerConfiguration.getTransformerClass()).newInstance();
                transformer.init(partnerConfiguration);

                /*
                 * Configure data enricher
                 */

                enricher = new Enrichment();
                enricher.init(partnerConfiguration);
            }

        } catch (Exception e) {
            if (partnerConfiguration != null)
                LOGGER.log(Level.WARNING, "Cannot initialize enrichment service for " + partnerConfiguration.getSystemId() + " recommender", e);
        }
        try {
            QueryGeneratorApi queryGen = (QueryGeneratorApi) Class.forName(partnerConfiguration.getQueryGeneratorClass()).newInstance();
            this.defaultQueryGen = queryGen;
            queryGeneratorMapping.put(partnerConfiguration.getQueryGeneratorClass(), queryGen);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
            LOGGER.log(Level.SEVERE, "Cannot initialize query generator for " + partnerConfiguration.getSystemId() + " recommender", e1);
        }
    }

    private void setPartnerConfigParamters(PartnerConfiguration badge) {
        if (partnerConfiguration.getSystemId().equals(badge.getSystemId())) {
            if (badge.getApiKey() != null) {
                partnerConfiguration.setApiKey(badge.getApiKey());
            }
            if (badge.getUserName() != null) {
                partnerConfiguration.setUserName(badge.getUserName());
            }
            if (badge.getPassword() != null) {
                partnerConfiguration.setPassword(badge.getPassword());
            }
        }
    }

    public void registerPartnerAtServer() {
        LOGGER.log(Level.INFO, "Starting Partner Helper Thread:" + PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getSystemId());
        this.regThread = new Thread(new PartnerRegistrationThread(partnerConfiguration));
        regThread.setName(PartnerConfigurationCache.CONFIG.partnerConfiguration.getSystemId() + " Registration Thread");
        regThread.start();
    }

    public void unregisterPartnerAtServer() {
        regThread.interrupt();
        PartnerBadge badge = PartnerConfigurationCache.CONFIG.getBadge();
        DefaultClientConfig jClientconfig = new DefaultClientConfig();
        jClientconfig.getClasses().add(JacksonJsonProvider.class);
        Client client = new Client(new URLConnectionClientHandler(), jClientconfig);
        WebResource service = client.resource(partnerConfiguration.getFederatedRecommenderURI() + "unregister");
        LOGGER.log(Level.INFO, "Unregistering Partner: " + badge.getSystemId() + " at " + partnerConfiguration.getFederatedRecommenderURI());
        Builder builder = service.accept(MediaType.APPLICATION_JSON);
        builder.type(MediaType.APPLICATION_JSON).post(PartnerBadge.class, badge);
    }

    public PartnerConfiguration getPartnerConfiguration() {
        return partnerConfiguration;
    }

    public PartnerConnectorApi getPartnerConnector() {
        return partnerConnector;
    }

    public ITransformer getTransformer() {
        return transformer;
    }

    public Enrichment getEnricher() {
        return enricher;
    }

    public void setEnricher(Enrichment enricher) {
        this.enricher = enricher;
    }

    // TODO: hziak + rrubien: check for optimization, client could be configured
    // here aswell
    public Client getClientJacksonJson() {
        return clientJacksonJson;
    }

    // TODO: hziak + rrubien: check for optimization, client could be configured
    // here aswell
    public Client getClientJAXBContext() {
        return clientJAXBContext;
    }

    // TODO: hziak + rrubien: check for optimization, client could be configured
    // here aswell
    public Client getClientDefault() {
        return clientDefault;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * returns the query generator, if none is given it returns the default
     * 
     * @param queryGen
     * @return
     */
    public QueryGeneratorApi getQueryGenerator(String queryGen) {
        if (queryGen == null) {
            return this.defaultQueryGen;
        }
        QueryGeneratorApi returnGen = queryGeneratorMapping.get(queryGen);
        if (returnGen == null) {
            try {
                QueryGeneratorApi queryGenerator = (QueryGeneratorApi) Class.forName(queryGen).newInstance();
                queryGeneratorMapping.put(queryGen, queryGenerator);
                return queryGenerator;
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
                LOGGER.log(Level.SEVERE, "Cannot load query generation class: " + queryGen + " for partner " + partnerConfiguration.getSystemId(), e1);
            }
            return this.defaultQueryGen;
        } else
            return returnGen;
    }

    public PartnerBadge getBadge() {
        return partnerConfiguration;
    }

    public boolean getIntializedFlag() {
        return intializedFlag;
    }

    public void setIntializedFlag(boolean intializedFlag) {
        this.intializedFlag = intializedFlag;
    }

}

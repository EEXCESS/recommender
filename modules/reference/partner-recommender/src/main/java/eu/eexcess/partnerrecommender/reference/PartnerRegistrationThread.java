package eu.eexcess.partnerrecommender.reference;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;

public class PartnerRegistrationThread implements Runnable{
	private static final long timeout = 2000000;
	private static final Logger logger = Logger.getLogger(PartnerRegistrationThread.class.getName());
	private PartnerConfiguration config;

	public PartnerRegistrationThread(PartnerConfiguration config) {
		this.config=config;
	}
	
	@Override
	public void run() {
		
		PartnerBadge badge =PartnerConfigurationCache.CONFIG.getBadge();
		DefaultClientConfig jClientconfig = new DefaultClientConfig();
		jClientconfig.getClasses().add(JacksonJsonProvider.class);
		
		Client client = new Client( new URLConnectionClientHandler(), jClientconfig);
		WebResource service = client.resource(config.federatedRecommenderURI+"register");
		Builder builder = service.accept(MediaType.WILDCARD);
		
		while(true){
		try {
			logger.log(Level.INFO,"Registering Partner: "+badge.getSystemId()+" at "+ config.federatedRecommenderURI+"register with endpoint "+badge.getPartnerConnectorEndpoint() +" thread name:" +Thread.currentThread().getName());
			
			builder.type(MediaType.APPLICATION_JSON).post( badge);
			} 
		catch (com.sun.jersey.api.client.UniformInterfaceException|com.sun.jersey.api.client.ClientHandlerException e) {
				logger.log(Level.SEVERE,"Could not register client "+badge.getSystemId()+ " at "+PartnerConfigurationCache.CONFIG.getPartnerConfiguration().federatedRecommenderURI +" retrying in "+timeout+"ms",e);
			}
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
				logger.log(Level.SEVERE,"Timout for RegistrationThreat for Partner  "+badge.getSystemId()+" could not be set",e);	
		}
			
		}
	}

}

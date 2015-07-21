package eu.eexcess.partnerwebservice;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;

/**
 * ContextListener to start the partner registration thread
 * 
 * @author hziak
 *
 */
public class PartnerContextListener implements ServletContextAttributeListener, ServletContextListener {
    private static final Logger logger = Logger.getLogger(PartnerContextListener.class.getName());

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        PartnerConfigurationCache.CONFIG.unregisterPartnerAtServer();
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        logger.log(Level.INFO, "Starting Partner Helper Thread:" + PartnerConfigurationCache.CONFIG.getPartnerConfiguration().getSystemId());
        PartnerConfigurationCache.CONFIG.registerPartnerAtServer();
    }

    @Override
    public void attributeAdded(ServletContextAttributeEvent arg0) {
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent arg0) {
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent arg0) {
    }

}

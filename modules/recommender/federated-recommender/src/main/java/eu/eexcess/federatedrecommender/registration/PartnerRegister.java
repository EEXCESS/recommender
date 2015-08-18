/* Copyright (C) 2014 
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensees holding valid Know-Center Commercial licenses may use this file in
accordance with the Know-Center Commercial License Agreement provided with 
the Software or, alternatively, in accordance with the terms contained in
a written agreement between Licensees and Know-Center.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.eexcess.federatedrecommender.registration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.IOUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import eu.eexcess.dataformats.PartnerBadge;

/**
 * Registration for the partners PartnerRecommenderApis
 * 
 * @author hziak
 *
 */
@XmlRootElement(name = "eexcess-registered-partners")
public class PartnerRegister {
    private static final Logger LOGGER = Logger.getLogger(PartnerRegister.class.getName());
    @XmlElement(name = "partners")
    private List<PartnerBadge> partners = new ArrayList<PartnerBadge>();
    private Map<String, Client> partnerToClient = new HashMap<String, Client>();
    private Map<String, byte[]> favIconCache = new HashMap<String, byte[]>();

    public List<PartnerBadge> getPartners() {
        synchronized (partners) {
            return partners;
        }

    }

    public void addPartner(PartnerBadge badge) {
        synchronized (partners) {
            checkAndFetchFavIcon(badge);
            partners.add(badge);
            DefaultClientConfig config = new DefaultClientConfig();
            Client client = Client.create(config); // new Client(null, config);
            partnerToClient.put(badge.getSystemId(), client);
        }
    }

    /**
     * Checks if the favicon url is avaiable and it was allready fetched before
     * 
     * @param badge
     */

    private void checkAndFetchFavIcon(PartnerBadge badge) {
        if (badge.getFavIconURI() != null && favIconCache.get(badge.getFavIconURI()) == null) {
            DefaultClientConfig config = new DefaultClientConfig();
            Client favClient = Client.create(config);
            WebResource resource = favClient.resource(badge.getFavIconURI());
            InputStream favIconInputStream = resource.get(InputStream.class);

            try {
                favIconCache.put(badge.getSystemId(), IOUtils.toByteArray(favIconInputStream));
                favIconInputStream.close();

            } catch (IOException e) {
                LOGGER.log(Level.INFO, "could not retrieve favicon from partner", e);
            }
        }
    }

    public synchronized void removePartner(PartnerBadge badge) {
        synchronized (partners) {
            partners.remove(badge);
            favIconCache.remove(badge.getSystemId());
        }
    }

    public synchronized Client getClient(String systemID) {
        return partnerToClient.get(systemID);

    }

    /**
     * returns the favIconChache that hold the inputStream of the partners
     * favIcons
     * 
     * @return
     */
    public Map<String, byte[]> getFavIconCache() {
        return favIconCache;
    }

}

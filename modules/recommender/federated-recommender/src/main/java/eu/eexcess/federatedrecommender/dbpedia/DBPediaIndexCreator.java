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
package eu.eexcess.federatedrecommender.dbpedia;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

/**
 * Class to create the solrIndex with the given Dbpedia mapping files at the
 * given solr index uri
 * 
 * @author hziak
 *
 */
public class DBPediaIndexCreator {
    private static final String USAGEMESSAGE = "Usage: \n" + "-s <SolrServerUri>   (e.g. http://localhost:8983/solr/)\n"
            + "-i <DbpediaMappingBasedFile i> .... <DbpediaMappingBasedFile n>\n"
            + "Note: For example use the Dbpedia files named mappingbased_properties_en.nt or mappingbased_properties_en_uris_de.nt \n"
            + "mappingbased_properties_en.nt should always be used as basefile and first file in the index.\n"
            + "For other languages then english and german adaptation of the SolrSchema should be done";
    private static final Logger logger = Logger.getLogger(DBPediaIndexCreator.class.getName());

    private DBPediaIndexCreator() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            logger.log(Level.INFO, USAGEMESSAGE);
            return;
        }
        int solrServerUriIndex = -1;
        int dbPediaBaseFilesIndex = -1;
        for (int i = 0; i < args.length; i++) {
            if ("-s".equals(args[i]))
                solrServerUriIndex = i;
            else if ("-i".equals(args[i]))
                dbPediaBaseFilesIndex = i;
        }

        String solrServerUri = null;
        if (solrServerUriIndex >= 0 && solrServerUriIndex + 1 < args.length)
            solrServerUri = args[solrServerUriIndex + 1];
        if (solrServerUri == null) {
            logger.log(Level.SEVERE, "Solr server uri not found \n " + USAGEMESSAGE);
            return;
        }
        FederatedRecommenderConfiguration configuration = new FederatedRecommenderConfiguration();
        configuration.solrServerUri = solrServerUri;
        List<String> dbPediaFileList = null;
        if (dbPediaBaseFilesIndex >= 0 && dbPediaBaseFilesIndex + 1 < args.length) {
            if (dbPediaBaseFilesIndex < solrServerUriIndex)
                dbPediaFileList = Arrays.asList(args).subList(dbPediaBaseFilesIndex + 1, solrServerUriIndex);
            else
                dbPediaFileList = Arrays.asList(args).subList(dbPediaBaseFilesIndex + 1, args.length);
        }

        DbPediaSolrIndex dbPediaSolrIndex = new DbPediaSolrIndex(configuration);
        if (dbPediaFileList != null) {

            for (int j = 0; j < dbPediaFileList.size(); j++) {
                try {
                    dbPediaSolrIndex.createIndex(dbPediaFileList.get(j), j * 30000000, true); /*
                                                                                               * 40000000
                                                                                               * to
                                                                                               * not
                                                                                               * override
                                                                                               * old
                                                                                               * entry
                                                                                               * 's
                                                                                               * with
                                                                                               * the
                                                                                               * same
                                                                                               * id
                                                                                               * !
                                                                                               */
                } catch (FederatedRecommenderException e) {
                    logger.log(Level.WARNING, "", e);
                }
            }

        } else {
            logger.log(Level.INFO, "No files to index, just doing reference counting");
        }

        try {
            dbPediaSolrIndex.createReferenceCounts();
        } catch (FederatedRecommenderException e) {
            logger.log(Level.SEVERE, "", e);

        }

    }

}

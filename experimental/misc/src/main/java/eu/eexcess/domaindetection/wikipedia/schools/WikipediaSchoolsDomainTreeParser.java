/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Raoul Rubien
 */

package eu.eexcess.domaindetection.wikipedia.schools;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.eexcess.logger.PianoLogger;

public class WikipediaSchoolsDomainTreeParser extends IndexWriterRessource {

    private static final Logger LOGGER = PianoLogger.getLogger(WikipediaSchoolsDomainTreeParser.class);
    private static final String DOMAIN_TREE_ENTRYPOINT = "wp/index/subject.htm";
//    private String domainTreeRootSite;

    public WikipediaSchoolsDomainTreeParser(String wikiForSchoolsRoot) {
//        domainTreeRootSite = wikiForSchoolsRoot;
    }

    /**
     * The first command line argument specifies the root folder of an extracted
     * Wikipedia for Schools dump.
     * 
     * @param args
     *            args[0]='/home/hugo/.../wikipedia-schools-3.0.2/'
     */
    public static void main(String[] args) {
        if (args.length >= 1 && new File(args[0]).isDirectory()) {
            try (WikipediaSchoolsDomainTreeParser domainTreeParser = new WikipediaSchoolsDomainTreeParser(args[0] + DOMAIN_TREE_ENTRYPOINT)) {
                domainTreeParser.open();
                domainTreeParser.run();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "error during parsing", e);
            }
        } else {
            LOGGER.warning("no valid Wikipedia for Schools root specified");
        }
    }

    private void run() {
    }
}

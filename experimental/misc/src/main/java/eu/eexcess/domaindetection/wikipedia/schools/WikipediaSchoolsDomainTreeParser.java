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
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.eexcess.logger.PianoLogger;

public class WikipediaSchoolsDomainTreeParser extends IndexWriterRessource {

    private static final Logger LOGGER = PianoLogger.getLogger(WikipediaSchoolsDomainTreeParser.class);
    private static final String DOMAIN_TREE_ENTRYPOINT_DIRECTORY = "wp/index/";
    private static final String DOMAIN_TREE_SITE_PREFIX = "subject.";
    private static final String DOMAIN_TREE_SITE_SUFFIX = ".htm";
//    private static final String DOMAIN_TREE_SITE_SEPARATOR = ".";

    // private String domainTreeRootSite;

    public WikipediaSchoolsDomainTreeParser(File outIndexPath) {
        super(outIndexPath);
    }

    /**
     * The first command line argument specifies the root folder of an extracted
     * Wikipedia for Schools dump.
     * 
     * @param args
     *            args[0]='/home/hugo/.../wikipedia-schools-3.0.2/'
     */
    public static void main(String[] args) {
        try {
            File tempDir = File.createTempFile("wikipedia-schools-domains-index-" + WikipediaSchoolsDumpIndexer.class.getSimpleName(), "");
            if (tempDir.delete() && tempDir.mkdir()) {
                buildTree(tempDir, args);
            } else {
                LOGGER.severe("failed to perform indexing: unable to crate folder [" + tempDir.getCanonicalPath() + "]");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "failed to perform indexing: unable to crate folder", e);
        }
    }

    private static void buildTree(File tempDir, String[] args) {
        try (WikipediaSchoolsDomainTreeParser parser = new WikipediaSchoolsDomainTreeParser(tempDir)) {
            long timestamp = System.currentTimeMillis();
            parser.run(args);
            LOGGER.info("total duration [" + (System.currentTimeMillis() - timestamp) + "]ms");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "error during indexing", e);
            return;
        }
    }

    private void run(String[] args) throws IOException {
        if (args.length > 0) {
            Path domainTreeRoot = FileSystems.getDefault().getPath(args[0] + DOMAIN_TREE_ENTRYPOINT_DIRECTORY);
            collectDomainFiles(domainTreeRoot);

            // TODO
            // parse domains (subjects) from filename
            // build the domain tree
            // store relations to index
            // but also create a csv or sth. that explains clearly the tree
            // replace " " by "_" of domains in dump indexer
        } else {
            throw new IOException("no surce path given");
        }
    }

    private List<File> collectDomainFiles(Path domainTreeRoot) throws IOException {
        Set<String> whiteList = new HashSet<String>();
        whiteList.add(DOMAIN_TREE_SITE_SUFFIX);
        Set<String> blackList = new HashSet<String>();
        List<Path> matchedFiles = new ArrayList<Path>();
        WikipediaSchoolsDumpIndexer.collectPathFiles(domainTreeRoot, whiteList, blackList, matchedFiles);

        List<File> domainFiles = new ArrayList<File>();
        for (Iterator<Path> iterator = matchedFiles.iterator(); iterator.hasNext();) {
            File file = new File(iterator.next().toString());
            if (file.isFile()) {
                if (file.getName().startsWith(DOMAIN_TREE_SITE_PREFIX)) {
                    domainFiles.add(file);
                    LOGGER.info("memorized file [" + file.getCanonicalFile() + "] for later processing");
                }
            }
        }
        return domainFiles;
    }

}

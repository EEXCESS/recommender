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

package eu.eexcess.federatedrecommender.config;

import org.apache.lucene.util.Version;

import java.io.File;

/**
 * this class keeps settings about local resources (indices, files, etc. needed
 * for running unit tests on this package
 * 
 * @author Raoul Rubien
 *
 */
public class Settings {

    public static final Version LuceneVersion = Version.LATEST;
    public static final double RamBufferSizeMB = 512.0;
    /**
     * index containing all TREC data
     */
    public static TestIndexSettings BaseIndex = new TestIndexSettings("generalized-base", "/opt/data/trec-uncompressed", "/opt/data/redde/base-index",
            "/opt/data/redde/base-index-sapled", null);

    public static boolean isResourceAvailable(TestIndexSettings settings) {

        if (!(new File(settings.documentsPath)).canRead()) {
            return printWarning(settings.documentsPath);
        }
        return true;
    }

    public static boolean isWordNet20ResourceAvailable() {
        if (!(new File(WordNet.Path_2_0)).canRead()) {
            return printWarning(WordNet.Path_2_0);
        }
        return true;
    }

    public static boolean isWordNet30ResourceAvailable() {
        if (!(new File(WordNet.Path_3_0)).canRead()) {
            return printWarning(WordNet.Path_3_0);
        }
        return true;
    }

    public static boolean isWordNetDomainsResourceAvailable() {
        if (!(new File(WordnetDomains.Path)).canRead()) {
            return printWarning(WordnetDomains.Path);
        } else if (!(new File(WordnetDomains.CSVDomainPath)).canRead()) {
            return printWarning(WordnetDomains.CSVDomainPath);
        }
        return true;
    }

    private static boolean printWarning(String path) {
        System.err.println("resource does not exist [" + path + "]");
        return false;
    }

    public static class TestIndexSettings {

        public String testSetName;
        public String documentsPath;
        public String baseIndexPath;
        public String sampledIndexPath;
        public int topicNumbers[];

        public TestIndexSettings(String testSetName, String documentsPath, String baseIndexPath, String sampleIndexPath, int topics[]) {
            this.testSetName = testSetName;
            this.documentsPath = documentsPath;
            this.baseIndexPath = baseIndexPath;
            this.sampledIndexPath = sampleIndexPath;
            this.topicNumbers = topics;
        }
    }

    public static class WordNet {
        public static final String Path_2_0 = "/opt/data/wordnet/WordNet-2.0/dict";
        public static final String Path_3_0 = "/opt/data/wordnet/WordNet-3.0/dict";
    }

    public static class WordnetDomains {
        public static final String Path = "/opt/data/wordnet-domains/wn-domains-3.2/wn-domains-3.2-20070223";
        public static final String CSVDomainPath = "/opt/data/wordnet-domains/wn-domains-3.2/wn-domains-3.2-tree.csv";
    }

}

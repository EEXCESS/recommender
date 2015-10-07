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

package eu.eexcess.sourceselection.redde.indexer.topterm;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.sf.extjwnl.JWNLException;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopScoreDocCollector;

import eu.eexcess.federatedrecommender.utils.tree.BaseTreeNode;
import eu.eexcess.federatedrecommender.utils.tree.NodeInspector;
import eu.eexcess.federatedrecommender.utils.tree.ValueSetTreeNode;

public class DBDomainSampler extends TopTermToWNDomain {

    public static class SampleArguments {
        /**
         * result index name
         */
        public String indexName;
        /**
         * terms to use for sampling
         */
        public Set<ValueSetTreeNode<String>> sampleDomains;
    }

    public static class WordNetArguments {
        public String wordnetPath;
        public String wordnetDomainsPath;
        public String wordnetDomainCsvTreePath;
    }

    private ValueSetTreeNode<String> domainToTermsTree;

    /**
     * Creates an instance of this class.
     * 
     * @param sourceIndexPath
     *            path to base lucene index
     * @param arguments
     *            parameters where samples are being stored and what domains to
     *            use for sampling them
     * @throws IOException
     */
    DBDomainSampler(String baseIndexPath, WordNetArguments wnArgs) throws IOException, JWNLException {
        super(baseIndexPath, wnArgs.wordnetPath, wnArgs.wordnetDomainsPath, wnArgs.wordnetDomainCsvTreePath);
    }

    public ValueSetTreeNode<String> alignTerms(int fromTopTermIndex, int toTopTermIndex) throws Exception {
        domainToTermsTree = super.assignToDomains(fromTopTermIndex, toTopTermIndex);
        return domainToTermsTree;
    }

    public void sample(Set<SampleArguments> sampleArgs) throws IllegalStateException, ParseException, IOException {

        if (domainToTermsTree == null) {
            throw new IllegalStateException("no terms aligned");
        }

        for (SampleArguments subSample : sampleArgs) {

            // merge requested domain-terms
            Set<String> terms = distinctUnifyValues(subSample.sampleDomains);

            // sample with domain-term dependent query
            String queryString = String.join("", terms);

            Query query = new QueryParser(DBDomainSampler.fieldOfInterest, new EnglishAnalyzer()).parse(queryString);
            TopScoreDocCollector collector = TopScoreDocCollector.create(1000, false);
            new IndexSearcher(inIndexReader).search(query, collector);
            // ScoreDoc[] docs = collector.topDocs().scoreDocs;
            // TODO: create and store docs to new index called subSample.name
        }
        throw new UnsupportedOperationException("not implemented yet");
    }

    Set<String> distinctUnifyValues(Set<ValueSetTreeNode<String>> trees) {
        final Set<String> unified = new HashSet<String>();

        NodeInspector<String> operator = (n) -> {
            for (String value : ((ValueSetTreeNode<String>) n).getValues()) {
                unified.add(value);
            }
            return false;
        };
        for (ValueSetTreeNode<String> tree : trees) {
            BaseTreeNode.depthFirstTraverser(tree, operator);
        }
        return unified;
    }
}

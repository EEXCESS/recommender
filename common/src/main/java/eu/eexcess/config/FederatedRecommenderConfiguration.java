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
package eu.eexcess.config;

public class FederatedRecommenderConfiguration {

    private String solrServerUri;
    private int graphHitsLimitPerQuery;
    private int graphQueryDepthLimit;
    private int graphMaxPathLength;
    private String wikipediaIndexDir; // directory of the wikipedia index
    private String evaluationQueriesFile; // file path to the queries file for
                                          // evaluation
    private long partnersTimeout;
    private int numRecommenderThreads;
    private String statsLogDatabase;
    private String defaultPickerName;
    private String[] sourceSelectors;
    private String wordnetPath;
    private String wordnetDomainFilePath;

    public String getSolrServerUri() {
        return solrServerUri;
    }

    public void setSolrServerUri(String solrServerUri) {
        this.solrServerUri = solrServerUri;
    }

    public int getGraphHitsLimitPerQuery() {
        return graphHitsLimitPerQuery;
    }

    public void setGraphHitsLimitPerQuery(int graphHitsLimitPerQuery) {
        this.graphHitsLimitPerQuery = graphHitsLimitPerQuery;
    }

    public int getGraphQueryDepthLimit() {
        return graphQueryDepthLimit;
    }

    public void setGraphQueryDepthLimit(int graphQueryDepthLimit) {
        this.graphQueryDepthLimit = graphQueryDepthLimit;
    }

    public int getGraphMaxPathLength() {
        return graphMaxPathLength;
    }

    public void setGraphMaxPathLength(int graphMaxPathLength) {
        this.graphMaxPathLength = graphMaxPathLength;
    }

    public String getWikipediaIndexDir() {
        return wikipediaIndexDir;
    }

    public void setWikipediaIndexDir(String wikipediaIndexDir) {
        this.wikipediaIndexDir = wikipediaIndexDir;
    }

    public String getEvaluationQueriesFile() {
        return evaluationQueriesFile;
    }

    public void setEvaluationQueriesFile(String evaluationQueriesFile) {
        this.evaluationQueriesFile = evaluationQueriesFile;
    }

    public long getPartnersTimeout() {
        return partnersTimeout;
    }

    public void setPartnersTimeout(long partnersTimeout) {
        this.partnersTimeout = partnersTimeout;
    }

    public int getNumRecommenderThreads() {
        return numRecommenderThreads;
    }

    public void setNumRecommenderThreads(int numRecommenderThreads) {
        this.numRecommenderThreads = numRecommenderThreads;
    }

    public String getStatsLogDatabase() {
        return statsLogDatabase;
    }

    public void setStatsLogDatabase(String statsLogDatabase) {
        this.statsLogDatabase = statsLogDatabase;
    }

    public String getDefaultPickerName() {
        return defaultPickerName;
    }

    public void setDefaultPickerName(String defaultPickerName) {
        this.defaultPickerName = defaultPickerName;
    }

    public String[] getSourceSelectors() {
        return sourceSelectors;
    }

    public void setSourceSelectors(String[] sourceSelectors) {
        this.sourceSelectors = sourceSelectors;
    }

    public String getWordnetPath() {
        return wordnetPath;
    }

    public void setWordnetPath(String wordnetPath) {
        this.wordnetPath = wordnetPath;
    }

    public String getWordnetDomainFilePath() {
        return wordnetDomainFilePath;
    }

    public void setWordnetDomainFilePath(String wordnetDomainFilePath) {
        this.wordnetDomainFilePath = wordnetDomainFilePath;
    }

}

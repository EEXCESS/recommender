/**
 * Copyright (C) 2014
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
 */

package eu.eexcess.wikipedialocal.recommender;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.w3c.dom.Document;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.DocumentBadgeList;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerdata.reference.PartnerdataLogger;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.api.PartnerConnectorApi;
import eu.eexcess.partnerrecommender.reference.PartnerConnectorBase;

/**
 * Implementation for a Local Wikipedia index
 * 
 * @author Hermann Ziak
 */
public class PartnerConnector extends PartnerConnectorBase implements PartnerConnectorApi {

	private static final Logger logger = Logger.getLogger(PartnerConnector.class.getName());


	private static final String[] FIELD_CONTENTS = {"sectionText","sectionTitle","title"};

	
	private PartnerConfiguration partnerConfig = null;


	
	
	public PartnerConnector() {
		
		
	}

	@Override
	public ResultList queryPartnerNative(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger dataLogger)
					throws IOException {
		partnerConfig = partnerConfiguration;
		ResultList resultList = new ResultList();
		
		Analyzer analyzer = new EnglishAnalyzer();
		File directoryPath = new File(PartnerConfigurationCache.CONFIG.getPartnerConfiguration().searchEndpoint);
		Directory directory = FSDirectory.open(directoryPath );
		IndexReader indexReader = IndexReader.open(directory );
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		QueryParser queryParser = new MultiFieldQueryParser(FIELD_CONTENTS,analyzer);
		queryParser.setDefaultOperator(Operator.AND);
		String queryString = PartnerConfigurationCache.CONFIG.getQueryGenerator(partnerConfiguration.queryGeneratorClass).toQuery(userProfile);
		Query query = null;
		try {
			query = queryParser.parse(queryString);
		} catch (ParseException e) {
			
			//logger.log(Level.SEVERE, "could not parse input query", e);
		}
		if(userProfile.numResults==null)
			userProfile.numResults=10;
		TopDocs topDocs = indexSearcher.search(query, userProfile.numResults);
		for (ScoreDoc sDocs : topDocs.scoreDocs) {
			Result result = new Result();
			result.documentBadge=new DocumentBadge("", "", PartnerConfigurationCache.CONFIG.getBadge().systemId);
			org.apache.lucene.document.Document  doc=indexSearcher.doc(sDocs.doc);
			if(doc!=null){
				IndexableField title = doc.getField("title");
				IndexableField sectionTitle = doc.getField("sectionTitle");
				IndexableField category = doc.getField("category");
				IndexableField sectionText = doc.getField("sectionText");
				if(sectionText!=null)
					result.description= sectionText.stringValue();
				if(category!=null){
				}
				if(title!=null && sectionTitle!=null)				
					result.title = title.stringValue() +" - " + sectionTitle.stringValue();
				resultList.results.add(result );
			}
		}
		
		
		resultList.totalResults=topDocs.totalHits;
		return resultList;
	}

	@Override
	public Document queryPartner(PartnerConfiguration partnerConfiguration, SecureUserProfile userProfile, PartnerdataLogger logger)
					throws IOException {

		return null;
	}

	@Override
	public Document queryPartnerDetails(
			PartnerConfiguration partnerConfiguration,
			DocumentBadge document, PartnerdataLogger logger)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	

	

}

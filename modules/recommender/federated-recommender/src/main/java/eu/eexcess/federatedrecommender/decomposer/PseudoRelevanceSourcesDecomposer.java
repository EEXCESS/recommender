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
package eu.eexcess.federatedrecommender.decomposer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.HighFreqTerms.DocFreqComparator;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import eu.eexcess.config.FederatedRecommenderConfiguration;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.dataformats.userprofile.SecureUserProfileEvaluation;
import eu.eexcess.federatedrecommender.FederatedRecommenderCore;
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations;
import eu.eexcess.federatedrecommender.interfaces.SecureUserProfileDecomposer;
import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;

/**
 * Query Expansion based on pseudo relevance from the own sources
 * 
 * @author hziak
 *
 */

public class PseudoRelevanceSourcesDecomposer implements
		SecureUserProfileDecomposer<SecureUserProfile,SecureUserProfileEvaluation> {
	private static final Logger logger = Logger
			.getLogger(PseudoRelevanceSourcesDecomposer.class.getName());

	

	/*
	 * gets all results from the partners and puts the filtered top terms in the secureuserprofile
	 * if queryExpansionSourcePartner is not empty than these partners are used for the expansion
	 * @see eu.eexcess.federatedrecommender.interfaces.SecureUserProfileDecomposer#decompose(eu.eexcess.dataformats.userprofile.SecureUserProfile)
	 */
	@Override
	public SecureUserProfile decompose(SecureUserProfileEvaluation inputSecureUserProfile) {
		FederatedRecommenderCore fCore = null;
		
		try {
			fCore = FederatedRecommenderCore.getInstance(null);
		} catch (FederatedRecommenderException e) {
			logger.log(
					Level.SEVERE,
					"Error getting FederatedRecommenderCore,was perhabs not initialized correctly",
					e);
		}
		Set<String> keywords = new HashSet<String>();
		for (ContextKeyword cKeyword : inputSecureUserProfile.contextKeywords) {
			keywords.add(cKeyword.text);
		}
		//	tmpSUP.partnerList = inputSecureUserProfile.queryExpansionSourcePartner;
		List<PartnerBadge> tmpPartnerList = new ArrayList<PartnerBadge>();
		for(PartnerBadge partnerBadge:inputSecureUserProfile.partnerList){
			tmpPartnerList.add(partnerBadge);
		}
		inputSecureUserProfile.partnerList= inputSecureUserProfile.queryExpansionSourcePartner;
		PartnersFederatedRecommendations pFR = fCore.getPartnersRecommendations(inputSecureUserProfile);
		inputSecureUserProfile.partnerList=tmpPartnerList;
		
		Directory directory = new RAMDirectory();

		Analyzer analyzer = new StopAnalyzer(Version.LUCENE_48);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_48,
				analyzer);
		IndexWriter writer = null;

		try {
			writer = new IndexWriter(directory, config);
			for (ResultList resultLists : pFR.getResults().values()) {
				for (Result result : resultLists.results) {
					addDoc(writer, result.description);
					addDoc(writer, result.title);
				}
			}

			writer.close();

			IndexReader reader = DirectoryReader.open(directory);
			TermStats[] tStats = null;
			try {
				tStats = HighFreqTerms.getHighFreqTerms(reader, 20, "content",
						new DocFreqComparator());
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Could not open HighFreqTerms", e);
			} finally {
				reader.close();
			}
			if(tStats!=null){
			for (TermStats termStats : tStats) {
				String utf8String = termStats.termtext.utf8ToString();
				if (utf8String.length() > 4)
					if(!checkHighFreqTermsQuery(utf8String.toLowerCase(),keywords))
					if (keywords.add(utf8String.toLowerCase())) {
							inputSecureUserProfile.contextKeywords
									.add(new ContextKeyword(utf8String,
											termStats.docFreq / 100.0,ExpansionType.EXPANSION));
					}
			}
			}
			else logger.log(Level.SEVERE,"TermStats was null!");
		} catch (IOException e) {
			logger.log(Level.SEVERE,
					"There was and error writing/reading the Index", e);
		}

		logger.log(Level.INFO, "Source   Expansion: " + keywords.toString() +" Partners: "+inputSecureUserProfile.queryExpansionSourcePartner);
		return inputSecureUserProfile;
	}
	/**
	 * checks if the term contains parts of the query
	 * @param term
	 * @param keywords
	 * @return
	 */
	private boolean checkHighFreqTermsQuery(String term,
			Set<String> keywords) {
		for (String keyword : keywords) {
			if(keyword.toLowerCase().contains(term.toLowerCase()))
					return true;
			if(term.toLowerCase().contains(keyword.toLowerCase()))
					return true;
		}
		return false;
	}
	/**
	 * adds documents to the index
	 * @param writer
	 * @param content
	 * @throws IOException
	 */
	private static void addDoc(IndexWriter writer, String content)
			throws IOException {
		if (content != null) {
			FieldType fieldType = new FieldType();
			fieldType.setStoreTermVectors(true);
			fieldType.setStoreTermVectorPositions(true);
			fieldType.setIndexed(true);
			fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
			fieldType.setStored(true);
			Document doc = new Document();
			doc.add(new Field("content", content, fieldType));
			writer.addDocument(doc);
		}
	}
	@Override
	public void setConfiguration(FederatedRecommenderConfiguration fedRecConfig)
			throws FederatedRecommenderException {
		logger.log(Level.INFO,"Nothing todo with FederatedRecommenderConfiguration, not needed.");
	}

	
	
	

}

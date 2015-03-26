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
package eu.eexcess.federatedrecommender.evaluation.picker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.update.processor.TextProfileSignature;
import org.omg.CORBA.ExceptionList;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.evaluation.EvaluationResultList;
import eu.eexcess.dataformats.evaluation.EvaluationResultLists;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.dataformats.PFRChronicle;
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations;
import eu.eexcess.federatedrecommender.decomposer.SerendiptiyDecomposer;
import eu.eexcess.federatedrecommender.interfaces.PartnersFederatedRecommendationsPicker;

/**
 * BlockPicker implementation for evaluation Combines several results from
 * different algorithms into one resultlist. Each block is in equal if possible
 * depending on the numResults
 * 
 * @author hziak
 *
 */
public class BlockPicker implements PartnersFederatedRecommendationsPicker {
	private static final Logger logger = Logger.getLogger(BlockPicker.class.getName());
	final private  String basic = "Basic";
	final private  String diversity = "Diversity";
	final private String serendiptiy = "Serendipity";
	final private ModifiableSolrParams params =new ModifiableSolrParams();
	@Override
	public ResultList pickResults(PFRChronicle pFRChronicle, int numResults) {
		// TODO Auto-generated method stub
		return null;
	}

	void getTopResults(SecureUserProfile secureUserProfile,
			EvaluationResultList list, int numResults, ResultList result,
			Integer totalResults) {
		params.set("quantRate", (int) 0.02f);
	    params.set("minTokenLen", 3);
		ArrayList<Result> results = new ArrayList<Result>();
		
		for (int i = 0; i < numResults && i < list.results.size()
				&& result.results.size() < totalResults; i++) {
			
			Result o = list.results.get(i);
			boolean found = false;
			byte[] signNewResult= getFuzzyHashSignature(o);
			for (Result selectedResult : result.results) {
				if(Arrays.equals(signNewResult, getFuzzyHashSignature(selectedResult))){
					found=true;
					break;
				}
			}
			if (!found) //TODO: Fuzzy Hash should be uses here!
				result.results.add(o); 
			else {
				numResults++; // leaving one out -> increasing num results
				totalResults++;
			}
		}
	}

	private byte[] getFuzzyHashSignature(Result o) {
		TextProfileSignature tPSignatur =  new TextProfileSignature();
		
		
		tPSignatur.init(params );
		if(o.description!=null)
			tPSignatur.add(o.description);
		else if(o.title!=null){
			tPSignatur.add(o.title);
			if(o.previewImage!=null)
				tPSignatur.add(o.previewImage);
		}
		return tPSignatur.getSignature();
	}

	/**
	 * FiFo Picker
	 */
	@Override
	public ResultList pickResults(SecureUserProfile secureUserProfile,
			PartnersFederatedRecommendations resultList,
			List<PartnerBadge> partners, int numResults) {
		ResultList result = new ResultList();
		for (int i = 0; i <= numResults; i++)
			for (PartnerBadge partnerBadge : partners) {
				if (resultList.getResults().get(partnerBadge) != null)
					if (resultList.getResults().get(partnerBadge).results
							.size() > 0) {
						result.results.add(resultList.getResults().get(
								partnerBadge).results.get(0));
						resultList.getResults().get(partnerBadge).results
								.remove(0);
					}
			}

		return result;
	}

	/**
	 * generated the block based list, first block is first list in the
	 * {@link EvaluationResultLists} numBlocks is num lists in
	 * {@link EvaluationResultLists} if results/block not is even (10/3) then we
	 * add one to the first block (but also checking if every block has enough
	 * results)
	 * 
	 * @param resultLists
	 * @param numResults
	 * @param partners
	 * @return
	 * @throws Exception
	 *             in case of too small result lists
	 */
	public EvaluationResultList pickBlockResults(
			EvaluationResultLists resultLists, Integer numResults)
			throws Exception {
		
		Integer size = resultLists.results.size();
		if(size==null){
			size=10;
		}
	
		EvaluationResultList basicList=null;
		EvaluationResultList diversityList=null;
		EvaluationResultList serendipityList=null;
		if(size<3)
		logger.log(Level.INFO, "At least one block will be missing in the result list");
		int unknownCounter=0;
		for (EvaluationResultList list : resultLists.results) {
			switch (list.provider) {
			case basic:
				basicList = list;
				break;
			case diversity:
				diversityList = list;
				break;
			case serendiptiy:
				serendipityList = list;
				break;

			default:
				unknownCounter++;
				break;
			}
		}
		if(unknownCounter>0){
		logger.log(Level.WARNING, "There was at least one unrecognized block (num:"+unknownCounter+")");
			size-=unknownCounter;
		}
		
		Double numResultsPerBlock = (((double) numResults) / size);
		Double basicResultListBlock = numResultsPerBlock;
		if (resultLists.results.getFirst() != null) {
			double nRPBMO = numResultsPerBlock % 1;
			if (nRPBMO > 0.0) {
				basicResultListBlock = 1.0 - nRPBMO + numResultsPerBlock;
				numResultsPerBlock = numResultsPerBlock - nRPBMO;

				while (basicResultListBlock + (numResultsPerBlock * (size - 1)) < numResults) {
					if ((basicResultListBlock + (numResultsPerBlock + 1)
							* (size - 1)) <= numResults)
						numResultsPerBlock += 1;
					else if (basicResultListBlock + 1 <= resultLists.results
							.getFirst().results.size())
						basicResultListBlock += 1;
					else
						numResultsPerBlock += 1;
				}
			}
		}
		Double serendipityResultListBlock= numResultsPerBlock;
		if(serendipityList!=null){
			if(serendipityList.results.size()<serendipityResultListBlock){
				basicResultListBlock = basicResultListBlock + (serendipityResultListBlock-serendipityList.results.size()); 
				serendipityResultListBlock=(double) serendipityList.results.size();
			}
		}
		else
			serendipityResultListBlock=0.0;
		
		Double diversityResultListBlock = numResultsPerBlock;
		if(diversityList!=null){
			if(diversityList.results.size()<diversityResultListBlock){
				basicResultListBlock = basicResultListBlock + (diversityResultListBlock-diversityList.results.size()); 
				diversityResultListBlock=(double) diversityList.results.size();
			}
		}else
			diversityResultListBlock=0.0;
		if(basicList!=null)
			if(basicList.results.size()<basicResultListBlock){
				basicResultListBlock= (double) basicList.results.size();
		}
		EvaluationResultList resultList = new EvaluationResultList(
				new ResultList());
		resultList.totalResults=(int) (basicResultListBlock+diversityResultListBlock+serendipityResultListBlock);
		resultList.provider = "BlockPicker (";
		int counter = 0;
		while (counter < size) {
			
			EvaluationResultList list=null;
			switch (counter) {
			case 0:
				list=basicList;
				numResultsPerBlock=basicResultListBlock;
				break;
			case 1:
				list=diversityList;
				numResultsPerBlock=diversityResultListBlock;
				break;
			case 2:
				list=serendipityList;
				numResultsPerBlock=serendipityResultListBlock;
				break;

			default:
				
				break;
			}
			counter++;
			if(list!=null){
				
				if (resultList.results.isEmpty()){
					resultList.provider += list.provider+ "("+basicResultListBlock.intValue()+"),";
				}
				else {
					 resultList.provider +=list.provider+ "("+numResultsPerBlock.intValue()+")";
					 if(list.provider!=serendiptiy)
							resultList.provider +=  ",";
				}
				getTopResults(new SecureUserProfile(), list,
						numResultsPerBlock.intValue(), resultList,
						numResults);
		
			}else size++;
		}
		resultList.provider += ")";

		return resultList;
	}

}

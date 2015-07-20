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

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.evaluation.EvaluationResultList;
import eu.eexcess.dataformats.evaluation.EvaluationResultLists;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.dataformats.PFRChronicle;
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations;
import eu.eexcess.federatedrecommender.interfaces.PartnersFederatedRecommendationsPicker;

/**
 * BlockPicker implementation for evaluation Combines several results from
 * different algorithms into one resultlist. Each block is in equal if possible
 * depending on the numResults
 * 
 * @author hziak
 *
 */
public class BlockPicker extends PartnersFederatedRecommendationsPicker {

    private static final Logger LOGGER = Logger.getLogger(BlockPicker.class.getName());
    private static final String BASIC = "Basic";
    private static final String DIVERSITY = "Diversity";
    private static final String SERENDIPITY = "Serendipity";

    public BlockPicker() {
        super();
    }

    @Override
    public ResultList pickResults(PFRChronicle pFRChronicle, int numResults) {
        return null;
    }

    /**
     * returnes the top results and checks with a fuzzyHash
     * 
     * @param resultsToAdd
     * @param numResultsToAdd
     * @param finalResultList
     * @param totalResults
     */
    void getTopResults(ResultList resultsToAdd, int numResultsToAdd, ResultList finalResultList, Integer totalResults) {
        for (int i = 0; i < numResultsToAdd && i < resultsToAdd.results.size() && finalResultList.results.size() < totalResults; i++) {
            boolean found = false;
            Result o = resultsToAdd.results.get(i);
            byte[] signNewResult = getFuzzyHashSignature(o);
            for (Result selectedResult : finalResultList.results) {
                if (Arrays.equals(signNewResult, getFuzzyHashSignature(selectedResult))) {
                    found = true;
                    break;
                }
            }
            if (!found)
                finalResultList.results.add(o);
            else {
                numResultsToAdd++; // leaving one out -> increasing num results
                totalResults++;
            }
        }
    }

    /**
     * FiFo Picker
     */
    @Override
    public ResultList pickResults(SecureUserProfile secureUserProfile, PartnersFederatedRecommendations resultList, List<PartnerBadge> partners, int numResults) {
        ResultList result = new ResultList();
        for (int i = 0; i <= numResults; i++)
            for (PartnerBadge partnerBadge : partners) {
                if (resultList.getResults().get(partnerBadge) != null && !resultList.getResults().get(partnerBadge).results.isEmpty()) {
                    result.results.add(resultList.getResults().get(partnerBadge).results.get(0));
                    resultList.getResults().get(partnerBadge).results.remove(0);
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
    public EvaluationResultList pickBlockResults(EvaluationResultLists resultLists, Integer numResults) throws Exception {

        Integer blocks = resultLists.results.size();
        EvaluationResultList basicList = null;
        EvaluationResultList diversityList = null;
        EvaluationResultList serendipityList = null;
        if (blocks < 3)
            LOGGER.log(Level.INFO, "At least one block will be missing in the result list");
        int unknownCounter = 0;
        for (EvaluationResultList list : resultLists.results) {
            switch (list.provider) {
            case BASIC:
                basicList = list;
                break;
            case DIVERSITY:
                diversityList = list;
                break;
            case SERENDIPITY:
                serendipityList = list;
                break;

            default:
                unknownCounter++;
                break;
            }
        }
        if (unknownCounter > 0) {
            LOGGER.log(Level.WARNING, "There was at least one unrecognized block (num:" + unknownCounter + ")");
            blocks -= unknownCounter;
        }
        int maxSize = 0;
        if (serendipityList != null)
            maxSize += serendipityList.results.size();
        if (diversityList != null)
            maxSize += diversityList.results.size();
        if (basicList != null)
            maxSize += basicList.results.size();
        if (maxSize < numResults)
            numResults = maxSize;
        EvaluationResultList resultList = new EvaluationResultList(splitAndGetResultsFromBlockLists(resultLists, numResults, blocks, basicList, diversityList, serendipityList));

        return resultList;
    }

    private ResultList splitAndGetResultsFromBlockLists(EvaluationResultLists resultLists, Integer numResults, Integer numBlocks, EvaluationResultList basicList,
            EvaluationResultList diversityList, EvaluationResultList serendipityList) {
        int basicCount = 0;
        int diversityCount = 0;
        int serendipityCount = 0;
        ResultList resultList = new ResultList();
        int basicSize = 0;
        if (basicList != null)
            basicSize = basicList.results.size();
        int diversitySize = 0;
        if (diversityList != null)
            diversitySize = diversityList.results.size();
        int serendipitySize = 0;
        if (serendipityList != null)
            serendipitySize = serendipityList.results.size();

        while ((basicCount + diversityCount + serendipityCount) < numResults) {

            if (basicSize > basicCount) {
                basicCount += 1;
                if (basicCount + diversityCount + serendipityCount >= numResults)
                    break;
            }

            if (diversitySize > diversityCount) {
                diversityCount += 1;
                if (basicCount + diversityCount + serendipityCount >= numResults)
                    break;
            }
            if (serendipitySize > serendipityCount) {
                serendipityCount += 1;
                if (basicCount + diversityCount + serendipityCount >= numResults)
                    break;
            }
            int allTogether = basicSize + diversitySize + serendipitySize;
            if ((diversityList == null || diversityList.results.isEmpty()) && (diversityList == null || diversityList.results.isEmpty())
                    && (basicList == null || basicList.results.isEmpty()) || (allTogether < numResults && allTogether == basicCount + diversityCount + serendipityCount))
                break;
        }
        resultList = assignAndGetTopResults(numBlocks, basicList, diversityList, serendipityList, resultList, basicCount, diversityCount, serendipityCount, numResults);
        resultList.totalResults = resultList.results.size();
        return resultList;
    }

    /**
     * assigns calculated amount of documents to the lists and retrieves the top
     * results {@link BlockPicker.getTopResults()}
     * 
     * @param numResults
     * @param blockCount
     * @param basicList
     * @param diversityList
     * @param serendipityList
     * @param numResultsPerBlock
     * @param basicResultListBlock
     * @param serendipityResultListBlock
     * @param diversityResultListBlock
     * @param resultList
     * @param serendipityCount
     * @param diversityCount
     * @param basicCount
     * @param numResults
     */
    private ResultList assignAndGetTopResults(Integer blockCount, ResultList basicList, ResultList diversityList, ResultList serendipityList, ResultList resultList,
            int basicCount, int diversityCount, int serendipityCount, Integer numResults) {
        resultList.provider = "BlockPicker (";
        int counter = 0;
        int numListResultsToAdd = 0;
        while (counter < blockCount) {

            ResultList resultListToAdd = null;
            switch (counter) {
            case 0:
                if (basicList != null && !basicList.results.isEmpty()) {
                    resultListToAdd = basicList;
                    numListResultsToAdd = basicCount;
                    break;
                } else {
                    counter++;
                    blockCount++;
                }
            case 1:
                if (diversityList != null && !diversityList.results.isEmpty()) {
                    resultListToAdd = diversityList;
                    numListResultsToAdd = diversityCount;
                    break;
                } else {
                    counter++;
                    blockCount++;
                }
            case 2:
                if (serendipityList != null) {
                    resultListToAdd = serendipityList;
                    numListResultsToAdd = serendipityCount;
                    break;
                }

            default:
                break;
            }
            counter++;
            if (resultListToAdd != null) {

                resultList.provider += resultListToAdd.provider + "(" + numListResultsToAdd + ")";
                if (resultListToAdd.provider != SERENDIPITY)
                    resultList.provider += ",";
                getTopResults(resultListToAdd, numListResultsToAdd, resultList, numResults);

            } else
                blockCount++;
        }
        resultList.provider += ")";
        return resultList;
    }

}

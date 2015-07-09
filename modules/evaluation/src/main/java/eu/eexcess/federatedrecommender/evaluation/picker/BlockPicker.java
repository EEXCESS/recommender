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
    public BlockPicker() {
        super();
    }

    private static final Logger logger = Logger.getLogger(BlockPicker.class.getName());
    final private String basic = "Basic";
    final private String diversity = "Diversity";
    final private String serendiptiy = "Serendipity";

    @Override
    public ResultList pickResults(PFRChronicle pFRChronicle, int numResults) {
        // TODO Auto-generated method stub
        return null;
    }

    void getTopResults(SecureUserProfile secureUserProfile, EvaluationResultList list, int numResults, ResultList result, Integer totalResults) {

        // ArrayList<Result> results = new ArrayList<Result>();

        for (int i = 0; i < numResults && i < list.results.size() && result.results.size() < totalResults; i++) {
            boolean found = false;
            Result o = list.results.get(i);
            byte[] signNewResult = getFuzzyHashSignature(o);
            for (Result selectedResult : result.results) {
                if (Arrays.equals(signNewResult, getFuzzyHashSignature(selectedResult))) {
                    found = true;
                    break;
                }
            }
            if (!found)
                result.results.add(o);
            else {
                numResults++; // leaving one out -> increasing num results
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
                if (resultList.getResults().get(partnerBadge) != null)
                    if (resultList.getResults().get(partnerBadge).results.size() > 0) {
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
            logger.log(Level.INFO, "At least one block will be missing in the result list");
        int unknownCounter = 0;
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
        if (unknownCounter > 0) {
            logger.log(Level.WARNING, "There was at least one unrecognized block (num:" + unknownCounter + ")");
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
        EvaluationResultList resultList = new EvaluationResultList(splitAndGetResultsFromBlockLists(resultLists, numResults, blocks, basicList, diversityList,
                serendipityList));

        return resultList;
    }

    private ResultList splitAndGetResultsFromBlockLists(EvaluationResultLists resultLists, Integer numResults, Integer numBlocks,
            EvaluationResultList basicList, EvaluationResultList diversityList, EvaluationResultList serendipityList) {
        //
        //
        // if (basicList == null || basicList.results.isEmpty()) {
        // numBlocks--;
        // basicResultListBlock = 0.0;
        // }
        // if (diversityList == null || diversityList.results.isEmpty()) {
        // numBlocks--;
        // diversityResultListBlock = 0.0;
        // }
        //
        // if (serendipityList == null || serendipityList.results.isEmpty()) {
        // numBlocks--;
        // serendipityResultListBlock = 0.0;
        // }
        // Double numResultsPerBlock = (((double) numResults) / numBlocks);
        // basicResultListBlock = assignTemporaryBlockSize(basicList,
        // basicResultListBlock, numResultsPerBlock);
        // diversityResultListBlock = assignTemporaryBlockSize(diversityList,
        // diversityResultListBlock, numResultsPerBlock);
        // serendipityResultListBlock =
        // assignTemporaryBlockSize(serendipityList, serendipityResultListBlock,
        // numResultsPerBlock);
        //
        // while (basicResultListBlock + diversityResultListBlock +
        // serendipityResultListBlock < numResults && numResultsPerBlock % 1 >
        // 0.0) {
        // double basicTempSize = assignTemporaryBlockSize(basicList,
        // basicResultListBlock, numResultsPerBlock + 1);
        // if (basicTempSize > basicResultListBlock) {
        // basicResultListBlock = basicTempSize;
        // }
        //
        // }

        ResultList selectedBasicList = new ResultList();
        if (basicList != null)
            selectedBasicList.provider = basicList.provider;
        ResultList selectedDiversityList = new ResultList();
        if (diversityList != null)
            selectedDiversityList.provider = diversityList.provider;
        ResultList selectedSerendipityList = new ResultList();
        if (serendipityList != null)
            selectedSerendipityList.provider = serendipityList.provider;
        ResultList resultList = new ResultList();
        while (selectedBasicList.results.size() + selectedDiversityList.results.size() + selectedSerendipityList.results.size() < numResults) {
            if (popAndAddsListElement(numResults, basicList, selectedBasicList, selectedDiversityList.results.size() + selectedSerendipityList.results.size()))
                break;
            if (popAndAddsListElement(numResults, diversityList, selectedDiversityList,
                    selectedSerendipityList.results.size() + selectedBasicList.results.size()))
                break;
            if (popAndAddsListElement(numResults, serendipityList, selectedSerendipityList,
                    selectedDiversityList.results.size() + selectedBasicList.results.size()))
                break;
        }
        resultList = assignAndGetTopResults(numBlocks, selectedBasicList, selectedDiversityList, selectedSerendipityList, resultList);
        resultList.totalResults = resultList.results.size();
        return resultList;
    }

    private Boolean popAndAddsListElement(Integer numResults, EvaluationResultList originList, ResultList selectedList, Integer otherListsElementCount) {
        if (originList != null && !originList.results.isEmpty()) {
            if ((selectedList.results.size() + otherListsElementCount) == numResults)
                return true;
            selectedList.results.add(originList.results.pop());

        }
        return false;

    }

    /**
     * assigns the themporary block size and checks if size is not higher the
     * number of avaiable results
     * 
     * @param basicList
     * @param basicResultListBlock
     * @param numResultsPerBlock
     * @return
     */
    private Double assignTemporaryBlockSize(EvaluationResultList basicList, Double basicResultListBlock, Double numResultsPerBlock) {
        if (basicResultListBlock == null)
            if (basicList.results.size() > numResultsPerBlock)
                basicResultListBlock = numResultsPerBlock;
            else
                basicResultListBlock = (double) basicList.results.size();
        return basicResultListBlock;
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
     */
    private ResultList assignAndGetTopResults(Integer blockCount, ResultList basicList, ResultList diversityList, ResultList serendipityList,
            ResultList resultList) {
        resultList.provider = "BlockPicker (";
        int counter = 0;
        int numListResults = 0;
        while (counter < blockCount) {

            ResultList list = null;
            switch (counter) {
            case 0:
                if (basicList != null && !basicList.results.isEmpty()) {
                    list = basicList;
                    numListResults = basicList.results.size();
                    break;
                } else
                    counter++;
            case 1:
                if (diversityList != null && !diversityList.results.isEmpty()) {
                    list = diversityList;
                    numListResults = diversityList.results.size();
                    break;
                } else
                    counter++;
            case 2:
                if (serendipityList != null) {
                    list = serendipityList;
                    numListResults = serendipityList.results.size();
                    break;
                }

            default:
                break;
            }
            counter++;
            if (list != null) {

                resultList.provider += list.provider + "(" + numListResults + ")";
                if (list.provider != serendiptiy)
                    resultList.provider += ",";

                resultList.results.addAll(list.results);
            } else
                blockCount++;
        }
        resultList.provider += ")";
        return resultList;
    }

}

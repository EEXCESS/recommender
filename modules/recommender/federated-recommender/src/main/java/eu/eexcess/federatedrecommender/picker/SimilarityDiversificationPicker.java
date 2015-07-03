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
package eu.eexcess.federatedrecommender.picker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.dataformats.PFRChronicle;
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations;
import eu.eexcess.federatedrecommender.dataformats.ResultSimilarity;
import eu.eexcess.federatedrecommender.interfaces.PartnersFederatedRecommendationsPicker;

/**
 * Similar to suggested algorithm in
 * "Improving recommendation lists through topic diversification" Ziegler et al
 * 
 * @author hziak
 */
public class SimilarityDiversificationPicker extends PartnersFederatedRecommendationsPicker {

    /**
     * thetaF is a value between 0.0 and 1.0 larger values favor diversification
     * smaller values favor the original result sorting
     */
    private Double thetaF;
    private Comparator<ResultSimilarity> simResultComperator = new Comparator<ResultSimilarity>() {
        @Override
        public int compare(ResultSimilarity o1, ResultSimilarity o2) {
            if (o1.getSimilarity() < o2.getSimilarity())
                return -1;
            if (o1.getSimilarity() > o2.getSimilarity())
                return 1;
            return 0;
        }
    };

    public SimilarityDiversificationPicker(Double thetaF) {
        this.thetaF = thetaF;
    }

    /**
     * Function not implemented
     */
    @Override
    public ResultList pickResults(PFRChronicle pFRChronicle, int numResults) {

        return null;
    }

    @Override
    public ResultList pickResults(SecureUserProfile secureUserProfile, PartnersFederatedRecommendations resultList, List<PartnerBadge> partners, int numResults) {
        ResultList results = new ResultList();
        Set<PartnerBadge> keys = resultList.getResults().keySet();

        int valuesSize = 0;
        for (ResultList rL : resultList.getResults().values()) {
            valuesSize += rL.results.size();
        }
        int resultSize = results.results.size();
        while (resultSize < numResults && (resultSize < valuesSize)) {
            for (PartnerBadge partnerBadge : keys) {
                ResultList partnerResultList = resultList.getResults().get(partnerBadge);
                if (partnerResultList != null)
                    if (partnerResultList.results != null)
                        if (partnerResultList.results.isEmpty()) {
                            results.results.add(partnerResultList.results.get(0));
                            partnerResultList.results.remove(0);
                            resultSize = results.results.size();
                        }
            }
        }
        return diversify(results);
    }

    private ResultList diversify(ResultList resultList) {
        ResultList diversifiedResults = new ResultList();
        List<ResultSimilarity> tmpResult = new ArrayList<ResultSimilarity>();
        for (Result result : resultList.results) {
            tmpResult.add(sim(result, tmpResult));
        }
        Collections.sort(tmpResult, simResultComperator);

        for (ResultSimilarity resultSimilarity : tmpResult) {
            diversifiedResults.results.add((Result) resultSimilarity);
        }
        return diversifiedResults;
    }

    /**
     * Calculates the similarity between the given result and the already chosen
     * results in tmpResult
     * 
     * @param result
     * @param tmpResult
     * @return
     */
    private ResultSimilarity sim(Result result, List<ResultSimilarity> tmpResult) {
        ResultSimilarity resultWithSim = new ResultSimilarity(result);
        double similarity = 0.0;
        for (ResultSimilarity resultSimilarity : tmpResult) {
            similarity += resultSimilarity.calcSimilarity(result);
        }
        similarity += similarity / tmpResult.size();
        resultWithSim.setSimilarity(similarity - thetaF);
        return resultWithSim;
    }

}

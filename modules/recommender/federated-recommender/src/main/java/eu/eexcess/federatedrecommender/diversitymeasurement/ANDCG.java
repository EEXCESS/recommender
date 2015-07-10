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
package eu.eexcess.federatedrecommender.diversitymeasurement;

import java.util.ArrayList;
import java.util.List;

import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;

/**
 * a-nDCG[1] diversity measurement
 * 
 * [1] Novelty and Diversity in Information Retrieval Evaluation Charles L. A.
 * Clarke Maheedhar Kolla Gordon V. Cormack Olga Vechtomova Azin Ashkan Stefan
 * Büttcher Ian MacKinnon
 * 
 * @author hziak
 * 
 */
public class ANDCG {
    /**
     * calculates the initial gain vector based on the judgment if the document
     * contains the actual query and the alpha value.
     * 
     * @param numElements
     * @param alpha
     * @param results
     * @param nuggets
     * @return
     */
    private List<Double> calculateGainVector(int numElements, double alpha, List<Result> results, List<String> nuggets) {
        List<Double> gainVector = new ArrayList<Double>(numElements);
        for (int x = 0; x < numElements; x++) {
            Double k = 0.0;
            for (int i = 0; i < x; i++) {
                k += nuggetJudge(nuggets, results.get(i)) * Math.pow(1.0 - alpha, sumNuggedJuge(nuggets, results, i - 1)); // modification:
                // nugget
                // is
                // given
                // to
                // the
                // judge
                // aswell

            }
            gainVector.add(k);
        }
        return gainVector;
    }

    private int sumNuggedJuge(List<String> nuggets, List<Result> results, int k) {
        int sum = 0;
        for (int i = 0; i <= k; i++) {
            sum += nuggetJudge(nuggets, results.get(i));
        }
        return sum;
    }

    /**
     * returns 1 if the judge finds the "nugget" in the document else returns 0
     * 
     * @param string
     * @param i
     * @return
     */
    private int nuggetJudge(List<String> nuggets, Result result) {
        for (String nugget : nuggets) {
            if (result.title.toLowerCase().contains(nugget.toLowerCase()))
                return 1;
            if (result.description.toLowerCase().contains(nugget.toLowerCase()))
                return 1;
        }
        return 0;
    }

    /**
     * DCG[k] = Sum (j=1 to K) G[j]/ (log2 (1 + j)).
     * 
     * @return
     */
    private List<Double> calculateDiscountedCumulativGain(List<Double> gainVector) {
        List<Double> dCG = new ArrayList<Double>();
        for (int k = 0; k < gainVector.size(); k++) {
            Double dCGK = 0.0;
            for (int i = 0; i <= k; i++) {
                if (i < 2) {
                    dCGK += gainVector.get(i);
                } else {
                    double to = 1.0 + i;
                    double log1 = Math.log(to);
                    double log = log1 / Math.log(2.0);
                    double test = gainVector.get(i) / log;
                    dCGK += test;
                }
            }
            dCG.add(dCGK);
        }

        return dCG;
    }

    /**
     * Calculates the NormalizedDiscountCumulativGain for the given result list
     * and the given "nuggets" were the "nuggets" represent the query terms. If
     * alpha (between 0.1 and 1.0) is higher diversity is rewared more. (0.5
     * seems to be a good choice )
     * 
     * @param alpha
     * @param documents
     * @param nuggets
     * @return
     */
    public List<Double> calculate(double alpha, ResultList documents, List<String> nuggets) {

        List<Double> gainVector = calculateGainVector(documents.results.size(), alpha, documents.results, nuggets);
        List<Double> discountedCumulativGain = calculateDiscountedCumulativGain(gainVector);
        return normalizedDiscountCumulativGain(discountedCumulativGain);
    }

    /**
     * 
     * @param discountedCumulativGain
     * @return
     */
    private List<Double> normalizedDiscountCumulativGain(List<Double> discountedCumulativGain) {
        // TODO: add function
        return discountedCumulativGain;

    }

}

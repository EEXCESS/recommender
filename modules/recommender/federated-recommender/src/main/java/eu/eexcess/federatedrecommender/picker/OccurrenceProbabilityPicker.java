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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.Interest;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.dataformats.PFRChronicle;
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations;
import eu.eexcess.federatedrecommender.interfaces.PartnersFederatedRecommendationsPicker;

/**
 * Picks results based on the occurrence probability of the query in the results
 * 
 * @author hziak
 *
 */
public class OccurrenceProbabilityPicker extends PartnersFederatedRecommendationsPicker {

    private static final Logger logger = Logger.getLogger(OccurrenceProbabilityPicker.class.getName());

    public OccurrenceProbabilityPicker() {
        super();
    }

    @Override
    public ResultList pickResults(PFRChronicle pFRChronicle, int numResults) {
        List<PartnersFederatedRecommendations> pfrc = pFRChronicle.getChronicle();

        SortableValueMap<Result, Integer> results = new SortableValueMap<Result, Integer>();
        for (PartnersFederatedRecommendations partnersFederatedRecommendations : pfrc) {
            Collection<ResultList> keys = partnersFederatedRecommendations.getResults().values();
            for (ResultList resultList : keys) {
                for (Result result : resultList.results)
                    if (results.containsKey(result))
                        results.put(result, results.get(result) + 1);
                    else
                        results.put(result, 1);
            }

        }
        results.sortByValue();
        List<Result> resTmpList = new ArrayList<Result>();
        if (results.keySet().size() > numResults)
            for (int i = 0; i < results.keySet().size() && i < numResults; i++) {
                resTmpList.add((Result) results.keySet().toArray()[i]);
            }
        else
            resTmpList.addAll(results.keySet());

        ResultList resultList = new ResultList();
        resultList.results.addAll(resTmpList);
        return resultList;
    }

    /**
     * Pick results out of a single result list trys to reweight the results
     * 
     * @param secureUserProfile
     * 
     * @param resultList
     * @param partners
     * @param numResults
     */
    @Override
    public ResultList pickResults(SecureUserProfile secureUserProfile, PartnersFederatedRecommendations resultList, List<PartnerBadge> partners, int numResults) {
        ResultList returnedResults = new ResultList();
        List<PartnerBadge> listToDraw = new ArrayList<PartnerBadge>();
        for (PartnerBadge partnerBadge : resultList.getResults().keySet()) {
            double calcPartnerWeight = calcPartnerWeight(resultList.getResults().get(partnerBadge), secureUserProfile);
            if (calcPartnerWeight < 0.1)
                calcPartnerWeight = 0.1;

            Integer d =  new Double(calcPartnerWeight * 100).intValue();
            while (d > 0) {
                listToDraw.add(partnerBadge);
                d--;
            }
            // very often the search term is not in the result list at all, to
            // be sure to not leave out
            // providers they have at least the weight of 0.1
        }
        List<PartnerBadge> removeList = new ArrayList<PartnerBadge>();
        LinkedList<Result> results = new LinkedList<Result>();
        Random generater = new Random(secureUserProfile.hashCode());

        Collections.shuffle(listToDraw, generater);
        while (results.size() < numResults) {
            listToDraw.removeAll(removeList);
            removeList.clear();
            if (listToDraw.size() == 0)
                break;
            PartnerBadge partner = listToDraw.get(new Double(generater.nextDouble() * listToDraw.size()).intValue());
            try {
                Result resultToAdd = resultList.getResults().get(partner).results.get(0);
                if (resultToAdd == null)
                    break;
                byte[] signNewResult = getFuzzyHashSignature(resultToAdd);
                Result found = null;
                for (Result selectedResult : results) {
                    if (Arrays.equals(signNewResult, getFuzzyHashSignature(selectedResult))) {
                        found = selectedResult;
                        break;
                    }
                }
                if (found == null) {
                    results.add(resultToAdd);
                } else {
                    if (found.resultGroup != null)
                        found.resultGroup.add(resultToAdd);
                    else {
                        found.resultGroup = new LinkedList<Result>();
                        found.resultGroup.add(resultToAdd);
                    }

                }
     
                resultList.getResults().get(partner).results.remove(0);

            } catch (Exception e) {
                logger.log(Level.INFO, "Picker leaving out " + partner.getSystemId() + ", no results left");
                removeList.add(partner);
            }
        }

        returnedResults.results = results;
        return returnedResults;
    }

    /**
     * calculates the weight for the single result to create the weight for
     * {@link calcPartnerWeight}
     * 
     * @param result
     * @param secureUserProfile
     * @return
     */
    private double calcResultWeight(Result result, SecureUserProfile secureUserProfile) {
        double maxTitleEntries = 0;
        double maxDescriptionEntries = 0;
        // TODO: use stemmed versions and preprocess the hole contented in a
        // better way
        for (ContextKeyword context : secureUserProfile.contextKeywords) {
            if (result.title != null && result.title.toLowerCase().contains(context.text.toLowerCase()))
                    maxTitleEntries++;
        }
        for (Interest interest : secureUserProfile.interestList) {
            if (result.title != null && result.title.toLowerCase().contains(interest.text.toLowerCase()))
                    maxTitleEntries++;
        }
        double queryWeight = 0;
        if (secureUserProfile.contextKeywords.size() + secureUserProfile.interestList.size() > 0)
            queryWeight = ((maxDescriptionEntries + maxTitleEntries) / 2) / (double) (secureUserProfile.contextKeywords.size() + secureUserProfile.interestList.size());
        return queryWeight;
    }

    /**
     * Calculation of the partner weight by similarity to the
     * {@link SecureUserProfile} and the partner's language model
     * 
     * @param partnerBadge
     * @param secureUserProfile
     * @return
     */

    private double calcPartnerWeight(ResultList resultList, SecureUserProfile secureUserProfile) {
        double partnerWeight = 0.0;
            if(resultList!=null){
            for (Result result : resultList.results) {
                partnerWeight += calcResultWeight(result, secureUserProfile);
            }
            if (!resultList.results.isEmpty())
                return partnerWeight / resultList.results.size();
            else
                return 0.0;
        }
            return 0.0;
    }

    /**
     * helper class to sort the results
     * 
     * @author hziak
     *
     * @param <K>
     * @param <V>
     */
    public class SortableValueMap<K, V extends Comparable<V>> extends LinkedHashMap<K, V> {
        private static final long serialVersionUID = 1L;

        public SortableValueMap() {
        }

        public SortableValueMap(Map<K, V> map) {
            super(map);
        }

        public void sortByValue() {
            List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(entrySet());

            Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
                @Override
                public int compare(Map.Entry<K, V> entry1, Map.Entry<K, V> entry2) {
                    return -entry1.getValue().compareTo(entry2.getValue()); // descending!
                }
            });

            clear();

            for (Map.Entry<K, V> entry : list) {
                put(entry.getKey(), entry.getValue());
            }
        }

    }

}

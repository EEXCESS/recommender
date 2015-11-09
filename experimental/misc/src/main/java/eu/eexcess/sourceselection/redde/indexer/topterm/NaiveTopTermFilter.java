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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.relationship.Relationship;
import net.sf.extjwnl.data.relationship.RelationshipFinder;

public class NaiveTopTermFilter extends Resources {

    NaiveTopTermFilter(String indexPath, String wordnetPath) throws IOException, JWNLException {
        super(indexPath, wordnetPath);
    }

    /**
     * calculates a list of words that have less relations to all other words
     * 
     * @param startFrom
     *            first term to take out of top terms
     * @param to
     *            last term to take out of top terms; start from < to number of
     *            top terms to use as input
     * @return a sorted list of word relations the first have less, the last
     *         have more relations to other words
     * @throws Exception
     */
    TreeSet<WordRelation> getTopUnrelatedTerms(int startFrom, int to) throws Exception {

        String[] topTerms = getTopTerms(startFrom, to);

        // calculate relations of all terms to all other terms
        Vector<WordRelation> topTermRelations = getBinaryRelations(topTerms);

        HashMap<String, WordRelation> wordRelationWeight = new HashMap<String, WordRelation>();
        // normalize relations using minimum relation (max distance)
        for (WordRelation relation : topTermRelations) {
            relation.setNormalizedRelation(1.00 - (relation.getWeightedRelation() / WordRelation.getMaxDistance()));
            relation.setNormalizedNumSynsets(relation.getNumSynSets() / WordRelation.getMaxNumSynSets());
            WordRelation.setTotalRelations(WordRelation.getTotalRelations() + relation.getNormalizedRelation());

            // reduce ~n² relations to n: sum up all relations for each same
            // source term
            WordRelation seenRelation = wordRelationWeight.put(relation.getSource(), relation);
            if (seenRelation == null) {
                relation.setAverageRelation(relation.getNormalizedRelation());
            } else {
                relation.setAverageRelation(seenRelation.getAverageRelation() + relation.getNormalizedRelation());
            }
        }

        // sort relations according to the averaged reduced relation
        TreeSet<WordRelation> sortedRelations = new TreeSet<WordRelation>();
        for (Map.Entry<String, WordRelation> entry : wordRelationWeight.entrySet()) {
            WordRelation relation = entry.getValue();
            // normalize average relation
            relation.setAverageRelation(relation.getAverageRelation() / WordRelation.getTotalRelations());
            sortedRelations.add(relation);
        }
        return sortedRelations;
    }

    /**
     * takes an array of words and calculates all relations in between them
     * considering their wordnet relations found as POS.NOUN, POS.VERB and
     * POS.ADJECTIVE
     * <p>
     * words not found in wordnet or without any relations (noun, verb or
     * adjective) are not considered in the result
     * <p>
     * {@link #getBinaryRelations(int, int)} ∈ O(n²)
     * 
     * @param topTerms
     *            array of words
     */
    private Vector<WordRelation> getBinaryRelations(String[] topTerms) {
        Vector<WordRelation> topTermRelations = new Vector<WordRelation>();

        for (String sourceTerm : topTerms) {
            for (String targetTerm : topTerms) {
                if (((Object) sourceTerm) == ((Object) targetTerm)) {
                    continue;
                }
                WordRelation similarity = new WordRelation();
                try {
                    relativeSimilarityByType(similarity, sourceTerm, targetTerm, POS.NOUN);
                    relativeSimilarityByType(similarity, sourceTerm, targetTerm, POS.VERB);
                    relativeSimilarityByType(similarity, sourceTerm, targetTerm, POS.ADJECTIVE);
                } catch (JWNLException | CloneNotSupportedException | IllegalArgumentException ok) {
                }
                try {
                    similarity.setWeightedRelation(weightSimilarityAttributes(similarity));
                    if (WordRelation.getMinDistance() > similarity.getWeightedRelation()) {
                        WordRelation.setMinDistance(similarity.getWeightedRelation());
                    }
                    if (WordRelation.getMaxDistance() < similarity.getWeightedRelation()) {
                        WordRelation.setMaxDistance(similarity.getWeightedRelation());
                    }

                    if (WordRelation.getMinNumSynSet() > similarity.getNumSynSets()) {
                        WordRelation.setMinNumSynSet(similarity.getNumSynSets());
                    }
                    if (WordRelation.getMaxNumSynSets() < similarity.getNumSynSets()) {
                        WordRelation.setMaxNumSynSets(similarity.getNumSynSets());
                    }

                    topTermRelations.add(similarity);
                } catch (IllegalArgumentException e) {
                }
            }
        }
        return topTermRelations;
    }

    private double weightSimilarityAttributes(WordRelation similarity) throws IllegalArgumentException {

        int numValidAttributes = 3;
        double adjectiveWeight = 1.0 / 3.0, nounWeight = 1.0 / 3.0, verbWeight = 1.0 / 3.0;
        double adjectiveSimilarity = 0, nounSimilarity = 0, verbSimilarity = 0;

        if (Double.isNaN(similarity.getRelativeAdjectiveRelation()) || Double.isInfinite(similarity.getRelativeAdjectiveRelation())) {
            numValidAttributes--;
        } else {
            adjectiveSimilarity = similarity.getRelativeAdjectiveRelation();
        }

        if (Double.isNaN(similarity.getRelativeVerbRelation()) || Double.isInfinite(similarity.getRelativeVerbRelation())) {
            numValidAttributes--;
        } else {
            verbSimilarity = similarity.getRelativeVerbRelation();
        }

        if (Double.isNaN(similarity.getRelativeNounRelation()) || Double.isInfinite(similarity.getRelativeNounRelation())) {
            numValidAttributes--;
        } else {
            nounSimilarity = similarity.getRelativeNounRelation();
        }

        if (numValidAttributes <= 0) {
            throw new IllegalArgumentException("no attributes to weight available");
        }

        return (nounSimilarity * nounWeight + adjectiveSimilarity * adjectiveWeight + verbSimilarity * verbWeight) / (double) numValidAttributes;
    }

    private void relativeSimilarityByType(WordRelation relation, String source, String target, POS type) throws JWNLException, CloneNotSupportedException,
            NullPointerException {

        relation.setSource(source);
        relation.setTarget(target);
        IndexWord sourceWord = null, targetWord = null;

        Synset sourceSynset = null, targetSynset = null;
        try {
            sourceWord = wordnetDict.lookupAllIndexWords(source).getIndexWord(type);
            // sourceWord.sortSenses();
            sourceSynset = sourceWord.getSenses().get(0);

            if (Double.isNaN(relation.getNumSynSets())) {
                relation.setNumSynSets(sourceWord.getSenses().size());
            }
        } catch (NullPointerException npe) {
            throw new IllegalArgumentException("cannot find index of source [" + source + "] with type " + type);
        }
        try {
            targetWord = wordnetDict.lookupAllIndexWords(target).getIndexWord(type);
            // targetWord.sortSenses();
            targetSynset = targetWord.getSenses().get(0);
        } catch (NullPointerException npe) {
            throw new IllegalArgumentException("cannot find inxex of target [" + target + "] with type " + type);
        }

        Relationship mostInterestingRelation = RelationshipFinder.findRelationships(sourceSynset, targetSynset, PointerType.HYPERNYM).getShallowest();

        int depth = Integer.MAX_VALUE;
        try {
            depth = mostInterestingRelation.getDepth();
            if (type == POS.ADJECTIVE) {
                relation.setRelativeAdjectiveRelation(depth);
            } else if (type == POS.NOUN) {
                relation.setRelativeNounRelation(depth);
            } else if (type == POS.VERB) {
                relation.setRelativeVerbRelation(depth);
            }
        } catch (NullPointerException npe) {
            // if word-net contains source and dest then there is also a
            // relation
            throw new IllegalArgumentException("words have no relation");
        }

        if (type == POS.ADJECTIVE) {
            if (WordRelation.getMaxAdjetiveDistance() <= depth) {
                WordRelation.setMaxAdjetiveDistance(depth);
            }
            if (WordRelation.getMinAdjectiveDistance() > depth) {
                WordRelation.setMinAdjectiveDistance(depth);
            }
        } else if (type == POS.NOUN) {
            if (WordRelation.getMaxNounDistance() <= depth) {
                WordRelation.setMaxNounDistance(depth);
            }
            if (WordRelation.getMinNounDistance() > depth) {
                WordRelation.setMinNounDistance(depth);
            }
        } else if (type == POS.VERB) {
            if (WordRelation.getMaxVerbDistance() <= depth) {
                WordRelation.setMaxVerbDistance(depth);
            }
            if (WordRelation.getMinVerbDistance() > depth) {
                WordRelation.setMinVerbDistance(depth);
            }
        }
    }

}

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

/**
 * Represents a word relation inbetween two words: source and target
 * 
 * @author Raoul Rubien
 *
 */
public class WordRelation implements Comparable<WordRelation> {

    /**
     * binary relation parameters
     */
    private static double MaxNounDistance = Double.MIN_VALUE;
    private static double MinNounDistance = Double.MAX_VALUE;
    private static double MaxVerbDistance = Double.MIN_VALUE;
    private static double MinVerbDistance = Double.MAX_VALUE;
    private static double MaxAdjetiveDistance = Double.MIN_VALUE;
    private static double MinAdjectiveDistance = Double.MAX_VALUE;
    private static double MaxDistance = Double.MIN_VALUE;
    private static double MinDistance = Double.MAX_VALUE;

    private double relativeNounRelation = Double.NaN;
    private double relativeVerbRelation = Double.NaN;
    private double relativeAdjectiveRelation = Double.NaN;

    private double weightedRelation = Double.NaN;
    private double normalizedRelation = Double.NaN;

    private String source;
    private String target;

    /**
     * 1:n relation parameters
     */
    private static double totalRelations = 0;
    private double averageRelation = 0;

    private static double MaxNumSynSets = Double.MIN_VALUE;
    private static double MinNumSynSet = Double.MAX_VALUE;

    private double numSynSets = Double.NaN;
    private double normalizedNumSynsets = Double.NaN;

    public static String fieldsToString() {
        return "min/max-weighted-distance [" + MinDistance + "/" + MaxDistance + "] min/max-noun distance [" + MinNounDistance + "/" + MaxNounDistance
                + "] min-max-verb distance [" + MinVerbDistance + "/" + MaxVerbDistance + "] min/max-adjective distance [" + MinAdjectiveDistance + "/"
                + MaxAdjetiveDistance + "]";
    }

    @Override
    public int compareTo(WordRelation o) {
        if (this == o || this.averageRelation == o.averageRelation || this.averageRelation < o.averageRelation) {
            return -1;
        }
        return 1;
    }

    @Override
    public String toString() {
        return "relation [" + source + "] -> [" + target + "] normalized[" + normalizedRelation + "] total-normalized[" + averageRelation + "] weighted["
                + weightedRelation + "] noun/verb/adjective-rel.[" + relativeNounRelation + "/" + relativeVerbRelation + "/" + relativeAdjectiveRelation
                + "] normalized/max-synset[" + normalizedNumSynsets + "/" + MaxNumSynSets + "]";
    }

    public static double getMaxNounDistance() {
        return MaxNounDistance;
    }

    public static void setMaxNounDistance(double maxNounDistance) {
        MaxNounDistance = maxNounDistance;
    }

    public static double getMinNounDistance() {
        return MinNounDistance;
    }

    public static void setMinNounDistance(double minNounDistance) {
        MinNounDistance = minNounDistance;
    }

    public static double getMaxVerbDistance() {
        return MaxVerbDistance;
    }

    public static void setMaxVerbDistance(double maxVerbDistance) {
        MaxVerbDistance = maxVerbDistance;
    }

    public static double getMinVerbDistance() {
        return MinVerbDistance;
    }

    public static void setMinVerbDistance(double minVerbDistance) {
        MinVerbDistance = minVerbDistance;
    }

    public static double getMaxAdjetiveDistance() {
        return MaxAdjetiveDistance;
    }

    public static void setMaxAdjetiveDistance(double maxAdjetiveDistance) {
        MaxAdjetiveDistance = maxAdjetiveDistance;
    }

    public static double getMinAdjectiveDistance() {
        return MinAdjectiveDistance;
    }

    public static void setMinAdjectiveDistance(double minAdjectiveDistance) {
        MinAdjectiveDistance = minAdjectiveDistance;
    }

    public static double getMaxDistance() {
        return MaxDistance;
    }

    public static void setMaxDistance(double maxDistance) {
        MaxDistance = maxDistance;
    }

    public static double getMinDistance() {
        return MinDistance;
    }

    public static void setMinDistance(double minDistance) {
        MinDistance = minDistance;
    }

    public double getRelativeNounRelation() {
        return relativeNounRelation;
    }

    public void setRelativeNounRelation(double relativeNounRelation) {
        this.relativeNounRelation = relativeNounRelation;
    }

    public double getRelativeVerbRelation() {
        return relativeVerbRelation;
    }

    public void setRelativeVerbRelation(double relativeVerbRelation) {
        this.relativeVerbRelation = relativeVerbRelation;
    }

    public double getRelativeAdjectiveRelation() {
        return relativeAdjectiveRelation;
    }

    public void setRelativeAdjectiveRelation(double relativeAdjectiveRelation) {
        this.relativeAdjectiveRelation = relativeAdjectiveRelation;
    }

    public double getWeightedRelation() {
        return weightedRelation;
    }

    public void setWeightedRelation(double weightedRelation) {
        this.weightedRelation = weightedRelation;
    }

    public double getNormalizedRelation() {
        return normalizedRelation;
    }

    public void setNormalizedRelation(double normalizedRelation) {
        this.normalizedRelation = normalizedRelation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public static double getTotalRelations() {
        return totalRelations;
    }

    public static void setTotalRelations(double totalRelations) {
        WordRelation.totalRelations = totalRelations;
    }

    public double getAverageRelation() {
        return averageRelation;
    }

    public void setAverageRelation(double averageRelation) {
        this.averageRelation = averageRelation;
    }

    public static double getMaxNumSynSets() {
        return MaxNumSynSets;
    }

    public static void setMaxNumSynSets(double maxNumSynSets) {
        MaxNumSynSets = maxNumSynSets;
    }

    public static double getMinNumSynSet() {
        return MinNumSynSet;
    }

    public static void setMinNumSynSet(double minNumSynSet) {
        MinNumSynSet = minNumSynSet;
    }

    public double getNumSynSets() {
        return numSynSets;
    }

    public void setNumSynSets(double numSynSets) {
        this.numSynSets = numSynSets;
    }

    public double getNormalizedNumSynsets() {
        return normalizedNumSynsets;
    }

    public void setNormalizedNumSynsets(double normalizedNumSynsets) {
        this.normalizedNumSynsets = normalizedNumSynsets;
    }

}

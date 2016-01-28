package eu.eexcess.federatedrecommender.picker

import com.aliasi.matrix.SvdMatrix
import eu.eexcess.dataformats.PartnerBadge
import eu.eexcess.dataformats.result.Result
import eu.eexcess.dataformats.result.ResultList
import eu.eexcess.dataformats.userprofile.SecureUserProfile
import eu.eexcess.federatedrecommender.dataformats.PFRChronicle
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations
import eu.eexcess.federatedrecommender.interfaces.PartnersFederatedRecommendationsPicker
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger


/**
 * Created by hziak on 25.01.16.
 */
class LSIPicker : PartnersFederatedRecommendationsPicker() {
    private val LOGGER = Logger.getLogger("eu.eexcess.federatedRecommender.picker.LSIPicker")
    private var NUM_FACTORS = 2

    override fun pickResults(secureUserProfile: SecureUserProfile?, resultList: PartnersFederatedRecommendations?, partners: MutableList<PartnerBadge>?, numResults: Int): ResultList? {


        val combinedResults = ArrayList<Result>()
        resultList?.results?.entries?.forEach { element -> combinedResults.addAll(element?.value?.results!!) }
        combinedResults?.forEach { }


        val termDocumentMatrix = createTermDocMatrix(secureUserProfile, combinedResults)
        //                combinedResults?.forEach{ println(it.description) }
        var svdMatrix = calcSvdMatrix(termDocumentMatrix)
        NUM_FACTORS = secureUserProfile?.contextKeywords?.size!!

        val scales = svdMatrix.singularValues()
        val termVectors = svdMatrix.leftSingularVectors()
        val docVectors = svdMatrix.rightSingularVectors()

        //        println("\nSCALES")
        //        for (k in 0..NUM_FACTORS - 1)
        //            System.out.printf("%d  %4.2f\n", k, scales[k])


        var keywordList = String()
        secureUserProfile?.contextKeywords?.forEachIndexed { x, contextKeyword ->
            contextKeyword.text.split(" ").forEach {
                if (keywordList.length > 0)
                    keywordList += " " + it
                else
                    keywordList = it
            }
        };


        //        println("\nTERM VECTORS")
        //        for (i in termVectors.indices) {
        //            print("(")
        //            for (k in 0..NUM_FACTORS - 1) {
        //                if (k > 0) print(", ")
        //                System.out.printf("% 5.2f", termVectors[i][k])
        //            }
        //            print(")  ")
        //            println(message = secureUserProfile!!.contextKeywords[i]!!?.text)
        //        }


        val search: ResultList;
        if (secureUserProfile?.numResults != null)
            search = search(scales, termVectors, docVectors, keywordList, combinedResults, secureUserProfile?.numResults as Int)
        else
            search = search(scales, termVectors, docVectors, keywordList, combinedResults, 10)
        return search;

    }

    private fun search(scales: DoubleArray?, termVectors: Array<out DoubleArray>?, docVectors: Array<out DoubleArray>?, keywordList: String, combinedResults: ArrayList<Result>, numResults: Int): ResultList {
        val terms = keywordList.split(" |,".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray() // space or comma separated

        val queryVector = DoubleArray(terms.size)
        Arrays.fill(queryVector, 0.0)

        for (term in terms)
            addTermVector(term, termVectors, queryVector, terms)


        //        println("\nQuery=" + Arrays.asList<String>(*terms))
        //        print("Query Vector=(")
        //        for (k in queryVector.indices) {
        //            if (k > 0) print(", ")
        //            System.out.printf("% 5.2f", queryVector[k])
        //        }
        //        println(" )")

        println("\nDOCUMENT SCORES VS. QUERY")

        val dotProductMap = HashMap<Int, Double>()
        for (j in docVectors!!.indices) {
            if (docVectors[j] != null) {
                val score = dotProduct(queryVector, docVectors[j], scales!!)
                dotProductMap.put(j, score)
            }
            // double score = cosine(queryVector,docVectors[j],scales);

        }
        dotProductMap.entries.forEach { key -> System.out.printf("   % 5.2f  %s\n", key.value, combinedResults[key.key]) }
        var resultList = ResultList()
        var sortedEntries = dotProductMap.entries.sortedByDescending { key -> key.value }


        sortedEntries.forEach(
                {
                    if (resultList.results.size < numResults) {
                        resultList.results.add(combinedResults[it.key])
                    } else return resultList
                })
        return resultList
    }

    internal fun dotProduct(xs: DoubleArray, ys: DoubleArray, scales: DoubleArray): Double {
        var sum = 0.0
        for (k in xs.indices)
            try {
                sum += xs[k] * ys[k] * scales[k]
            } catch(e: Exception) {
                LOGGER.log(Level.WARNING, "Index out of Bounce", e)
            }
        return sum
    }


    internal fun addTermVector(term: String, termVectors: Array<out DoubleArray>?, queryVector: DoubleArray, terms: Array<String>) {
        for (i in terms.indices) {
            if (terms[i] == term) {
                for (j in 0..NUM_FACTORS - 1) {
                    try {
                        queryVector[j] += termVectors!![i]!![j]
                    } catch(e: Exception) {
                        LOGGER.log(Level.WARNING, "Index out of Bounce", e)
                    }
                }
                return
            }
        }
    }

    private fun calcSvdMatrix(termDocumentMatrix: Array<out DoubleArray>?): SvdMatrix {
        val featureInit = 0.01
        val initialLearningRate = 0.005
        val annealingRate = 1000
        val regularization = 0.00
        val minImprovement = 0.0000
        val minEpochs = 10
        val maxEpochs = 5000

        //        println("  Computing SVD")
        //
        //        println("    maxFactors=" + NUM_FACTORS)
        //        println("    featureInit=" + featureInit)
        //        println("    initialLearningRate=" + initialLearningRate)
        //        println("    annealingRate=" + annealingRate)
        //        println("    regularization" + regularization)
        //        println("    minImprovement=" + minImprovement)
        //        println("    minEpochs=" + minEpochs)
        //        println("    maxEpochs=" + maxEpochs)

        val matrix = SvdMatrix.svd(termDocumentMatrix,
                NUM_FACTORS,
                featureInit,
                initialLearningRate,
                annealingRate.toDouble(),
                regularization,
                null,
                minImprovement,
                minEpochs,
                maxEpochs)

        val scales = matrix.singularValues()
        val termVectors = matrix.leftSingularVectors()
        val docVectors = matrix.rightSingularVectors()
        return matrix
    }

    private fun createTermDocMatrix(secureUserProfile: SecureUserProfile?, combinedResults: ArrayList<Result>): Array<out DoubleArray>? {

        var size = secureUserProfile?.contextKeywords?.size!!

        val matrix: Array<out DoubleArray>? = Array<DoubleArray>(size, { DoubleArray(combinedResults?.size) })

        secureUserProfile?.contextKeywords?.forEachIndexed { x, contextKeyword ->

            contextKeyword.text.split(" ").forEach {
                combinedResults?.forEachIndexed { y, document ->
                    val combinedTitleDesc = document.title + " " + document.description
                    var counter = 0;
                    combinedTitleDesc.split("\\s").forEach { element ->
                        if (element.contains(it)) {
                            counter += 1
                        }
                        matrix!![x]!![y] += counter;
                    }
                }

            }
        }





        return matrix
    }


    /**
     * Function not implemented
     */
    override fun pickResults(pFRChronicle: PFRChronicle?, numResults: Int): ResultList? {
        throw UnsupportedOperationException()
    }

}
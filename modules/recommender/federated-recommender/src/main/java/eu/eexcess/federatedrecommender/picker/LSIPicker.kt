package eu.eexcess.federatedrecommender.picker

import com.aliasi.matrix.SvdMatrix
import eu.eexcess.dataformats.PartnerBadge
import eu.eexcess.dataformats.result.Result
import eu.eexcess.dataformats.result.ResultList
import eu.eexcess.dataformats.userprofile.FeatureVector
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
    // private var NUM_FACTORS = 3
    override fun pickResults(secureUserProfile: SecureUserProfile?, resultList: PartnersFederatedRecommendations?, partners: MutableList<PartnerBadge>?, numResults: Int): ResultList? {
        var numFactors = secureUserProfile?.contextKeywords?.size!! //!! + secureUserProfile?.userVector?.vector?.size!!

        val combinedResults = ArrayList<Result>()
        resultList?.results?.entries?.forEach { element -> combinedResults.addAll(element?.value?.results!!) }
        val termDocumentMatrix = createTermDocMatrix(secureUserProfile, combinedResults)
        var svdMatrix = calcSvdMatrix(termDocumentMatrix, numFactors)

        val scales = svdMatrix.singularValues()
        //val termVectors = svdMatrix.leftSingularVectors()
        //val docVectors = svdMatrix.rightSingularVectors()
        var featureMatrix =createFeatureMatrix(termDocumentMatrix,combinedResults)

        var keywordList = String()
        secureUserProfile?.contextKeywords?.forEachIndexed { x, contextKeyword ->
            if (keywordList.length > 0)
                keywordList += "," + contextKeyword.text
            else
                keywordList = contextKeyword.text
        };

        val search = search2(scales,  featureMatrix ,combinedResults, secureUserProfile!!)

        return search;

    }

    private fun search2(scales: DoubleArray?, featureMatrix: Array<out DoubleArray>, combinedResults: ArrayList<Result>, secureUserProfile: SecureUserProfile): ResultList? {

     //   var docVectors = DoubleArray(scales?.size!! +FeatureVector().vector.size )
        val dotProductMap = HashMap<Int, Double>()
        var tmpVector = DoubleArray(secureUserProfile.userVector.vector.size + scales?.size!!)
        scales?.forEachIndexed { i, d ->
            tmpVector[i] = scales.get(i)
        }
        secureUserProfile.userVector.vector.forEachIndexed { i, d ->
            tmpVector[i+scales?.size!!] = secureUserProfile.userVector.vector[i]
        }


        for(i in featureMatrix.first().indices){
            val tmpFeatureMatrix = DoubleArray(featureMatrix.size)
        featureMatrix.forEachIndexed { j, d ->
            tmpFeatureMatrix[j] = featureMatrix[j][i]
        }

        var score = dotProduct(tmpVector, tmpFeatureMatrix)
        print(score.toString() +", ")
        dotProductMap.put(i, score)
        }

//        for(j in featureMatrix!!.indices){
//            var score = dotProduct(tmpVector, tmpFeatureMatrix)
//            print(score.toString() +", ")
//            dotProductMap.put(j, score)
//        }
//        println()
//
//        println()
//        println("query vector")
//        tmpVector.forEach { print(" " + it + ",") }
//        println()
//
//
//        println()
//        println("feature matrix")
//        featureMatrix.forEach { print("{")
//            it.forEach { print(it.toString() +",") }
//            println("},")
//        }

//        for (j in docVectors!!.indices) {
//            if (docVectors[j] != null) {
//                val score = dotProduct(queryVector, docVectors[j])
//                dotProductMap.put(j, score)
//            }
//            // double score = cosine(queryVector,docVectors[j],scales);
//
//        }
        var resultList = ResultList()
        var sortedEntries = dotProductMap.entries.sortedByDescending { key -> key.value }
        if (secureUserProfile?.numResults == null)
            secureUserProfile.numResults = 10

        for (i in sortedEntries.indices) {
            println(" " + sortedEntries.get(i).key + " " + sortedEntries.get(i).value + " " + combinedResults[sortedEntries.get(i).key].title + " " + combinedResults[sortedEntries.get(i).key].mediaType + " " + combinedResults[sortedEntries.get(i).key].licence)
        }
        sortedEntries.forEach(
                {
                    if (resultList.results.size < secureUserProfile?.numResults) {
                        resultList.results.add(combinedResults[it.key])
                    } else return resultList
                })
        return resultList
    }

//    private fun search(scales: DoubleArray?,  keywordList: String, combinedResults: ArrayList<Result>, secureUserProfile: SecureUserProfile, numFactors: Int): ResultList {
//        val terms = keywordList.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray() // space or comma separated
//
////        val queryVector = DoubleArray(docVectors?.first()?.size!!)
////        // secureUserProfile.contextKeywords.size+secureUserProfile.userVector.vector.size
////        Arrays.fill(queryVector, 0.0)
////        for (term in terms)
////            addTermVector(term, termVectors, queryVector, terms, numFactors)
////
////
////
////        println("\nQuery=" + Arrays.asList<String>(*terms))
////        print("Query Vector=(")
////        for (k in queryVector.indices) {
////            if (k > 0) print(", ")
////            System.out.printf("% 5.2f", queryVector[k])
////        }
////        println(" )")
//
//        println("\nDOCUMENT SCORES VS. QUERY")
//
//        val dotProductMap = HashMap<Int, Double>()
//        for (j in docVectors!!.indices) {
//            if (docVectors[j] != null) {
//                val score = dotProduct(queryVector, docVectors[j])
//                dotProductMap.put(j, score)
//            }
//            // double score = cosine(queryVector,docVectors[j],scales);
//
//        }
//        //   dotProductMap.entries.forEach { key -> System.out.printf("   % 5.2f  %s\n", key.value, combinedResults[key.key]) }
//        var resultList = ResultList()
//        var sortedEntries = dotProductMap.entries.sortedByDescending { key -> key.value }
//        if (secureUserProfile?.numResults == null)
//            secureUserProfile.numResults = 10
//
//        for (i in sortedEntries.indices) {
//            println(" " + sortedEntries.get(i).key + " " + sortedEntries.get(i).value + " " + combinedResults[sortedEntries.get(i).key].title + " " + combinedResults[sortedEntries.get(i).key].mediaType + " " + combinedResults[sortedEntries.get(i).key].licence)
//        }
//        sortedEntries.forEach(
//                {
//                    if (resultList.results.size < secureUserProfile?.numResults) {
//                        resultList.results.add(combinedResults[it.key])
//                    } else return resultList
//                })
//        return resultList
//    }

    internal fun dotProduct(xs: DoubleArray, ys: DoubleArray): Double {


        var sum = 0.0
        for (k in ys.indices)
            try {
                sum += xs[k] * ys[k] // * scales[k]
            } catch(e: Exception) {
                LOGGER.log(Level.WARNING, "Index out of Bounce", e)
            }
        return sum
    }


//    internal fun addTermVector(term: String, termVectors: Array<out DoubleArray>?, queryVector: DoubleArray, terms: Array<String>, numFactors: Int) {
//        for (i in terms.indices) {
//            if (terms[i].equals(term)) {
//                for (j in 0..numFactors - 1) {
//                    try {
//                        queryVector[j] += termVectors!![i]!![j]
//                    } catch(e: Exception) {
//                        LOGGER.log(Level.WARNING, "Index out of Bounce", e)
//                    }
//                }
//                return
//            }
//        }
//    }

    private fun calcSvdMatrix(termDocumentMatrix: Array<out DoubleArray>?, numFactors: Int): SvdMatrix {
        val featureInit = 0.01
        val initialLearningRate = 0.001
        val annealingRate = 1000
        val regularization = 0.00
        val minImprovement = 0.0001
        val minEpochs = 100
        val maxEpochs = 50000
        val matrix = SvdMatrix.svd(termDocumentMatrix,
                numFactors,
                featureInit,
                initialLearningRate,
                annealingRate.toDouble(),
                regularization,
                null,
                minImprovement,
                minEpochs,
                maxEpochs)
        return matrix
    }


    private fun createTermDocMatrix(secureUserProfile: SecureUserProfile?, combinedResults: ArrayList<Result>): Array<out DoubleArray> {


        val matrix: Array<out DoubleArray> = Array(secureUserProfile?.contextKeywords?.size!!, { DoubleArray(combinedResults.size) })

        secureUserProfile?.contextKeywords?.forEachIndexed { x, contextKeyword ->

            contextKeyword.text.split(" ").forEach {
                combinedResults.forEachIndexed { y, document ->
                    val combinedTitleDesc = document.title + " " + document.description
                    var counter = 0;
                    combinedTitleDesc.split("\\s").forEach { element ->
                        if (element.contains(it)) {
                            counter += 1
                        }

                    }
                    matrix[x][y] += counter;
                }

            }
        }
        return matrix
    }


    fun createFeatureMatrix(oldFeatureMatrix: Array<out DoubleArray>, combinedResults: ArrayList<Result>): Array<out DoubleArray> {

        val matrix: Array<out DoubleArray> = Array(oldFeatureMatrix.size + FeatureVector().vector.size, { DoubleArray(oldFeatureMatrix.first().size) })


        for(x in oldFeatureMatrix.indices){
          oldFeatureMatrix[x].forEachIndexed { y, d ->
                matrix[x][y]=d
            }
        }


        FeatureVector().vector.forEachIndexed { i, d ->
            var x = i + oldFeatureMatrix.size
            combinedResults.forEachIndexed { y, document ->
                when (i) {
                    0 -> if (document.mediaType.equals("text", true)) matrix[x][y] = 1.0 else matrix[x][y] = 0.0
                    1 -> if (document.mediaType.equals("video", true)) matrix[x][y] = 1.0 else matrix[x][y] = 0.0
                    2 -> if (document.mediaType.equals("picture", true)) matrix[x][y] = 1.0 else matrix[x][y] = 0.0
                    3 -> if (document.licence != null && document.licence.contains("Apache", true)) matrix[x][y] = 1.0 else matrix[x][y] = 0.0
                    4 -> if (document.date != null && !document.date.isEmpty()) matrix[x][y] = 1.0 else matrix[x][y] = 0.0
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

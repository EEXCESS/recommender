package eu.eexcess.federatedrecommender.picker

import eu.eexcess.dataformats.PartnerBadge
import eu.eexcess.dataformats.result.ResultList
import eu.eexcess.dataformats.userprofile.SecureUserProfile
import eu.eexcess.federatedrecommender.dataformats.PFRChronicle
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations
import eu.eexcess.federatedrecommender.interfaces.PartnersFederatedRecommendationsPicker
import java.util.*

/**
 * Created by hziak on 09.12.15.
 */

class FeatureMapPicker : PartnersFederatedRecommendationsPicker() {

    private var partnersFeatureMatrix: PartnerFeatureMatrix? = null
    private val poolSize=1000;
    private var pool=ArrayList<PartnerBadge>() //SystemID's of the partners according to the amount of tickets

    override fun pickResults(pFRChronicle: PFRChronicle, numResults: Int): ResultList {
        throw UnsupportedOperationException()
    }


    override fun pickResults(secureUserProfile: SecureUserProfile?, resultList: PartnersFederatedRecommendations?, partners: MutableList<PartnerBadge>?, numResults: Int): ResultList? {


        var partnerFeatureMatrix = retrievePartnersFeatureFector(partners).getMatrix()
        var userVector = secureUserProfile?.userVector?.vector
        var partnerSelectionVector: DoubleArray
        partnerSelectionVector = DoubleArray(partners!!.size)
         for (p in partnerFeatureMatrix.indices) {
            for (pi in partnerFeatureMatrix[p].indices) {
                val times = userVector!![pi].times(partnerFeatureMatrix[p][pi])
                partnerSelectionVector[p] += times
            }

        }
        var factor : Double = 0.0;
//        System.out.println("PartnerVector")
//        partnerSelectionVector.forEach {e -> System.out.print(e.toString() +" ")  }
//        System.out.println("")
        var partnerSelectionVecSum = partnerSelectionVector.sum()
        for( p in partnerSelectionVector.indices){
            var counter = partnerSelectionVector[p]/partnerSelectionVecSum*poolSize
            while(counter>0){
                if(resultList!!.results.containsKey(partners!!.get(p)))
                    pool.add(partners!!.get(p))
                counter--
            }
        }

//        System.out.println(pool.partition{it.systemId.equals("Partner1")}.first.size)
//        System.out.println(pool.partition{it.systemId.equals("Partner2")}.first.size)
//        System.out.println(pool.partition{it.systemId.equals("Partner3")}.first.size)

        var revisedResultList: ResultList
        revisedResultList=drawFromPool(resultList,numResults)
        return revisedResultList;
    }

    private fun drawFromPool(resultList: PartnersFederatedRecommendations?, numResults: Int): ResultList {
        var revisedResultList= ResultList()
        while(numResults>revisedResultList.results.size && resultList!!.results.values.sumBy { it.results.size }>0){
            var randomIndex=(Math.random()*pool.size).toInt()
            if(randomIndex>=pool.size)
                randomIndex-=pool.size-1
            else if(randomIndex<0)
                randomIndex=0
            var sysID =pool.get(randomIndex)
            pool.drop(randomIndex)
            if(resultList!!.results.get(sysID)!!.results.size>0) {
                   revisedResultList!!.results!!.add(resultList!!.results.get(sysID)!!.results.first)
                   resultList!!.results.get(sysID)!!.results.drop(0)
            }
        }
        return revisedResultList
    }

    fun retrievePartnersFeatureFector(partners: MutableList<PartnerBadge>?): PartnerFeatureMatrix {
        this.partnersFeatureMatrix =  PartnerFeatureMatrix(partners!!.first()!!.featureVector!!.vector!!.size,partners!!.size)
        for(p in partners.indices){
            val vector = partners[p].featureVector.vector;
            val newVector = DoubleArray(vector.size);
            for( i in vector.indices){
                newVector[i] = vector[i]
            }
            this.partnersFeatureMatrix!!.setRow(p,newVector)
        }
        return this.partnersFeatureMatrix!!;
    }

}













package eu.eexcess.federatedrecommender.picker

import eu.eexcess.dataformats.PartnerBadge
import eu.eexcess.dataformats.result.Result
import eu.eexcess.dataformats.result.ResultList
import eu.eexcess.dataformats.userprofile.FeatureVector
import eu.eexcess.dataformats.userprofile.SecureUserProfile
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations
import org.junit.Test
import java.util.*


/**
 * Created by hziak on 09.12.15.
 */

class FeatureMapPickerTest {


    @Test fun featureTest() {
        var featureMapPicker = FeatureMapPicker()
        var userProfile = SecureUserProfile()
        userProfile.userVector.text = 0.4
        userProfile.userVector.video = 0.0
        userProfile.userVector.picture = 0.1
        userProfile.userVector.openLicence = 0.8
        userProfile.userVector.dateExisting = 0.5

        var partners = ArrayList<PartnerBadge>()
        val partner1 = PartnerBadge()
        val partner2 = PartnerBadge()
        val partner3 = PartnerBadge()
        val partner4 = PartnerBadge()
        partner1.systemId="Partner1"
        partner1.featureVector = FeatureVector()
        partner1.featureVector.text=3.0
        partner1.featureVector.openLicence=2.0
        partner1.featureVector.picture=0.0
        partner1.featureVector.dateExisting=0.0
        partner1.featureVector.video=0.0
        partners.add(partner1)
        partner2.systemId="Partner2"
        partner1.featureVector = FeatureVector()
        partner2.featureVector.text=2.0
        partner2.featureVector.openLicence=2.0
        partner2.featureVector.picture=1.0
        partner2.featureVector.dateExisting=0.0
        partner2.featureVector.video=0.0
        partners.add(partner2)
        partner3.systemId="Partner3"
        partner1.featureVector = FeatureVector()
        partner3.featureVector.text=0.0
        partner3.featureVector.openLicence=0.0
        partner3.featureVector.picture=2.0
        partner3.featureVector.dateExisting=0.0
        partner3.featureVector.video=3.0
        partners.add(partner3)
        partner4.systemId="Partner4"
        partner1.featureVector = FeatureVector()
        partner4.featureVector.text=3.0
        partner4.featureVector.openLicence=0.0
        partner4.featureVector.picture=3.0
        partner4.featureVector.dateExisting=0.0
        partner4.featureVector.video=0.0
        partners.add(partner4)


        var resultList = PartnersFederatedRecommendations()
        var resList1 = ResultList()
        var resList1Element = Result()
        resList1Element.description="Partner1 Description"
        resList1.results.add(resList1Element);
        resultList.results.put(partner1, resList1);

        var resList2 = ResultList()
        var resList2Element = Result()
        resList2Element.description="Partner2 Description"
        resList2.results.add(resList2Element);
        resList2.results.add(resList2Element);
        resList2.results.add(resList2Element);
        resList2.results.add(resList2Element);
        resList2.results.add(resList2Element);

        resultList.results.put(partner2, resList2);


        var resList3 = ResultList()
        var resList3Element = Result()
        resList3Element.description="Partner3 Description"
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);
        resList3.results.add(resList3Element);

        resultList.results.put(partner3, resList3);

        var result =featureMapPicker.pickResults(userProfile, resultList, partners, 20);
        result!!.results.forEach { e ->
            System.out.println(e.description)
        }

    }


    @Test fun featureTestNeutral() {
        var featureMapPicker = FeatureMapPicker()
        var userProfile = SecureUserProfile()
        featureMapPicker.pickResults(userProfile, null, null, 10);

    }

}

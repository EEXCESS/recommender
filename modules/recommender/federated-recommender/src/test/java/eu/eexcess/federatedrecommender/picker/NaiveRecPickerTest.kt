package eu.eexcess.federatedrecommender.picker


import eu.eexcess.dataformats.PartnerBadge
import eu.eexcess.dataformats.result.Result
import eu.eexcess.dataformats.result.ResultList
import eu.eexcess.dataformats.userprofile.ContextKeyword
import eu.eexcess.dataformats.userprofile.FeatureVector
import eu.eexcess.dataformats.userprofile.SecureUserProfile
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations
import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Created by hziak on 25.01.16.
 */
class NaiveRecPickerTest {

    @Test fun testPickResultsTextLow() {
        var NaiveRecPicker = NaiveRecPicker()
        var userProfile = SecureUserProfile()
        userProfile.preferences.text = 0.0
        userProfile.preferences.video = 1.9
        userProfile.preferences.picture = 1.9
        userProfile.preferences.openLicence = 1.9
        userProfile.preferences.dateExisting = 1.9
        userProfile.contextKeywords.add(ContextKeyword("Lord Byron"))
        userProfile.contextKeywords.add(ContextKeyword("Ada Lovelace"))

        var partners = ArrayList<PartnerBadge>()
        val partner1 = PartnerBadge()
        val partner2 = PartnerBadge()
        val partner3 = PartnerBadge()
        val partner4 = PartnerBadge()
        partner1.systemId="Partner1"
        partner1.featureVector = FeatureVector()
        partner1.featureVector.text=1.0
        partner1.featureVector.openLicence=1.0
        partner1.featureVector.picture=0.0
        partner1.featureVector.dateExisting=0.0
        partner1.featureVector.video=0.0
        partners.add(partner1)
        partner2.systemId="Partner2"
        partner2.featureVector = FeatureVector()
        partner2.featureVector.text=1.0
        partner2.featureVector.openLicence=1.0
        partner2.featureVector.picture=1.0
        partner2.featureVector.dateExisting=0.0
        partner2.featureVector.video=0.0
        partners.add(partner2)
        partner3.systemId="Partner3"
        partner3.featureVector = FeatureVector()
        partner3.featureVector.text=0.0
        partner3.featureVector.openLicence=0.0
        partner3.featureVector.picture=1.0
        partner3.featureVector.dateExisting=0.0
        partner3.featureVector.video=1.0
        partners.add(partner3)
        partner4.systemId="Partner4"
        partner4.featureVector = FeatureVector()
        partner4.featureVector.text=1.0
        partner4.featureVector.openLicence=0.0
        partner4.featureVector.picture=3.0
        partner4.featureVector.dateExisting=0.0
        partner4.featureVector.video=0.0
        partners.add(partner4)


        var resultList= getResultList(partner1,partner2)




        var result =NaiveRecPicker.pickResults(userProfile, resultList, partners, 20);
        println("Test Text Low")
        result!!.results.forEach { e ->
            System.out.println(e.title)
        }

    }
    @Test fun testPickResultsNoPref() {
        var NaiveRecPicker = NaiveRecPicker()
        var userProfile = SecureUserProfile()

        userProfile.contextKeywords.add(ContextKeyword("Lord Byron"))
        userProfile.contextKeywords.add(ContextKeyword("Ada Lovelace"))
        userProfile.preferences.dateExisting=1.0;
        userProfile.preferences.openLicence=1.0;
        userProfile.preferences.picture=1.0;
        userProfile.preferences.text=1.0;
        userProfile.preferences.video=1.0;

        var partners = ArrayList<PartnerBadge>()
        val partner1 = PartnerBadge()
        val partner2 = PartnerBadge()
        val partner3 = PartnerBadge()
        val partner4 = PartnerBadge()
        partner1.systemId="Partner1"

        partners.add(partner1)
        partner2.systemId="Partner2"
        partners.add(partner2)
        partner3.systemId="Partner3"
        partners.add(partner3)
        partner4.systemId="Partner4"
        partners.add(partner4)


        var resultList = getResultList(partner1,partner2)





        var result =NaiveRecPicker.pickResults(userProfile, resultList, partners, 20);
        println("testPickResultsNoPref")
        result!!.results.forEach { e ->
            System.out.println(e.title)
        }

    }




    @Test fun testPickResultsTextHigh() {

        var naiveRecPicker= NaiveRecPicker()
        var userProfile = SecureUserProfile()
        userProfile.preferences.text = 5.0
        userProfile.preferences.video = 0.0
        userProfile.preferences.picture = 0.0
        userProfile.preferences.openLicence = 0.0
        userProfile.preferences.dateExisting = 0.0
        userProfile.contextKeywords.add(ContextKeyword("Lord Byron"))
        userProfile.contextKeywords.add(ContextKeyword("Ada Lovelace"))

        var partners = ArrayList<PartnerBadge>()
        val partner1 = PartnerBadge()
        val partner2 = PartnerBadge()
        val partner3 = PartnerBadge()
        val partner4 = PartnerBadge()
        partner1.systemId="Partner1"
        partners.add(partner1)

        partner2.systemId="Partner2"
        partners.add(partner2)

        partner3.systemId="Partner3"
        partners.add(partner3)

        partner4.systemId="Partner4"
        partners.add(partner4)


        var resultList = getResultList(partner1,partner2)




        var result =naiveRecPicker.pickResults(userProfile, resultList, partners, 20);
        println("Test Text High")
        result!!.results.forEach { e ->
            System.out.println(e.title)
        }


    }

    @Test fun testPickResultsTextLowNeutral() {
        var naiveRecPicker = NaiveRecPicker()
        var userProfile = SecureUserProfile()
        userProfile.preferences.text = 0.0
        userProfile.preferences.video = 6.0
        userProfile.preferences.picture = 6.0
        userProfile.preferences.openLicence = -6.0
        userProfile.preferences.dateExisting = 6.0
        userProfile.contextKeywords.add(ContextKeyword("Lord Byron"))
        userProfile.contextKeywords.add(ContextKeyword("Ada Lovelace"))

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
        partner2.featureVector = FeatureVector()
        partner2.featureVector.text=2.0
        partner2.featureVector.openLicence=2.0
        partner2.featureVector.picture=1.0
        partner2.featureVector.dateExisting=0.0
        partner2.featureVector.video=0.0
        partners.add(partner2)
        partner3.systemId="Partner3"
        partner3.featureVector = FeatureVector()
        partner3.featureVector.text=0.0
        partner3.featureVector.openLicence=0.0
        partner3.featureVector.picture=2.0
        partner3.featureVector.dateExisting=0.0
        partner3.featureVector.video=3.0
        partners.add(partner3)
        partner4.systemId="Partner4"
        partner4.featureVector = FeatureVector()
        partner4.featureVector.text=3.0
        partner4.featureVector.openLicence=0.0
        partner4.featureVector.picture=3.0
        partner4.featureVector.dateExisting=0.0
        partner4.featureVector.video=0.0
        partners.add(partner4)


        var resultList= getResultListNeutral(partner1,partner2)




        var result =naiveRecPicker.pickResults(userProfile, resultList, partners, 20);
        println("Test Text Low")
                result!!.results.forEach { e ->
                    System.out.println(e.title)
                }

    }
    @Test fun testPickResultsNoPrefNeutral() {
        var naiveRecPicker = NaiveRecPicker()
        var userProfile = SecureUserProfile()

        userProfile.contextKeywords.add(ContextKeyword("Lord Byron"))
        userProfile.contextKeywords.add(ContextKeyword("Ada Lovelace"))

        var partners = ArrayList<PartnerBadge>()
        val partner1 = PartnerBadge()
        val partner2 = PartnerBadge()
        val partner3 = PartnerBadge()
        val partner4 = PartnerBadge()
        partner1.systemId="Partner1"

        partners.add(partner1)
        partner2.systemId="Partner2"
        partners.add(partner2)
        partner3.systemId="Partner3"
        partners.add(partner3)
        partner4.systemId="Partner4"
        partners.add(partner4)


        var resultList = getResultListNeutral(partner1,partner2)





        var result =naiveRecPicker.pickResults(userProfile, resultList, partners, 20);
        println("testPickResultsNoPref")
                result!!.results.forEach { e ->
                    System.out.println(e.title)
                }

    }




    @Test fun testPickResultsTextHighNeutral() {
        var naiveRecPicker = NaiveRecPicker()
        var userProfile = SecureUserProfile()
        userProfile.preferences.text = 1.0
        userProfile.preferences.video = 0.0
        userProfile.preferences.picture = 0.0
        userProfile.preferences.openLicence = 0.0
        userProfile.preferences.dateExisting = 0.0
        userProfile.contextKeywords.add(ContextKeyword("Lord Byron"))
        userProfile.contextKeywords.add(ContextKeyword("Ada Lovelace"))

        var partners = ArrayList<PartnerBadge>()
        val partner1 = PartnerBadge()
        val partner2 = PartnerBadge()
        val partner3 = PartnerBadge()
        val partner4 = PartnerBadge()
        partner1.systemId="Partner1"
        partners.add(partner1)

        partner2.systemId="Partner2"
        partners.add(partner2)

        partner3.systemId="Partner3"
        partners.add(partner3)

        partner4.systemId="Partner4"
        partners.add(partner4)


        var resultList = getResultListNeutral(partner1,partner2)




        var result =naiveRecPicker.pickResults(userProfile, resultList, partners, 20);
        println("Test Text High")
                result!!.results.forEach { e ->
                    System.out.println(e.title)
                }


    }


    private fun getResultList(partner1: PartnerBadge, partner2: PartnerBadge): PartnersFederatedRecommendations? {
        var restultList= PartnersFederatedRecommendations()
        var resList1 = ResultList()
        var resList1Element1 = Result()
        resList1Element1.description=""
        resList1Element1.title="[image]Critique of Lovelace Meta-Analysis p1"
        resList1Element1.mediaType="image"
        var resList1Element2 = Result()
        resList1Element2.description="Burg (oder Stadteingang) über Mauer und Fels. Davor freies Feld mit Bauer, Kuh und Ziege. Im Hintergrund ein rosses Kreuz. Auf der Rückseite handschriftlicher Eintrag von Marcel Herwegh: \"Après un dessin de G.H. de l'époque de Balingen. L'original se trouve chez ma soeur Ada de Paula-Souza; il m'a été donné par un ancien condisciple de G.H.En hommage après un concert spirituel ou je me suis fait entendre à Reutlingen (Würtemberg) en l'année 1878 ou 1880.... M.H.\". Das Original (BH 0170a) befindet sich in Liestal."
        resList1Element2.title="[Text]Fotografie, Zeichnung von Georg Herwegh (Foto) p1"
        resList1Element2.mediaType="text"
        var resList1Element3 = Result()
        resList1Element3.description="Notizbuch (kleinformatig), brauner Ledereinband und goldener Verschluss. Text auf 52 Seiten. Widmung (Byron-Zitat) von Emma Herwegh. Nebst Text, Skizze einer Landkarte und geometrische Aufzeichnungen."
        resList1Element3.title="[Text]Notizbuch p1"
        resList1Element3.mediaType="text"
        var resList1Element4 = Result()
        resList1Element4.description=""
        resList1Element4.title="[image] Bild eines jungen Mannes mit rotem Hut (mit Feder) blauer Weste und Stock in der Hand. 3/4 Figur. p1"
        resList1Element4.mediaType="image"
        resList1.results.add(resList1Element1);
        resList1.results.add(resList1Element2);
        resList1.results.add(resList1Element3);
        resList1.results.add(resList1Element4);

        restultList.results.put(partner1, resList1);



        var resList2 = ResultList()
        var resList2Element1 = Result()
        resList2Element1.description="Augusta Ada King, Countess of Lovelace (10 December 1815 – 27 November 1852), born Augusta Ada Byron and now commonly known as Ada Lovelace , was an English mathematician and writer chiefly known for her work on Charles Babbage 's early mechanical general-purpose computer, the Analytical Engine . Her notes on the engine include what is recognised as the first algorithm intended to be carried out by a machine. Because of this, she is often described as the world's first computer programmer . . Lovelace was born 10 December 1815 as the only child of the poet Lord Byron and his wife Anne Isabella Byron . Ada Lovelace Biography , biography.com All Byron's other children were born out of wedlock to other women. Byron separated from his wife a month after Ada was born and left England forever four months later, eventually dying of disease in the Greek War of Independence when Ada was eight years old. Ada's mother remained bitter towards Lord Byron and promoted Ada's interest in mathematics and logic in an effort to prevent her from developing what she saw as the insanity seen in her father, but Ada remained interested in him despite this (and was, upon her eventual death, buried next to him at her request). Ada described her approach as poetical science and herself as an Analyst (& Metaphysician). As a young adult, her mathematical talents led her to an ongoing working relationship and friendship with fellow British mathematician Charles Babbage, and in particular Babbage's work on the Analytical Engine. Between 1842 and 1843, she translated an article by Italian military engineer Luigi Menabrea on the engine, which she supplemented with an elaborate set of notes of her own, simply called Notes . These notes contain what many consider to be the first computer program—that is, an algorithm designed to be carried out by a machine. Lovelace's notes are important in the early history of computers . She also developed a vision on the capability of computers to go beyond mere calculating or number-crunching, while others, including Babbage himself, focused only on those capabilities."
        resList2Element1.title="[Text]Ada Lovelace - 4b57r4c7 p2"
        resList2Element1.mediaType="text"
        var resList2Element2 = Result()
        resList2Element2.description=""
        resList2Element2.title="[image Date Licence] LEP - The Lord of the Collider Rings at CERN 1980-2000 p2"
        resList2Element2.mediaType="image"
        resList2Element2.date="1980"
        resList2Element2.licence="Apache 2.0"
        var resList2Element3 = Result()
        resList2Element3.description="\"München\" (Überschrift in der Ablage); \"Reiseblätter / München\" (Überschrift Manuskript); \"Music is a strange thing. Byron. / Sachsen bessert Wahlheim aus, Preussen flickt am Kölner Dome...\"; ein Blatt faltet, vier Seiten beschrieben."
        resList2Element3.title="[Text]Manuskript p2"
        resList2Element3.mediaType="text"
        var resList2Element4 = Result()
        resList2Element4.description="englische Literaturẹnglische Literatur.Als englische Literatur bezeichnete man lange Zeit die englischsprachige Literatur Großbritanniens und der ehemaligen britischen Kolonien..."
        resList2Element4.title="[Text]englische Literatur p2"
        resList2Element4.mediaType="text"
        resList2.results.add(resList2Element1);
        resList2.results.add(resList2Element2);
        resList2.results.add(resList2Element3);
        resList2.results.add(resList2Element4);

        restultList.results.put(partner2, resList2);
        return restultList
    }
    private fun getResultListNeutral(partner1: PartnerBadge, partner2: PartnerBadge): PartnersFederatedRecommendations? {
        var restultList= PartnersFederatedRecommendations()
        var resList1 = ResultList()
        var resList1Element1 = Result()
        resList1Element1.description=""
        resList1Element1.title="[image] Fotografie, Zeichnung von Georg Herwegh (Foto) p1"
        resList1Element1.mediaType="image"
        var resList1Element2 = Result()
        resList1Element2.description=""
        resList1Element2.title="[Text] Fotografie, Zeichnung von Georg Herwegh (Foto) p1"
        resList1Element2.mediaType="text"
        var resList1Element3 = Result()
        resList1Element3.description=""
        resList1Element3.title="[Text] Fotografie, Zeichnung von Georg Herwegh (Foto) p1"
        resList1Element3.mediaType="text"
        var resList1Element4 = Result()
        resList1Element4.description=""
        resList1Element4.title="[image] Fotografie, Zeichnung von Georg Herwegh (Foto) p1"
        resList1Element4.mediaType="image"
        resList1.results.add(resList1Element1);
        resList1.results.add(resList1Element2);
        resList1.results.add(resList1Element3);
        resList1.results.add(resList1Element4);

        restultList.results.put(partner1, resList1);



        var resList2 = ResultList()
        var resList2Element1 = Result()
        resList2Element1.description=""
        resList2Element1.title="[Text] Fotografie, Zeichnung von Georg Herwegh (Foto) p2 res"
        resList2Element1.mediaType="text"
        resList2Element1.licence="restricted"
        var resList2Element2 = Result()
        resList2Element2.description=""
        resList2Element2.title="[image Date Licence] Fotografie, Zeichnung von Georg Herwegh (Foto) p2"
        resList2Element2.mediaType="image"
        resList2Element2.date="1980"
        resList2Element2.licence="Apache 2.0"
        var resList2Element3 = Result()
        resList2Element3.description=""
        resList2Element3.title="[Text] Fotografie, Zeichnung von Georg Herwegh (Foto) p2"
        resList2Element3.mediaType="text"
        var resList2Element4 = Result()
        resList2Element4.description=""
        resList2Element4.title="[Text] Fotografie, Zeichnung von Georg Herwegh (Foto) p2 res"
        resList2Element4.mediaType="text"
        resList2Element4.licence="restricted"
        resList2.results.add(resList2Element1);
        resList2.results.add(resList2Element2);
        resList2.results.add(resList2Element3);
        resList2.results.add(resList2Element4);

        restultList.results.put(partner2, resList2);
        return restultList
    }

}
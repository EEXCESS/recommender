package eu.eexcess.partnerrecommender.reference;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;
import eu.eexcess.partnerrecommender.reference.LuceneQueryGeneratorFieldTermConjunctionMainTopic;

public class LuceneQueryGeneratorFieldTermConjunctionMainTopicTest {

    @Test
    public void multibleExpansionsTest() {

        LuceneQueryGeneratorFieldTermConjunctionMainTopic gen = new LuceneQueryGeneratorFieldTermConjunctionMainTopic();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1");
        userProfile.contextKeywords.add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword("k2");
        userProfile.contextKeywords.add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        userProfile.contextKeywords.add(keyword3);
        ContextKeyword keyword8 = new ContextKeyword("k8");
        keyword8.setIsMainTopic(true);
        userProfile.contextKeywords.add(keyword8);
        ContextKeyword keyword9 = new ContextKeyword("k9");
        keyword9.setIsMainTopic(true);
        userProfile.contextKeywords.add(keyword9);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.contextKeywords.add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword7);
//        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
//            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("(k8 AND k9) AND (k1 OR k2 OR k3 OR k4 OR (k5 OR k6 OR k7))"));
            // assertTrue(result.equals("k1 OR k2 OR k3 OR k4 OR (k5 OR k6 OR k7)"));
//        }
    }

    @Test
    public void singleExpansionsTest() {

        LuceneQueryGeneratorFieldTermConjunctionMainTopic gen = new LuceneQueryGeneratorFieldTermConjunctionMainTopic();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1");
        userProfile.contextKeywords.add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword("k2");
        keyword2.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword2);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("k1 OR (k2)"));
            // assertTrue(result.equals("k1 OR (k2)"));
        }
    }

    @Test
    public void multibleExpansionsBetweenTwoTest() {

        LuceneQueryGeneratorFieldTermConjunctionMainTopic gen = new LuceneQueryGeneratorFieldTermConjunctionMainTopic();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1");
        userProfile.contextKeywords.add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword("k2");
        keyword2.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        keyword3.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.contextKeywords.add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("k1 OR (k2 OR k3) OR k4 OR (k5 OR k6 OR k7)"));
        }
        // assertTrue(result.equals("k1 OR (k2 OR k3) OR k4 OR (k5 OR k6 OR k7)"));
    }

    @Test
    public void multibleExpansionsBetweenTest() {

        LuceneQueryGeneratorFieldTermConjunctionMainTopic gen = new LuceneQueryGeneratorFieldTermConjunctionMainTopic();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1");
        userProfile.contextKeywords.add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword("k2");
        keyword2.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");

        userProfile.contextKeywords.add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.contextKeywords.add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            // assertTrue(result.equals("k1 OR (k2) OR k3 OR k4 OR (k5 OR k6 OR k7)"));
            assertTrue(result.equals("k1 OR (k2) OR k3 OR k4 OR (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleExpansionsStartTest() {

        LuceneQueryGeneratorFieldTermConjunctionMainTopic gen = new LuceneQueryGeneratorFieldTermConjunctionMainTopic();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1");
        keyword1.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword("k2");
        keyword2.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        keyword3.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.contextKeywords.add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("(k1 OR k2 OR k3) OR k4 OR (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleSerendipityStartTest() {

        LuceneQueryGeneratorFieldTermConjunctionMainTopic gen = new LuceneQueryGeneratorFieldTermConjunctionMainTopic();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1");
        keyword1.expansion = ExpansionType.SERENDIPITY;
        userProfile.contextKeywords.add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword("k2");
        keyword2.expansion = ExpansionType.SERENDIPITY;
        userProfile.contextKeywords.add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        keyword3.expansion = ExpansionType.SERENDIPITY;
        userProfile.contextKeywords.add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.contextKeywords.add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.expansion = ExpansionType.SERENDIPITY;
        userProfile.contextKeywords.add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.expansion = ExpansionType.SERENDIPITY;
        userProfile.contextKeywords.add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.expansion = ExpansionType.SERENDIPITY;
        userProfile.contextKeywords.add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("(k1 OR k2 OR k3) OR k4 AND (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleExpansionsConjunctionTestBrackets() {

        LuceneQueryGeneratorFieldTermConjunctionMainTopic gen = new LuceneQueryGeneratorFieldTermConjunctionMainTopic();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1 (K5 K9)");
        userProfile.contextKeywords.add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword(" k2 K5 K6 ");
        userProfile.contextKeywords.add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        userProfile.contextKeywords.add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.contextKeywords.add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("(k1 AND (K5 AND K9)) OR ( k2 AND K5 AND K6 ) OR k3 OR k4 OR (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleExpansionsConjunctionTest() {

        LuceneQueryGeneratorFieldTermConjunctionMainTopic gen = new LuceneQueryGeneratorFieldTermConjunctionMainTopic();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1 K5");
        userProfile.contextKeywords.add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword(" k2 K5 K6 ");
        userProfile.contextKeywords.add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        userProfile.contextKeywords.add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.contextKeywords.add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("(k1 AND K5) OR ( k2 AND K5 AND K6 ) OR k3 OR k4 OR (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleExpansionsConjunctionTestMultipleSpaces() {

        LuceneQueryGeneratorFieldTermConjunctionMainTopic gen = new LuceneQueryGeneratorFieldTermConjunctionMainTopic();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1  K5");
        userProfile.contextKeywords.add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword(" k2  K5  K6 ");
        userProfile.contextKeywords.add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        userProfile.contextKeywords.add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.contextKeywords.add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("(k1 AND K5) OR ( k2 AND K5 AND K6 ) OR k3 OR k4 OR (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleExpansionsConjunctionTestDisabled() {

        LuceneQueryGeneratorFieldTermConjunctionMainTopic gen = new LuceneQueryGeneratorFieldTermConjunctionMainTopic();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1 K5");
        userProfile.contextKeywords.add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword(" k2 K5 K6 ");
        userProfile.contextKeywords.add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        userProfile.contextKeywords.add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.contextKeywords.add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.expansion = ExpansionType.PSEUDORELEVANCEWP;
        userProfile.contextKeywords.add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(false);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("k1 AND K5 OR  k2 AND K5 AND K6  OR k3 OR k4"));
        }
    }

}

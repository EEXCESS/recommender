package eu.eexcess.partnerrecommender.reference;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;

public class LuceneQueryGeneratorFieldTermConjunctionTest {

    @Test
    public void multibleExpansionsTest() {

        LuceneQueryGeneratorFieldTermConjunction gen = new LuceneQueryGeneratorFieldTermConjunction();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1");
        userProfile.getContextKeywords().add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword("k2");
        userProfile.getContextKeywords().add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        userProfile.getContextKeywords().add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.getContextKeywords().add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("k1 OR k2 OR k3 OR k4 OR (k5 OR k6 OR k7)"));
            // assertTrue(result.equals("k1 OR k2 OR k3 OR k4 OR (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void singleExpansionsTest() {

        LuceneQueryGeneratorFieldTermConjunction gen = new LuceneQueryGeneratorFieldTermConjunction();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1");
        userProfile.getContextKeywords().add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword("k2");
        keyword2.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword2);
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

        LuceneQueryGeneratorFieldTermConjunction gen = new LuceneQueryGeneratorFieldTermConjunction();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1");
        userProfile.getContextKeywords().add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword("k2");
        keyword2.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        keyword3.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.getContextKeywords().add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword7);
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

        LuceneQueryGeneratorFieldTermConjunction gen = new LuceneQueryGeneratorFieldTermConjunction();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1");
        userProfile.getContextKeywords().add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword("k2");
        keyword2.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");

        userProfile.getContextKeywords().add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.getContextKeywords().add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword7);
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

        LuceneQueryGeneratorFieldTermConjunction gen = new LuceneQueryGeneratorFieldTermConjunction();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1");
        keyword1.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword("k2");
        keyword2.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        keyword3.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.getContextKeywords().add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("(k1 OR k2 OR k3) OR k4 OR (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleSerendipityStartTest() {

        LuceneQueryGeneratorFieldTermConjunction gen = new LuceneQueryGeneratorFieldTermConjunction();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1");
        keyword1.setExpansion(ExpansionType.SERENDIPITY);
        userProfile.getContextKeywords().add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword("k2");
        keyword2.setExpansion(ExpansionType.SERENDIPITY);
        userProfile.getContextKeywords().add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        keyword3.setExpansion(ExpansionType.SERENDIPITY);
        userProfile.getContextKeywords().add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.getContextKeywords().add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.setExpansion(ExpansionType.SERENDIPITY);
        userProfile.getContextKeywords().add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.setExpansion(ExpansionType.SERENDIPITY);
        userProfile.getContextKeywords().add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.setExpansion(ExpansionType.SERENDIPITY);
        userProfile.getContextKeywords().add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("(k1 OR k2 OR k3) OR k4 AND (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleExpansionsConjunctionTestBrackets() {

        LuceneQueryGeneratorFieldTermConjunction gen = new LuceneQueryGeneratorFieldTermConjunction();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1 (K5 K9)");
        userProfile.getContextKeywords().add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword(" k2 K5 K6 ");
        userProfile.getContextKeywords().add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        userProfile.getContextKeywords().add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.getContextKeywords().add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("(k1 AND (K5 AND K9)) OR ( k2 AND K5 AND K6 ) OR k3 OR k4 OR (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleExpansionsConjunctionTest() {

        LuceneQueryGeneratorFieldTermConjunction gen = new LuceneQueryGeneratorFieldTermConjunction();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1 K5");
        userProfile.getContextKeywords().add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword(" k2 K5 K6 ");
        userProfile.getContextKeywords().add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        userProfile.getContextKeywords().add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.getContextKeywords().add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("(k1 AND K5) OR ( k2 AND K5 AND K6 ) OR k3 OR k4 OR (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleExpansionsConjunctionTestMultipleSpaces() {

        LuceneQueryGeneratorFieldTermConjunction gen = new LuceneQueryGeneratorFieldTermConjunction();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1  K5");
        userProfile.getContextKeywords().add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword(" k2  K5  K6 ");
        userProfile.getContextKeywords().add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        userProfile.getContextKeywords().add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.getContextKeywords().add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("(k1 AND K5) OR ( k2 AND K5 AND K6 ) OR k3 OR k4 OR (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleExpansionsConjunctionTestDisabled() {

        LuceneQueryGeneratorFieldTermConjunction gen = new LuceneQueryGeneratorFieldTermConjunction();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1 K5");
        userProfile.getContextKeywords().add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword(" k2 K5 K6 ");
        userProfile.getContextKeywords().add(keyword2);
        ContextKeyword keyword3 = new ContextKeyword("k3");
        userProfile.getContextKeywords().add(keyword3);
        ContextKeyword keyword4 = new ContextKeyword("k4");
        userProfile.getContextKeywords().add(keyword4);
        ContextKeyword keyword5 = new ContextKeyword("k5");
        keyword5.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword5);
        ContextKeyword keyword6 = new ContextKeyword("k6");
        keyword6.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword6);
        ContextKeyword keyword7 = new ContextKeyword("k7");
        keyword7.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword7);
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration().isQueryExpansionEnabled()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(false);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("k1 AND K5 OR  k2 AND K5 AND K6  OR k3 OR k4"));
        }
    }

}

package eu.eexcess.partnerrecommender.reference;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.api.PartnerConfigurationCache;

public class LuceneQueryGeneratorTest {

    @Test
    public void multibleExpansionsTest() {

        LuceneQueryGenerator gen = new LuceneQueryGenerator();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1 k3");
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
        PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setQueryGeneratorClass("eu.eexcess.partnerrecommender.reference.LuceneQueryGenerator");
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("k1 OR k3 OR k2 OR k3 OR k4 OR (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void singleExpansionsTest() {

        LuceneQueryGenerator gen = new LuceneQueryGenerator();
        SecureUserProfile userProfile = new SecureUserProfile();
        ContextKeyword keyword1 = new ContextKeyword("k1");
        userProfile.getContextKeywords().add(keyword1);
        ContextKeyword keyword2 = new ContextKeyword("k2");
        keyword2.setExpansion(ExpansionType.PSEUDORELEVANCEWP);
        userProfile.getContextKeywords().add(keyword2);
        PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setQueryGeneratorClass("eu.eexcess.partnerrecommender.reference.LuceneQueryGenerator");
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("k1 OR (k2)"));
        }
    }

    @Test
    public void multibleExpansionsBetweenTwoTest() {

        LuceneQueryGenerator gen = new LuceneQueryGenerator();
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
        PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setQueryGeneratorClass("eu.eexcess.partnerrecommender.reference.LuceneQueryGenerator");
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration()) {
            try {
                PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
                String result = gen.toQuery(userProfile);
                System.out.println(result);
                assertTrue(result.equals("k1 OR (k2 OR k3) OR k4 OR (k5 OR k6 OR k7)"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void multibleExpansionsBetweenTest() {

        LuceneQueryGenerator gen = new LuceneQueryGenerator();
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
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("k1 OR (k2) OR k3 OR k4 OR (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleExpansionsStartTest() {

        LuceneQueryGenerator gen = new LuceneQueryGenerator();
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
        PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setQueryGeneratorClass("eu.eexcess.partnerrecommender.reference.LuceneQueryGenerator");
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("(k1 OR k2 OR k3) OR k4 OR (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleSerendipityStartTest() {

        LuceneQueryGenerator gen = new LuceneQueryGenerator();
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
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration()) {
            PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(true);
            String result = gen.toQuery(userProfile);
            System.out.println(result);
            assertTrue(result.equals("(k1 OR k2 OR k3) OR k4 AND (k5 OR k6 OR k7)"));
        }
    }

    @Test
    public void multibleSerendipityStartTestDisabled() {

        LuceneQueryGenerator gen = new LuceneQueryGenerator();
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
        PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setQueryGeneratorClass("eu.eexcess.partnerrecommender.reference.LuceneQueryGenerator");

        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration()) {
            try {
                PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(false);
                String result = gen.toQuery(userProfile);
                System.out.println(result);
                assertTrue(result.equals("k4"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void multibleSerendipityStartTestNull() {

        LuceneQueryGenerator gen = new LuceneQueryGenerator();
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
        PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setQueryGeneratorClass("eu.eexcess.partnerrecommender.reference.LuceneQueryGenerator");
        synchronized (PartnerConfigurationCache.CONFIG.getPartnerConfiguration()) {
            try {
                PartnerConfigurationCache.CONFIG.getPartnerConfiguration().setIsQueryExpansionEnabled(null);
                String result = gen.toQuery(userProfile);
                System.out.println(result);
                assertTrue(result.equals("k4"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

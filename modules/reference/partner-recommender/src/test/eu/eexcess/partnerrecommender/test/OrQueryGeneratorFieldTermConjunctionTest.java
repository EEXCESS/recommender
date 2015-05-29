package eu.eexcess.partnerrecommender.test;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.ExpansionType;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.reference.OrQueryGenerator;
import eu.eexcess.partnerrecommender.reference.OrQueryGeneratorFieldTermConjunction;

public class OrQueryGeneratorFieldTermConjunctionTest {

	


		private static final OrQueryGeneratorFieldTermConjunction gen = new OrQueryGeneratorFieldTermConjunction();

		@Test
		public void multibleExpansionsTest() {
			
			SecureUserProfile userProfile = new SecureUserProfile();
			ContextKeyword keyword1 = new ContextKeyword("k1");
			userProfile.contextKeywords.add(keyword1);
			ContextKeyword keyword2 = new ContextKeyword("k2");
			userProfile.contextKeywords.add(keyword2);
			ContextKeyword keyword3 = new ContextKeyword("k3");
			userProfile.contextKeywords.add(keyword3);
			ContextKeyword keyword4 = new ContextKeyword("k4");
			userProfile.contextKeywords.add(keyword4);
			ContextKeyword keyword5 = new ContextKeyword("k5");
			keyword5.expansion=ExpansionType.PSEUDORELEVANCEWP;
			userProfile.contextKeywords.add(keyword5);
			ContextKeyword keyword6 = new ContextKeyword("k6");
			keyword6.expansion=ExpansionType.PSEUDORELEVANCEWP;
			userProfile.contextKeywords.add(keyword6);
			ContextKeyword keyword7 = new ContextKeyword("k7");
			keyword7.expansion=ExpansionType.PSEUDORELEVANCEWP;
			userProfile.contextKeywords.add(keyword7);
			String result =gen.toQuery(userProfile );
			System.out.println(result);
			assertTrue(result.equals("k1 OR k2 OR k3 OR k4 OR k5 OR k6 OR k7"));
		}

		@Test
		public void multibleExpansionsConjunctionTest() {
			
			SecureUserProfile userProfile = new SecureUserProfile();
			ContextKeyword keyword1 = new ContextKeyword("k1 k5");
			userProfile.contextKeywords.add(keyword1);
			ContextKeyword keyword2 = new ContextKeyword(" k2 k5 k6 ");
			userProfile.contextKeywords.add(keyword2);
			ContextKeyword keyword3 = new ContextKeyword("k3");
			userProfile.contextKeywords.add(keyword3);
			ContextKeyword keyword4 = new ContextKeyword("k4");
			userProfile.contextKeywords.add(keyword4);
			ContextKeyword keyword5 = new ContextKeyword("k5");
			keyword5.expansion=ExpansionType.PSEUDORELEVANCEWP;
			userProfile.contextKeywords.add(keyword5);
			ContextKeyword keyword6 = new ContextKeyword("k6");
			keyword6.expansion=ExpansionType.PSEUDORELEVANCEWP;
			userProfile.contextKeywords.add(keyword6);
			ContextKeyword keyword7 = new ContextKeyword("k7");
			keyword7.expansion=ExpansionType.PSEUDORELEVANCEWP;
			userProfile.contextKeywords.add(keyword7);
			String result =gen.toQuery(userProfile );
			System.out.println(result);
			assertTrue(result.equals("k1 AND k5 OR  k2 AND k5 AND k6  OR k3 OR k4 OR k5 OR k6 OR k7"));
		}
}




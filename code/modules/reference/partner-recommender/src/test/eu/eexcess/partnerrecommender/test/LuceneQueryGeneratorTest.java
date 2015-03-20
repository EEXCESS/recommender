package eu.eexcess.partnerrecommender.test;
import static org.junit.Assert.*;

import org.junit.Test;

import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.partnerrecommender.reference.LuceneQueryGenerator;
import eu.eexcess.partnerrecommender.reference.OrQueryGenerator;


public class LuceneQueryGeneratorTest {


	@Test
	public void multibleExpansionsTest() {
		
		LuceneQueryGenerator gen = new LuceneQueryGenerator();
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
		keyword5.expansion=true;
		userProfile.contextKeywords.add(keyword5);
		ContextKeyword keyword6 = new ContextKeyword("k6");
		keyword6.expansion=true;
		userProfile.contextKeywords.add(keyword6);
		ContextKeyword keyword7 = new ContextKeyword("k7");
		keyword7.expansion=true;
		userProfile.contextKeywords.add(keyword7);
		String result =gen.toQuery(userProfile );
		System.out.println(result);
		assertTrue(result.equals("\"k1\" \"k2\" \"k3\" \"k4\" OR (\"k5\" OR \"k6\" OR \"k7\")"));
	}

	
	@Test
	public void singleExpansionsTest() {
		
		LuceneQueryGenerator gen = new LuceneQueryGenerator();
		SecureUserProfile userProfile = new SecureUserProfile();
		ContextKeyword keyword1 = new ContextKeyword("k1");
		userProfile.contextKeywords.add(keyword1);
		ContextKeyword keyword2 = new ContextKeyword("k2");
		keyword2.expansion=true;
		userProfile.contextKeywords.add(keyword2);
		String result =gen.toQuery(userProfile );
		System.out.println(result);
		assertTrue(result.equals("\"k1\" OR (\"k2\")"));
	}
	
	@Test
	public void multibleExpansionsBetweenTwoTest() {
		
		LuceneQueryGenerator gen = new LuceneQueryGenerator();
		SecureUserProfile userProfile = new SecureUserProfile();
		ContextKeyword keyword1 = new ContextKeyword("k1");
		userProfile.contextKeywords.add(keyword1);
		ContextKeyword keyword2 = new ContextKeyword("k2");
		keyword2.expansion=true;
		userProfile.contextKeywords.add(keyword2);
		ContextKeyword keyword3 = new ContextKeyword("k3");
		keyword3.expansion=true;
		userProfile.contextKeywords.add(keyword3);
		ContextKeyword keyword4 = new ContextKeyword("k4");
		userProfile.contextKeywords.add(keyword4);
		ContextKeyword keyword5 = new ContextKeyword("k5");
		keyword5.expansion=true;
		userProfile.contextKeywords.add(keyword5);
		ContextKeyword keyword6 = new ContextKeyword("k6");
		keyword6.expansion=true;
		userProfile.contextKeywords.add(keyword6);
		ContextKeyword keyword7 = new ContextKeyword("k7");
		keyword7.expansion=true;
		userProfile.contextKeywords.add(keyword7);
		String result =gen.toQuery(userProfile );
		System.out.println(result);
		assertTrue(result.equals("\"k1\" OR (\"k2\" OR \"k3\") OR \"k4\" OR (\"k5\" OR \"k6\" OR \"k7\")"));
	}

	@Test
	public void multibleExpansionsBetweenTest() {
		
		LuceneQueryGenerator gen = new LuceneQueryGenerator();
		SecureUserProfile userProfile = new SecureUserProfile();
		ContextKeyword keyword1 = new ContextKeyword("k1");
		userProfile.contextKeywords.add(keyword1);
		ContextKeyword keyword2 = new ContextKeyword("k2");
		keyword2.expansion=true;
		userProfile.contextKeywords.add(keyword2);
		ContextKeyword keyword3 = new ContextKeyword("k3");
		
		userProfile.contextKeywords.add(keyword3);
		ContextKeyword keyword4 = new ContextKeyword("k4");
		userProfile.contextKeywords.add(keyword4);
		ContextKeyword keyword5 = new ContextKeyword("k5");
		keyword5.expansion=true;
		userProfile.contextKeywords.add(keyword5);
		ContextKeyword keyword6 = new ContextKeyword("k6");
		keyword6.expansion=true;
		userProfile.contextKeywords.add(keyword6);
		ContextKeyword keyword7 = new ContextKeyword("k7");
		keyword7.expansion=true;
		userProfile.contextKeywords.add(keyword7);
		String result =gen.toQuery(userProfile );
		System.out.println(result);
		assertTrue(result.equals("\"k1\" OR (\"k2\") OR \"k3\" \"k4\" OR (\"k5\" OR \"k6\" OR \"k7\")"));
	}

	@Test
	public void multibleExpansionsStartTest() {
		
		LuceneQueryGenerator gen = new LuceneQueryGenerator();
		SecureUserProfile userProfile = new SecureUserProfile();
		ContextKeyword keyword1 = new ContextKeyword("k1");
		keyword1.expansion=true;
		userProfile.contextKeywords.add(keyword1);
		ContextKeyword keyword2 = new ContextKeyword("k2");
		keyword2.expansion=true;
		userProfile.contextKeywords.add(keyword2);
		ContextKeyword keyword3 = new ContextKeyword("k3");
		keyword3.expansion=true;
		userProfile.contextKeywords.add(keyword3);
		ContextKeyword keyword4 = new ContextKeyword("k4");
		userProfile.contextKeywords.add(keyword4);
		ContextKeyword keyword5 = new ContextKeyword("k5");
		keyword5.expansion=true;
		userProfile.contextKeywords.add(keyword5);
		ContextKeyword keyword6 = new ContextKeyword("k6");
		keyword6.expansion=true;
		userProfile.contextKeywords.add(keyword6);
		ContextKeyword keyword7 = new ContextKeyword("k7");
		keyword7.expansion=true;
		userProfile.contextKeywords.add(keyword7);
		String result =gen.toQuery(userProfile );
		System.out.println(result);
		assertTrue(result.equals("(\"k1\" OR \"k2\" OR \"k3\") OR \"k4\" OR (\"k5\" OR \"k6\" OR \"k7\")"));
	}
}

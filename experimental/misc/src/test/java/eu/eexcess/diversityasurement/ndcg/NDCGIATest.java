package eu.eexcess.diversityasurement.ndcg;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

/**
 * 
 * @author hziak
 *
 */
public class NDCGIATest {

		@Test
		public void testNDCGIAExample() {
			NDCGIA ndcg = new NDCGIA();
			NDCGResultList list = new NDCGResultList();
			list.results = new ArrayList<NDCGResult>();
			NDCGResult nDCGResult1 = new NDCGResult();
			ArrayList<NDCGIACategory> categories = new ArrayList<NDCGIACategory>();
			
			NDCGIACategory cat1 = new NDCGIACategory("c1", 0.7);
			categories.add(cat1);
			nDCGResult1.categories.add(cat1);
			nDCGResult1.nDCGRelevance = 4;
			nDCGResult1.title = "D1";

			list.results.add(nDCGResult1);

			NDCGResult nDCGResult2 = new NDCGResult();
			nDCGResult2.nDCGRelevance = 3;
			NDCGIACategory cat2 = new NDCGIACategory("c2", 0.3);
			categories.add(cat2);
			nDCGResult2.categories.add(cat2);
			nDCGResult2.title = "D8";
			list.results.add(nDCGResult2);

			NDCGResult nDCGResult3 = new NDCGResult();
			nDCGResult3.nDCGRelevance = 4;
			nDCGResult3.categories.add(cat1);
			nDCGResult3.title = "D2";
			list.results.add(nDCGResult3);

			NDCGResult nDCGResult4 = new NDCGResult();
			nDCGResult4.nDCGRelevance = 2;
			nDCGResult4.categories.add(cat2);
			nDCGResult4.title = "D9";
			list.results.add(nDCGResult4);

			NDCGResult nDCGResult5 = new NDCGResult();
			nDCGResult5.nDCGRelevance = 2;
			nDCGResult5.categories.add(cat2);
			nDCGResult5.title = "D10";
			list.results.add(nDCGResult5);

			NDCGResult nDCGResult6 = new NDCGResult();
			nDCGResult6.nDCGRelevance = 3;
			nDCGResult6.categories.add(cat1);
			nDCGResult6.title = "D3";
			list.results.add(nDCGResult6);

			NDCGResult nDCGResult7 = new NDCGResult();
			nDCGResult7.nDCGRelevance = 2;
			nDCGResult7.categories.add(cat1);
			nDCGResult7.title = "D4";

			list.results.add(nDCGResult7);
			NDCGResult nDCGResult8 = new NDCGResult();
			nDCGResult8.nDCGRelevance = 2;
			nDCGResult8.categories.add(cat1);
			nDCGResult8.title = "D5";

			list.results.add(nDCGResult8);
			NDCGResult nDCGResult9 = new NDCGResult();
			nDCGResult9.nDCGRelevance = 0;
			nDCGResult9.categories.add(cat1);
			nDCGResult9.title = "D6";
			list.results.add(nDCGResult9);

			NDCGResult nDCGResult10 = new NDCGResult();
			nDCGResult10.nDCGRelevance = 0;
			nDCGResult10.categories.add(cat1);
			nDCGResult10.title = "D7";
			list.results.add(nDCGResult10);

		Double nDCGValue = ndcg.calcNDCGIA(list,categories, 5);
		// assert(nDCGValue == 0.9315085232327253);
		System.out.println(nDCGValue);
		assertEquals(nDCGValue, 0.71, 1E-2);
	}
	@Test
	public void testNDCGIAExample2() {
		NDCGIA ndcg = new NDCGIA();
		NDCGResultList list = new NDCGResultList();
		list.results = new ArrayList<NDCGResult>();
		NDCGResult nDCGResult3 = new NDCGResult();
		nDCGResult3.nDCGRelevance = 3;
		nDCGResult3.categories.add(new NDCGIACategory("c1", 0.7));
		nDCGResult3.title = "D2";
		list.results.add(nDCGResult3);

		NDCGResult nDCGResult1 = new NDCGResult();
		nDCGResult1.categories.add(new NDCGIACategory("c1", 0.7));
		nDCGResult1.categories.add(new NDCGIACategory("c2", 0.3));
		nDCGResult1.nDCGRelevance = 4;
		nDCGResult1.title = "D1";
		list.results.add(nDCGResult1);

		NDCGResult nDCGResult4 = new NDCGResult();
		nDCGResult4.nDCGRelevance = 3;
		nDCGResult4.categories.add(new NDCGIACategory("c1", 0.7));
		nDCGResult4.title = "D9";
		list.results.add(nDCGResult4);

		
		NDCGResult nDCGResult2 = new NDCGResult();
		nDCGResult2.nDCGRelevance = 2;
		nDCGResult2.categories.add(new NDCGIACategory("c2", 0.3));
		nDCGResult2.title = "D8";
		list.results.add(nDCGResult2);

		NDCGResult nDCGResult5 = new NDCGResult();
		nDCGResult5.nDCGRelevance = 1;
		nDCGResult5.categories.add(new NDCGIACategory("c2", 0.3));
		nDCGResult5.title = "D10";
		list.results.add(nDCGResult5);

		Double nDCGValue = ndcg.calcNDCGIA(list, null, 5);
		// assert(nDCGValue == 0.9315085232327253);
		System.out.println(nDCGValue);
		assertEquals(nDCGValue, 0.9, 1E-2);
	}
}

package eu.eexcess.diversityasurement.ndcg;

import java.util.ArrayList;

import org.junit.Test;

/**
 * 
 * @author hziak
 *
 */
public class NDCGTest {

	@Test
	public void testSorting() {
		NDCG ndcg = new NDCG();
		NDCGResultList list = new NDCGResultList();
		list.results = new ArrayList<NDCGResult>();
		NDCGResult nDCGResult1 = new NDCGResult();
		nDCGResult1.nDCGRelevance = 1;
		nDCGResult1.title = "T1";

		list.results.add(nDCGResult1);

		NDCGResult nDCGResult2 = new NDCGResult();
		nDCGResult2.nDCGRelevance = 2;
		nDCGResult2.title = "T2";
		list.results.add(nDCGResult2);

		NDCGResult nDCGResult3 = new NDCGResult();
		nDCGResult3.nDCGRelevance = 3;
		nDCGResult3.title = "T3";
		list.results.add(nDCGResult3);

		NDCGResult nDCGResult4 = new NDCGResult();
		nDCGResult4.nDCGRelevance = 4;
		nDCGResult4.title = "T4";
		list.results.add(nDCGResult4);
		NDCGResultList result = ndcg.getRelevanceSortedResultList(list, null, 5);
		assert (result.results.get(0).title == "T4");
		assert (result.results.get(1).title == "T3");
		assert (result.results.get(2).title == "T2");
		assert (result.results.get(3).title == "T1");
	}

	@Test
	public void testNDCG() {
		NDCG ndcg = new NDCG();
		NDCGResultList list = new NDCGResultList();
		list.results = new ArrayList<NDCGResult>();
		NDCGResult nDCGResult1 = new NDCGResult();
		nDCGResult1.nDCGRelevance = 1;
		nDCGResult1.title = "T1";

		list.results.add(nDCGResult1);

		NDCGResult nDCGResult2 = new NDCGResult();
		nDCGResult2.nDCGRelevance = 2;
		nDCGResult2.title = "T2";
		list.results.add(nDCGResult2);

		NDCGResult nDCGResult3 = new NDCGResult();
		nDCGResult3.nDCGRelevance = 3;
		nDCGResult3.title = "T3";
		list.results.add(nDCGResult3);

		NDCGResult nDCGResult4 = new NDCGResult();
		nDCGResult4.nDCGRelevance = 4;
		nDCGResult4.title = "T4";
		list.results.add(nDCGResult4);

		Double calcNDCG = ndcg.calcNDCG(list, null, 10);
		System.out.println(calcNDCG);
		assert (calcNDCG == 0.546527660686785);
	}

	// //Not using the same algorithm anymore -> example is not usable
	// public void testNDCGWikipediaExample() {
	// NDCG ndcg = new NDCG();
	// NDCGResultList list = new NDCGResultList();
	// list.results= new ArrayList<NDCGResult>();
	// NDCGResult nDCGResult1 = new NDCGResult();
	// nDCGResult1.nDCGRelevance=4;
	// nDCGResult1.title="T1";
	//
	// list.results.add(nDCGResult1);
	//
	// NDCGResult nDCGResult2 = new NDCGResult();
	// nDCGResult2.nDCGRelevance=5;
	// nDCGResult2.title="T2";
	// list.results.add(nDCGResult2);
	//
	// NDCGResult nDCGResult3 = new NDCGResult();
	// nDCGResult3.nDCGRelevance=3;
	// nDCGResult3.title="T3";
	// list.results.add(nDCGResult3);
	//
	// NDCGResult nDCGResult4 = new NDCGResult();
	// nDCGResult4.nDCGRelevance=4;
	// nDCGResult4.title="T4";
	// list.results.add(nDCGResult4);
	//
	// NDCGResult nDCGResult5 = new NDCGResult();
	// nDCGResult5.nDCGRelevance=2;
	// nDCGResult5.title="T5";
	// list.results.add(nDCGResult5);
	//
	// NDCGResult nDCGResult6 = new NDCGResult();
	// nDCGResult6.nDCGRelevance=2;
	// nDCGResult6.title="T6";
	// list.results.add(nDCGResult6);
	//
	// NDCGResult nDCGResult7 = new NDCGResult();
	// nDCGResult7.nDCGRelevance=3;
	// nDCGResult7.title="T7";
	//
	// list.results.add(nDCGResult7);
	// NDCGResult nDCGResult8 = new NDCGResult();
	// nDCGResult8.nDCGRelevance=2;
	// nDCGResult8.title="T8";
	//
	// list.results.add(nDCGResult8);
	// NDCGResult nDCGResult9 = new NDCGResult();
	// nDCGResult9.nDCGRelevance=0;
	// nDCGResult9.title="T9";
	// list.results.add(nDCGResult9);
	//
	// NDCGResult nDCGResult10 = new NDCGResult();
	// nDCGResult10.nDCGRelevance=0;
	// nDCGResult10.title="T10";
	// list.results.add(nDCGResult10);
	// Double nDCGValue = ndcg.calcNDCG(list, null,10);
	// //assert(nDCGValue == 0.9315085232327253);
	// System.out.println(nDCGValue);
	// assertEquals(nDCGValue, 0.88,1E-3);
	// }
	@Test
	public void testNDCGIASELECTVarifyOne() {
		NDCG ndcg = new NDCG();
		NDCGResultList list = new NDCGResultList();
		list.results = new ArrayList<NDCGResult>();
		NDCGResult nDCGResult1 = new NDCGResult();
		nDCGResult1.nDCGRelevance = 3;
		nDCGResult1.title = "T1";

		list.results.add(nDCGResult1);

		NDCGResult nDCGResult2 = new NDCGResult();
		nDCGResult2.nDCGRelevance = 4;
		nDCGResult2.title = "T2";
		list.results.add(nDCGResult2);

		NDCGResult nDCGResult3 = new NDCGResult();
		nDCGResult3.nDCGRelevance = 3;
		nDCGResult3.title = "T3";
		list.results.add(nDCGResult3);
		Double calcNDCG = ndcg.calcNDCG(list, null, 3);
		System.out.println(calcNDCG);
		assert (calcNDCG == 0.87);
	}
	@Test
	public void testNDCGIASELECTVarifyTWO() {
		NDCG ndcg = new NDCG();
		NDCGResultList list = new NDCGResultList();
		list.results = new ArrayList<NDCGResult>();
		NDCGResult nDCGResult1 = new NDCGResult();
		nDCGResult1.nDCGRelevance = 4;
		nDCGResult1.title = "T1";

		list.results.add(nDCGResult1);

		NDCGResult nDCGResult2 = new NDCGResult();
		nDCGResult2.nDCGRelevance = 2;
		nDCGResult2.title = "T2";
		list.results.add(nDCGResult2);

		NDCGResult nDCGResult3 = new NDCGResult();
		nDCGResult3.nDCGRelevance = 1;
		nDCGResult3.title = "T3";
		list.results.add(nDCGResult3);
		Double calcNDCG = ndcg.calcNDCG(list, null, 3);
		System.out.println(calcNDCG);
		assert (calcNDCG == 0.87);
	}
}

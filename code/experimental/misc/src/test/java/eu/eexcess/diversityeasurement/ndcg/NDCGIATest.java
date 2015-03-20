package eu.eexcess.diversityeasurement.ndcg;

import static org.junit.Assert.*;

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
		list.results= new ArrayList<NDCGResult>();
		NDCGResult nDCGResult1 = new NDCGResult();
		nDCGResult1.categories.add(new NDCGIACategory("c1", 0.0, 0.7));
		nDCGResult1.nDCGRelevance=4;
		nDCGResult1.title="D1";
		
		list.results.add(nDCGResult1);
		
		NDCGResult nDCGResult2 = new NDCGResult();
		nDCGResult2.nDCGRelevance=3;
		nDCGResult2.categories.add(new NDCGIACategory("c2", 0.0, 0.3));
		nDCGResult2.title="D8";
		list.results.add(nDCGResult2);
		
		NDCGResult nDCGResult3 = new NDCGResult();
		nDCGResult3.nDCGRelevance=3;
		nDCGResult3.categories.add(new NDCGIACategory("c1", 0.0, 0.7));
		nDCGResult3.title="D2";
		list.results.add(nDCGResult3);
		
		NDCGResult nDCGResult4 = new NDCGResult();
		nDCGResult4.nDCGRelevance=4;
		nDCGResult4.categories.add(new NDCGIACategory("c2", 0.0, 0.3));
		nDCGResult4.title="D9";
		list.results.add(nDCGResult4);
		
		NDCGResult nDCGResult5 = new NDCGResult();
		nDCGResult5.nDCGRelevance=2;
		nDCGResult5.categories.add(new NDCGIACategory("c2", 0.0, 0.3));
		nDCGResult5.title="D10";
		list.results.add(nDCGResult5);
		
		NDCGResult nDCGResult6 = new NDCGResult();
		nDCGResult6.nDCGRelevance=2;
		nDCGResult6.categories.add(new NDCGIACategory("c1", 0.0, 0.7));
		nDCGResult6.title="D3";
		list.results.add(nDCGResult6);
		
		NDCGResult nDCGResult7 = new NDCGResult();
		nDCGResult7.nDCGRelevance=3;
		nDCGResult7.categories.add(new NDCGIACategory("c1", 0.0, 0.7));
		nDCGResult7.title="D4";
		
		list.results.add(nDCGResult7);
		NDCGResult nDCGResult8 = new NDCGResult();
		nDCGResult8.nDCGRelevance=2;
		nDCGResult8.categories.add(new NDCGIACategory("c1", 0.0, 0.7));
		nDCGResult8.title="D5";
		
		list.results.add(nDCGResult8);
		NDCGResult nDCGResult9 = new NDCGResult();
		nDCGResult9.nDCGRelevance=0;
		nDCGResult9.categories.add(new NDCGIACategory("c1", 0.0, 0.7));
		nDCGResult9.title="D6";
		list.results.add(nDCGResult9);
		
		NDCGResult nDCGResult10 = new NDCGResult();
		nDCGResult10.nDCGRelevance=0;
		nDCGResult10.categories.add(new NDCGIACategory("c1", 0.0, 0.7));
		nDCGResult10.title="D7";
		list.results.add(nDCGResult10);
		Double nDCGValue = ndcg.calcNDCGIA(list,5);
		//assert(nDCGValue == 0.9315085232327253);
		System.out.println(nDCGValue);
		assertEquals(nDCGValue, 0.7,1E-3);
	}
}

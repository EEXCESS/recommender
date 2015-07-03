package eu.eexcess.federatedrecommender.evaluation.picker;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;

import eu.eexcess.dataformats.evaluation.EvaluationResultList;
import eu.eexcess.dataformats.evaluation.EvaluationResultLists;
import eu.eexcess.dataformats.result.DocumentBadge;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.evaluation.picker.BlockPicker;
import junit.framework.TestCase;

public class BlockPickerTest extends TestCase {
	
	/**
	 * Tests if equal elements are removed
	 */
	@Test
	public void testEqualElement() {
		BlockPicker blockPicker = new BlockPicker();
		
		ResultList result= new ResultList();
		int numResults=10;
		ResultList parent = new ResultList();
		Result e = new Result();
		e.title="test1";
		e.documentBadge=new DocumentBadge("test1");
		
		parent.results.add(e);
		EvaluationResultList list = new EvaluationResultList(parent);
		SecureUserProfile secureUserProfile = null;
		
		blockPicker.getTopResults(secureUserProfile, list, numResults, result,numResults);
		ResultList result2 =  (ResultList) SerializationUtils.clone(result);
		blockPicker.getTopResults(secureUserProfile, list, numResults, result2,numResults);
		assertEquals(result, result2);
	}
	
	
	
	
	@Test
	public void testListArrengments() {
		BlockPicker blockPicker = new BlockPicker();
		Integer numResults=10;
		EvaluationResultLists resultLists = new EvaluationResultLists();
		EvaluationResultList resultList1 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL1T"+i;
			resultList1.results.add(result);	
		}
		resultList1.provider= "Basic";
		resultLists.results.add(resultList1);
		EvaluationResultList resultList2 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL2T"+i;
			resultList2.results.add(result);	
		}
		resultList2.provider="Diversity";
		resultLists.results.add(resultList2);
		EvaluationResultList resultList3 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL3T"+i;
			resultList3.results.add(result);	
		}
		resultList3.provider= "Serendipity";
		resultLists.results.add(resultList3);
		EvaluationResultList evalList= null;
		try {
			evalList =blockPicker.pickBlockResults(resultLists , numResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Result iterable_element : evalList.results) {
			System.out.println(iterable_element.title);
		}
		assertEquals(true, evalList.results.size()==10 );
		assertEquals(true, evalList.results.get(0).title.equals("RL1T0") );
		assertEquals(true, evalList.results.get(4).title.equals("RL2T0"));
	}
	
	
	@Test
	public void testListArrengmentsSizeTooSmall() {
		BlockPicker blockPicker = new BlockPicker();
		Integer numResults=10;
		EvaluationResultLists resultLists = new EvaluationResultLists();
		EvaluationResultList resultList1 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL1T"+i;
			resultList1.results.add(result);	
		}
		resultList1.provider= "Basic";
		resultLists.results.add(resultList1);
		EvaluationResultList resultList2 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 2; i++){
			Result result = new Result();
			result.title="RL2T"+i;
			resultList2.results.add(result);	
		}
		resultList2.provider="Diversity";
		resultLists.results.add(resultList2);
		EvaluationResultList resultList3 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 2; i++){
			Result result = new Result();
			result.title="RL3T"+i;
			resultList3.results.add(result);	
		}
		resultList3.provider= "Serendipity";
		resultLists.results.add(resultList3);
		EvaluationResultList evalList= null;
		try {
			evalList =blockPicker.pickBlockResults(resultLists , numResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Result iterable_element : evalList.results) {
			System.out.println(iterable_element.title);
		}
		System.out.println(evalList.provider);
		assertEquals(true, evalList.results.size()==10 );
		assertEquals(true, evalList.results.get(0).title.equals("RL1T0") );
		assertEquals(true, evalList.results.get(6).title.equals("RL2T0"));
		assertEquals(true, evalList.results.get(8).title.equals("RL3T0"));
	}
	
	
	@Test
	public void testListSize() {
		BlockPicker blockPicker = new BlockPicker();
		
		EvaluationResultLists resultLists = new EvaluationResultLists();
		EvaluationResultList resultList1 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL1T"+i;
			resultList1.results.add(result);	
		}
		resultList1.provider= "Basic";
		
		resultLists.results.add(resultList1);
		EvaluationResultList resultList2 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL2T"+i;
			resultList2.results.add(result);	
		}
		resultList2.provider="Diversity";
		resultLists.results.add(resultList2);
		EvaluationResultList resultList3 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL3T"+i;
			resultList3.results.add(result);	
		}
		resultList3.provider= "Serendipity";
		resultLists.results.add(resultList3);
		EvaluationResultList evalList= null;
		for(int i =0 ;i<90;i++){
		try {
			evalList =blockPicker.pickBlockResults(resultLists , i);
			assertEquals(i, evalList.results.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	}
	@Test
	public void testTopResultSize() {
		BlockPicker blockPicker = new BlockPicker();
		
//		EvaluationResultLists resultLists = new EvaluationResultLists();
		EvaluationResultList resultList1 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 200; i++){
			Result result = new Result();
			result.title="RL1T"+i;
			resultList1.results.add(result);	
		}
		
		
		for (int j = 0; j < resultList1.results.size(); j++) {
			ResultList result = new ResultList();
			blockPicker.getTopResults(null, resultList1, j, result ,j);
			assertEquals(j, result.results.size());
		}
		
		
	}
	
	@Test
	public void testListArrengmentsDiversityMissing() {
		System.out.println("DiversityBlockMissing");
		BlockPicker blockPicker = new BlockPicker();
		Integer numResults=10;
		EvaluationResultLists resultLists = new EvaluationResultLists();
		EvaluationResultList resultList1 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL1T"+i;
			resultList1.results.add(result);	
		}
		resultList1.provider= "Basic";
		resultLists.results.add(resultList1);

		EvaluationResultList resultList3 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL3T"+i;
			resultList3.results.add(result);	
		}
		resultList3.provider= "Serendipity";
		resultLists.results.add(resultList3);
		EvaluationResultList evalList= null;
		try {
			evalList =blockPicker.pickBlockResults(resultLists , numResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Result iterable_element : evalList.results) {
			System.out.println(iterable_element.title);
		}
		
		System.out.println("End DiversityBlockMissing");
		
		assertEquals(true, evalList.results.size()==10 );
		assertEquals(true, evalList.results.get(0).title.equals("RL1T0") );
		assertEquals(true, evalList.results.get(5).title.equals("RL3T0"));
		assertEquals(true, evalList.results.get(9).title.equals("RL3T4"));
	}
	
	@Test
	public void testListArrengmentsSerendipityMissing() {
		System.out.println("SerendipityBlockMissing");
		BlockPicker blockPicker = new BlockPicker();
		Integer numResults=10;
		EvaluationResultLists resultLists = new EvaluationResultLists();
		EvaluationResultList resultList1 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL1T"+i;
			resultList1.results.add(result);	
		}
		resultList1.provider= "Basic";
		resultLists.results.add(resultList1);
		EvaluationResultList resultList2 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL2T"+i;
			resultList2.results.add(result);	
		}
		resultList2.provider="Diversity";
		resultLists.results.add(resultList2);

		EvaluationResultList evalList= null;
		try {
			evalList =blockPicker.pickBlockResults(resultLists , numResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Result iterable_element : evalList.results) {
			System.out.println(iterable_element.title);
		}
		System.out.println("End SerendipityBlockMissing");
		assertEquals(true, evalList.results.size()==10 );
		assertEquals(true, evalList.results.get(0).title.equals("RL1T0"));
		assertEquals(true, evalList.results.get(5).title.equals("RL2T0"));
	}
	
	
	@Test
	public void testListArrengmentsBasicMissing() {
		System.out.println("BasicBlockMissing");
		BlockPicker blockPicker = new BlockPicker();
		Integer numResults=10;
		EvaluationResultLists resultLists = new EvaluationResultLists();
		EvaluationResultList resultList1 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL1T"+i;
			resultList1.results.add(result);	
		}
		resultList1.provider= "Basic";
		resultLists.results.add(resultList1);
		EvaluationResultList resultList2 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL2T"+i;
			resultList2.results.add(result);	
		}
		resultList2.provider="Diversity";
		resultLists.results.add(resultList2);

		EvaluationResultList evalList= null;
		try {
			evalList =blockPicker.pickBlockResults(resultLists , numResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Result iterable_element : evalList.results) {
			System.out.println(iterable_element.title);
		}
		System.out.println(evalList.provider);
		System.out.println("End BasicBlockMissing");
		assertEquals(true, evalList.results.size()==10 );
		
	}
	
	@Test
	public void testListArrengmentsDuplicates() {
		BlockPicker blockPicker = new BlockPicker();
		Integer numResults=10;
		EvaluationResultLists resultLists = new EvaluationResultLists();
		EvaluationResultList resultList1 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="RL1T"+i;
			resultList1.results.add(result);	
		}
		resultList1.provider= "Basic";
		resultLists.results.add(resultList1);
		
		EvaluationResultList resultList2 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			if(i<4)
				result.title="RL1T"+i;
			else
				result.title="RL2T"+i;
			resultList2.results.add(result);	
		}
		resultList2.provider="Diversity";
		resultLists.results.add(resultList2);
		
		EvaluationResultList resultList3 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			if(i<4)
				result.title="RL1T"+i;
			else
				result.title="RL3T"+i;
			resultList3.results.add(result);	
		}
		resultList3.provider= "Serendipity";
		resultLists.results.add(resultList3);
		
		EvaluationResultList evalList= null;
		try {
			evalList =blockPicker.pickBlockResults(resultLists , numResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Result iterable_element : evalList.results) {
			System.out.println(iterable_element.title);
		}
		assertEquals(true, evalList.results.size()==10 );
		assertEquals(true, evalList.results.get(0).title.equals("RL1T0"));
		assertEquals(true, evalList.results.get(4).title.equals("RL2T4"));
		assertEquals(true, evalList.results.get(7).title.equals("RL3T4"));
	}
	
	
	@Test
	public void testListArrengmentsNearDuplicates() {
		BlockPicker blockPicker = new BlockPicker();
		Integer numResults=10;
		EvaluationResultLists resultLists = new EvaluationResultLists();
		EvaluationResultList resultList1 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="Result One "+i;
			result.description="DescriptionROne"+i;
			resultList1.results.add(result);	
		}
		resultList1.results.get(0).description=" In 1984 he killed a bartender during a robbery in Salt Lake City, and the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad in the U.S. in 14 years, became a focus of media attention in June 2010. ";
		resultList1.results.get(1).description=" In 1984 he killed a bartender during a robbery in Salt Lake City, and the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad in the U.S. in 14 years, became a focus of media attention in June 2010. ";
		resultList1.results.get(2).description=" In 1984 he killed a bartender during a robbery in Salt Lake City, and the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad in the U.S. in 14 years, became a focus of media attention in June 2010. ";
		resultList1.results.get(3).description=" In 1984 he killed a bartender during a robbery in Salt Lake City, and the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad in the U.S. in 14 years, became a focus of media attention in June 2010. ";
		resultList1.provider= "Basic";
		resultLists.results.add(resultList1);
		
		EvaluationResultList resultList2 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="Result Two "+i;
			result.description="DescriptionRTwo"+i;
			resultList2.results.add(result);	
			
		}
		resultList2.results.get(0).description=" In 1984 she killed an bartender during a robbery in Salt Lake City, and the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad in the U.S. in 14 years, became a focus of media attention in June 2010. ";
		resultList2.results.get(1).description=" In 1984 she killed an bartender during a robbery in Salt Lake City, and the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad in the U.S. in 14 years, became a focus of media attention in June 2010. ";
		resultList2.results.get(2).description=" In 1984 she killed an bartender during a robbery in Salt Lake City, and the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad in the U.S. in 14 years, became a focus of media attention in June 2010. ";
		resultList2.results.get(3).description=" In 1984 she killed an bartender during a robbery in Salt Lake City, and the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad in the U.S. in 14 years, became a focus of media attention in June 2010. ";
		resultList2.provider="Diversity";
		resultLists.results.add(resultList2);
		
		EvaluationResultList resultList3 = new EvaluationResultList(new ResultList());
		for(int i = 0; i< 30; i++){
			Result result = new Result();
			result.title="Result Three "+i;
			result.description="DescriptionRThree"+i;
			resultList3.results.add(result);	
		}
		resultList3.provider= "Serendipity";
		resultList3.results.get(0).description=" In 1984 we killed an burtender during a robberi in Solt Lake City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
		resultList3.results.get(1).description=" In 2030 we will kill an burtender during a robberi in New York City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
		resultList3.results.get(2).description=" In 1984 we killed an burtender during a robberi in New York City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
		
		resultLists.results.add(resultList3);
		
		EvaluationResultList evalList= null;
		try {
			evalList =blockPicker.pickBlockResults(resultLists , numResults);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Result iterable_element : evalList.results) {
			System.out.println(iterable_element.title);
			System.out.println(iterable_element.description);
		}
		assertEquals(true, evalList.results.size()==10 );
		assertEquals(true, evalList.results.get(0).title.equals("Result One 0"));
		assertEquals(true, evalList.results.get(4).title.equals("Result Two 4"));
		assertEquals(true, evalList.results.get(7).title.equals("Result Three 1"));
		assertEquals(true, evalList.results.get(8).title.equals("Result Three 3"));
	}
}

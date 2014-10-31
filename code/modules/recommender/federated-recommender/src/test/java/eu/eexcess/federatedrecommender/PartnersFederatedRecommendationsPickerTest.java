package eu.eexcess.federatedrecommender;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.federatedrecommender.dataformats.PFRChronicle;
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations;
import eu.eexcess.federatedrecommender.interfaces.PartnersFederatedRecommendationsPicker;
import eu.eexcess.federatedrecommender.picker.OccurrenceProbabilityPicker;

public class PartnersFederatedRecommendationsPickerTest {

	@Test
	public void firstPickerTest() {
		PartnersFederatedRecommendationsPicker picker = new OccurrenceProbabilityPicker();
		PFRChronicle pFRChronicle = new PFRChronicle();
		PartnersFederatedRecommendations partnersFederatedRecommendations=new PartnersFederatedRecommendations();
		PartnersFederatedRecommendations partnersFederatedRecommendations2=new PartnersFederatedRecommendations();
		Result result1 = new Result();
		result1.collectionName="a";
		result1.description=" description result1";
		Result result2 = new Result();
		result2.collectionName="b";
		result2.description=" description result2";
		Result result3 = new Result();
		result3.collectionName="c";
		result3.description=" description result3";
		Result result5 = new Result();
		result5.collectionName="d";
		result5.description=" description result5";
		Result result6 = new Result();
		result6.collectionName="e";
		result6.description=" description result6";
		Result result7 = new Result();
		result7.collectionName="f";
		result7.description=" description result7";
		
		ResultList rList = new ResultList();
		rList.results.add(result1);
		rList.results.add(result2);
		rList.results.add(result5 );
		rList.results.add(result6 );
		rList.results.add(result7 );
		
		rList.results.add(result3 );
		rList.results.add(result3 );
		rList.results.add(result3 );
		rList.results.add(result3 );
		rList.results.add(result3 );
		rList.results.add(result3 );
		rList.results.add(result3 );
		ResultList rList2 = new ResultList();
		rList2.results.add(result1);
		rList2.results.add(result2);
		ResultList rList3 = new ResultList();
		rList3.results.add(result1);
		ResultList rList4 = new ResultList();
		rList4.results.add(result7);
		rList4.results.add(result7);
		rList4.results.add(result7);
		rList4.results.add(result7);
		rList4.results.add(result7);
		rList4.results.add(result7);
		rList4.results.add(result7);
		rList4.results.add(result7);
		rList4.results.add(result7);
		rList4.results.add(result7);
		partnersFederatedRecommendations.getResults().put(new PartnerBadge(),rList );
		partnersFederatedRecommendations.getResults().put(new PartnerBadge(),rList2 );
		partnersFederatedRecommendations2.getResults().put(new PartnerBadge(),rList2 );
		partnersFederatedRecommendations2.getResults().put(new PartnerBadge(),rList4 );
		partnersFederatedRecommendations.getResults().put(new PartnerBadge(),rList3 );
		pFRChronicle.addRecommendations(partnersFederatedRecommendations);
		pFRChronicle.addRecommendations(partnersFederatedRecommendations2);
		ResultList result= picker.pickResults(pFRChronicle,10);
		assertEquals(result7, result.results.get(0));
	}
	@Test
	public void secondPickerTest() {
		PartnersFederatedRecommendationsPicker picker = new OccurrenceProbabilityPicker();
		PFRChronicle pFRChronicle = new PFRChronicle();
		PartnersFederatedRecommendations partnersFederatedRecommendations=new PartnersFederatedRecommendations();
		PartnersFederatedRecommendations partnersFederatedRecommendations2=new PartnersFederatedRecommendations();
		Result result1 = new Result();
		result1.collectionName="a";
		result1.description=" description result1";
		Result result2 = new Result();
		result2.collectionName="b";
		result2.description=" description result2";
		Result result3 = new Result();
		result3.collectionName="c";
		result3.description=" description result3";
		Result result5 = new Result();
		result5.collectionName="d";
		result5.description=" description result5";
		Result result6 = new Result();
		result6.collectionName="e";
		result6.description=" description result6";
		Result result7 = new Result();
		result7.collectionName="f";
		result7.description=" description result7";
		
		ResultList rList = new ResultList();
		rList.results.add(result1);
		rList.results.add(result2);
		rList.results.add(result5 );
		rList.results.add(result6 );
		rList.results.add(result7 );
		
		rList.results.add(result3 );
		rList.results.add(result3 );
		rList.results.add(result3 );
		rList.results.add(result3 );
		rList.results.add(result3 );
		rList.results.add(result3 );
		rList.results.add(result3 );
		ResultList rList2 = new ResultList();
		rList2.results.add(result1);
		rList2.results.add(result2);
		ResultList rList3 = new ResultList();
		rList3.results.add(result1);
		ResultList rList4 = new ResultList();

		partnersFederatedRecommendations.getResults().put(new PartnerBadge(),rList );
		partnersFederatedRecommendations.getResults().put(new PartnerBadge(),rList2 );
		partnersFederatedRecommendations2.getResults().put(new PartnerBadge(),rList2 );
		partnersFederatedRecommendations2.getResults().put(new PartnerBadge(),rList4 );
		partnersFederatedRecommendations.getResults().put(new PartnerBadge(),rList3 );
		pFRChronicle.addRecommendations(partnersFederatedRecommendations);
		pFRChronicle.addRecommendations(partnersFederatedRecommendations2);
		ResultList result= picker.pickResults(pFRChronicle,10);
		assertEquals(result3, result.results.get(0));
	}
	

}

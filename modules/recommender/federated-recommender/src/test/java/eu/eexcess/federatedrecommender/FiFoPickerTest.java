package eu.eexcess.federatedrecommender;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.dataformats.PartnersFederatedRecommendations;
import eu.eexcess.federatedrecommender.picker.FiFoPicker;

public class FiFoPickerTest {

    @Test
    public void standartTest() {
        FiFoPicker picker = new FiFoPicker();
        int numResults = 10;
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();
        PartnerBadge badge1 = new PartnerBadge();
        badge1.setSystemId("Partner1");
        partners.add(badge1);
        PartnerBadge badge2 = new PartnerBadge();
        badge2.setSystemId("Partner2");
        partners.add(badge2);

        ResultList resultList1 = new ResultList();
        for (int i = 0; i < 30; i++) {
            Result result = new Result();
            result.title = "RL1T" + (i + 1);
            resultList1.results.add(result);
        }
        resultList1.provider = "Diversity";

        ResultList resultList2 = new ResultList();
        for (int i = 0; i < 30; i++) {
            Result result = new Result();
            result.title = "RL2T" + (i + 1);
            resultList2.results.add(result);
        }
        resultList2.provider = "Serendipity";
        ResultList evalList = null;
        PartnersFederatedRecommendations resultList = new PartnersFederatedRecommendations();
        resultList.getResults().put(badge1, resultList1);
        resultList.getResults().put(badge2, resultList2);

        try {
            SecureUserProfile profile = null;
            evalList = picker.pickResults(profile, resultList, partners, numResults);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Result iterable_element : evalList.results) {
            System.out.println(iterable_element.title);
        }
        assertEquals(true, evalList.results.size() == 10);
        assertEquals(true, evalList.results.get(0).title.equals("RL1T1"));
        assertEquals(true, evalList.results.get(1).title.equals("RL2T1"));
        assertEquals(true, evalList.results.get(2).title.equals("RL1T2"));
        assertEquals(true, evalList.results.get(3).title.equals("RL2T2"));
        assertEquals(true, evalList.results.get(4).title.equals("RL1T3"));
        assertEquals(true, evalList.results.get(5).title.equals("RL2T3"));
        assertEquals(true, evalList.results.get(6).title.equals("RL1T4"));
        assertEquals(true, evalList.results.get(7).title.equals("RL2T4"));
        assertEquals(true, evalList.results.get(8).title.equals("RL1T5"));
        assertEquals(true, evalList.results.get(9).title.equals("RL2T5"));

    }

    @Test
    public void fuzzyTest() {
        FiFoPicker picker = new FiFoPicker();
        int numResults = 10;
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();
        PartnerBadge badge1 = new PartnerBadge();
        badge1.setSystemId("Partner1");
        partners.add(badge1);
        PartnerBadge badge2 = new PartnerBadge();
        badge2.setSystemId("Partner2");
        partners.add(badge2);

        ResultList resultList1 = new ResultList();
        for (int i = 0; i < 30; i++) {
            Result result = new Result();
            result.title = "RL1T" + (i + 1);
            if (i < 2) {
                result.description = " In 1984 we killed an burtender during a robberi in Solt Lake City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
            } else if (i < 5) {
                result.description = " In 2030 we will kill an burtender during a robberi in New York City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
            } else if (i < 9) {
                result.description = " In 1984 we killed an burtender during a robberi in New York City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
            }

            resultList1.results.add(result);
        }
        resultList1.provider = "Diversity";

        ResultList resultList2 = new ResultList();
        for (int i = 0; i < 30; i++) {
            Result result = new Result();
            result.title = "RL2T" + (i + 1);
            if (i < 2) {
                result.description = " In 1984 we killed an burtender during a robberi in Solt Lake City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
            } else if (i < 5) {
                result.description = " In 2030 we will kill an burtender during a robberi in New York City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
            } else if (i < 9) {
                result.description = " In 1984 we killed an burtender during a robberi in New York City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
            }
            resultList2.results.add(result);
        }
        resultList2.provider = "Serendipity";
        ResultList evalList = null;
        PartnersFederatedRecommendations resultList = new PartnersFederatedRecommendations();
        resultList.getResults().put(badge1, resultList1);
        resultList.getResults().put(badge2, resultList2);

        try {
            SecureUserProfile profile = null;
            evalList = picker.pickResults(profile, resultList, partners, numResults);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Result iterable_element : evalList.results) {
            System.out.println(iterable_element.title + " " + iterable_element.description + " " + iterable_element.resultGroup);
        }
        assertEquals(true, evalList.results.size() == 10);
        assertEquals(true, evalList.results.get(0).title.equals("RL1T1"));
        assertEquals(true, evalList.results.get(1).title.equals("RL1T3"));
        assertEquals(true, evalList.results.get(2).title.equals("RL1T10"));
        assertEquals(true, evalList.results.get(3).title.equals("RL2T10"));
        assertEquals(true, evalList.results.get(4).title.equals("RL1T11"));
        assertEquals(true, evalList.results.get(5).title.equals("RL2T11"));
        assertEquals(true, evalList.results.get(6).title.equals("RL1T12"));
        assertEquals(true, evalList.results.get(7).title.equals("RL2T12"));
        assertEquals(true, evalList.results.get(8).title.equals("RL1T13"));
        assertEquals(true, evalList.results.get(9).title.equals("RL2T13"));

    }

    @Test
    public void fuzzyTooLessToPickTest() {
        FiFoPicker picker = new FiFoPicker();
        int numResults = 100;
        List<PartnerBadge> partners = new ArrayList<PartnerBadge>();
        PartnerBadge badge1 = new PartnerBadge();
        badge1.setSystemId("Partner1");
        partners.add(badge1);
        PartnerBadge badge2 = new PartnerBadge();
        badge2.setSystemId("Partner2");
        partners.add(badge2);

        ResultList resultList1 = new ResultList();
        for (int i = 0; i < 30; i++) {
            Result result = new Result();
            result.title = "RL1T" + (i + 1);
            if (i < 2) {
                result.description = " In 1984 we killed an burtender during a robberi in Solt Lake City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
            } else if (i < 5) {
                result.description = " In 2030 we will kill an burtender during a robberi in New York City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
            } else if (i < 9) {
                result.description = " In 1984 we killed an burtender during a robberi in New York City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
            }

            resultList1.results.add(result);
        }
        resultList1.provider = "Diversity";

        ResultList resultList2 = new ResultList();
        for (int i = 0; i < 30; i++) {
            Result result = new Result();
            result.title = "RL2T" + (i + 1);
            if (i < 2) {
                result.description = " In 1984 we killed an burtender during a robberi in Solt Lake City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
            } else if (i < 5) {
                result.description = " In 2030 we will kill an burtender during a robberi in New York City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
            } else if (i < 9) {
                result.description = " In 1984 we killed an burtender during a robberi in New York City, with in the next year killed an attorney in an unsuccessful escape attempt. He was sentenced to life imprisonment for the first murder and received the death penalty for the second. In a series of appeals, defense attorneys presented mitigating evidence of the troubled upbringing of Gardner, who had spent nearly his entire adult life in incarceration. His legal team took the case all the way to the U.S. Supreme Court, which declined to intervene. His execution at Utah State Prison, the first to be carried out by firing squad of the U.S. in 14 years, became a focus on media attention in June 2010. ";
            }
            resultList2.results.add(result);
        }
        resultList2.provider = "Serendipity";
        ResultList evalList = null;
        PartnersFederatedRecommendations resultList = new PartnersFederatedRecommendations();
        resultList.getResults().put(badge1, resultList1);
        resultList.getResults().put(badge2, resultList2);

        try {
            SecureUserProfile profile = null;
            evalList = picker.pickResults(profile, resultList, partners, numResults);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Result iterable_element : evalList.results) {
            System.out.println(iterable_element.title + " " + iterable_element.description);
        }
        assertEquals(true, evalList.results.size() == 44);
        assertEquals(true, evalList.results.get(0).title.equals("RL1T1"));
        assertEquals(true, evalList.results.get(1).title.equals("RL1T3"));
        assertEquals(true, evalList.results.get(2).title.equals("RL1T10"));
        assertEquals(true, evalList.results.get(3).title.equals("RL2T10"));
        assertEquals(true, evalList.results.get(4).title.equals("RL1T11"));
        assertEquals(true, evalList.results.get(5).title.equals("RL2T11"));
        assertEquals(true, evalList.results.get(6).title.equals("RL1T12"));
        assertEquals(true, evalList.results.get(7).title.equals("RL2T12"));
        assertEquals(true, evalList.results.get(8).title.equals("RL1T13"));
        assertEquals(true, evalList.results.get(9).title.equals("RL2T13"));

    }

}

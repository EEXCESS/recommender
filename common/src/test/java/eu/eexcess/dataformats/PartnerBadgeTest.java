package eu.eexcess.dataformats;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PartnerBadgeTest {

    @Test public void testUpdatePartnerResponseTimeSimple() throws Exception {
        PartnerBadge partnerBadge = new PartnerBadge();
        long respTime = 3000;
        partnerBadge.updatePartnerResponseTime(respTime);
        assertTrue(Long.compare(respTime, partnerBadge.getShortTimeResponseTime()) == 0);
    }

    @Test public void testUpdatePartnerResponseTime2Times() throws Exception {
        PartnerBadge partnerBadge = new PartnerBadge();
        long respTime = 3000;
        long respTime2 = 0;
        partnerBadge.updatePartnerResponseTime(respTime);
        partnerBadge.updatePartnerResponseTime(respTime2);
        assertTrue(Long.compare((respTime + respTime2) / 2, partnerBadge.getShortTimeResponseTime()) == 0);
    }

    @Test public void testUpdatePartnerResponseTime3Times() throws Exception {
        PartnerBadge partnerBadge = new PartnerBadge();
        long respTime = 3000;
        long respTime2 = 0;
        long respTime3 = 150;
        final long time2 = (respTime + respTime2) / 2;

        partnerBadge.updatePartnerResponseTime(respTime);
        partnerBadge.updatePartnerResponseTime(respTime2);
        assertTrue(Long.compare(time2, partnerBadge.getShortTimeResponseTime()) == 0);
        partnerBadge.updatePartnerResponseTime(respTime3);
        final long time3 = (time2 + respTime3) / 2;
        assertTrue(Long.compare(time3, partnerBadge.getShortTimeResponseTime()) == 0);
    }

}

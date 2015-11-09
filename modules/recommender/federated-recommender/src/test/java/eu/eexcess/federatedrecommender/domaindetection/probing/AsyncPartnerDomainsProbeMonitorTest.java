/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Raoul Rubien
 */

package eu.eexcess.federatedrecommender.domaindetection.probing;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerDomain;
import eu.eexcess.federatedrecommender.domaindetection.AsyncPartnerDomainsProbeMonitor;
import eu.eexcess.federatedrecommender.domaindetection.AsyncPartnerDomainsProbeMonitor.ProbeResultChanged;
import eu.eexcess.federatedrecommender.registration.PartnerRegister;

public class AsyncPartnerDomainsProbeMonitorTest implements ProbeResultChanged {

    private static class TestableAsyncPartnerDomainsProbeMonitor extends AsyncPartnerDomainsProbeMonitor {

        int numFailedProbeCallbacks = 0;
        int numProbeCallbacks = 0;

        public TestableAsyncPartnerDomainsProbeMonitor(File wordnetDir, File wordNetFile, int numRandomPhrases, int considerNumPartnerResults,
                long probeDurationTimeout) {
            super(wordnetDir, wordNetFile, numRandomPhrases, considerNumPartnerResults, probeDurationTimeout);
        }

        public int numberOfRunningProbes() {
            synchronized (runningProbes) {
                return runningProbes.size();
            }
        }

        public void blockUntilThreadsFinished() {
            System.out.print("waiting for termination ");
            while (numberOfRunningProbes() > 0) {
                synchronized (Thread.currentThread()) {
                    try {
                        Thread.currentThread().wait(100);
                    } catch (InterruptedException e) {
                    }
                }
                System.out.print("#");
            }
            System.out.print(" done.");
        }

        @Override
        public void onProbeFailedCallback(String partnerId) {
            super.onProbeFailedCallback(partnerId);
            numFailedProbeCallbacks++;
        }

        @Override
        public void onProbeDoneCallback(String partnerId, Set<PartnerDomain> probeResult) {
            super.onProbeDoneCallback(partnerId, probeResult);
            numProbeCallbacks++;
        }
    }

    private static PartnerRegister partnerRegister = new PartnerRegister();

    private List<Map<String, Set<PartnerDomain>>> probeResults = new LinkedList<Map<String, Set<PartnerDomain>>>();

    public static TestableAsyncPartnerDomainsProbeMonitor newProbeMonitor(int numPrases, long timeout) throws DomainDetectorException {
        return new TestableAsyncPartnerDomainsProbeMonitor(new File("/opt/data/wordnet/WordNet-2.0/dict/"), new File(
                "/opt/data/wordnet-domains/wn-domains-3.2/wn-domains-3.2-20070223"), numPrases, 5, timeout);
    }

    @BeforeClass
    public static void setupPartners() {
        Map<String, String> partners = new HashMap<String, String>();
        partners.put("Opensearch", "http://127.0.0.1/eexcess-partner-reference-opensearch-1.0-SNAPSHOT/partner/");
        partners.put("Mendeley", "http://127.0.0.1/eexcess-partner-mendeley-1.0-SNAPSHOT/partner/");
        partners.put("ZBW", "http://127.0.0.1/eexcess-partner-zbw-1.0-SNAPSHOT/partner/");
        partners.put("Europeana", "http://127.0.0.1/eexcess-partner-europeana-1.0-SNAPSHOT/partner/");
        partners.put("Wissenmedia", "http://127.0.0.1/eexcess-partner-wissenmedia-1.0-SNAPSHOT/partner/");

        for (Map.Entry<String, String> entry : partners.entrySet()) {
            PartnerBadge partner = new PartnerBadge();
            partner.setSystemId(entry.getKey());
            partner.setPartnerConnectorEndpoint(entry.getValue());
            AsyncPartnerDomainsProbeMonitorTest.partnerRegister.addPartner(partner);
        }
    }

    @Test
    public void probe_startDoubleProbing_oneRefused() throws DomainDetectorException {

        PartnerBadge foo = partnerRegister.getPartners().get(0);
        TestableAsyncPartnerDomainsProbeMonitor probeMonitor = newProbeMonitor(5, 60 * 1000);
        assertEquals(0, probeMonitor.numberOfRunningProbes());
        // request a probe
        probeMonitor.probe(foo, partnerRegister.getClient(foo.getSystemId()));
        // expect to be scheduled
        assertEquals(1, probeMonitor.numberOfRunningProbes());

        // request the same probe a 2nd time
        probeMonitor.probe(foo, partnerRegister.getClient(foo.getSystemId()));
        // expect not to be scheduled
        assertEquals(1, probeMonitor.numberOfRunningProbes());

        probeMonitor.blockUntilThreadsFinished();
    }

    @Test
    public void probe_startLongProbe_expectTimeout() throws DomainDetectorException {
        TestableAsyncPartnerDomainsProbeMonitor probeMonitor = newProbeMonitor(15, 100);
        PartnerBadge foo = partnerRegister.getPartners().get(0);
        probeMonitor.probe(foo, partnerRegister.getClient(foo.getSystemId()));
        probeMonitor.blockUntilThreadsFinished();
        assertThat(probeMonitor.numFailedProbeCallbacks, greaterThan(0));
    }

    @Test
    public void probe_severalProbes_expectAllCallbacks() throws DomainDetectorException {

        TestableAsyncPartnerDomainsProbeMonitor probeMonitor = newProbeMonitor(2, 4 * 1000);
        probeMonitor.setCallback(this);

        for (PartnerBadge foo : partnerRegister.getPartners()) {
            probeMonitor.probe(foo, partnerRegister.getClient(foo.getSystemId()));
        }

        probeMonitor.blockUntilThreadsFinished();
        assertEquals(probeMonitor.numFailedProbeCallbacks, 0);
        assertEquals(probeResults.size(), probeMonitor.numProbeCallbacks);

        int n = 0;
        for (Map<String, Set<PartnerDomain>> resultMap : probeResults) {
            n++;
            for (Map.Entry<String, Set<PartnerDomain>> entry : resultMap.entrySet()) {
                StringBuilder info = new StringBuilder("result [" + n + "] [" + entry.getKey() + "] => {");
                for (PartnerDomain domain : entry.getValue()) {
                    info.append("[" + domain.getName() + "=" + domain.getWeight() + "] ");
                }
                System.out.println(info + "}");
            }
        }
    }

    @Override
    public void onProbeResultsChanged(Map<String, Set<PartnerDomain>> updatedProbes) {
        probeResults.add(updatedProbes);
    }
}

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

package eu.eexcess.federatedrecommender.domaindetection;

import com.sun.jersey.api.client.Client;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.PartnerDomain;
import eu.eexcess.federatedrecommender.domaindetection.AsyncPartnerDomainsProbe.ProbeDoneCallback;
import eu.eexcess.federatedrecommender.domaindetection.probing.DomainDetector;
import eu.eexcess.federatedrecommender.domaindetection.probing.DomainDetectorException;
import eu.eexcess.federatedrecommender.domaindetection.probing.PartnerDomainsProbe;
import eu.eexcess.federatedrecommender.domaindetection.wordnet.WordnetDomainsDetector;
import eu.eexcess.federatedrecommender.registration.PartnerRegister;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;



public class AsyncPartnerDomainsProbeTest {

    private static PartnerRegister partnerRegister = new PartnerRegister();
    private static DomainDetector detector = null;

    @BeforeClass
    public static void setupDomainDetector() throws DomainDetectorException {
        AsyncPartnerDomainsProbeTest.detector = new WordnetDomainsDetector(new File("/opt/data/wordnet/WordNet-2.0/dict/"), new File(
                "/opt/data/wordnet-domains/wn-domains-3.2/wn-domains-3.2-20070223"), true);
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
            AsyncPartnerDomainsProbeTest.partnerRegister.addPartner(partner);
        }
    }

    @Test
    public void probePartner_expectTimeout() throws Exception {

        PartnerBadge partnerConfig = partnerRegister.getPartners().get(0);
        Client partnerClient = partnerRegister.getClient(partnerConfig.getSystemId());
        PartnerDomainsProbe partnerProbe = new PartnerDomainsProbe(detector, 10);
        AsyncPartnerDomainsProbe prober = new AsyncPartnerDomainsProbe(partnerConfig, partnerClient, partnerProbe, 30);
        Callback callback = new Callback(Thread.currentThread());
        prober.addCallback(callback);

        prober.probeAsyncronous();

        assertTrue(prober.isRunning());

        System.out.print("waiting for termination of probe #");
        while (prober.isRunning()) {
            synchronized (Thread.currentThread()) {
                Thread.currentThread().wait(150);
            }
            System.out.print("#");
        }
        System.out.print(" done.");
        // expect the controller to have stopped the task and then have
        // terminated itself
        assertFalse(prober.isRunning());
        // expect asynchronous probing not to be ready until now
        assertEquals(callback.numCallbacks, 0);
    }

    private static class Callback implements ProbeDoneCallback {

        public int numCallbacks = 0;
        public Map<Integer, Set<PartnerDomain>> results = new HashMap<Integer, Set<PartnerDomain>>();
        private Thread testThread;

        public Callback(Thread testThread) {
            this.testThread = testThread;
        }

        @Override
        public void onProbeDoneCallback(String partnerId, Set<PartnerDomain> probeResult) {
            System.out.println("received probe finished callback");
            if (testThread != null) {
                synchronized (testThread) {
                    results.put(++numCallbacks, probeResult);

                    testThread.notify();
                }
            }
        }

        @Override
        public void onProbeFailedCallback(String partnerId) {
            System.out.println("received probe failed callback");
            if (testThread != null) {
                synchronized (testThread) {
                    testThread.notify();
                }
            }
        }
    }
}

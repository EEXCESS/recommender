/* Copyright (C) 2010 
"Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
(Know-Center), Graz, Austria, office@know-center.at.

Licensees holding valid Know-Center Commercial licenses may use this file in
accordance with the Know-Center Commercial License Agreement provided with 
the Software or, alternatively, in accordance with the terms contained in
a written agreement between Licensees and Know-Center.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.eexcess.federatedrecommender.domaindetection;

import at.knowcenter.ie.Language;
import at.knowcenter.ie.languagedetection.LanguageDetector;
import at.knowcenter.util.term.TermSet;
import at.knowcenter.util.term.TypedTerm;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import eu.eexcess.dataformats.PartnerBadge;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.dataformats.userprofile.ContextKeyword;
import eu.eexcess.dataformats.userprofile.SecureUserProfile;
import eu.eexcess.federatedrecommender.domaindetection.probing.Domain;
import eu.eexcess.federatedrecommender.domaindetection.probing.DomainDetector;
import eu.eexcess.federatedrecommender.domaindetection.wordnet.WordnetDomainsDetector;
import eu.eexcess.federatedrecommender.registration.PartnerRegister;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author rkern@know-center.at
 */
public class PartnerToDomainTest {
    private static final Logger logger = Logger.getLogger(PartnerToDomainTest.class.getName());

    private final PartnerRegister partnerRegister = new PartnerRegister();

    /**
     * Creates a new instance of this class.
     */

    public PartnerToDomainTest() {
        String zbw = null;
        // String europeana = null, mendeley = null, kimCollect = null,
        // wissensserver = null;

        // europeana =
        // "http://localhost:38080/eexcess-partner-europeana-1.0-SNAPSHOT/partner/recommend/";

        // europeana =
        // "http://eexcess-dev.joanneum.at/eexcess-partner-europeana-1.0-SNAPSHOT/partner/recommend/";
        // mendeley =
        // "http://eexcess-dev.joanneum.at/eexcess-partner-mendeley-1.0-SNAPSHOT/partner/recommend/";
        zbw = "http://eexcess-dev.joanneum.at/eexcess-partner-zbw-1.0-SNAPSHOT/partner/recommend/";
        // kimCollect =
        // "http://eexcess-dev.joanneum.at/eexcess-partner-kim-collect-1.0-SNAPSHOT/partner/recommend/";
        // wissensserver =
        // "http://eexcess-dev.joanneum.at/eexcess-partner-wissenmedia-1.0-SNAPSHOT/partner/recommend/";

        // if (false) {
        // PartnerBadge badge = new PartnerBadge();
        // badge = new PartnerBadge();
        // badge.setSystemId("Europeana");
        // badge.setPartnerConnectorEndpoint(europeana);
        // badge.setTags(new ArrayList<String>() {/**
        // *
        // */
        // private static final long serialVersionUID = 1L;
        //
        // {
        // add("Europe");
        // add("Culture");
        // }});
        // partnerRegister.addPartner(badge);
        // }

        // if (false) {
        // PartnerBadge badge2 = new PartnerBadge();
        // badge2 = new PartnerBadge();
        // badge2.setSystemId("Mendeley");
        // badge2.setPartnerConnectorEndpoint(mendeley);
        // badge2.setTags(new ArrayList<String>() {
        // /**
        // *
        // */
        // private static final long serialVersionUID = 1L;
        //
        // {
        // add("Science");
        // add("Journals");
        // }
        // });
        // partnerRegister.addPartner(badge2);
        // }
        PartnerBadge badge3 = new PartnerBadge();
        badge3.setSystemId("ZBW");
        badge3.setPartnerConnectorEndpoint(zbw);
        badge3.setTags(new ArrayList<String>() {
            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            {
                add("Economy");
                add("Articles");
            }
        });
        partnerRegister.addPartner(badge3);

        // if (false) {
        // PartnerBadge badge4 = new PartnerBadge();
        // badge4.setSystemId("KIMCollect");
        // badge4.setPartnerConnectorEndpoint(kimCollect);
        // badge4.setTags(new ArrayList<String>() {
        // /**
        // *
        // */
        // private static final long serialVersionUID = 1L;
        //
        // {
        // add("Swiss");
        // add("Culture");
        // }
        // });
        // partnerRegister.addPartner(badge4);
        // }

        // if (false) {
        // PartnerBadge badge5 = new PartnerBadge();
        // badge5.setSystemId("Wissenmedia");
        // badge5.setPartnerConnectorEndpoint(wissensserver);
        // badge5.setTags(new ArrayList<String>() {
        // /**
        // *
        // */
        // private static final long serialVersionUID = 1L;
        //
        // {
        // add("Articles");
        // add("Culture");
        // }
        // });
        // partnerRegister.addPartner(badge5);
        // }
    }

    public static void main(String[] args) throws Exception {
        PartnerToDomainTest parnerToDomainTest = new PartnerToDomainTest();
        parnerToDomainTest.test();
    }

    private ResultList getPartnerResult(PartnerBadge partner, SecureUserProfile secureUserProfile) {
        ResultList resultList = new ResultList();

        Client client = partnerRegister.getClient(partner.getSystemId());
        if (client != null) {
            try {
                WebResource resource = client.resource(partner.getPartnerConnectorEndpoint());
                resource.accept(MediaType.APPLICATION_JSON);

                resultList = resource.post(ResultList.class, secureUserProfile);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Partner: " + partner.getSystemId() + " is not working currently.", e);
            }

        }
        return resultList;
    }

    public void test() throws Exception {
        // WordnetDomainsDetector domainDetector = new WordnetDomainsDetector(
        // new File("/opt/data/wordnet/WordNet-3.0/dict"),
        // new File("/opt/data/wordnet-domains/xwnd/xwnd-30g"), false);
        DomainDetector domainDetector = new WordnetDomainsDetector(new File("/opt/data/wordnet/WordNet-2.0/dict"), new File(
                "/opt/data/wordnet-domains/wn-domains-3.2/wn-domains-3.2-20070223"), true);

        Language[] languages = new Language[] { Language.English, Language.French, Language.German, Language.Italian, Language.Spanish };
        LanguageDetector languageDetector = new LanguageDetector(languages);

        List<PartnerBadge> partners = partnerRegister.getPartners();
        Map<String, TermSet<TypedTerm>> partnerToDomainToCount = new HashMap<String, TermSet<TypedTerm>>();
        for (PartnerBadge partner : partners) {
            partnerToDomainToCount.put(partner.getSystemId(), new TermSet<TypedTerm>(new TypedTerm.AddingWeightTermMerger()));
        }
        Map<String, TermSet<TypedTerm>> partnerToLanguageToCount = new HashMap<String, TermSet<TypedTerm>>();
        for (PartnerBadge partner : partners) {
            partnerToLanguageToCount.put(partner.getSystemId(), new TermSet<TypedTerm>(new TypedTerm.AddingWeightTermMerger()));
        }

        int maxWords = 1000;
        int maxResults = 10;

        Collection<Domain> allDomains = domainDetector.getAllDomains();

        FastVector attributes = new FastVector();
        FastVector domainVector = new FastVector();
        for (Domain domain : allDomains) {
            domainVector.addElement(domain.getName());
        }
        Attribute domainAttribute = new Attribute("domain", domainVector);
        attributes.addElement(domainAttribute);
        FastVector languageVector = new FastVector();
        for (Language language : languages) {
            languageVector.addElement(language.asTwoChars());
        }
        Attribute languageAttribute = new Attribute("language", languageVector);
        attributes.addElement(languageAttribute);
        FastVector partnerVector = new FastVector();
        for (PartnerBadge partner : partners) {
            partnerVector.addElement(partner.getSystemId());
        }
        Attribute partnerAttribute = new Attribute("partner", partnerVector);
        attributes.addElement(partnerAttribute);
        Instances instances = new Instances("results", attributes, maxWords * maxResults);

        Set<String> wordsToIgnore = new HashSet<String>();
        for (int i = 0; i < maxWords; i++) {
            String ambiguousPhrase = domainDetector.drawRandomAmbiguousWord(wordsToIgnore);
            System.out.println("+++ Searching for ambiguous phrase '" + ambiguousPhrase + "' (" + (i + 1) + "/" + maxWords + ")");
            wordsToIgnore.add(ambiguousPhrase);

            SecureUserProfile sup = new SecureUserProfile();
            sup.setContextKeywords(Arrays.asList(new ContextKeyword(ambiguousPhrase)));

            for (PartnerBadge partner : partners) {
                System.out.println("Searching partner '" + partner.getSystemId() + "'");

                TermSet<TypedTerm> domainToCount = partnerToDomainToCount.get(partner.getSystemId());
                ResultList partnerResult = getPartnerResult(partner, sup);
                Set<String> seenResults = new HashSet<String>();
                for (Result result : partnerResult.results.subList(0, Math.min(maxResults, partnerResult.results.size()))) {
                    // System.out.println("Got result '" + result.title + "'");

                    if (result.title != null && !result.title.trim().isEmpty()) {
                        String title = result.title.trim();
                        String titleNormalised = title.replaceAll("\\W", "").toLowerCase(Locale.US);

                        if (!seenResults.contains(titleNormalised)) {
                            Language language = languageDetector.detect(title);
                            String domainName = null;
                            if (language == Language.English) {
                                // map the result to domains
                                Set<Domain> detectedDomains = domainDetector.detect(title);
                                for (Domain domain : detectedDomains) {
                                    if (domainName == null) {
                                        domainName = domain.getName();
                                    }
                                    domainToCount.add(new TypedTerm(domain.getName(), null, 1f));
                                }
                                System.out.println("Got domains for '" + title + "' -> '" + detectedDomains + "'");
                            } else {
                                System.out.println("Got language for '" + title + "' -> '" + language + "'");
                            }
                            partnerToLanguageToCount.get(partner.getSystemId()).add(new TypedTerm(language.asTwoChars(), null, 1));
                            Instance instance = new Instance(3);
                            instance.setDataset(instances);
                            if (domainName != null) {
                                instance.setValue(domainAttribute, domainName);
                            }
                            instance.setValue(languageAttribute, language.asTwoChars());
                            instance.setValue(partnerAttribute, partner.getSystemId());
                            instances.add(instance);

                            seenResults.add(titleNormalised);
                        }
                    }
                }
            }
            for (Entry<String, TermSet<TypedTerm>> e : partnerToDomainToCount.entrySet()) {
                System.out.println(e.getKey() + " -> " + e.getValue());
            }
            for (Entry<String, TermSet<TypedTerm>> e : partnerToLanguageToCount.entrySet()) {
                System.out.println(e.getKey() + " -> " + e.getValue());
            }

            ArffSaver saver = new ArffSaver();
            saver.setDestination(new FileOutputStream(new File("/opt/data/eexcess/partner-to-domain-running.arff")));
            saver.setInstances(instances);
            saver.writeBatch();
        }

        // System.out.println(partnerToDomainToCount);
    }
}

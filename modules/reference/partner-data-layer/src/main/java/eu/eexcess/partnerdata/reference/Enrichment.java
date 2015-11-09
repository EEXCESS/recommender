/* Copyright (C) 2014
"JOANNEUM RESEARCH Forschungsgesellschaft mbH" 
 Graz, Austria, digital-iis@joanneum.at.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package eu.eexcess.partnerdata.reference;

import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import eu.eexcess.config.PartnerConfiguration;
import eu.eexcess.dataformats.result.Result;
import eu.eexcess.partnerdata.api.IEnrichment;
import eu.eexcess.partnerdata.reference.enrichment.EnrichmentResult;
import eu.eexcess.partnerdata.reference.enrichment.EnrichmentServicesProxy;

public class Enrichment implements IEnrichment{
	
	protected PartnerConfiguration partnerConfig;

	EnrichmentServicesProxy services;

	@Override
    public void init(PartnerConfiguration partnerConfig)
	{
		this.partnerConfig = partnerConfig;
		this.services  = new EnrichmentServicesProxy(this.partnerConfig);
	}
	
	@Override
    public Document enrichResultList(Document input, PartnerdataLogger logger){
		if (!this.partnerConfig.getEnableEnriching()) return input;
		PartnerdataTracer.dumpFile(this.getClass(), this.partnerConfig, input, "before-enrichment", logger);
		OntModel model = XMLTools.createModel(input);

		OntModel modelEnriched = XMLTools.createModel(input);

		String queryContent = createSPARQLqueryForToEnrichment();

		if (this.partnerConfig.getPartnerDataRequestsTrace()){// for debugging:
			PartnerdataTracer.debugTrace(this.partnerConfig, queryContent);
			Query queryDebug = QueryFactory.create(queryContent);	
			QueryExecution qeDebug = QueryExecutionFactory.create(queryDebug, model);
			ResultSet queryResultsDebug =  qeDebug.execSelect();
			ResultSetFormatter.out(System.out, queryResultsDebug);
		}
		Query query = QueryFactory.create(queryContent);	
		QueryExecution qe = QueryExecutionFactory.create(query, model);

		ResultSet queryResults =  qe.execSelect();
		

		while (queryResults.hasNext()) {
			
			QuerySolution querySol = queryResults.nextSolution();
			Resource edmAggregatedCHO = querySol.getResource("edmAggregatedCHO");
			Resource enrichedObject = modelEnriched.getResource("http://eexcess.eu/schema/Object");
			Resource enrichedProxy = modelEnriched.getResource("http://eexcess.eu/schema/Proxy");
			Resource enrichedEEXCESSProxyItem = modelEnriched.createResource(edmAggregatedCHO.getURI() + "/enrichedProxy/", enrichedProxy );
			Resource enrichedAggregation = modelEnriched.getResource("http://www.openarchives.org/ore/terms/Aggregation");
			Resource enrichedOreAggregationItem = modelEnriched.createResource(edmAggregatedCHO.getURI() + "/enrichedAggregation/", enrichedAggregation);
			Resource eEXCESSObjectItem = modelEnriched.createResource(edmAggregatedCHO.getURI(), enrichedObject );

			Resource enrichedAgent = modelEnriched.getResource("http://eexcess.eu/schema/Agent");
			Resource metadataEnrichmentAgent = modelEnriched.createResource("http://www.eexcess.eu/data/agents/metadataEnrichmentAgent/", enrichedAgent );

			modelEnriched.add(modelEnriched.createStatement(enrichedEEXCESSProxyItem,
					modelEnriched.getProperty("http://www.openarchives.org/ore/terms/proxyIn"),
					enrichedOreAggregationItem));
			modelEnriched.add(modelEnriched.createStatement(enrichedOreAggregationItem,
					modelEnriched.getProperty("http://www.europeana.eu/schemas/edm/aggregatedCHO"),
					eEXCESSObjectItem));
			modelEnriched.add(modelEnriched.createStatement(enrichedEEXCESSProxyItem,
					modelEnriched.getProperty("http://www.openarchives.org/ore/terms/proxyFor"),
					modelEnriched.createResource(edmAggregatedCHO.getURI())));

			modelEnriched.add(modelEnriched.createStatement(enrichedOreAggregationItem,
					modelEnriched.getProperty("http://www.europeana.eu/schemas/edm/dataProvider"),
					metadataEnrichmentAgent));

			Result result = new Result();
			
			Literal proxyIdentifier = querySol.getLiteral("proxyIdentifier");
			if (proxyIdentifier != null)
				result.documentBadge.id = proxyIdentifier.toString();

			Literal proxyTitle = querySol.getLiteral("proxyTitle");
			if (proxyTitle != null) {
				result.title = proxyTitle.toString();
				enriching(modelEnriched, enrichedEEXCESSProxyItem, proxyTitle, 
						 logger);
						
			}

			Literal proxyDescription = querySol.getLiteral("proxyDescription");
			if (proxyDescription != null) {
				result.description = proxyDescription.toString();
				enriching(modelEnriched, enrichedEEXCESSProxyItem, proxyDescription, 
						logger);

			}
			/*
			Literal proxyCreator = querySol.getLiteral("proxyCreator");
			if (proxyCreator != null)
				result.creator = proxyCreator.toString();
				*/	
			Literal dctermsDate = querySol.getLiteral("date");
			if (dctermsDate != null)
				result.date = dctermsDate.toString();
	
			Literal edmProviderName = querySol.getLiteral("edmProviderName");
			if (edmProviderName != null) {
				result.documentBadge.provider = edmProviderName.toString();
			}
			
			Literal proxyLanguage = querySol.getLiteral("proxyLanguage");
			if (proxyLanguage != null) {
				result.language = proxyLanguage.toString();
			}

			Resource oreIsShownAt = querySol.getResource("oreIsShownAt");
			if (oreIsShownAt != null) {
				result.documentBadge.uri = oreIsShownAt.toString();
			}
			Resource edmPreview = querySol.getResource("edmPreview");
			if (edmPreview != null) {
				if (!edmPreview.toString().equalsIgnoreCase("http://www.europeana.eu/edm/"))
					result.previewImage = edmPreview.toString();
			}
		}

		Document output = XMLTools.convertStringToDocument(XMLTools.writeModel(modelEnriched));
		PartnerdataTracer.dumpFile(this.getClass(), this.partnerConfig, output, "done-enrichment", logger);
		return output;
	}


	protected void enriching(OntModel modelEnriched,
			Resource enrichedEEXCESSProxyItem, Literal proxyTitle,
			PartnerdataLogger logger) {
		if (proxyTitle != null) {
			Set<EnrichmentResult> enriched = services.enrich(proxyTitle.toString(), logger);
			for (Iterator<EnrichmentResult> iterator = enriched.iterator(); iterator.hasNext();) {
				EnrichmentResult enrichmentResult = (EnrichmentResult) iterator.next();
				if (enrichmentResult != null && 
						enrichmentResult.getWord() != null && 
						!enrichmentResult.getWord().trim().isEmpty())
				{
					if (enrichmentResult.getLatitude() != 0 && enrichmentResult.getLongitude() != 0)
					{
						Resource enrichedResourceType = modelEnriched.getResource(enrichmentResult.getUri());
						modelEnriched.add(
								modelEnriched.createStatement(enrichedEEXCESSProxyItem,
										modelEnriched.getProperty("http://www.w3.org/2003/01/geo/wgs84-pos/Point"),
										enrichedResourceType
							));
						Literal literalLat = modelEnriched.createLiteral(""+enrichmentResult.getLatitude());
						modelEnriched.add(
								modelEnriched.createStatement(enrichedResourceType,
										modelEnriched.getProperty("http://www.w3.org/2003/01/geo/wgs84-pos/lat"),
										literalLat
							));
						Literal literalLong = modelEnriched.createLiteral(""+enrichmentResult.getLongitude());
						modelEnriched.add(
								modelEnriched.createStatement(enrichedResourceType,
										modelEnriched.getProperty("http://www.w3.org/2003/01/geo/wgs84-pos/long"),
										literalLong
							));
					}
					if ( enrichmentResult.getUri() == null || 
							enrichmentResult.getUri().trim().isEmpty()) {
						Literal literal = null;
						if (enrichmentResult.getLanguage() != null && !enrichmentResult.getLanguage().isEmpty()	)
							literal = modelEnriched.createLiteral(enrichmentResult.getWord(), "en");
						else 
							literal = modelEnriched.createLiteral(enrichmentResult.getWord());
	
						modelEnriched.add(
								modelEnriched.createStatement(enrichedEEXCESSProxyItem,
										modelEnriched.getProperty("http://purl.org/dc/elements/1.1/subject"),
										literal
							));
					} else {
						Resource enrichedResourceType = modelEnriched.getResource(enrichmentResult.getUri());
						Literal literal = null;
						if (enrichmentResult.getLanguage() != null && !enrichmentResult.getLanguage().isEmpty()	)
							literal = modelEnriched.createLiteral(enrichmentResult.getWord(), "en");
						else 
							literal = modelEnriched.createLiteral(enrichmentResult.getWord());
	
						modelEnriched.add(
								modelEnriched.createStatement(enrichedEEXCESSProxyItem,
										modelEnriched.getProperty("http://purl.org/dc/elements/1.1/subject"),
										enrichedResourceType)); 
						modelEnriched.add(
								modelEnriched.createStatement(enrichedResourceType,
										RDFS.label,
										literal)); 
	//					
	//					if (enrichmentResult.getType() != null && !enrichmentResult.getType().isEmpty()) {
	//						modelEnriched.add(
	//								modelEnriched.createStatement(enrichedResourceType,
	//										RDF.type,
	//										modelEnriched.getResource(enrichmentResult.getType()))); 
	//					}
						
						if (enrichmentResult.getType() != null && !enrichmentResult.getType().isEmpty()) {
							modelEnriched.add(
									modelEnriched.createStatement(enrichedResourceType,
											RDF.type,
											modelEnriched.getResource(enrichmentResult.getType()))); 
						}
	
	
					}
	
				}
			}
		}
	}


	@Override
    public Document enrichResultObject(Document input){
		return input;
	}

	protected String createSPARQLqueryForToEnrichment() {
		String queryContent = "";
		queryContent += getRDFPrefixes();
		
		// ?eexcessObject
		queryContent += "SELECT ?eexcessProxy ?oreAggregation ?edmAggregatedCHO  ?proxyIdentifier ?proxyTitle ?oreIsShownAt ?proxyDescription ?proxyCreator ?edmProviderName ?oreCollectionName ?proxyLanguage ?date ?edmPreview ";
		queryContent += "WHERE {  ";
		queryContent += "?eexcessProxy rdf:type eexcess:Proxy . ";
		queryContent += "?eexcessProxy ore:proxyIn ?oreAggregation . ";
		queryContent += "?oreAggregation rdf:type ore:Aggregation . ";
		queryContent += "?oreAggregation edm:provider ?edmProvider . ";
		queryContent += "?edmProvider foaf:name ?edmProviderName . ";
		queryContent += "?oreAggregation edm:aggregatedCHO ?edmAggregatedCHO . ";
		
		queryContent += "OPTIONAL { ?eexcessProxy dc:identifier ?proxyIdentifier . } "; 
		queryContent += "OPTIONAL { ?eexcessProxy dc:title ?proxyTitle . }  ";
		queryContent += "OPTIONAL { ?eexcessProxy dc:description ?proxyDescription . }  ";
		queryContent += "OPTIONAL { ?eexcessProxy dcterms:date ?date . }  ";
		queryContent += "OPTIONAL { ?eexcessProxy edm:language ?proxyLanguage . }  ";
		queryContent += "OPTIONAL { ?oreAggregation edm:isShownAt ?oreIsShownAt . } "; 
		queryContent += "OPTIONAL { ?oreAggregation edm:preview ?edmPreview . } "; 
		queryContent += "OPTIONAL { ?oreAggregation edm:collectionName ?oreCollectionName . } "; 
		
		queryContent += "OPTIONAL { ?eexcessProxy dc:creator ?proxyCreator . }  ";

		queryContent += " } ";
		
		return queryContent;
	}

	
	private String getRDFPrefixes() {
		String queryContent  = "";
		queryContent += "PREFIX eexcess: <http://eexcess.eu/schema/> ";
		queryContent += "PREFIX dcterms: <http://purl.org/dc/terms/> ";
		queryContent += "PREFIX dc: <http://purl.org/dc/elements/1.1/> ";
		queryContent += "PREFIX ore: <http://www.openarchives.org/ore/terms/> ";
		queryContent += "PREFIX edm: <http://www.europeana.eu/schemas/edm/> ";
		queryContent += "PREFIX foaf: <http://xmlns.com/foaf/0.1/> ";
		queryContent += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";
		queryContent += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
		queryContent += "PREFIX dbpedia: <http://dbpedia.org/ontology/> ";
		return queryContent;
	}

}

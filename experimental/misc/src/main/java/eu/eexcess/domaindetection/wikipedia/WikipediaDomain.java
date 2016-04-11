package eu.eexcess.domaindetection.wikipedia;

import eu.eexcess.federatedrecommender.domaindetection.probing.Domain;

public class WikipediaDomain extends Domain {
	private final String name;

	public WikipediaDomain(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}

}

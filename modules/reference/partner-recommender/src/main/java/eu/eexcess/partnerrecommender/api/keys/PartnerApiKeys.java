package eu.eexcess.partnerrecommender.api.keys;

import java.util.ArrayList;

import eu.eexcess.config.PartnerConfiguration;

public class PartnerApiKeys {
	
	
	private ArrayList<PartnerConfiguration> partners = new ArrayList<PartnerConfiguration>();

	@Override
	public String toString() {
		return "PartnerApiKeys [partners=" + getPartners() + "]";
	}

	public ArrayList<PartnerConfiguration> getPartners() {
		return partners;
	}

	public void setPartners(ArrayList<PartnerConfiguration> partners) {
		this.partners = partners;
	}
	
}

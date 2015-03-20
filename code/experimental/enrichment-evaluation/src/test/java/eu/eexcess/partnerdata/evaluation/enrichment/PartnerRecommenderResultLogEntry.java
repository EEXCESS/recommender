package eu.eexcess.partnerdata.evaluation.enrichment;

import java.util.ArrayList;

import org.w3c.dom.Node;

public class PartnerRecommenderResultLogEntry {

	private String enrichedProxyKey;
	
	private String proxyKey;
	
	private ArrayList<Node> enrichedNodes = new ArrayList<Node>();
	
	private Node proxy;
	
	private Node enrichedProxy;

	public Node getProxy() {
		return proxy;
	}

	public void setProxy(Node proxy) {
		this.proxy = proxy;
	}

	public Node getEnrichedProxy() {
		return enrichedProxy;
	}

	public void setEnrichedProxy(Node enrichedProxy) {
		this.enrichedProxy = enrichedProxy;
	}

	public ArrayList<Node> getEnrichedNodes() {
		return enrichedNodes;
	}

	public void addEnrichedNode(Node node) {
		if (this.enrichedNodes == null)
			this.enrichedNodes = new ArrayList<Node>();
		this.enrichedNodes.add(node);
	}

	public void setEnrichedNodes(ArrayList<Node> enrichedNodes) {
		this.enrichedNodes = enrichedNodes;
	}

	public String getEnrichedProxyKey() {
		return enrichedProxyKey;
	}

	public void setEnrichedProxyKey(String enrichedProxyKey) {
		this.enrichedProxyKey = enrichedProxyKey;
	}

	public String getProxyKey() {
		return proxyKey;
	}

	public void setProxyKey(String proxyKey) {
		this.proxyKey = proxyKey;
	}
	
	
	
}

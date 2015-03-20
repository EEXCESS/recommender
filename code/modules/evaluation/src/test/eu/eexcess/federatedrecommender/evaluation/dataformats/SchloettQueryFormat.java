package eu.eexcess.federatedrecommender.evaluation.dataformats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;



public class SchloettQueryFormat implements Serializable{
	private static final long serialVersionUID = 4001008589235643878L;
	

	private HashMap<String, LinkedHashMap<String, String>> querieMap;
	
	public SchloettQueryFormat(){
		
	}
	public SchloettQueryFormat(HashMap<String, LinkedHashMap<String, String>> querieMap2) {
		this.setQuerieMap(querieMap2);
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "SchloettQueryFormat [querieMap=" + getQuerieMap() + "]";
	}

	public HashMap<String, LinkedHashMap<String, String>> getQuerieMap() {
		return querieMap;
	}

	public void setQuerieMap(HashMap<String, LinkedHashMap<String, String>> querieMap2) {
		//System.out.println(querieMap2);
		this.querieMap = querieMap2;
	}

	
}

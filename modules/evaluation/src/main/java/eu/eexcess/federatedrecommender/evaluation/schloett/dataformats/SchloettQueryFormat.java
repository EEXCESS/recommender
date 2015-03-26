package eu.eexcess.federatedrecommender.evaluation.schloett.dataformats;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;



public class SchloettQueryFormat implements Serializable{
	private static final long serialVersionUID = 4001008589235643878L;
	

	private HashMap<String, LinkedHashMap<String, Object>> map;
	
	public SchloettQueryFormat(){
		
	}
	public SchloettQueryFormat(HashMap<String, LinkedHashMap<String, Object>> format) {
		this.setMap(format);

	}

	@Override
	public String toString() {
		return "SchloettQueryFormat [map=" + getMap() + "]";
	}

	public HashMap<String, LinkedHashMap<String, Object>> getMap() {
		return map;
	}

	public void setMap(HashMap<String, LinkedHashMap<String, Object>> map) {
		this.map = map;
	}

	
}

package eu.eexcess.test;

import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Hello world!
 *
 */
public class PrivacyProxy 
{

	private static final String EEXCESS_RESPONSE_VAR_NAME_RESULTS = "result";
	private static final String EEXCESS_URL_DEV_SERVER = "http://eexcess-dev.joanneum.at/eexcess-privacy-proxy/api/v1/recommend";
	private static final String EEXCESS_URL_DEV_SERVER_LONG = "http://eexcess-dev.joanneum.at/eexcess-privacy-proxy/api/v1/recommend?fr_url=http://eexcess-dev.joanneum.at/eexcess-federated-recommender-web-service-1.0-SNAPSHOT/recommender/recommend";
	private static final String EEXCESS_URL_STABLE_SERVER = "http://eexcess.joanneum.at/eexcess-privacy-proxy/api/v1/recommend";

	String responseString = "";
	String userprofile ="";
	
	ArrayList<String> contextList = new ArrayList<String>();
 	ArrayList<EEXCESSResultItem> results;
	
	protected String logDuration;
 	
 
	
	public PrivacyProxy()
	{

	}


	public void recommendDevServer() {
		recommend(EEXCESS_URL_DEV_SERVER);
	}

	public void recommendDevLongServer() {
		recommend(EEXCESS_URL_DEV_SERVER_LONG);
	}

	public void recommendStableServer() {
		recommend(EEXCESS_URL_STABLE_SERVER);
	}

	public void recommend(String url) {
		HttpClient httpClient = new DefaultHttpClient();

	    try {
	        HttpPost request = new HttpPost(url);
	        createUserProfile();
	        
	        StringEntity params =new StringEntity(userprofile);
	        
	        request.addHeader("content-type", "application/json");
	        request.setEntity(params);
	        HttpResponse response = httpClient.execute(request);
            responseString = EntityUtils.toString(response.getEntity());
	        net.sf.json.JSONObject responseJSON = (net.sf.json.JSONObject) JSONSerializer.toJSON(responseString);
        	this.results = new ArrayList<EEXCESSResultItem>();
	        if (responseJSON.has(EEXCESS_RESPONSE_VAR_NAME_RESULTS))
	        {
	        	JSONArray resultsArray = (JSONArray)responseJSON.get(EEXCESS_RESPONSE_VAR_NAME_RESULTS);
	        	for (int i = 0; i < resultsArray.size(); i++) {
	        		EEXCESSResultItem item = new EEXCESSResultItem();
	        		JSONObject resultsItem = resultsArray.getJSONObject(i);
	        		if (resultsItem.has("id"))
	        			item.setId(resultsItem.getString("id"));
	        		if (resultsItem.has("title"))
	        			item.setTitle(resultsItem.getString("title"));
	        		if (resultsItem.has("description"))
	        			item.setDescription(resultsItem.getString("description"));
	        		if (resultsItem.has("uri"))
	        			item.setUri(resultsItem.getString("uri"));
	        		if (resultsItem.has("previewImage"))
	        			item.setPreviewImage(resultsItem.getString("previewImage"));
	        		if (resultsItem.has("facets")) {
	        			JSONObject facet = resultsItem.getJSONObject("facets");
		        		if (facet.has("provider"))
		        			item.setProvider(facet.getString("provider"));
	        			
	        		}
	        		results.add(item);	
				}
	        }
	    }catch (Exception ex) {
	    } finally {
	        httpClient.getConnectionManager().shutdown();
	    }
	}

	
	public void createUserProfile() {
		JSONObject root = new JSONObject();
		root.element("numResults", 40);
		JSONArray contextKeywords = new JSONArray();
		for (int i = 0; i < contextList.size(); i++) {
			String actValue = contextList.get(i);
			JSONObject contextKeyword = new JSONObject();
			contextKeyword.element("text", actValue);
			contextKeyword.element("weight", 0.1);
			contextKeyword.element("reason", "manual");
			contextKeywords.add(contextKeyword);
		}
		
		root.element("contextKeywords", contextKeywords);
		this.userprofile  = root.toString(2);
	}

	public String getResponseString() {
		return responseString;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	public ArrayList<EEXCESSResultItem> getResults() {
		return results;
	}

	public void setResults(ArrayList<EEXCESSResultItem> results) {
		this.results = results;
	}

	public String getUserprofile() {
		return userprofile;
	}

	public void setUserprofile(String userprofile) {
		this.userprofile = userprofile;
	}

	public ArrayList<String> getContextList() {
		return contextList;
	}

	public void setContextList(ArrayList<String> contextList) {
		this.contextList = contextList;
	}


	public String getLogDuration() {
		return logDuration;
	}


	public void setLogDuration(String logDuration) {
		this.logDuration = logDuration;
	}

	public void addContext(String string) {
		if (this.contextList == null)
			this.contextList = new ArrayList<>();
		this.contextList.add(string);
	}
	
	public int countProvider(String provider)
	{
		if (this.results == null) return 0;
		int ret = 0;
		for (int i = 0; i < results.size(); i++) {
			if (this.results.get(i).getProvider() != null )
			{
				if (!this.results.get(i).getProvider().trim().isEmpty() )
				{
					if (this.results.get(i).getProvider().trim().equalsIgnoreCase(provider) )
						ret++;
				}
			}
		}
		return ret;
	}

}

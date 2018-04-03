package com.sa.search.util;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import flexjson.JSONDeserializer;

public class SynonymHelper {
	private static Log m_log = LogFactory.getLog(SynonymHelper.class);

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public static HashMap<String, List<String>> getSynonymList(String url){
		HashMap<String, List<String>> managedSynonyms = null;
		//String url = "http://localhost:8983/solr/collection1/schema/analysis/synonyms/english";		
		try{
			HttpMethod  get = new GetMethod(url);
			HttpClient httpClient = new HttpClient();
			int response = httpClient.executeMethod(get);
			
			Object s = new JSONDeserializer().deserialize( get.getResponseBodyAsString() );

			HashMap o = (HashMap)s;

			HashMap map = (HashMap)o.get("synonymMappings");
			managedSynonyms = (HashMap<String, List<String>>)map.get("managedMap");
		}catch(Exception e){
			m_log.error(e.getCause());
			e.printStackTrace();
		}
		return managedSynonyms;
	}
	
	public static int removeSynonym(String url, String word) {
		int statusCode = 0;
		
		try {	    			    		
			url = url + "/"+word;
			DeleteMethod delete = new DeleteMethod(url);
			HttpClient httpClient = new HttpClient();
			delete.addRequestHeader("Content-Type", "application/json");

			statusCode = httpClient.executeMethod(delete);

			
		}catch(Exception e) {
			m_log.error(e.getCause());
			e.printStackTrace();
		}		
		
		return statusCode;
	}
	
	public static int addSynonym(String url, String json) {
		int statusCode = 0;
		
		try {	//Post new synonym to main solr
			PostMethod post = new PostMethod(url);
			HttpClient httpClient = new HttpClient();
			post.addRequestHeader("Content-Type", "application/json");
	
			StringRequestEntity  input = new StringRequestEntity (json, "application/json","UTF-8");	            
			post.setRequestEntity(input);
	
			statusCode = httpClient.executeMethod(post);
		
		}catch(Exception e) {
			m_log.error(e.getCause());
			e.printStackTrace();
		}		
		
		return statusCode;
	}
	
	
	public static int reloadCore(String url) {
		int statusCode = 0;
		
		try {	
			HttpMethod  post = new GetMethod(url);
			HttpClient httpClient = new HttpClient();
			statusCode = httpClient.executeMethod(post);
		
		}catch(Exception e) {
			m_log.error(e.getCause());
			e.printStackTrace();
		}		
		
		return statusCode;
	}
	
}

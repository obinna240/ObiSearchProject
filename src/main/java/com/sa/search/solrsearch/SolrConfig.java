package com.sa.search.solrsearch;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;


public class SolrConfig {
	private String solrURL;
	private String solrCore;

	private String solrQueryURL;
	private String solrCoreActionURL;
	
	private SolrClient solrClient;

	private static Log m_log = LogFactory.getLog(SolrConfig.class);

	public String getQueryURL() {
		
		if (solrQueryURL == null){
			solrQueryURL = solrURL ;
			
			if (!solrQueryURL.endsWith("/")){
				solrQueryURL += "/";
			}
			
			if (StringUtils.isNotBlank(solrCore)){
				solrQueryURL += solrCore + "/";
			}
			else {
				solrQueryURL += "live/";
			}
		}
		return solrQueryURL;
	}
	
	public String getCoresActionURL(){
		
		if (solrCoreActionURL == null){
			solrCoreActionURL = solrURL + "/admin/cores?action=";
		}
		
		return solrCoreActionURL;
	}

	public String getSolrURL() {
		return solrURL;
	}
	public void setSolrURL(String solrURL) {
		this.solrURL = solrURL;
	}
	
	public String getSolrCore() {
		return solrCore;
	}
	
	public void setSolrCore(String solrCore) {
		this.solrCore = solrCore;
	}

	public SolrClient getSolrClient() {
		if (solrClient == null){
			solrClient = new HttpSolrClient(getQueryURL());
		}
		return solrClient;
	}

}

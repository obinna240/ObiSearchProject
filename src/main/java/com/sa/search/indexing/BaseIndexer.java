package com.sa.search.indexing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.sa.search.solrsearch.SolrConfig;
import com.sa.search.util.DateHelper;

/**
 * Shared indexing functionality  
 */
public class BaseIndexer implements Runnable {
	
	Thread runner;
	boolean bDeleteOthers;
	SolrConfig solrConfig;
	String indexDateTime;
	
	protected Map<String, Boolean> directoriesMap;
	
	private static final String SOLR_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	private static Log m_log = LogFactory.getLog(BaseIndexer.class);
	
	/**
	 * This should be overidden in the subclass to do the actual indexing 
	 */
	@Override
	public void run() {
	}
	
	/**
	 * This should be called on the subclass instance to initiate the indexing thread
	 */
	public void startIndexing(){

		//We flag everything in a particular index run with the same timestamp
		indexDateTime = getSolrDateAndTime(new Date());
		runner = new Thread(this);
		runner.start();
	}
	
	public Thread getThread() {
		return runner;
	}
	
	void init(SolrConfig solrConfig, boolean bDeleteOthers){
		this.bDeleteOthers = bDeleteOthers;
		this.solrConfig = solrConfig;		
	}

	/**
	 * Add latitude & longitude data as a single comma separated field
	 */
	/*void indexAddressLocation(Map<String, Object> docData, Address address){
		if (address != null) {
			if (address.getLatitude() != null && address.getLongitude() != null){
				docData.put("location", address.getLatitude().toString() + "," + address.getLongitude().toString());
			}
				
			//TODO do we need to index some extra info if the vendor has specified a radius?
			//Double rad = v.getCoveredRadius();
		}
	}*/
	
	/**
	 * Adds a generic map of name-value pairs to the index as a single document  
	 */
	void addDocToIndex(Map<String, Object> docData){
		
		try {
			SolrClient solrClient = solrConfig.getSolrClient();
			
			docData.put("indextime", indexDateTime);
			
			SolrInputDocument doc = new SolrInputDocument();
			Set<String> keySet = docData.keySet();
			Object fieldVal = null;
			
			for (String fieldName : keySet) {
				
				//Multi-valued items are expected in the keyset with names like 
				//name.1, name.2 etc
				
				if (fieldName.endsWith(".1")){
					fieldName = StringUtils.substringBefore(fieldName, ".");
					
					for (int i = 1; i < 1000; i++){
						String checkFieldName = fieldName + "." + Integer.toString(i);
						fieldVal = docData.get(checkFieldName);
						
						if (fieldVal != null){
							doc.addField(fieldName, fieldVal);
						}
						else {
							break;
						}
					}
				}
				else if (StringUtils.contains(fieldName, ".")){
					//do nothing - field.2, field.3 etc will already have been taken care of above  
				}
				else {
					fieldVal = docData.get(fieldName);
					doc.addField(fieldName, fieldVal);
				}
			}
			
			UpdateResponse response;

			m_log.debug("Sending doc to Solr..");
			response = solrClient.add(doc);
			m_log.debug("Server response = " + response.getStatus());

			softCommit();
			
			//Add in small pause so that we don't overwhelm the server(s)
			Thread.sleep(50);
			
		} catch (Exception e) {
			m_log.error("Failed to add documents to index ", e);
		} 
	}
	
	/**
	 * Full commit to disk (possibly slow)
	 */
	private void commit() {
		SolrClient solrClient = solrConfig.getSolrClient();
		try {
			solrClient.commit();
		} catch (Exception e) {
			m_log.error("Failed to commit updates to index ", e);
		}
	}
	
	/**
	 * Soft commit - immediately visible from searches, but not necessarily written to disk at the Solr server
	 * Solr server will be configured to auto commit to disk after a time period
	 */
	private void softCommit() {
		SolrClient solrClient = solrConfig.getSolrClient();
		try {
			solrClient.commit(false, true, true);
		} catch (Exception e) {
			m_log.error("Failed to soft commit updates to index ", e);
		}
	}
	
	String getSolrDateAndTime(Date d){
		SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'");
		return sdfOut.format(d); 
	}
	
	String getSolrDateNoTime(Date d){
		SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'");
		return sdfOut.format(d); 
	}
	
	String getSolrDateNoTimeEnd(Date d){
		SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy-MM-dd'T'23:59:59'Z'");
		return sdfOut.format(d); 
	}
	
	/**
	 * Basic HTML tag removal 
	 */
	String deHTML(String html){
		if (html != null) {
			Document doc = Jsoup.parse(html);
			return doc.text();
		
			//String deHTMLdString = html.replaceAll("\\<.*?>","");
			//deHTMLdString = deHTMLdString.replaceAll("\\r\\n", "");
			//return deHTMLdString;
		} else {
			return "";
		}
	}
	
	/**
	 * This whitelist allows a full range of text and structural body HTML: a, b, blockquote, br, 
	 * caption, cite, code, col, colgroup, dd, div, dl, dt, em, h1, h2, h3, h4, h5, h6, i, img, li, ol, p,
	 *  pre, q, small, span, strike, strong, sub, sup, table, tbody, td, tfoot, th, thead, tr, u, ul 
     * 

	 *//*
	String deHTMLCMSContent(String html){
		if (html != null) {
			 return Jsoup.clean(html,Whitelist.basic().addEnforcedAttribute("a", "class", "inactiveLinks").addTags("span"));
		} else {
			return "";
		}
	}*/
	
	/**
	 * Removes any documents which were indexed prior to the current index batch.
	 * Additional doc query can be used to apply this to a specific document type only 
	 */
	void deleteOldDocuments(String docQuery) {
		
		String deleteQuery = "";
		try {
			m_log.info("Deleting old items...");
			deleteQuery = String.format(docQuery + " +indextime:{* TO %s}", indexDateTime);
			SolrClient solrClient = solrConfig.getSolrClient();
			solrClient.deleteByQuery(deleteQuery);
		} catch (Exception e) {
			m_log.error("Failure executing delete query " + deleteQuery, e);
		} 
	}
	
	
	/**
	 * Removes any documents 
	 */
	public void deleteDocument(String id) {
		
		try {
			m_log.info("Deleting documet: " + id);
			SolrClient solrClient = solrConfig.getSolrClient();
			solrClient.deleteById(id);
			solrClient.commit();
			
		} catch (Exception e) {
			m_log.error("Failure executing delete id ", e);
		} 
	}
	
	/**
	 * Takes a list of category IDs and adds them to the index data, along with all parent category IDs
	 * Also include category id 0, to represent the root category, which everything should belong to 
	 */
	void addCategories(String catField, List<String> catIDs, HashMap<String, Object> docData) throws Exception {
		String catPrefix = catField + ".";
//		if (catIDs == null) {
//			docData.put(catPrefix + "1", "0"); //Add category 0 (= 'All') in for everything
//		}
//		else {
		if (catIDs != null) {	
			//docData.put(catPrefix + "1", "0"); //Add category 0 (= 'All') in for everything
		
			//int suffix = 2;
			int suffix = 1;
			for (String catId : catIDs){
				if (StringUtils.isNotBlank(catId)) {
					docData.put(catPrefix + suffix, catId);	
					suffix++;
				}
			}
			
		}
	}
	
	void addDates(Date fromDate, Date toDate, HashMap<String, Object> docData) throws Exception {
		
		if (fromDate != null && toDate == null) {
			int suffix = 1;
			String solrDate = DateHelper.convertDateToString(fromDate, SOLR_DATE_PATTERN);
			docData.put("dates" + suffix, solrDate);	
			docData.put("startDate", solrDate);	
			
			
		} else if (fromDate != null && toDate != null) {
			
			List<Date> dates = DateHelper.getDaysBetweenDates(fromDate, toDate);
			if (dates != null) {
				List<String> solrDates = new ArrayList<String>();
				for (Date date : dates) {
					String solrDate = DateHelper.convertDateToString(date, SOLR_DATE_PATTERN);
					solrDates.add(solrDate);
				}
				String solrDate = DateHelper.convertDateToString(fromDate, SOLR_DATE_PATTERN);
				docData.put("startDate", solrDate);	
				solrDate = DateHelper.convertDateToString(toDate, SOLR_DATE_PATTERN);
				docData.put("endDate", solrDate);	
				docData.put("dates", solrDates);	
			}
		}
	}
	
	void addDates(List<Date> dates, HashMap<String, Object> docData) throws Exception {
		
		if (dates != null) {
			List<String> solrDates = new ArrayList<String>();
			Date fromDate = dates.get(0);
			Date toDate = dates.get(dates.size()-1);
			
			for (Date date : dates) {
				String solrDate = DateHelper.convertDateToString(date, SOLR_DATE_PATTERN);
				solrDates.add(solrDate);
			}
			String solrDate = DateHelper.convertDateToString(fromDate, SOLR_DATE_PATTERN);
			docData.put("startDate", solrDate);	
			solrDate = DateHelper.convertDateToString(toDate, SOLR_DATE_PATTERN);
			docData.put("endDate", solrDate);	
			docData.put("dates", solrDates);	
		}
	}
	
}
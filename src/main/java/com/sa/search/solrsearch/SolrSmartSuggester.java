package com.sa.search.solrsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.sa.search.api.cms.controller.bean.SmartSuggestCategory;
import com.sa.search.api.cms.controller.bean.SmartSuggestData;
import com.sa.search.api.cms.controller.bean.SmartSuggestHeader;
import com.sa.search.db.mongo.dao.SAGazetteerDAO;
import com.sa.search.db.mongo.dao.SystemConfigDAO;
import com.sa.search.db.mongo.model.SAGazetteer;
import com.sa.search.db.mongo.model.SAGazetteer.Coverage;
import com.sa.search.db.mongo.model.SmartSuggestCategoryConfig;
import com.sa.search.db.mongo.model.SystemConfig;
//import com.sa.search.service.MongoUIDaoService;

@Component
public class SolrSmartSuggester {

	private static Log m_log = LogFactory.getLog(SolrSmartSuggester.class);
	
	private int maxResults = 10;
	private int snippetSize= 200;
	
	
	@Autowired private SearchServices searchService;
	@Autowired private SystemConfigDAO systemConfigDAO;
	@Autowired private SAGazetteerDAO saGazetteerDAO;

	//@Autowired private MongoUIDaoService mongoUIDaoService;


	public List<SmartSuggestCategory> getSmartSuggestions(HttpServletRequest request, ModelMap modelMap, SearchInfo searchInfo) {
	
	   	String queryText = searchInfo.getSearchText();
		 
	   	List<SmartSuggestCategory> response = null;
	    try {
	
	    	
	    	Map<String, SmartSuggestCategory> catMap = new HashMap<String, SmartSuggestCategory>();
	    	
	    	SystemConfig config = systemConfigDAO.getDefaultSystemConfig();
	    	
	    	Map<String, SmartSuggestCategoryConfig> smartSuggestCatConfigMap = config.getSmartSuggestCatConfig();
	    	
	    	// recommended links
	    	// Should this go into config?
	    	SmartSuggestCategory ssRecommendedCat = createSSRecomendedLinks(catMap, queryText, modelMap, request);
	    		    			
	    	for (String key: smartSuggestCatConfigMap.keySet()) {
	    		
	    		SmartSuggestCategoryConfig catConfig = smartSuggestCatConfigMap.get(key);
	    		if (catConfig.isEnabled()) {				
	    			SmartSuggestCategory ssCat = createSSCategory(catConfig, catMap, queryText, modelMap, searchInfo, request);
	    		}
			
	    	}						

			response = new ArrayList<SmartSuggestCategory>(catMap.values());

			Collections.sort(response, new DisplayOrderComparator());
	    }
	    catch (Exception e) {
	    	m_log.error("SmartSuggest failed. Query text = " + queryText, e);
	    	response = new ArrayList<SmartSuggestCategory>();
	    }

	    return response;
	}
	
	private SmartSuggestData createLastResultItem(DisplayItem item,
			SmartSuggestCategory cat, String queryText, int resultCount,SmartSuggestCategoryConfig ssCat, SearchInfo searchInfo) {
		
		//SystemConfig config = systemConfigDAO.getDefaultSystemConfig();
		//String context = config.getContextRoot();
		String action = "/api/cms/search/getSearchResults?q="+queryText;
		
		SmartSuggestData resultItem = new SmartSuggestData((resultCount)+" more...");
		
		String description = "Click here to search all results";			
       	resultItem.setSecondary(description);       	
       	String url = action + "&ft="+ssCat.getFilterType();   

       	//apply postcode for non-content items only
       	if (StringUtils.isNotBlank(searchInfo.getPc())) {
       		url = url + "&pc="+searchInfo.getPc();
       	}
   		resultItem.setOnclick("window.location = '" + url + "'");
    	
    	return resultItem;
		
	}
	
	public int getMaxResults() {
		return maxResults;
	}


	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public int getSnippetSize() {
		return snippetSize;
	}


	public void setSnippetSize(int snippetSize) {
		this.snippetSize = snippetSize;
	}

	private static class CatComparator implements Comparator<SmartSuggestCategory> {
		public int compare(SmartSuggestCategory c1, SmartSuggestCategory c2) {
			return c1.getHeader().getTitle().compareTo(c2.getHeader().getTitle());
        }
    }
	
	private static class DisplayOrderComparator implements Comparator<SmartSuggestCategory> {
		public int compare(SmartSuggestCategory c1, SmartSuggestCategory c2) {
			return c1.getDisplayOrder().compareTo(c2.getDisplayOrder());
        }
    }	
	
	private SmartSuggestData createSSDataItem(DisplayItem item, SmartSuggestCategory cat) {
		
		String  title = item.getTitle();
		// replace quotes as this seems to cause link not to work
		if (StringUtils.isNotBlank(title)) {
			title = title.replaceAll("'", "");
		}
		SmartSuggestData resultItem = new SmartSuggestData(title);
		String description = item.getContent();
	
		/*if (item.isRecommendedLink()) {
			description = item.getRecommendedLinkText();
		} else {
			description = 
		}*/
		
		if (description != null && description.length() > snippetSize) {
			description = description.substring(0, snippetSize);
			description = StringUtils.substringBeforeLast(description, " ") + " ...";
		}
		
       	resultItem.setSecondary(description);
    	//resultItem.setImage(item.getImageUrl());
    	
    	String url = item.getUrl();
 
    	if (StringUtils.isNotBlank(url) && !StringUtils.equals(url, "#")) {
    		resultItem.setOnclick("window.location = '" + url+ "'");
    	}
    	
    	return resultItem;
	}


	private SmartSuggestCategory createSSCategory(SmartSuggestCategoryConfig ssCat, Map<String, SmartSuggestCategory> catMap,
			String queryText, ModelMap modelMap, SearchInfo searchInfo, HttpServletRequest request) {
		 
		int pCatLimit = ssCat.getLimit(); 
		String pCatId = ssCat.getName();
	
		//If category is null, create it
		SmartSuggestCategory cat = catMap.get(pCatId);		
		if (cat == null) {
			cat = new SmartSuggestCategory();			
			SmartSuggestHeader header = new SmartSuggestHeader(ssCat.getDisplayText(), 10, pCatLimit);
			cat.setHeader(header);
			ArrayList<SmartSuggestData> data = new ArrayList<SmartSuggestData>();		       	
	       	cat.setData(data);
	    	cat.setDisplayOrder(ssCat.getDisplayOrder());
	    	
	       	catMap.put(pCatId, cat);
	       	     
	       	String filterTypes = ssCat.getFilterType();
	       	if (StringUtils.isNotBlank(filterTypes)) {
				String[] ids = filterTypes.split(";");
				List<String> idList = searchInfo.getDocumentTypeList();
				for (String id : ids) {
					idList.add(id);
				}
			}
		}
		
		try{
		
			searchService.doSearch(request, modelMap, searchInfo, true);
			SearchResult result = (SearchResult)modelMap.get("searchResult");
			
			int resultCount = result.getTotalResults();
			cat.getHeader().setNum(resultCount);
			
			int total = pCatLimit;
			if (resultCount < pCatLimit){
				total = resultCount;
			}else if (resultCount > pCatLimit){
				total = pCatLimit - 1;
			}
							
			int i = 0;
			Iterator<DisplayItem> it = result.getItems().iterator();
			while(it.hasNext() && i<total){
				DisplayItem item = (DisplayItem)it.next();
				SmartSuggestData resultItem = createSSDataItem(item, cat);
				cat.getData().add(resultItem);
				i++;
			}
			
			if (resultCount > pCatLimit){
				if (it.hasNext()){
					SmartSuggestData resultItem = createLastResultItem((DisplayItem)it.next(),cat, queryText, (resultCount-total), ssCat, searchInfo);
					cat.getData().add(resultItem);
				}
			}
			
			return cat;
		}catch(Exception e){
			m_log.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	private SmartSuggestCategory createSSRecomendedLinks(Map<String, SmartSuggestCategory> catMap, String queryText, ModelMap modelMap,HttpServletRequest request ) {
		
		int limit = 10;
		SmartSuggestCategory cat = new SmartSuggestCategory();	
		SmartSuggestHeader header = new SmartSuggestHeader("Recommended Links", 10, limit);
		cat.setHeader(header);
		ArrayList<SmartSuggestData> data = new ArrayList<SmartSuggestData>();		       	
       	cat.setData(data);
       	cat.setDisplayOrder(-1);
       	
       	catMap.put("recommendedLinks", cat);
		
		//Get the smart suggestions for this pCatId
		try{
			SearchInfo searchInfo = (SearchInfo)request.getSession().getAttribute("searchInfo");
			if (searchInfo == null){
				searchInfo = new SearchInfo();
			}
			
			String location = null;
			String[] splits = queryText.split(" in | near ");
			if (splits.length == 2) {
				 queryText = splits[0].trim();
				 location = splits[1].trim();
			}
			if (StringUtils.isNotBlank(location) ) {
				List<SAGazetteer> locations = null;
				// are we a town/city?
				if (StringUtils.isNotBlank(location) && StringUtils.isAlpha(location)) {
					
					//if (getDefaultSearchPostcodeValidate() == true) {
					//	locations = saGazetteerDAO.findByLocation(location, Coverage.LOCALAUTHORITIES);
					//} else {
						locations = saGazetteerDAO.findByLocation(location, Coverage.UK);
					//}
					
				} 
				if (locations != null && locations.size() == 1) {
					SAGazetteer saGazetteer = locations.get(0);
					if (StringUtils.isNotBlank(saGazetteer.getPostcode())) {
						String searchPC = saGazetteer.getPostcode();
						searchInfo.setPc(searchPC);
						searchInfo.setRadius("5");
					}
				}
				
			}
			
			searchInfo.setSearchText(queryText);
			
			searchInfo.setSmartSuggest(true);
		
			searchService.doSearch(request, modelMap, searchInfo, true);
			SearchResult result = (SearchResult)modelMap.get("searchResult");
						
			// do we have any recommended links
			int i = 0;
			List<DisplayItem> recomendedLinks = result.getRecommendedLinks();
			if (recomendedLinks != null && recomendedLinks.size() > 0) {
				Iterator<DisplayItem> it = recomendedLinks.iterator();
				while(it.hasNext() && i < limit){
					DisplayItem item = (DisplayItem)it.next();
					SmartSuggestData resultItem = createSSDataItem(item, cat);	
					cat.getData().add(resultItem);
					i++;
				}
			}

			return cat;
		} catch(Exception e){
			m_log.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
}

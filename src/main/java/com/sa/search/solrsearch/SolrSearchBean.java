package com.sa.search.solrsearch;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.IntervalFacet;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.sa.search.api.cms.json.CmsDocument;
import com.sa.search.config.SearchConstants;
import com.sa.search.db.mongo.dao.FacetDAO;
import com.sa.search.db.mongo.dao.SearchConfigDAO;
import com.sa.search.db.mongo.dao.SearchLogDAO;
import com.sa.search.db.mongo.model.SearchLog;
import com.sa.search.solrsearch.ResultFacet.ResultFacetEntry;
import com.sa.search.util.DataHelper;
import com.sa.search.util.DateHelper;

public class SolrSearchBean extends BaseSearchBean implements ISearchProvider {
	private static Log m_log = LogFactory.getLog(SolrSearchBean.class);

	@Autowired private SearchConfigDAO searchConfigDAO;
	@Autowired private FacetDAO facetDAO;
	@Autowired private SearchLogDAO searchLogDAO;
	
	private String getTextQuery(String searchText) {
		//return searchText of format - 
		//+title:("Dementia Care")^20 OR  +title:(Dementia AND Care)^4.0 OR  +title:(Dementia OR Care)
		//+content:("Dementia Care")^10 OR  +content:(Dementia AND Care)^2.0 OR  +content:(Dementia OR Care)
		
		if (searchText != null){
			//Remove extra whitespaces from input searchText
			searchText = searchText.trim();
			searchText = searchText.replaceAll("\\s+", " ");
		}		
		
		String queryText  = ClientUtils.escapeQueryChars(searchText);
		
		StringBuffer query = new StringBuffer();
		
		String[] queryFields = new String[] {"titleExactish", "combinedExactish"};
		Double[] boosts = new Double[]{4.0, 3.0};
		String exactQuery = getRelevenceQueryExact(queryText, queryFields, boosts);
		query.append("(" + exactQuery + ")");
		if (StringUtils.contains(searchText, ' ') == true){
			query.append(" OR ");
			String andQuery = getRelevenceQuery(queryText, queryFields, boosts, "AND");
			query.append("(" + andQuery + ")");
			query.append(" OR ");
			String orQuery = getRelevenceQuery(queryText, queryFields, boosts, "OR");
			query.append("(" + orQuery + ")");
		}
		
		queryFields = new String[] {"title", "combinedStemmed", "totalStemmed"};
		boosts = new Double[]{1.0, 1.0, 1.0};
		String titleQuery = getRelevenceQuery(queryText, queryFields, boosts);
		query.append(" OR ");
		query.append("(" + titleQuery + ")");
		
		if (StringUtils.contains(searchText, ' ') == true){
			query.append(" OR ");
			String andQuery = getRelevenceQuery(queryText, queryFields, boosts, "AND");
			query.append("(" + andQuery + ")");
			query.append(" OR ");
			String orQuery = getRelevenceQuery(queryText, queryFields, boosts, "OR");
			query.append("(" + orQuery + ")");
		}

		return query.toString();
	}
	
	private String getRelevenceQueryExact(String searchText, String[] fields, Double[] boosts) {
		
		//return searchText of format - 
		//titleExactish:("Dementia Care")^20 OR +combinedExactish:("Dementia Care")^20
		StringBuffer relevanceQuery = new StringBuffer();
		int count = 0;
		for (String field : fields) {
			StringBuffer query = new StringBuffer();
			if (count > 0) {
				query.append(" OR ");
			}
			String queryBoost = "^" + boosts[count];
			query.append(field + ":");
			query.append("(\"" + searchText + "\")");
			query.append(queryBoost);
			relevanceQuery.append(query);
			count++;
		}
		
		return relevanceQuery.toString();
	}
	
	private String getRelevenceQuery(String searchText, String[] fields, Double[] boosts) {
		
		//return searchText of format - 
		//titleExactish:("Dementia Care")^20 OR +combinedExactish:("Dementia Care")^20
		StringBuffer relevanceQuery = new StringBuffer();
		int count = 0;
		for (String field : fields) {
			StringBuffer query = new StringBuffer();
			if (count > 0) {
				query.append(" OR ");
			}
			String queryBoost = "^" + boosts[count];
			query.append(field + ":");
			query.append("(" + searchText + ")");
			query.append(queryBoost);
			relevanceQuery.append(query);
			count++;
		}
		
		return relevanceQuery.toString();
	}

	private String getRelevenceQuery(String searchText, String[] fields, Double[] boosts, String operator) {
		StringBuffer relevanceQuery = new StringBuffer();
		int count = 0;
		for (String field : fields) {
			StringBuffer query = new StringBuffer();
			if (count > 0) {
				query.append(" OR ");
			}
			String queryBoost = "^" + boosts[count];
			query.append(field + ":(");
			
			String[] tokens = searchText.split(" ");
			boolean isFirst = true;
			if (tokens != null && tokens.length>0){
				for (int i=0; i<tokens.length;i++){
					if (!isFirst){
						query.append(" " + operator + " ");
					}
					query.append(tokens[i]);
					isFirst = false;
				}
			}
			query.append(")");
			query.append(queryBoost);
			relevanceQuery.append(query);
			count++;
		}
		
		return relevanceQuery.toString();
	}
	
	
	private String getTextQuery_alt(String searchText) {
		//return searchText of format - 
		//+title:("Dementia Care")^20 OR  +title:(Dementia AND Care)^4.0 OR  +title:(Dementia OR Care)
		//+content:("Dementia Care")^10 OR  +content:(Dementia AND Care)^2.0 OR  +content:(Dementia OR Care)
		
		if (searchText != null){
			//Remove extra whitespaces from input searchText
			searchText = searchText.trim();
			searchText = searchText.replaceAll("\\s+", " ");
		}		
		StringBuffer query = new StringBuffer();
		
		String titleExactQuery = getRelevenceQuery(searchText, "titleExactish", new Double[]{4.0, 2.0, 2.0});
		String contentExactQuery = getRelevenceQuery(searchText, "combinedExactish", new Double[]{3.0, 2.0, 2.0});
	 
		query.append("("+ titleExactQuery + ")");
		query.append(" OR ");
		query.append("("+ contentExactQuery + ")");
		
		String titleQuery = getRelevenceQuery(searchText, "title", new Double[]{1.0, 1.0, 1.0});
		String contentQuery = getRelevenceQuery(searchText, "combinedStemmed", new Double[]{1.0, 1.0, 1.0});
		String totalQuery = getRelevenceQuery(searchText, "totalStemmed", new Double[]{1.0, 1.0, 1.0});
		
		query.append(" OR ("+ titleQuery + ")");
		query.append(" OR ");
		query.append("("+ contentQuery + ")");
		query.append(" OR ");
		query.append("("+ totalQuery + ")");
	
		return query.toString();
	}
	
	private String getRelevenceQuery(String searchText, String field, Double[] boosts) {
		//return searchText of format - 
		//+title:("Dementia Care")^20 OR  +title:(Dementia AND Care)^4.0 OR  +title:(Dementia OR Care)
		
		String phraseQueryBoost = "^" + boosts[0];
		String andQueryBoost = "^" + boosts[1];;
		String orQueryBoost = "^" + boosts[2];;
	
		StringBuffer relevanceQuery = new StringBuffer();
			// Return as it is if blank or single word
		if (StringUtils.isBlank(searchText) || !StringUtils.contains(searchText, ' ')){
			relevanceQuery.append("+" + field + ":");
			relevanceQuery.append(searchText);
			relevanceQuery.append(phraseQueryBoost);
			return relevanceQuery.toString();
		}
		
		StringBuffer phraseQuery = new StringBuffer();
		phraseQuery.append("+" + field + ":");
		phraseQuery.append("(\"" + searchText + "\")");
		
		StringBuffer andQuery = new StringBuffer();
		andQuery.append("+" + field + ":(");
		
		StringBuffer orQuery = new StringBuffer();
		orQuery.append("+" + field + ":(");
		
			
		String[] tokens = searchText.split(" ");
		boolean isFirst = true;
		if (tokens != null && tokens.length>0){
			for (int i=0; i<tokens.length;i++){
				if (!isFirst){
					andQuery.append(" AND ");
					orQuery.append(" OR ");
				}
				andQuery.append(tokens[i]);
				orQuery.append(tokens[i]);
				isFirst = false;
			}
		}
		
		andQuery.append(")");
		orQuery.append(")");

		relevanceQuery.append("( ").append(phraseQuery.toString()).append(" )").append(phraseQueryBoost);
		relevanceQuery.append(" OR ");
		relevanceQuery.append("( ").append(andQuery.toString()).append(" )").append(andQueryBoost);
		relevanceQuery.append(" OR ");
		relevanceQuery.append("( ").append(orQuery.toString()).append(" )").append(orQueryBoost);
				
		return relevanceQuery.toString();
	}
			
	private void addHighlightingQuery(SolrQuery q, int snippetLength) {
		q.setHighlight(true);
		q.setHighlightSnippets(1);

		q.setHighlightFragsize(snippetLength);
		q.setParam("hl.fl", "title, content, classification");
		q.setParam("hl.simple.pre", "<strong>");
		q.setParam("hl.simple.post", "</strong>");
	}
	
	private void addSpellCheckQuery(SolrQuery q, String searchText, boolean rhSet) {
		StringBuilder spellCheckBuff = new StringBuilder();
		spellCheckBuff.append(searchText);
	
		if (spellCheckBuff.length() > 0) {
			if (rhSet == false) {
				q.setRequestHandler("/spell");
			}
			q.add("spellcheck", "true");
			q.add("spellcheck.q", spellCheckBuff.toString());
			q.add("spellcheck.collate", "true");
		}
	}
	
	private void addDocumentTypeQuery(List<String> documentTypeList, SolrQuery q) {
	
		if (documentTypeList != null && documentTypeList.size() > 0) {
			String sectionnOperator = " OR ";
			
			StringBuffer fq = new StringBuffer("(");
	    	for (String id : documentTypeList) {
	    		if (id != null)
	    		{
	    			if (fq.length() > 1) {
		    			fq.append(sectionnOperator);
		    		}
	    			
	    			fq.append("\"" + id + "\"");
		    	}
	    	}
	    	fq.append(")");    	
	      	q.addFilterQuery("docType:" + fq.toString());
	    	
	    }
		
		//q.addFilterQuery("(docType:" + documentType + ")");
	}
	
	private void addSpatialSearch(String latitude, String longitude, String radius, SolrQuery q) {
			
		q.setParam("sfield", "location");
		q.setParam("pt", latitude + "," +  longitude);
		q.setParam("d", radius);

		//Sort by nearest first 
		q.setParam("sort", "geodist() asc");
		q.setParam("fl", "distance:geodist(),*");			
		q.addFilterQuery("{!geofilt}");
	}
	
	
	private void addContextFilter(List<String> contextIdList, SolrQuery q) {
		
		if (contextIdList != null && contextIdList.size() > 0) {
			String sectionnOperator = " OR ";
			
			StringBuffer fq = new StringBuffer("(");
	    	for (String id : contextIdList) {
	    		if (id != null)
	    		{
	    			if (fq.length() > 1) {
		    			fq.append(sectionnOperator);
		    		}
	    			
	    			fq.append("\"" + id + "\"");
		    	}
	    	}
	    	fq.append(")");    	
	      	q.addFilterQuery("contextGroup:" + fq.toString());
	    	
	    }
	}

	private void addSectionFilter(List<String> sectionIdList, SolrQuery q) {
		
		q.addFacetField("{!ex=foo}sitesection");
	
		if (sectionIdList != null && sectionIdList.size() > 0) {
			String sectionnOperator = " OR ";
			
			StringBuffer fq = new StringBuffer("(");
	    	for (String id : sectionIdList) {
	    		if (id != null)
	    		{
	    			if (fq.length() > 1) {
		    			fq.append(sectionnOperator);
		    		}
	    			
	    			fq.append("\"" + id + "\"");
		    	}
	    	}
	    	fq.append(")");    	
	      	q.addFilterQuery("{!tag=foo}sitesection:" + fq.toString());
	    	
	    }
	}
	
	private void addClassificationAndFilter(List<String> classificationAndIdList, SolrQuery q) {
		q.addFacetField("{!ex=foo}categoryAndTree");
		
		if (classificationAndIdList != null && classificationAndIdList.size() > 0) {
	    	//tag=foo thing comes from Solr 1.4 book p155
			String classificationOperator = " AND ";
		
			StringBuffer fq = new StringBuffer();
			fq.append("(");
	    	for (String id : classificationAndIdList) {
	    		if (id != null)
	    		{
	    			if (fq.length() > 1) {
	    				fq.append(classificationOperator);
		    		}
	    			
	    			fq.append("\"" + id + "\"");
		    	}
	    	}
	    	fq.append(")");    	
	    	q.addFilterQuery("{!tag=foo}categoryAndTree:" + fq.toString());    
	    }
	
	}
	
	private void addClassificationOrFilter(List<String> classificationOrIdList, SolrQuery q) {
		q.addFacetField("{!ex=foo}categoryOrTree");
		
		if (classificationOrIdList != null && classificationOrIdList.size() > 0) {
	    	//tag=foo thing comes from Solr 1.4 book p155
			String classificationOperator = " OR ";
		
			StringBuffer fq = new StringBuffer();
			fq.append("(");
	    	for (String id : classificationOrIdList) {
	    		if (id != null)
	    		{
	    			if (fq.length() > 1) {
	    				fq.append(classificationOperator);
		    		}
	    			
	    			fq.append("\"" + id + "\"");
		    	}
	    	}
	    	fq.append(")");    	
	    	q.addFilterQuery("{!tag=foo}categoryOrTree:" + fq.toString());    
	    }
	
	}

	private void addSearchableClassificationAndFilter(List<String> searchableClassificationAndrIdList, SolrQuery q) {
		
		if (searchableClassificationAndrIdList != null && searchableClassificationAndrIdList.size() > 0) {
			String classificationOperator = " AND ";
			
			StringBuffer fq = new StringBuffer();
			fq.append("(");
	    	for (String id : searchableClassificationAndrIdList) {
	    		if (id != null)
	    		{
	    			if (fq.length() > 1) {
	    				fq.append(classificationOperator);
		    		}
	    			
	    			fq.append("\"" + id + "\"");
		    	}
	    	}
	    	fq.append(")");    	 	
	    	q.addFilterQuery("{!tag=foo}categoryAndTree:" + fq.toString());
	    	
	    }
	}
	
	private void addSearchableClassificationOrFilter(List<String> searchableClassificationOrIdList, SolrQuery q) {
		
		if (searchableClassificationOrIdList != null && searchableClassificationOrIdList.size() > 0) {
			String classificationOperator = " OR ";
			
			StringBuffer fq = new StringBuffer();
			fq.append("(");
	    	for (String id : searchableClassificationOrIdList) {
	    		if (id != null)
	    		{
	    			if (fq.length() > 1) {
	    				fq.append(classificationOperator);
		    		}
	    			
	    			fq.append("\"" + id + "\"");
		    	}
	    	}
	    	fq.append(")");    	 	
	    	q.addFilterQuery("{!tag=foo}categoryOrTree:" + fq.toString());
	    	
	    }
	}
	
	private void addBoostClassificationOrFilter(List<String> boostClassificationOrIdList, SolrQuery q) {
		if (boostClassificationOrIdList != null && boostClassificationOrIdList.size() > 0) {

			StringBuffer  sbBoostFilter = new StringBuffer("(");
			for (String id : boostClassificationOrIdList) {
				if (sbBoostFilter.length() > 1) {
					sbBoostFilter.append(" OR ");
				}
				sbBoostFilter.append(id);	
			}
			sbBoostFilter.append(")");
			q.set("defType", "edismax");
			q.set("bq", "categoryOrTree:" + sbBoostFilter.toString() + "^1000");
		}
	}
	
	private void addDateQuery(String fromDate, String toDate, SolrQuery q) {
		String dateQuery = "";
		String defaultFromDate = SearchConstants.SOLR_QUERY_DATE_FORMAT.format(new Date());
		
		String solrDateFrom;
		String solrDateTo;
	
		if (fromDate != null) {
			solrDateFrom = DateHelper.parseSolrDate(fromDate, SearchConstants.START_DAY_TIME);
		} else {
			solrDateFrom = defaultFromDate;
		}
		if (toDate != null) {
			solrDateTo = DateHelper.parseSolrDate(toDate, SearchConstants.END_DAY_TIME);
		} else {	
			solrDateTo = "*";
		}	
		
		dateQuery = "dates:[" + solrDateFrom + " TO " + solrDateTo + "]";
		q.addFilterQuery(dateQuery);
		
//			if ("THISMONTH".equalsIgnoreCase(facetValue)) {
//				q.addFilterQuery("{!tag=" + facetName + "}" + facetName + ":[NOW/MONTH TO NOW/MONTH+1MONTHS]");
//			} else if ("LASTMONTH".equalsIgnoreCase(facetValue)) {
//				q.addFilterQuery("{!tag=" + facetName + "}" + facetName + ":[NOW/MONTH-1MONTHS TO NOW/MONTH]");
//			}
//			// Sticking these boys in for future use
//			else if ("THISYEAR".equalsIgnoreCase(facetValue)) {
//				q.addFilterQuery("{!tag=" + facetName + "}" + facetName + ":[NOW/YEAR TO NOW/MONTH+1MONTHS]");
//			} else if ("LASTYEAR".equalsIgnoreCase(facetValue)) {
//				q.addFilterQuery("{!tag=" + facetName + "}" + facetName + ":[NOW/YEAR-1YEARS TO NOW/YEAR]");
//			} else {
//				q.addFilterQuery("{!tag=" + facetName + "}" + facetName + ":[NOW/MONTH+1MONTHS-" + facetValue + " TO NOW/MONTH+1MONTHS]");
//			}
//
//		}
	}
	
	private void addPriceQuery(Double priceFrom, Double priceTo, SolrQuery q) {
		
		String priceQuery = "";
		String defaultFromPrice = "0";
		
		String solrPriceFrom;
		String solrPriceTo;
	
		if (priceFrom == null) {
			solrPriceFrom = defaultFromPrice;
		} else {
			solrPriceFrom = DataHelper.dfPrice.format(priceFrom);
		}

		if (priceTo == null) {
			solrPriceTo = "*";
		}else {
			solrPriceTo = DataHelper.dfPrice.format(priceTo);
		}
		
		priceQuery = "fromPrice:[" + solrPriceFrom + " TO " + solrPriceTo + "]";
		q.addFilterQuery(priceQuery);
	
	}
	
	private void addSortOption(SolrQuery q, SearchResult result, String sortOption, SearchInfo searchInfo, String searchText) {
		//Sorting options
		result.setRandomSortID(null);

		String sortField = null;
		ORDER sortOrder = null;
		boolean sortRequired = true;
		
		if (SortOption.ALPHABETICALLY.getOption().equalsIgnoreCase(sortOption)) {
			sortField = "title_display";
			sortOrder = ORDER.asc;
				
		} else if (SortOption.DATE.getOption().equalsIgnoreCase(sortOption)) {
			sortField = "startDate";
			sortOrder = ORDER.asc;
		
		} else if (SortOption.PRICE.getOption().equalsIgnoreCase(sortOption)) {
			sortField = "priceFrom";
			sortOrder = ORDER.asc;
		
		} else if (SortOption.DISTANCE.getOption().equalsIgnoreCase(sortOption)) {
			// done automatically when we do a location search
			sortRequired = false;
		} else if (StringUtils.indexOf(sortOption, "random_") == 0) {
				sortField = sortOption;
				sortOrder = ORDER.asc;
		} else if (SortOption.RANDOM.getOption().equalsIgnoreCase(sortOption)) {
			String randSort = searchInfo.getRandomSortId();
			
			if (randSort != null){
				sortField = randSort;
			}
			else {
				sortField = "random_" + new Random().nextInt(10000);
			}
			sortOrder = ORDER.asc;
			result.setRandomSortID(sortField);
			searchInfo.setRandomSortId(sortField);
		}

		if (sortRequired){
			SortClause sortClause = new SortClause(sortField, sortOrder);
			q.setSort(sortClause);
		}
	}

	@Override
	public SearchResult getSearchResults(HttpServletRequest request, ModelMap modelMap, SearchInfo searchInfo, boolean smartSuggest) {
		SearchResult result = new SearchResult();		

		
		if (searchInfo != null) { 
			boolean highlighting = false;
			int snippetLength = searchConfigDAO.findSnippetLength(250);

	    	// pagination
	    	int pageNum = 1;
	    	if (searchInfo.getPage() != null && searchInfo.getPage() > 0) {
	    		pageNum = searchInfo.getPage();
	    	}
			
			int pSize = 10;
			if (searchInfo.getPageSize() != null) {
				pSize = searchInfo.getPageSize();
			}

			// sort
	    	String sortOption = searchInfo.getSortOption();
	    	
	       	// rand sort
	    	String randomSortId = searchInfo.getRandomSortId();
			SolrQuery q = new SolrQuery();
			
			q.setStart((pageNum-1) * pSize);
			q.setRows(pSize);

			
			String searchText = searchInfo.getSearchText();		
			List<String> searchWords = new ArrayList<String>();
			
			boolean saMonitor = searchInfo.isSaMonitor();
			
			String queryText;
			boolean rhSet = false;	
			if (searchInfo.isAtozSearch() == true) {
				q.setRequestHandler("/search_atoz");
				queryText = searchText;
				rhSet = true;
			} else if (StringUtils.isNotBlank(sortOption) && 
						!SortOption.RELEVANCY.getOption().equalsIgnoreCase(sortOption)) {
				q.setRequestHandler("/search_sort");
				addSortOption(q, result, sortOption, searchInfo, searchText);
				queryText = searchText;
				rhSet = true;
			} else if (StringUtils.isNotBlank(searchText)) {
				queryText = getTextQuery(searchText);
					
			} else {
				queryText = "*:*";
			}
			q.setQuery(queryText);
			
			if (StringUtils.isNotBlank(searchText) && smartSuggest == false) {
				addHighlightingQuery(q, snippetLength);
				highlighting = true;
				addSpellCheckQuery(q, searchText, rhSet);
			} 
				
			
	    	boolean geoSearch = false;
	    	List<String> contextIdList = searchInfo.getContextIdList();
	    	// contexts
			addContextFilter(contextIdList,  q);

			List<String> sectionIdList = searchInfo.getSectionIdList();
	    	List<String> classificationAndIdList = searchInfo.getClassificationAndIdList();
	    	List<String> classificationOrIdList = searchInfo.getClassificationOrIdList();
	    
	    	List<String> searchableClassificationAndIdList = searchInfo.getSearchableClassificationAndIdList();
	    	List<String> searchableClassificationOrIdList = searchInfo.getSearchableClassificationOrIdList();
	    	
	    	List<String> boostClassificationOrIdList = null;
	    	//if (StringUtils.isBlank(searchText)) {
	    		boostClassificationOrIdList = searchInfo.getBoostClassificationOrIdList();
	    	//}
	    	
	    	if (smartSuggest == false) {
	
	    		// facets
		    	q.setFacet(true);
				q.setFacetLimit(-1); //By default solr returns only 100 facets. Setting this to -ve number will return unlimited number of facets.
				q.addFacetField("atoz");
		    	
				// classifications
		    	addSectionFilter(sectionIdList,  q);
		    	
	    		// classifications
		    	addClassificationAndFilter(classificationAndIdList,  q);
		     	addClassificationOrFilter(classificationOrIdList, q);
		     	
		     	// searchable classifications
		     	addSearchableClassificationAndFilter(searchableClassificationAndIdList, q);
		    	addSearchableClassificationOrFilter(searchableClassificationOrIdList, q);
			     
		    	// boost classifications
		    	//if (StringUtils.isBlank(searchText)) {
		    		addBoostClassificationOrFilter(boostClassificationOrIdList, q);
		    	//}
		    	
		    	// Geo
				String rad = searchInfo.getRadius();
		    	Double latitude = searchInfo.getLatitude();
		     	Double longitude = searchInfo.getLongitude();
		     
		     	if (latitude != null && longitude != null) {
		     		if (StringUtils.isBlank(rad)) {
		     			rad="99";
		     		}
		     		geoSearch = true;
		     		addSpatialSearch(latitude.toString(), longitude.toString(), rad, q);
		     	}
		     	
		    	Double priceFrom = searchInfo.getPriceFrom();
		     	Double priceTo = searchInfo.getPriceTo();
		     
		     	if (priceFrom != null || priceTo != null) {
		     		addPriceQuery(priceFrom, priceTo, q);
		     	}
		     		
		     	
		     	// Date facets
		     	// Note: facet.date deprecated, user range
		    	q.add("facet.range", "{!ex=dates}dates");
				q.add("f.dates.facet.range.start", "NOW");
				q.add("f.dates.facet.range.end", "NOW/MONTH+1YEARS");
				q.add("f.dates.facet.range.gap", "+1MONTHS");
				
				//Price facets
				//q.add("facet.range", "{!ex=priceFrom}priceFrom");
				//q.add("f.priceFrom.facet.range.start", "0");
				//q.add("f.priceFrom.facet.range.end", "100");
				//q.add("f.priceFrom.facet.range.gap", "20");
				//q.add("f.priceFrom.facet.range.other", "after");
				
				//facet.interval for pricing
				//&f.price.facet.interval.set={!key=foo}[0,10]&f.price.facet.interval.set={!key=bar}[10,100]
				// should be configurable
				q.add("facet.interval", "{!ex=priceFrom}priceFrom");
				q.add("f.priceFrom.facet.interval.set", "[0,20]");
				q.add("f.priceFrom.facet.inteval.set", "(20,50]");
				q.add("f.priceFrom.facet.inteval.set", "(50,100]");
				q.add("f.priceFrom.facet.inteval.set", "(100,250]");
				q.add("f.priceFrom.facet.inteval.set", "(250,500]");
				q.add("f.priceFrom.facet.inteval.set", "(500,*]");
	    	}
	    	
	    	// document type query 
    		addDocumentTypeQuery(searchInfo.getDocumentTypeList(), q);
    	    
	    	// date query 
	    	if (StringUtils.isNotBlank(searchInfo.getDateFrom())) {
	    		addDateQuery(searchInfo.getDateFrom(), searchInfo.getDateTo(), q);
	    	}
			
			try {
				//Do the query
				QueryResponse solrResponse = makeSolrQuery(q);
				SolrDocumentList solrResponseDocs = solrResponse.getResults();
				
				processSearchResults(searchInfo, searchText, pageNum, pSize,sortOption, result, snippetLength, highlighting, geoSearch, smartSuggest, solrResponse, solrResponseDocs);
				
				if (smartSuggest == false && saMonitor == false) {
					//
					//Log the search to the db for reporting purposes
					//We only log the initial search, not when the user is browsing or 
					//refining the search results via the facets
					//
					if (StringUtils.isNotBlank(searchText) &&  pageNum == 1 && 
							(classificationAndIdList == null || classificationAndIdList.size() == 0) &&
							(classificationOrIdList == null || classificationOrIdList.size() == 0)) {
						SearchLog searchLog = new SearchLog();
						searchLog.setSearchText(searchText);
						searchLog.setSearchTextLower(searchText.toLowerCase());
						searchLog.setLogDate(new Date());
						searchLogDAO.save(searchLog);
					}
				}
				
			} catch (Exception e) {
				m_log.error("Search failed ", e);
			}
		}
		
		return result;
	}
	
	private void processSearchResults(SearchInfo searchInfo, String searchText, Integer page, Integer pageSize, String sortOption, SearchResult result, 
			int snippetLength, boolean highlighting, boolean geoSearch, boolean smartSuggest, QueryResponse solrResponse,
			SolrDocumentList solrResultPageDocs) {

		// Populate result list
		result.setItems(new ArrayList<DisplayItem>());
		int hitLen = solrResultPageDocs == null ? 0 : solrResultPageDocs.size();

		for (int i = 0; i < hitLen; i++) {
			SolrDocument doc = solrResultPageDocs.get(i);
			DisplayItem di = populateDisplayItem(doc, solrResponse, snippetLength, highlighting, geoSearch);
			result.getItems().add(di);
		}

		if (smartSuggest == false) {
			SpellCheckResponse spellingResponse = solrResponse.getSpellCheckResponse();
			if (spellingResponse != null) {
				List<SpellingSuggestion> spellingSuggestions = result.getSpellingSuggestions();
				
				List<Suggestion> suggestions = spellingResponse.getSuggestions();
				String collatedResult = spellingResponse.getCollatedResult();
	
				if (StringUtils.isNotBlank(collatedResult)) {
					SpellingSuggestion spellingSuggestion = new SpellingSuggestion();
					spellingSuggestion.setSuggestion(collatedResult);
					if (searchInfo != null) {
						
						String json = searchInfo.toJson();
						SearchInfo searchInfoTn  = SearchInfo.fromJson(json);
						searchInfoTn.setPage(0);
						searchInfoTn.setSearchText(collatedResult);
						byte[]   bytesEncoded = Base64.encodeBase64(searchInfoTn.toJson().getBytes());
						String encoded = new String(bytesEncoded);
						spellingSuggestion.setEncodedUrl(encoded);
						
						String paramUrl = DataHelper.searchInfoToUrl(searchInfoTn);
						spellingSuggestion.setParamUrl(paramUrl);
						
					}
					spellingSuggestions.add(spellingSuggestion);
	
				} else {
					if (suggestions.size() > 0) {
						for (Suggestion suggestion : suggestions) {
							String suggestedWord = suggestion.getAlternatives().get(0);
							SpellingSuggestion spellingSuggestion = new SpellingSuggestion();
							spellingSuggestion.setSuggestion(suggestedWord);
							if (searchInfo != null) {
								
								String json = searchInfo.toJson();
								SearchInfo searchInfoTn  = SearchInfo.fromJson(json);
								searchInfoTn.setPage(0);
								searchInfoTn.setSearchText(suggestedWord);
								byte[]   bytesEncoded = Base64.encodeBase64(searchInfoTn.toJson().getBytes());
								String encoded = new String(bytesEncoded);
								spellingSuggestion.setEncodedUrl(encoded);
								
								String paramUrl = DataHelper.searchInfoToUrl(searchInfoTn);
								spellingSuggestion.setParamUrl(paramUrl);

							}
							spellingSuggestions.add(spellingSuggestion);
						}
					}
				}
			}
	
			// facets
			extractFacets(searchInfo, result, solrResponse);
			
			populatePaginationInfo(searchInfo, result, page, pageSize, solrResultPageDocs);
		} else {
			int totalResults = solrResultPageDocs == null? 0 : (int) solrResultPageDocs.getNumFound();
			result.setTotalResults(totalResults);
		}
		
		result.setSearchText(searchText);
		result.setSortOption(sortOption);
		
		setRecommendedLinks(result, page, searchText);
		
	}
	
	private DisplayItem populateDisplayItem(SolrDocument doc, QueryResponse solrResponse, int snippetLength, boolean highlighting, boolean geoSearch) {

		DisplayItem di = new DisplayItem();
		
		Map<String, Map<String, List<String>>> highlightMap = solrResponse.getHighlighting();

		String id = (String) doc.getFieldValue("id");

		di.setId(id);
		String title = (String) doc.getFieldValue("title_display");
		di.setTitle(title);
		
		// XXX TODO config this
		String url = (String) doc.getFieldValue("pageUrl");
			
		String content = (String) doc.getFieldValue("content_display");
		di.setContent(StringUtils.substring(content, 0, snippetLength));
		
		List<String> contexts = (List<String>)doc.getFieldValue("contextGroup");
		if (contexts != null) {
			di.setContextList(contexts);
		}

		String classifictions = (String) doc.getFieldValue("classifications_display");
		di.setClassifications(StringUtils.substring(classifictions, 0, snippetLength));
		
		Boolean smartSuggest =  (Boolean)doc.getFieldValue("smartSuggest");
		if (smartSuggest != null) {
			di.setSmartSuggest(smartSuggest.booleanValue());
		}

		if (doc.containsKey("location_0_coordinate")) {
			di.setLatitude((Double)doc.getFieldValue("location_0_coordinate"));
		}

		if (doc.containsKey("location_1_coordinate")) {
			di.setLongitude((Double)doc.getFieldValue("location_1_coordinate"));
		}
		
		//Distance between care home and user specified distance
		if (geoSearch == true)
		{
			if(doc.containsKey("distance"))
			{
				Double distanceFrom = (Double) doc.getFieldValue("distance");
				di.setDistance(distanceFrom);
			}
		}
		
//		if (StringUtils.isNotEmpty(url) && url.contains("~/media")) {
//
//			String protocol = StringUtils.substringBefore(url, "//");
//			String path = StringUtils.substringAfter(url, "//");
//			String domain = StringUtils.substringBefore(path, "/");
//			String mediaPath = StringUtils.substringAfter(path, "~");
//			url = protocol + "//" + domain + "/~" + mediaPath;
//		}
		// XXX

		di.setUrl(url); 

		if (highlighting && highlightMap != null) {
			
			// Highlights in title
			List<String> highlightSnippetsTitle = solrResponse.getHighlighting().get(id).get("title");
			if (highlightSnippetsTitle != null) {
				List<String> cleanedHighlightSnippetsTitle = new ArrayList<String>();
				cleanSnippets(cleanedHighlightSnippetsTitle, highlightSnippetsTitle);
				di.setHighlightSnippetsTitle(cleanedHighlightSnippetsTitle);
			}
			
			// content
			List<String> highlightSnippetsContent = solrResponse.getHighlighting().get(id).get("content");
			if (highlightSnippetsContent != null) {
				List<String> cleanedHighlightSnippetsContent = new ArrayList<String>();
				cleanSnippets(cleanedHighlightSnippetsContent, highlightSnippetsContent);
				di.setHighlightSnippetsContent(cleanedHighlightSnippetsContent);
			}
			
			// classifications
			List<String> highlightSnippetsClassification = solrResponse.getHighlighting().get(id).get("classification");
			if (highlightSnippetsClassification != null) {
				List<String> cleanedHighlightSnippetsClassification = new ArrayList<String>();
				cleanSnippets(cleanedHighlightSnippetsClassification, highlightSnippetsClassification);
				di.setHighlightSnippetsClassification(cleanedHighlightSnippetsClassification);
			}

		} else {
			//highlightSnippets = new ArrayList<String>();
			//highlightSnippets.add(StringUtils.substring(content, 0, snippetLength));
		}

		di.setSnippetLength(snippetLength);

		return di;
	}

	private void cleanSnippets(List<String> cleanedHighlightSnippets, List<String> highlightSnippets) {
		for (String snippet : highlightSnippets) {
	
			if (snippet!=null) {
				// Remove any non-ASCII chars
				String cleanSnippet = snippet.replaceAll("\\u00A0", " ").replaceAll("[^\\x00-\\x7F]", "");
	
				// Try to start on a word rather than some punctuation
				if (cleanSnippet.length() > 0 && !Character.isLetterOrDigit(cleanSnippet.charAt(0)) && !(cleanSnippet.charAt(0) == '<')) {
					cleanSnippet = StringUtils.substringAfter(cleanSnippet, " ");
				}
	
				cleanedHighlightSnippets.add(cleanSnippet);
			}
		}
	}
	
	/*
	private void extractFacetPivots(SearchResult result,
			QueryResponse solrResponse) {
		HashMap<Long,HashMap<LocalDate, Integer>> map = new HashMap<Long,HashMap<LocalDate, Integer>>();
		NamedList<List<PivotField>> lst = solrResponse.getFacetPivot();

		List<PivotField> list = lst.get("vendorid,availabledates");
		Iterator<PivotField> it = list.iterator();
		while (it.hasNext()){
			PivotField obj = it.next();
			Long vendorId = Long.parseLong(obj.getValue().toString());
			HashMap<LocalDate, Integer> dtMap = new HashMap<LocalDate, Integer>();
			List<PivotField> avDates = obj.getPivot();
			for (PivotField p:avDates){
				dtMap.put(new LocalDate((Date)p.getValue()), p.getCount());
			}
			map.put(vendorId, dtMap);
		}

		result.setBedAvailabilityMap(map);

	}
	 */

	private void extractFacets(SearchInfo searchInfo, SearchResult result, QueryResponse solrResponse) {
		List<FacetField> facetFields = solrResponse.getFacetFields();	
		
		if (facetFields != null){	
			List<ResultFacet> facetList =  result.getResultFacets();

			for (FacetField facetField : facetFields) {
				if (facetField.getName().equals("categoryAndTree")){
				 	List<String> searchableClassificationAndIdList = searchInfo.getSearchableClassificationAndIdList();
			   
					ResultFacet resultFacet = new ResultFacet();
					resultFacet.setName(facetField.getName());
					facetList.add(resultFacet);
					List<ResultFacetEntry> entries = resultFacet.getEntries();
					List<Count> values = facetField.getValues();
					if (values != null) {
						for (Count count : values) {
							String name = count.getName();
							if (name != null) {
								
								if (searchableClassificationAndIdList != null && searchableClassificationAndIdList.size() > 0) {
//									if (!searchableClassificationAndIdList.contains(name)) {
//										continue;
//									}
									if (!searchableClassificationAndIdList.stream().allMatch(str -> str.equals(name))) {
										continue;
									}									
								} 
								
								ResultFacetEntry resultFacetEntry = resultFacet.new ResultFacetEntry();
								resultFacetEntry.setName(name);
								resultFacetEntry.setCount(count.getCount());
								generateClassifiactionFacetLink(resultFacetEntry, searchInfo, false);
								entries.add(resultFacetEntry);
							}
						}
					} 
					
				} else if (facetField.getName().equals("categoryOrTree")){
			    	List<String> searchableClassificationOrIdList = searchInfo.getSearchableClassificationOrIdList();
							
					ResultFacet resultFacet = new ResultFacet();
					resultFacet.setName(facetField.getName());
					facetList.add(resultFacet);
					List<ResultFacetEntry> entries = resultFacet.getEntries();
					List<Count> values = facetField.getValues();
					if (values != null) {
						for (Count count : values) {
							String name = count.getName();
							
							if (name != null) {

								if (searchableClassificationOrIdList != null && searchableClassificationOrIdList.size() > 0) {
//									if (!searchableClassificationOrIdList.contains(name)) {
//										continue;
//									}
									if (!searchableClassificationOrIdList.stream().allMatch(str -> str.equals(name))) {
										continue;
									
									}
								} 

								ResultFacetEntry resultFacetEntry = resultFacet.new ResultFacetEntry();
								resultFacetEntry.setName(name);
								resultFacetEntry.setCount(count.getCount());
								generateClassifiactionFacetLink(resultFacetEntry, searchInfo, true);
								entries.add(resultFacetEntry);
							}
						}
					} 
					
				} else if (facetField.getName().equals("sitesection")){
						
					ResultFacet resultFacet = new ResultFacet();
					resultFacet.setName(facetField.getName());
					facetList.add(resultFacet);
					List<ResultFacetEntry> entries = resultFacet.getEntries();
					List<Count> values = facetField.getValues();
					if (values != null) {
						for (Count count : values) {
							if (count.getName() != null) {
								ResultFacetEntry resultFacetEntry = resultFacet.new ResultFacetEntry();
								resultFacetEntry.setName(count.getName());
								resultFacetEntry.setCount(count.getCount());
								generateSectionFacetLink(resultFacetEntry, searchInfo);
								entries.add(resultFacetEntry);
							}
						}
					}
				}
			}
		}
		

		List<RangeFacet> facetRanges = solrResponse.getFacetRanges();

		if (facetRanges != null) {
			for (RangeFacet rangeFacet : facetRanges) {
				if (rangeFacet.getName().equals("dates")) {

					ResultFacet resultFacet = new ResultFacet();
					resultFacet.setName(rangeFacet.getName());
					List<ResultFacet> facetList =  result.getResultFacets();
					facetList.add(resultFacet);
					List<ResultFacetEntry> entries = resultFacet.getEntries();
				
				    Date startDate = (Date)rangeFacet.getStart();
				    if (startDate != null) {
				    	try {
							String start = DateHelper.convertDateToString(startDate, DateFormat.getDateInstance(DateFormat.LONG, Locale.UK));
							resultFacet.setStart(start);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
					Date endDate = (Date)rangeFacet.getEnd();
					if (endDate != null) {
						try {
							String end = DateHelper.convertDateToString(endDate, DateFormat.getDateInstance(DateFormat.LONG, Locale.UK));
							resultFacet.setEnd(end);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				    String gap = (String)rangeFacet.getGap();
				    if (StringUtils.isNotBlank(gap)) {
				    	resultFacet.setGap(gap);
				    }
				    
				    Number before = rangeFacet.getBefore();
				    resultFacet.setBefore(before);
				    Number after = rangeFacet.getAfter();
				    resultFacet.setAfter(after);
				    Number between= rangeFacet.getBetween();
				    resultFacet.setBetween(between);
				    
				    List<RangeFacet.Count> values = rangeFacet.getCounts();
					if (values != null) {
						for (RangeFacet.Count count : values) {
							ResultFacetEntry resultFacetEntry = resultFacet.new ResultFacetEntry();
							resultFacetEntry.setName(count.getValue());
							resultFacetEntry.setCount(count.getCount());
							entries.add(resultFacetEntry);
						}
					}

				}
			}
		}

		List<IntervalFacet> facetIntevals = solrResponse.getIntervalFacets();

		if (facetIntevals != null) {
			for (IntervalFacet intervalFacet : facetIntevals) {
				if (intervalFacet.getField().equals("priceFrom")) {

					ResultFacet resultFacet = new ResultFacet();
					resultFacet.setName(intervalFacet.getField());
					List<ResultFacet> facetList =  result.getResultFacets();
					facetList.add(resultFacet);
					List<ResultFacetEntry> entries = resultFacet.getEntries();

					List<IntervalFacet.Count> values = intervalFacet.getIntervals();
					if (values != null) {
						for (IntervalFacet.Count count : values) {
							ResultFacetEntry resultFacetEntry = resultFacet.new ResultFacetEntry();
							resultFacetEntry.setName(count.getKey());
							resultFacetEntry.setCount(count.getCount());
							entries.add(resultFacetEntry);
						}
					}
				}
			}
		}
			
		
		//If A to Z search (i.e. we are drilling down to a specific letter) 
		//then we get the facets from a separate query. 
		SolrQuery atozCountQuery = new SolrQuery();
		//atozCountQuery.addFilterQuery(typeFilter);
		atozCountQuery.setRequestHandler("/search_atoz");
		atozCountQuery.setFacet(true);
		atozCountQuery.setFacetLimit(-1); //By default solr returns only 100 facets. Setting this to -ve number will return unlimited number of facets.	   
		atozCountQuery.addFacetField("atoz");
		
		// Add spatial search ?
		// Geo
		String rad = searchInfo.getRadius();
    	Double latitude = searchInfo.getLatitude();
     	Double longitude = searchInfo.getLongitude();
     
     	if (rad != null && latitude != null && longitude != null) {
     		addSpatialSearch(latitude.toString(), longitude.toString(), rad, atozCountQuery);
     	}
     	
		// a to z facets
		try {
			QueryResponse solrAtoZResponse = makeSolrQuery(atozCountQuery);
			List<FacetField> atoZFacetFields = solrAtoZResponse.getFacetFields();
			if (atoZFacetFields != null){	
				for (FacetField facetField : atoZFacetFields) {
					if (facetField.getName().equals("atoz")){
						ResultFacet resultFacet = new ResultFacet();
						resultFacet.setName(facetField.getName());
						List<ResultFacet> facetList =  result.getResultFacets();
						facetList.add(resultFacet);
						List<ResultFacetEntry> entries = resultFacet.getEntries();
						List<Count> values = facetField.getValues();
		
						if (values != null) {
							for (Count count : values) {
								if (count.getName() != null) {
									ResultFacetEntry resultFacetEntry = resultFacet.new ResultFacetEntry();
									resultFacetEntry.setName(count.getName());
									resultFacetEntry.setCount(count.getCount());
									//resultFacetEntry.setUrl(url);
									entries.add(resultFacetEntry);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			m_log.error("atoz search failed ", e);
		}
	
	}
	
	private void generateSectionFacetLink(ResultFacetEntry resultFacetEntry, SearchInfo searchInfo) {
		
		if (searchInfo != null) {
			String name = resultFacetEntry.getName();
			
			String json = searchInfo.toJson();
			SearchInfo searchInfoTn  = SearchInfo.fromJson(json);
			searchInfoTn.setPage(0);
			List<String> ids = searchInfoTn.getSectionIdList();
					
			setFacetSelected(resultFacetEntry, searchInfoTn, ids, name);
			
			byte[]   bytesEncoded = Base64.encodeBase64(searchInfoTn.toJson().getBytes());
			String encoded = new String(bytesEncoded);
			resultFacetEntry.setEncodedUrl(encoded);
			
			String paramUrl = DataHelper.searchInfoToUrl(searchInfoTn);
			resultFacetEntry.setParamUrl(paramUrl);
			
		}
	}
	
	private void generateClassifiactionFacetLink(ResultFacetEntry resultFacetEntry, SearchInfo searchInfo, boolean orClassification) {
			
		if (searchInfo != null) {
			String name = resultFacetEntry.getName();
			
			String json = searchInfo.toJson();
			SearchInfo searchInfoTn  = SearchInfo.fromJson(json);
			searchInfoTn.setPage(0);
		
			List<String> ids;
			if (orClassification == true) {
				ids = searchInfoTn.getClassificationOrIdList();
					
			} else {
				ids = searchInfoTn.getClassificationAndIdList();
			}
					
			setFacetSelected(resultFacetEntry, searchInfoTn, ids, name);
			
			byte[]   bytesEncoded = Base64.encodeBase64(searchInfoTn.toJson().getBytes());
			String encoded = new String(bytesEncoded);
			resultFacetEntry.setEncodedUrl(encoded);
			
			String paramUrl = DataHelper.searchInfoToUrl(searchInfoTn);
			resultFacetEntry.setParamUrl(paramUrl);
			
		}
	}
	
	private void setFacetSelected(ResultFacetEntry resultFacetEntry, SearchInfo searchInfoTn, List<String> ids, String name) {
		if (ids.contains(name)) {
			ids.remove(name);
			resultFacetEntry.setSelected(true);
		} else {
			ids.add(name);
			resultFacetEntry.setSelected(false);
		}
		
	}

	@Override
	@SuppressWarnings("unchecked")
	public CmsDocument findById(String id) {
		try {
			//Do the query
			SolrQuery q = new SolrQuery();
			
			q.setStart(0);
			q.setRows(1);
			q.setQuery("id:" + id);
		
			//q.add("id", id);
			QueryResponse solrResponse = makeSolrQuery(q);
			SolrDocumentList solrResponseDocs = solrResponse.getResults();
			
			if (solrResponseDocs.getNumFound() == 1 ) {		
				SolrDocument doc = solrResponseDocs.get(0);
				
				CmsDocument cmsDocument = new CmsDocument();
				cmsDocument.setId(id);
			
				String title = (String) doc.getFieldValue("title_display");
				cmsDocument.setPageTitle(title);
				
				// XXX TODO config this
				String pageUrl = (String) doc.getFieldValue("pageUrl");
				cmsDocument.setPageUrl(pageUrl);
				
				String content = (String) doc.getFieldValue("content_display");
				cmsDocument.setPageContent(content);
				
				Boolean smartSuggest =  (Boolean)doc.getFieldValue("smartSuggest");
				if (smartSuggest != null) {
					cmsDocument.setSmartSuggest(smartSuggest.booleanValue());
				}
					
				List<String> contexts = (List<String>)doc.getFieldValue("contextGroup");
				if (contexts != null) {
					cmsDocument.setContexts(contexts);
				}
				
				List<String> andClassifications = (List<String>)doc.getFieldValue("categoryAndTree");
				if (andClassifications != null) {
					cmsDocument.setAndClassifications(andClassifications);
				}
				
				List<String> orClassifications = (List<String>)doc.getFieldValue("categoryOrTree");
				if (orClassifications != null) {
					cmsDocument.setOrClassifications(orClassifications);
				}
				
				return cmsDocument;
			}
			
			
		} catch (Exception e) {
			m_log.error("Search failed ", e);
		}
		
		return null;
	}
	
//	public void reloadCore() {
//		SolrConfig solrConfig = getSolrConfig();	
//		String coreName = solrConfig.getSolrCore();
//		SolrClient solrClient = getSolrClient();
//		
//		try {
//						
//			CoreAdminResponse coreResponse = CoreAdminRequest.reloadCore(coreName, solrClient);			
//
//			m_log.info("Solr core reload status: "+coreResponse.getCoreStatus());
//		} catch (SolrServerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
}

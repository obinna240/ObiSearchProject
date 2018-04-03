package com.sa.search.api.cms.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sa.search.api.cms.controller.bean.SmartSuggestCategory;
import com.sa.search.config.SearchConstants;
import com.sa.search.solrsearch.SearchInfo;
import com.sa.search.solrsearch.SearchResult;
import com.sa.search.solrsearch.SearchServices;
import com.sa.search.view.controller.AdminController;
import com.sa.search.view.controller.form.SearchForm;

import flexjson.JSONSerializer;

@RequestMapping("/api/cms/search/**")
@Controller
public class SearchController  extends CmsController{
	private static Log m_log = LogFactory.getLog(SearchController.class);

	@Autowired private SearchServices searchService;
	
	@RequestMapping("/Search")  
	protected String search(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
		
		request.getSession().setAttribute("searchInfo", null);
		request.getSession().removeAttribute("searchInfo");
	
		// TODO Auto-generated method stub
		//SearchResult result = solrSearcher.getSearchResults(request.getParameter("searchText"));		
		
		model.put("msg",request.getParameter("searchText"));	
		//model.put("result",result);	
		return "searchResults";
	}

	@RequestMapping(value = "/getSearchResults")
	public @ResponseBody String getSearchResults(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "q", required = false) String searchText,
			
			@RequestParam(value = "t", required = false) String documentType,
			
			@RequestParam(value = "cid", required = false) String contextIdList,
			@RequestParam(value = "sid", required = false) String sectionIdList,
			@RequestParam(value = "caid", required = false) String classificationAndIdList,
			@RequestParam(value = "coid", required = false) String classificationOrIdList,

			@RequestParam(value = "scaid", required = false) String searchableClassificationAndIdList,
			@RequestParam(value = "scoid", required = false) String searchableClassificationOrIdList,
			@RequestParam(value = "bcid", required = false) String boostClassificationOrIdList,
			
			@RequestParam(value = "p", required = false) String pageNo,
			@RequestParam(value = "ps", required = false) String pageSize,
	
			@RequestParam(value = "so", required = false) String sortOption,
			@RequestParam(value = "rs", required = false) String randomSortId,
	
			@RequestParam(value = "pc", required = false) String searchPC,
			@RequestParam(value = "lat", required = false) String latitude,
			@RequestParam(value = "lng", required = false) String longitude,
			@RequestParam(value = "rad", required = false) String radius,
			
			@RequestParam(value = "df", required = false) String dateFrom,
			@RequestParam(value = "dt", required = false) String dateTo,
			@RequestParam(value = "dates", required = false) String dates,
			
			@RequestParam(value = "pf", required = false) String priceFrom,
			@RequestParam(value = "pt", required = false) String priceTo,
		
			
			@RequestParam(value = "atoz", required = false) String atoz,
			@RequestParam(value = "sa", required = false) String saMonitor
			
			) {
	
	
		request.getSession().setAttribute("searchInfo", null);
		request.getSession().removeAttribute("searchInfo");
		
		SearchInfo searchInfo = new SearchInfo();
	
		// search text
		
		if (StringUtils.isNotBlank(searchText)) {
			searchInfo.setSearchText(searchText);
		}
		
		if (StringUtils.isNotBlank(documentType)) {
			String[] ids = documentType.split(";");
			List<String> idList = searchInfo.getDocumentTypeList();
			for (String id : ids) {
				idList.add(id);
			}
			searchInfo.setDocumentTypeList(idList);
		}
		
		// metadata
		if (StringUtils.isNotBlank(contextIdList)) {
			String[] ids = contextIdList.split(";");
			List<String> idList = searchInfo.getContextIdList();
			for (String id : ids) {
				idList.add(id);
			}
		}
		
		
		if (StringUtils.isNotBlank(sectionIdList)) {
			String[] ids = sectionIdList.split(";");
			List<String> idList = searchInfo.getSectionIdList();
			for (String id : ids) {
				idList.add(id);
			}
		}
		
		if (StringUtils.isNotBlank(classificationAndIdList)) {
			String[] ids = classificationAndIdList.split(";");
			List<String> idList = searchInfo.getClassificationAndIdList();
			for (String id : ids) {
				idList.add(id);
			}
		}
		
		if (StringUtils.isNotBlank(classificationOrIdList)) {
			String[] ids = classificationOrIdList.split(";");
			List<String> idList = searchInfo.getClassificationOrIdList();
			for (String id : ids) {
				idList.add(id);
			}
		}
		
		if (StringUtils.isNotBlank(searchableClassificationAndIdList)) {
			String[] ids = searchableClassificationAndIdList.split(";");
			List<String> idList = searchInfo.getSearchableClassificationAndIdList();
			for (String id : ids) {
				idList.add(id);
			}
		}
		
		if (StringUtils.isNotBlank(searchableClassificationOrIdList)) {
			String[] ids = searchableClassificationOrIdList.split(";");
			List<String> idList = searchInfo.getSearchableClassificationOrIdList();
			for (String id : ids) {
				idList.add(id);
			}
		}
		
		if (StringUtils.isNotBlank(boostClassificationOrIdList)) {
			String[] ids = boostClassificationOrIdList.split(";");
			List<String> idList = searchInfo.getBoostClassificationOrIdList();
			for (String id : ids) {
				idList.add(id);
			}
		}
		
		// pagination Info
		try {
			if (StringUtils.isNotBlank(pageSize)) {
				searchInfo.setPageSize(Integer.parseInt(pageSize));
			}
		} catch (Exception e) {
			// Ignore and use the default
		}
		
		try {
			if (StringUtils.isNotBlank(pageNo)) {
				searchInfo.setPage(Integer.parseInt(pageNo));
			}
		} catch (Exception e) {
			// Ignore and use the default
		}
		
		//sort
		if (StringUtils.isNotBlank(sortOption)) {
			searchInfo.setSortOption(sortOption);
		}
	
		if (StringUtils.isNotBlank(randomSortId)) {
			searchInfo.setRandomSortId(randomSortId);
		}
		
		//spatial info
		if (StringUtils.isNotBlank(searchPC)) {
			searchInfo.setPc(searchPC);
		}
		
		try {
			if (StringUtils.isNotBlank(latitude)) {
				searchInfo.setLatitude(Double.parseDouble(latitude));
			}
		} catch (Exception e) {
			// Ignore and use the default
		}
		
		try {
			if (StringUtils.isNotBlank(longitude)) {
				searchInfo.setLongitude(Double.parseDouble(longitude));
			}
		} catch (Exception e) {
			// Ignore and use the default
		}
				
		
		if (StringUtils.isNotBlank(radius)) {
			searchInfo.setRadius(radius);
		}
		
		// dates
		if (StringUtils.isNotBlank(dateFrom)) {
			searchInfo.setDateFrom(dateFrom);
		}
		
		if (StringUtils.isNotBlank(dateTo)) {
			searchInfo.setDateTo(dateTo);
		}
		
		if (StringUtils.isNotBlank(dates)) {
			String[] ids = dates.split(";");
			List<String> idList = searchInfo.getDates();
			for (String id : ids) {
				idList.add(id);
			}
		}
		
		// price
		try {
			if (StringUtils.isNotBlank(priceFrom)) {
				searchInfo.setPriceFrom(Double.parseDouble(priceFrom));
			}
		} catch (Exception e) {
			// Ignore and use the default
		}
		
		try {
			if (StringUtils.isNotBlank(priceTo)) {
				searchInfo.setPriceTo(Double.parseDouble(priceTo));
			}
		} catch (Exception e) {
			// Ignore and use the default
		}
		
		// atoz 
		if (StringUtils.isNotBlank(atoz)) {
			searchInfo.setSearchText(atoz);
			searchInfo.setAtozSearch(true);
		}
	
		// saMonitor
		if (StringUtils.isNotBlank(saMonitor)) {
			searchInfo.setSaMonitor(true);
		}
		
		searchService.doSearch(request, modelMap, searchInfo, false);		
		SearchResult sr = (SearchResult) modelMap.get("searchResult");
		
		response.setCharacterEncoding("UTF-8");
		
		String retVal = SearchConstants.SERIALIZERS.get("DEFAULT").deepSerialize(sr); 
		return unicodeEscape(retVal);   
	}
	
	@RequestMapping(method=RequestMethod.GET, value = "/encoded/getSearchResultsByInfo")
	public @ResponseBody String processEncodedLinkGet(ModelMap modelMap, HttpServletRequest request, HttpServletResponse respons,
			@RequestParam(value = "search", required = true) String encodedJSON) {
		
		request.getSession().setAttribute("searchInfo", null);
		request.getSession().removeAttribute("searchInfo");
		
		SearchInfo searchInfo;
		String json = "";
		try {
			byte[] valueDecoded= Base64.decodeBase64(encodedJSON.getBytes());
			json = new String(valueDecoded);
			searchInfo = SearchInfo.fromJson(json);
		} catch (Exception e) {
			m_log.debug("Failed to deserialize searchInfo: " + encodedJSON);
			StringBuffer sbRequest = request.getRequestURL();
			String requestStr = sbRequest.toString();
			m_log.debug("Referrer: " + requestStr);
			searchInfo = new SearchInfo();	
		}
			
		searchService.doSearch(request, modelMap, searchInfo, false);
		SearchResult sr = (SearchResult) modelMap.get("searchResult");
       	String ret = new JSONSerializer().exclude("*.class").deepSerialize(sr);
   		return ret;
	}
	
		
	@RequestMapping(method=RequestMethod.POST, value = "/encoded/getSearchResultsByInfo")
	public @ResponseBody String processEncodedLinkPost(@RequestBody String encodedJSON, 
			ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
		
		request.getSession().setAttribute("searchInfo", null);
		request.getSession().removeAttribute("searchInfo");
		
		SearchInfo searchInfo;
		String json = "";
		try {
			byte[] valueDecoded= Base64.decodeBase64(encodedJSON.getBytes());
			json = new String(valueDecoded);
			searchInfo = SearchInfo.fromJson(json);
		} catch (Exception e) {
			m_log.debug("Failed to deserialize searchInfo: " + encodedJSON);
			StringBuffer sbRequest = request.getRequestURL();
			String requestStr = sbRequest.toString();
			m_log.debug("Referrer: " + requestStr);
			searchInfo = new SearchInfo();	
		}
			
		searchService.doSearch(request, modelMap, searchInfo, false);
		SearchResult sr = (SearchResult) modelMap.get("searchResult");
       	String ret = new JSONSerializer().exclude("*.class").deepSerialize(sr);
   		return ret;
	}
	
	@RequestMapping(method=RequestMethod.GET, value = "/getSearchResultsByInfo")
	public @ResponseBody String processLinkGet(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "search", required = true) String json) {
			
		request.getSession().setAttribute("searchInfo", null);
		request.getSession().removeAttribute("searchInfo");
		
		SearchInfo searchInfo = SearchInfo.fromJson(json);
			
		searchService.doSearch(request, modelMap, searchInfo, false);
		SearchResult sr = (SearchResult) modelMap.get("searchResult");
       	String ret = new JSONSerializer().exclude("*.class").deepSerialize(sr);
   		return ret;
	}	
	
	@RequestMapping(method=RequestMethod.POST, value = "/getSearchResultsByInfo")
	public @ResponseBody String processLinkPost(@RequestBody String json,
			ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
			
		request.getSession().setAttribute("searchInfo", null);
		request.getSession().removeAttribute("searchInfo");
		
		SearchInfo searchInfo = SearchInfo.fromJson(json);
			
		searchService.doSearch(request, modelMap, searchInfo, false);
		SearchResult sr = (SearchResult) modelMap.get("searchResult");
       	String ret = new JSONSerializer().exclude("*.class").deepSerialize(sr);
   		return ret;
	}	
	
	///SMART Suggest - 
	@RequestMapping(value = "/getSmartSuggestions")
   	public @ResponseBody String getSuggestionsAjax(HttpServletRequest request, ModelMap modelMap,
   		@RequestParam(value = "q", required = false) String searchText,
   		@RequestParam(value = "t", required = false) String smartSuggestCatList,
		@RequestParam(value = "cid", required = false) String contextIdList) {

    	SearchInfo searchInfo = new SearchInfo();
    	
    	// search text
		if (StringUtils.isNotBlank(searchText)) {
			searchInfo.setSearchText(searchText);
		}
		
		if (StringUtils.isNotBlank(smartSuggestCatList)) {
			String[] ids = smartSuggestCatList.split(";");
			List<String> idList = searchInfo.getSmartSuggestCatList();
			for (String id : ids) {
				idList.add(id);
			}
			searchInfo.setSmartSuggestCatList(idList);
		}
		
		// metadata
		if (StringUtils.isNotBlank(contextIdList)) {
			String[] ids = contextIdList.split(";");
			List<String> idList = searchInfo.getContextIdList();
			for (String id : ids) {
				idList.add(id);
			}
		}
    	
    	searchInfo.setSmartSuggest(true);
    	
    	searchService.doSmartSuggestSearch(request, modelMap, searchInfo);
    	SearchResult sr = (SearchResult) modelMap.get("searchResult");
       	String ret = new JSONSerializer().exclude("*.class").deepSerialize(sr);
   		return ret;
   	}
	
	
	@RequestMapping(method=RequestMethod.GET, value = "/getSmartSuggestionsByInfo")
	public @ResponseBody String smartSuggestionsLinkGet(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "search", required = true) String json) {
			
		
		SearchInfo searchInfo = SearchInfo.fromJson(json);
		searchInfo.setSmartSuggest(true);
		
		searchService.doSmartSuggestSearch(request, modelMap, searchInfo);
    	SearchResult sr = (SearchResult) modelMap.get("searchResult");
       	String ret = new JSONSerializer().exclude("*.class").deepSerialize(sr);
   		return ret;
	}	
	
	@RequestMapping(method=RequestMethod.POST, value = "/getSmartSuggestionsByInfo")
	public @ResponseBody String smartSuggestionsLinkPost(@RequestBody String json,
			ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
			
		SearchInfo searchInfo = SearchInfo.fromJson(json);
		searchInfo.setSmartSuggest(true);
		
		searchService.doSmartSuggestSearch(request, modelMap, searchInfo);
    	SearchResult sr = (SearchResult) modelMap.get("searchResult");
       	String ret = new JSONSerializer().exclude("*.class").deepSerialize(sr);
   		return ret;
	}	
	
}
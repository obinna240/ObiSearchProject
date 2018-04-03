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
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.sa.search.solrsearch.SortOption;
import com.sa.search.view.controller.AdminController;
import com.sa.search.view.controller.form.SearchForm;

import flexjson.JSONSerializer;

@RequestMapping("/api/cms/ui/search/**")
@Controller
public class UISearchController  {
	private static Log m_log = LogFactory.getLog(UISearchController.class);

	@Autowired private SearchServices searchService;
	
	public static String[] classifications1 = {"", "Mammals", "Birds", "Fish", "Reptiles", "Amphibians"};
	public static String[] classifications2 = {"", "Africa", "Antarctica", "Asia", "Australia", "Europe", "North America", "South America"};
	public static String[] contexts = {"", "Internet", "Intranet", "Extranet"};

	@ModelAttribute("sortOptions")
	public String[] sortOptions() {
		SortOption[] options = SortOption.values();
		String[] sortOptions = new String[options.length];
		int x = 0;
		for (SortOption option : options) {
			sortOptions[x] = option.getOption();
			x++;
		}
		
		return sortOptions;
	}
	
	@ModelAttribute("contexts")
	public String[] getContexts() {
		return contexts;
	}
	
	@ModelAttribute("classifications1")
	public String[] getClassifications1() {
		return classifications1;
	}
	
	@ModelAttribute("classifications2")
	public String[] getClassifications2() {
		return classifications2;
	}
	

	@RequestMapping(value = "/facetUI")
	public String processFacetUI(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "search", required = false) String encodedJSON) {
		
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
		
		request.getSession().setAttribute("searchInfo", searchInfo);
			
		searchService.doSearch(request, modelMap, searchInfo, false);
		SearchResult searchResult = (SearchResult)modelMap.get("searchResult");
			
		// get Atoz facets
		AdminController.configureAtoZ(modelMap, searchResult);

		if (searchResult != null && searchInfo.getLatitude() != null) {
			AdminController.configureMapResults(modelMap, searchResult.getItems(), searchInfo.getLatitude().toString(), searchInfo.getLongitude().toString());
		}
		
		SearchForm searchForm = new SearchForm();
		searchForm.setSearchText(searchInfo.getSearchText());
		searchForm.setSortOption(searchInfo.getSortOption());
		
		//searchForm.setLocation();
		//searchForm.setRadius(radius);
		//searchForm.setDateFrom(dateFrom);
		//searchForm.setDateTo(dateTo);
		
		modelMap.put("searchForm", searchForm);
		
		return "admin/search";
		
	}	
	
	@RequestMapping(value = "/suggestionUI")
	public String processSuggestionUI(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "search", required = false) String encodedJSON) {
		
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
		
		request.getSession().setAttribute("searchInfo", searchInfo);
			
		searchService.doSearch(request, modelMap, searchInfo, false);
		SearchResult searchResult = (SearchResult)modelMap.get("searchResult");
			
		// get Atoz facets
		AdminController.configureAtoZ(modelMap, searchResult);

		if (searchResult != null && searchInfo.getLatitude() != null) {
			AdminController.configureMapResults(modelMap, searchResult.getItems(), searchInfo.getLatitude().toString(), searchInfo.getLongitude().toString());
		}
		
		SearchForm searchForm = new SearchForm();
		searchForm.setSearchText(searchInfo.getSearchText());
		searchForm.setSortOption(searchInfo.getSortOption());
		
		//searchForm.setLocation();
		//searchForm.setRadius(radius);
		//searchForm.setDateFrom(dateFrom);
		//searchForm.setDateTo(dateTo);
		
		modelMap.put("searchForm", searchForm);
		
		return "admin/search";
		
	}	
	
	
	@RequestMapping(value = "/getSearchResultsUI")
	public String getSearchResults(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "q", required = false) String searchText,
			
			@RequestParam(value = "sid", required = false) String sectionIdList,
			@RequestParam(value = "caid", required = false) String classificationAndIdList,
			@RequestParam(value = "coid", required = false) String classificationOrIdList,
			
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
		
		// metadata
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
		SearchResult searchResult = (SearchResult)modelMap.get("searchResult");
		
		// get Atoz facets
		AdminController.configureAtoZ(modelMap, searchResult);

		if (searchResult != null && searchInfo.getLatitude() != null) {
			AdminController.configureMapResults(modelMap, searchResult.getItems(), searchInfo.getLatitude().toString(), searchInfo.getLongitude().toString());
		}
		
		SearchForm searchForm = new SearchForm();
		searchForm.setSearchText(searchInfo.getSearchText());
		searchForm.setSortOption(searchInfo.getSortOption());
		
		//searchForm.setLocation();
		//searchForm.setRadius(radius);
		//searchForm.setDateFrom(dateFrom);
		//searchForm.setDateTo(dateTo);
		
		modelMap.put("searchForm", searchForm);
		
		return "admin/search";
	}
	
	@RequestMapping(value = "/doPaginationUISearch")
	public String processPaginationUI(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(value = "search", required = false) String encodedJSON) {
		
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
		
		request.getSession().setAttribute("searchInfo", searchInfo);
			
		searchService.doSearch(request, modelMap, searchInfo, false);
		SearchResult searchResult = (SearchResult)modelMap.get("searchResult");
			
		// get Atoz facets
		AdminController.configureAtoZ(modelMap, searchResult);

		if (searchResult != null && searchInfo.getLatitude() != null) {
			AdminController.configureMapResults(modelMap, searchResult.getItems(), searchInfo.getLatitude().toString(), searchInfo.getLongitude().toString());
		}
		
		SearchForm searchForm = new SearchForm();
		searchForm.setSearchText(searchInfo.getSearchText());
		searchForm.setSortOption(searchInfo.getSortOption());
		
		//searchForm.setLocation();
		//searchForm.setRadius(radius);
		//searchForm.setDateFrom(dateFrom);
		//searchForm.setDateTo(dateTo);
		
		modelMap.put("searchForm", searchForm);
		
		return "admin/search";
		
	}	
//	public synchronized String doPaginationUISearch(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, 
//			@RequestParam(value = "ps", required = false) Integer pageSize,
//			@RequestParam(value = "p", required = false) Integer pageNo,
//			RedirectAttributes redirectAttributes) {
//		
//		SearchInfo searchInfo = (SearchInfo)request.getSession().getAttribute("searchInfo");
//		
//		if (pageSize != null) {
//			searchInfo.setPageSize(pageSize);
//		}
//		if (pageNo != null) {
//			searchInfo.setPage(pageNo);
//		}
//		
//		searchService.doSearch(request, modelMap, searchInfo, false);
//		SearchResult searchResult = (SearchResult)modelMap.get("searchResult");
//			
//		// get Atoz facets
//		AdminController.configureAtoZ(modelMap, searchResult);
//
//		if (searchResult != null && searchInfo.getLatitude() != null) {
//			AdminController.configureMapResults(modelMap, searchResult.getItems(), searchInfo.getLatitude().toString(), searchInfo.getLongitude().toString());
//		}
//			
//		SearchForm searchForm = new SearchForm();
//		searchForm.setSearchText(searchInfo.getSearchText());
//		searchForm.setSortOption(searchInfo.getSortOption());
//		
//		modelMap.put("searchForm", searchForm);
//		
//		return "admin/search";
//	}
	
	@RequestMapping(method=RequestMethod.GET, value = "/getSmartSuggestionsUI")
   	public @ResponseBody String getSuggestionsUIAjax(HttpServletRequest request, ModelMap modelMap) {
		String q = request.getParameter("q");
    	SearchInfo searchInfo = new SearchInfo();
    	searchInfo.setSearchText(q);
    	searchInfo.setSmartSuggest(true);
        
     	List<SmartSuggestCategory> result = searchService.doSmartSuggestSearch(request, modelMap, searchInfo);
       	String ret = new JSONSerializer().exclude("*.class").deepSerialize(result);
   		return ret;
  
   	}
	
}

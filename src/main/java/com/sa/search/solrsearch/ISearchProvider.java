package com.sa.search.solrsearch;


import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;

import com.sa.search.api.cms.json.CmsDocument;


public interface ISearchProvider {
	
	public SearchResult getSearchResults(HttpServletRequest request, ModelMap modelMap,  SearchInfo searchInfo, boolean smartSuggest);
	public CmsDocument findById(String id);
	//public void reloadCore();
}
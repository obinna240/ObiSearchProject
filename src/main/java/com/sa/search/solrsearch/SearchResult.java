package com.sa.search.solrsearch;

import java.util.ArrayList;
import java.util.List;


public class SearchResult extends BaseSearchResult {

	private List<DisplayItem> items;
	private List<DisplayItem> recommendedLinks;
	private List<ResultFacet> resultFacets = new ArrayList<ResultFacet>();
    
    private List<SpellingSuggestion> spellingSuggestions = new ArrayList<SpellingSuggestion>();
    private ResultLocation resultLocation;

	public List<DisplayItem> getItems() {
		return items;
	}
	
	public void setItems(List<DisplayItem> items) {
		this.items = items;
	}
	
	public List<DisplayItem> getRecommendedLinks() {
		return recommendedLinks;
	}

	public void setRecommendedLinks(List<DisplayItem> recommendedLinks) {
		this.recommendedLinks = recommendedLinks;
	}

	public List<SpellingSuggestion> getSpellingSuggestions() {
		return spellingSuggestions;
	}

	public void setSpellingSuggestions(List<SpellingSuggestion> spellingSuggestions) {
		this.spellingSuggestions = spellingSuggestions;
	}

	public List<ResultFacet> getResultFacets() {
		return resultFacets;
	}
	
	public void setResultFacets(List<ResultFacet> resultFacets) {
		this.resultFacets = resultFacets;
	}
	
	public ResultLocation getResultLocation() {
		return resultLocation;
	}
	
	public void setResultLocation(ResultLocation resultLocation) {
		this.resultLocation = resultLocation;
	}
}

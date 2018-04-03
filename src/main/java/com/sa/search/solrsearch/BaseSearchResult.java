package com.sa.search.solrsearch;

/**
 * Common search result attributes
 */
public class BaseSearchResult implements ISearchResult {

	private String searchText;
	private String searchType;

	private String sortOption="Relevancy";
	private String randomSortID;
	
	private ResultPagination resultPagination;
	   	
    private int totalResults;

    public String getSearchText() {
		return searchText;
	}
    
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}	
	
	public String getSearchType() {
		return searchType;
	}
	
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	
	public void setSortOption(String sortOption) {
		this.sortOption = sortOption;
	}
	
	public String getSortOption() {
		return sortOption;
	}
	
	public void setRandomSortID(String randomSortID) {
		this.randomSortID = randomSortID;
	}
	
	public String getRandomSortID() {
		return randomSortID;
	}

	public ResultPagination getResultPagination() {
		return resultPagination;
	}

	public void setResultPagination(ResultPagination resultPagination) {
		this.resultPagination = resultPagination;
	}
	
	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	public int getTotalResults() {
		return this.totalResults;
	}
	

}

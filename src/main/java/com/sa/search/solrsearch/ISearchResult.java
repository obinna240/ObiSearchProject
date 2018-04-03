package com.sa.search.solrsearch;

/**
 * Common search result item interface 
 */
public interface ISearchResult {
	
	public abstract String getSearchType();
	public abstract void setSearchType(String searchType);
	
	public abstract String getSortOption();
	public abstract void setSortOption(String sortOption);
	
	public abstract String getSearchText();
	public abstract void setSearchText(String searchText);
	
	public abstract String getRandomSortID();
	public abstract void setRandomSortID(String randomSortID);
	
	public abstract ResultPagination getResultPagination();
	public abstract void setResultPagination(ResultPagination resultPagination);
	
	public abstract int getTotalResults();
	public abstract void setTotalResults(int totalResults);
//		
//	public abstract int getPageSize();
//	public abstract void setPageSize(int pageSize);
//
//	public int getCurrentPage();
//	public void setCurrentPage(int currentPage);
//	
//	public int getFirstNavPage();
//	public void setFirstNavPage(int firstNavPage);
//	
//	public int getLastPage();
//	public void setLastPage(int lastPage) ;
//	
//	public int getLastNavPage();
//	public void setLastNavPage(int lastNavPage);
//	
//	public int getFirstResult();
//	public void setFirstResult(int firstResult);
//	
//	public int getLastResult();
//	public void setLastResult(int lastResult);
	
	

}
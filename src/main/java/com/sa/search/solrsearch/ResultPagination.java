package com.sa.search.solrsearch;

import java.util.ArrayList;
import java.util.List;


public class ResultPagination {

    private int pageSize;
    private int currentPage;
    private int lastPage;

    private int firstNavPage;
    private int lastNavPage;
    
    private int firstResult;
    private int lastResult;
    
    private ResultPaginationEntry firstNavPageEntry;
    private ResultPaginationEntry lastNavPageEntry;
    
    private List<ResultPaginationEntry> entries = new ArrayList<ResultPaginationEntry>();
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getLastPage() {
		return lastPage;
	}

	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}

	public int getFirstNavPage() {
		return firstNavPage;
	}

	public void setFirstNavPage(int firstNavPage) {
		this.firstNavPage = firstNavPage;
	}

	public int getLastNavPage() {
		return lastNavPage;
	}

	public void setLastNavPage(int lastNavPage) {
		this.lastNavPage = lastNavPage;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public int getLastResult() {
		return lastResult;
	}

	public void setLastResult(int lastResult) {
		this.lastResult = lastResult;
	}

	public ResultPaginationEntry getFirstNavPageEntry() {
		return firstNavPageEntry;
	}

	public void setFirstNavPageEntry(ResultPaginationEntry firstNavPageEntry) {
		this.firstNavPageEntry = firstNavPageEntry;
	}

	public ResultPaginationEntry getLastNavPageEntry() {
		return lastNavPageEntry;
	}

	public void setLastNavPageEntry(ResultPaginationEntry lastNavPageEntry) {
		this.lastNavPageEntry = lastNavPageEntry;
	}

	public List<ResultPaginationEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<ResultPaginationEntry> entries) {
		this.entries = entries;
	}

	public class ResultPaginationEntry {
		private int page;
		private String encodedUrl;
		private String paramUrl;
		
		public ResultPaginationEntry() {
		}

		public int getPage() {
			return page;
		}

		public void setPage(int page) {
			this.page = page;
		}

		public String getEncodedUrl() {
			return encodedUrl;
		}

		public void setEncodedUrl(String encodedUrl) {
			this.encodedUrl = encodedUrl;
		}

		public String getParamUrl() {
			return paramUrl;
		}

		public void setParamUrl(String paramUrl) {
			this.paramUrl = paramUrl;
		}
		
		
	}
}

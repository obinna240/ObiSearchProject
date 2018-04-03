package com.sa.search.db.mongo.model;
import java.util.Date;

public class SearchLog  implements java.io.Serializable {
	
	private String id;
	private String searchText;
	private String searchTextLower;
	private String searchType;
	private Integer resultCount;
	private Date logDate;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSearchText() {
		return searchText;
	}
	
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	
	public String getSearchTextLower() {
		return searchTextLower;
	}
	
	public void setSearchTextLower(String searchTextLower) {
		this.searchTextLower = searchTextLower;
	}
	
	public String getSearchType() {
		return searchType;
	}
	
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	
	public Integer getResultCount() {
		return resultCount;
	}
	
	public void setResultCount(Integer resultCount) {
		this.resultCount = resultCount;
	}
	
	public Date getLogDate() {
		return logDate;
	}
	
	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}
}



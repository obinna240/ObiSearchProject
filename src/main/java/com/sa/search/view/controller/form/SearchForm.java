package com.sa.search.view.controller.form;

import java.util.ArrayList;
import java.util.List;

public class SearchForm {

		
	private String searchText;
	private String location;
	private Integer radius;

	private String dateFrom;
	private String dateTo;
	
	private List<String> context = new ArrayList<String>();
	
	private List<String> searchableClassifications = new ArrayList<String>();
	private List<String> boostClassifications = new ArrayList<String>();
	
	private String sortOption;
	
	boolean showAllClassifications;

	public String getSearchText() {
		return searchText;
	}
	
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public Integer getRadius() {
		return radius;
	}
	
	public void setRadius(Integer radius) {
		this.radius = radius;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public List<String> getContext() {
		return context;
	}

	public void setContext(List<String> context) {
		this.context = context;
	}

	public List<String> getSearchableClassifications() {
		return searchableClassifications;
	}

	public void setSearchableClassifications(List<String> searchableClassifications) {
		this.searchableClassifications = searchableClassifications;
	}

	public List<String> getBoostClassifications() {
		return boostClassifications;
	}

	public void setBoostClassifications(List<String> boostClassifications) {
		this.boostClassifications = boostClassifications;
	}

	public String getSortOption() {
		return sortOption;
	}

	public void setSortOption(String sortOption) {
		this.sortOption = sortOption;
	}

	public boolean isShowAllClassifications() {
		return showAllClassifications;
	}

	public void setShowAllClassifications(boolean showAllClassifications) {
		this.showAllClassifications = showAllClassifications;
	}
	
	
}


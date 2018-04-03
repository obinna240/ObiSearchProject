package com.sa.search.db.mongo.model;


public class SmartSuggestCategoryConfig  implements IPcgSearchConfig {
	private String id;
	private boolean enabled;
	private String name;
	private String displayText;
	private String filterType;
	private int limit; //number of items to show in smartsuggest dropdown
	private Integer displayOrder;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDisplayText() {
		return displayText;
	}
	
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}
	
	public String getFilterType() {
		return filterType;
	}
	
	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
}

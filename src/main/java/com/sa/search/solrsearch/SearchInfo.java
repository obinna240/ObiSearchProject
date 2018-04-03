package com.sa.search.solrsearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class SearchInfo implements java.io.Serializable {
	
	private String searchText;
	private List<String> documentTypeList = new ArrayList<String>();

	private String pc;
	private String radius;

	private Integer page;
	private Integer pageSize;
	
	private String sortOption;
	private String randomSortId;
	
	private boolean smartSuggest;
	
	private Double latitude;
	private Double longitude;
	
	private String dateFrom;
	private String dateTo;
	private List<String> dates = new ArrayList<String>();;
	
	private List<String> contextIdList = new ArrayList<String>();
	private List<String> sectionIdList = new ArrayList<String>();
	private List<String> classificationAndIdList = new ArrayList<String>();
	private List<String> classificationOrIdList = new ArrayList<String>();
	
	private boolean atozSearch;
	private boolean saMonitor = false;

	private List<String> smartSuggestCatList = new ArrayList<String>();

	// restrict search to the following classifications
	private List<String> searchableClassificationAndIdList = new ArrayList<String>();
	private List<String> searchableClassificationOrIdList = new ArrayList<String>();

	// boost the following classifications
	private List<String> boostClassificationOrIdList = new ArrayList<String>();

	// price
	private Double priceFrom;
	private Double priceTo;
	
	
	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	
	public List<String> getDocumentTypeList() {
		return documentTypeList;
	}

	public void setDocumentTypeList(List<String> documentTypeList) {
		this.documentTypeList = documentTypeList;
	}

	public String getPc() {
		return pc;
	}
	
	public void setPc(String pc) {
		this.pc = pc;
	}
	
	public String getRadius() {
		return radius;
	}
	
	public void setRadius(String radius) {
		this.radius = radius;
	}
	
	public Integer getPage() {
		return page;
	}
	
	public void setPage(Integer page) {
		this.page = page;
	}
	
	public Integer getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	public String getSortOption() {
		return sortOption;
	}
	
	public void setSortOption(String sortOption) {
		this.sortOption = sortOption;
	}
	
	public String getRandomSortId() {
		return randomSortId;
	}
	
	public void setRandomSortId(String randomSortId) {
		this.randomSortId = randomSortId;
	}

	public boolean isSmartSuggest() {
		return smartSuggest;
	}

	public void setSmartSuggest(boolean smartSuggest) {
		this.smartSuggest = smartSuggest;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public List<String> getContextIdList() {
		return contextIdList;
	}

	public void setContextIdList(List<String> contextIdList) {
		this.contextIdList = contextIdList;
	}

	public List<String> getSectionIdList() {
		return sectionIdList;
	}

	public void setSectionIdList(List<String> sectionIdList) {
		this.sectionIdList = sectionIdList;
	}
	
	public List<String> getClassificationAndIdList() {
		return classificationAndIdList;
	}

	public void setClassificationAndIdList(List<String> classificationAndIdList) {
		this.classificationAndIdList = classificationAndIdList;
	}

	public List<String> getClassificationOrIdList() {
		return classificationOrIdList;
	}

	public void setClassificationOrIdList(List<String> classificationOrIdList) {
		this.classificationOrIdList = classificationOrIdList;
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

	public List<String> getDates() {
		return dates;
	}

	public void setDates(List<String> dates) {
		this.dates = dates;
	}

	public boolean isAtozSearch() {
		return atozSearch;
	}

	public void setAtozSearch(boolean atozSearch) {
		this.atozSearch = atozSearch;
	}
	
	public boolean isSaMonitor() {
		return saMonitor;
	}

	public void setSaMonitor(boolean saMonitor) {
		this.saMonitor = saMonitor;
	}

	public List<String> getSmartSuggestCatList() {
		return smartSuggestCatList;
	}

	public void setSmartSuggestCatList(List<String> smartSuggestCatList) {
		this.smartSuggestCatList = smartSuggestCatList;
	}

	public List<String> getSearchableClassificationAndIdList() {
		return searchableClassificationAndIdList;
	}

	public void setSearchableClassificationAndIdList(List<String> searchableClassificationAndIdList) {
		this.searchableClassificationAndIdList = searchableClassificationAndIdList;
	}

	public List<String> getSearchableClassificationOrIdList() {
		return searchableClassificationOrIdList;
	}

	public void setSearchableClassificationOrIdList(List<String> searchableClassificationOrIdList) {
		this.searchableClassificationOrIdList = searchableClassificationOrIdList;
	}

	public List<String> getBoostClassificationOrIdList() {
		return boostClassificationOrIdList;
	}

	public void setBoostClassificationOrIdList(List<String> boostClassificationOrIdList) {
		this.boostClassificationOrIdList = boostClassificationOrIdList;
	}

	public Double getPriceFrom() {
		return priceFrom;
	}

	public void setPriceFrom(Double priceFrom) {
		this.priceFrom = priceFrom;
	}

	public Double getPriceTo() {
		return priceTo;
	}

	public void setPriceTo(Double priceTo) {
		this.priceTo = priceTo;
	}

	public String toJson() {
        return new JSONSerializer().exclude("*.class").deepSerialize(this);
    }

	public static SearchInfo fromJson(String json) {
        return new JSONDeserializer<SearchInfo>().use(null, SearchInfo.class).deserialize(json);
    }

	public static String toJsonArray(Collection<SearchInfo> collection) {
        return new JSONSerializer().exclude("*.class").deepSerialize(collection);
    }

	public static Collection<SearchInfo> fromJsonArray(String json) {
        return new JSONDeserializer<List<SearchInfo>>().use(null, ArrayList.class).use("values", SearchInfo.class).deserialize(json);
    }
}

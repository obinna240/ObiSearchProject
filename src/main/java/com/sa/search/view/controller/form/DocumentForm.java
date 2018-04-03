package com.sa.search.view.controller.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

public class DocumentForm {
	
	private String id;
	
	@NotEmpty
	private String pageTitle;
	
	@NotEmpty
	private String pageContent;
	
	private String pageUrl;
	private String docType;
	private Boolean smartSuggest;
	
	private String mimeType;
	private String siteSection;
	private String imagePath;
		
	private String postcode;
	private Double latitude;
	private Double longitude;

	private String dateFrom;
	private String dateTo;
	private String dateList;

	private Double priceFrom;
	private Double priceTo;
	
	private List<String> context = new ArrayList<String>();
	
	private String classification1;
	private List<String> classification2 = new ArrayList<String>();
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPageTitle() {
		return pageTitle;
	}
	
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	
	public String getPageContent() {
		return pageContent;
	}
	
	public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
	}
	
	public String getPageUrl() {
		return pageUrl;
	}
	
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	
	public String getDocType() {
		return docType;
	}
	
	public void setDocType(String docType) {
		this.docType = docType;
	}
	
	public Boolean getSmartSuggest() {
		return smartSuggest;
	}
	
	public void setSmartSuggest(Boolean smartSuggest) {
		this.smartSuggest = smartSuggest;
	}
	
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getSiteSection() {
		return siteSection;
	}

	public void setSiteSection(String siteSection) {
		this.siteSection = siteSection;
	}

	public String getImagePath() {
		return imagePath;
	}
	
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	public String getPostcode() {
		return postcode;
	}
	
	public void setPostcode(String postcode) {
		this.postcode = postcode;
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
	
	public String getDateList() {
		return dateList;
	}

	public void setDateList(String dateList) {
		this.dateList = dateList;
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
	
	public List<String> getContext() {
		return context;
	}
	
	public void setContext(List<String> context) {
		this.context = context;
	}
	
	public String getClassification1() {
		return classification1;
	}
	
	public void setClassification1(String classification1) {
		this.classification1 = classification1;
	}
	
	public List<String> getClassification2() {
		return classification2;
	}
	
	public void setClassification2(List<String> classification2) {
		this.classification2 = classification2;
	}

}


package com.sa.search.api.cms.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.sa.search.api.cms.json.packets.MetaDataPacket;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class CmsDocument implements Serializable {
	
	/**
	 * 
	 */
	private String id;
	
	//
	private Integer umbNodeId;
	private String pageTitle;
	private String pageContent;
	private String pageUrl;
	private String docType;
	
	private String mimeType;
	private String siteSection;
	private String imagePath;
	
	private Double latitude;
	private Double longitude;
	
	private Date dateFrom;
	private Date dateTo;
	private List<Date> dates;
	
	private Double priceFrom;
	private Double priceTo;

	private List<String> contexts;
	private List<String> andClassifications;
	private List<String> orClassifications;

	private Boolean smartSuggest;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getUmbNodeId() {
		return umbNodeId;
	}

	public void setUmbNodeId(Integer umbNodeId) {
		this.umbNodeId = umbNodeId;
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

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public List<Date> getDates() {
		return dates;
	}

	public void setDates(List<Date> dates) {
		this.dates = dates;
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

	
	public List<String> getAndClassifications() {
		return andClassifications;
	}

	public void setAndClassifications(List<String> andClassifications) {
		this.andClassifications = andClassifications;
	}

	public List<String> getOrClassifications() {
		return orClassifications;
	}

	public void setOrClassifications(List<String> orClassifications) {
		this.orClassifications = orClassifications;
	}

	public List<String> getContexts() {
		return contexts;
	}

	public void setContexts(List<String> contexts) {
		this.contexts = contexts;
	}

	public Boolean getSmartSuggest() {
		return smartSuggest;
	}

	public void setSmartSuggest(Boolean smartSuggest) {
		this.smartSuggest = smartSuggest;
	}

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static CmsDocument fromJsonToCmsDocument(String json) {
        return new JSONDeserializer<CmsDocument>().use(null, CmsDocument.class).deserialize(json);
    }

	public static String toJsonArray(Collection<CmsDocument> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<CmsDocument> fromJsonArrayToDocuments(String json) {
        return new JSONDeserializer<List<CmsDocument>>().use(null, ArrayList.class).use("values", CmsDocument.class).deserialize(json);
    }
	
	
}

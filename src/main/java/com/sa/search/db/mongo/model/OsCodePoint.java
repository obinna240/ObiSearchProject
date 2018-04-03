package com.sa.search.db.mongo.model;

import java.util.Date;

public class OsCodePoint {
	private String id; // postcode short
	private String postcode;
	private Integer positionalQualityIndicator;
	private Integer eastings;
	private Integer northings;
	private String countryCode;
	private String nhsRegionalHaCode;	
	private String nhsHaCode;	
	private String adminCountyCode;	
	private String adminDistrictCode;	
	private String adminWardCode;	
	private Double latitude;
	private Double longtitude;
	private String county;
	private Date dateLastUpdated; // data last updated
	
	private String laId;
	private OsLocation osLocation;
	private String snacCode;
	
	public enum Coverage {
		UK,
		LOCALAUTHORITIES
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Integer getPositionalQualityIndicator() {
		return positionalQualityIndicator;
	}
	
	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public void setPositionalQualityIndicator(Integer positionalQualityIndicator) {
		this.positionalQualityIndicator = positionalQualityIndicator;
	}
	
	public Integer getEastings() {
		return eastings;
	}
	
	public void setEastings(Integer eastings) {
		this.eastings = eastings;
	}
	
	public Integer getNorthings() {
		return northings;
	}
	
	public void setNorthings(Integer northings) {
		this.northings = northings;
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public String getNhsRegionalHaCode() {
		return nhsRegionalHaCode;
	}
	
	public void setNhsRegionalHaCode(String nhsRegionalHaCode) {
		this.nhsRegionalHaCode = nhsRegionalHaCode;
	}
	
	public String getNhsHaCode() {
		return nhsHaCode;
	}
	
	public void setNhsHaCode(String nhsHaCode) {
		this.nhsHaCode = nhsHaCode;
	}
	
	public String getAdminCountyCode() {
		return adminCountyCode;
	}
	
	public void setAdminCountyCode(String adminCountyCode) {
		this.adminCountyCode = adminCountyCode;
	}
	
	public String getAdminDistrictCode() {
		return adminDistrictCode;
	}
	
	public void setAdminDistrictCode(String adminDistrictCode) {
		this.adminDistrictCode = adminDistrictCode;
	}
	
	public String getAdminWardCode() {
		return adminWardCode;
	}
	
	public void setAdminWardCode(String adminWardCode) {
		this.adminWardCode = adminWardCode;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public Double getLongtitude() {
		return longtitude;
	}
	
	public void setLongtitude(Double longtitude) {
		this.longtitude = longtitude;
	}
	
	public String getCounty() {
		return county;
	}
	
	public void setCounty(String county) {
		this.county = county;
	}
	
	public String getLaId() {
		return laId;
	}
	
	public void setLaId(String laId) {
		this.laId = laId;
	}
	
	public Date getDateLastUpdated() {
		return dateLastUpdated;
	}
	
	public void setDateLastUpdated(Date dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}
	
	public OsLocation getOsLocation() {
		return osLocation;
	}

	public void setOsLocation(OsLocation osLocation) {
		this.osLocation = osLocation;
	}

	public String getSnacCode() {
		return snacCode;
	}

	public void setSnacCode(String snacCode) {
		this.snacCode = snacCode;
	}
	
	
}

package com.sa.search.db.mongo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class SALocalAuthority implements java.io.Serializable {

	private Long id;
	
	private String officialName; //officialname	
	private String sortName; //sortname	
	private Long regionId;	 //regionid	
	//countyid	
	private String type;	// type
	private Long tier;		// tier
	//homepage	
	//contactpage	
	//domains	
	//runcount	
	//issitelive	
	//sendalert	
	//isesduser	
	//laaddressid	
	private String snacCode;	//snaccode	
	private Long countryId; 	//countryid
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOfficialName() {
		return officialName;
	}

	public void setOfficialName(String officialName) {
		this.officialName = officialName;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public Long getRegionId() {
		return regionId;
	}

	public void setRegionId(Long regionId) {
		this.regionId = regionId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getTier() {
		return tier;
	}

	public void setTier(Long tier) {
		this.tier = tier;
	}

	public String getSnacCode() {
		return snacCode;
	}

	public void setSnacCode(String snacCode) {
		this.snacCode = snacCode;
	}

	public Long getCountryId() {
		return countryId;
	}

	public void setCountryId(Long countryId) {
		this.countryId = countryId;
	}

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static SALocalAuthority fromJsonToSALocalAuthority(String json) {
        return new JSONDeserializer<SALocalAuthority>().use(null, SALocalAuthority.class).deserialize(json);
    }

	public static String toJsonArray(Collection<SALocalAuthority> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<SALocalAuthority> fromJsonArrayToSALocalAuthoritys(String json) {
        return new JSONDeserializer<List<SALocalAuthority>>().use(null, ArrayList.class).use("values", SALocalAuthority.class).deserialize(json);
    }

}

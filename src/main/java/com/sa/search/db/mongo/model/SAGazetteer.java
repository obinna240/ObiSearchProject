package com.sa.search.db.mongo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class SAGazetteer implements java.io.Serializable {

	private Long id;
	
	private String importId; 	// ID
	private String name;		//NAME1
	//NAME1_LANG,
	//NAME2,
	//NAME2_LANG,
	private String type; 		//TYPE,
	private String localType;	//LOCAL_TYPE,
	private Long easting;		//GEOMETRY_X,
	private Long northing;		//GEOMETRY_Y,
	private Double latitude;
	private Double longtitude;

	//MOST_DETAIL_VIEW_RES,
	//LEAST_DETAIL_VIEW_RES,
	//MBR_XMIN,
	//MBR_YMIN,
	//MBR_XMAX,
	//MBR_YMAX,
	private String postcodeDistrict;	//POSTCODE_DISTRICT,
	//POSTCODE_DISTRICT_URI,
	//POPULATED_PLACE,
	//POPULATED_PLACE_URI,
	//POPULATED_PLACE_TYPE,
	private String districtBorough;	//DISTRICT_BOROUGH,
	//DISTRICT_BOROUGH_URI,
	//DISTRICT_BOROUGH_TYPE,
	private String countyUnitary; 	//COUNTY_UNITARY,
	//COUNTY_UNITARY_URI,
	//ac	COUNTY_UNITARY_TYPE,
	//REGION,REGION_URI,
	private String country;			//COUNTRY,
	//COUNTRY_URI,
	//RELATED_SPATIAL_OBJECT,
	//SAME_AS_DBPEDIA,
	//SAME_AS_GEONAMES
	
	private String postcode;
	private String localGovtCode;
	
	public enum Coverage {
		UK,
		LOCALAUTHORITIES
	}

	public SAGazetteer() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getImportId() {
		return importId;
	}

	public void setImportId(String importId) {
		this.importId = importId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocalType() {
		return localType;
	}

	public void setLocalType(String localType) {
		this.localType = localType;
	}

	public Long getEasting() {
		return easting;
	}

	public void setEasting(Long easting) {
		this.easting = easting;
	}

	public Long getNorthing() {
		return northing;
	}

	public void setNorthing(Long northing) {
		this.northing = northing;
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

	public String getPostcodeDistrict() {
		return postcodeDistrict;
	}

	public void setPostcodeDistrict(String postcodeDistrict) {
		this.postcodeDistrict = postcodeDistrict;
	}

	public String getDistrictBorough() {
		return districtBorough;
	}

	public void setDistrictBorough(String districtBorough) {
		this.districtBorough = districtBorough;
	}

	public String getCountyUnitary() {
		return countyUnitary;
	}

	public void setCountyUnitary(String countyUnitary) {
		this.countyUnitary = countyUnitary;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getLocalGovtCode() {
		return localGovtCode;
	}

	public void setLocalGovtCode(String localGovtCode) {
		this.localGovtCode = localGovtCode;
	}

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static SAGazetteer fromJsonToSAGazetteer(String json) {
        return new JSONDeserializer<SAGazetteer>().use(null, SAGazetteer.class).deserialize(json);
    }

	public static String toJsonArray(Collection<SAGazetteer> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<SAGazetteer> fromJsonArrayToSAGazetteers(String json) {
        return new JSONDeserializer<List<SAGazetteer>>().use(null, ArrayList.class).use("values", SAGazetteer.class).deserialize(json);
    }

}

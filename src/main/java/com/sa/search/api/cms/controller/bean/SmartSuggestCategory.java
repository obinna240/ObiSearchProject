package com.sa.search.api.cms.controller.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class SmartSuggestCategory implements Serializable {

	private static final long serialVersionUID = 1L;
	private SmartSuggestHeader header;
	private ArrayList<SmartSuggestData> data;
	private Integer displayOrder;
	
	public SmartSuggestHeader getHeader() {
		return header;
	}
	public void setHeader(SmartSuggestHeader header) {
		this.header = header;
	}
	public ArrayList<SmartSuggestData> getData() {
		return data;
	}
	public void setData(ArrayList<SmartSuggestData> data) {
		this.data = data;
	}
	
	public static String dataToJsonArray(Collection<SmartSuggestData> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public static Collection<SmartSuggestData> fromJsonArrayToData(String json) {
        return new JSONDeserializer<List<SmartSuggestData>>().use(null, ArrayList.class).use("values", SmartSuggestData.class).deserialize(json);
    }
	
	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
	

}

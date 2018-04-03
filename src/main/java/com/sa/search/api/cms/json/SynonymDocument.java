package com.sa.search.api.cms.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class SynonymDocument implements Serializable {
		
	private String word;
	private List<String> synonymList = new ArrayList<String>();
	//private boolean replaceExisting;
	private boolean oneWay = false;

	public String getWord() {
		return word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public List<String> getSynonymList() {
		return synonymList;
	}
	
	public void setSynonymList(List<String> synonymList) {
		this.synonymList = synonymList;
	}
	
	public boolean isOneWay() {
		return oneWay;
	}

	public void setOneWay(boolean oneWay) {
		this.oneWay = oneWay;
	}

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static SynonymDocument fromJson(String json) {
        return new JSONDeserializer<SynonymDocument>().use(null, SynonymDocument.class).deserialize(json);
    }

	public static String toJsonArray(Collection<SynonymDocument> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<SynonymDocument> fromJsonArrayToDocuments(String json) {
        return new JSONDeserializer<List<SynonymDocument>>().use(null, ArrayList.class).use("values", SynonymDocument.class).deserialize(json);
    }
	
}

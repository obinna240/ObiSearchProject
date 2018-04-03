package com.sa.search.view.controller.form;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.constraints.Pattern;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import flexjson.JSONSerializer;

public class SynonymForm {
	
			
	@Pattern(regexp = "^[a-zA-Z]([\\w -]*[a-zA-Z])?$", message="Please enter a valid word")
	String word;
		
	//@Pattern(regexp = "^([\\p{Alnum}&\\s])+[\\p{Alnum}&\\s]+$", message="Please enter a valid comma separated list")
	@NotEmpty(message="Please enter a valid comma separated list")
	String synonyms;
	
	boolean oneWay = false;
	
	public String getWord() {
		return word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public String getSynonyms() {
		return synonyms;
	}
	
	public void setSynonyms(String synonyms) {
		this.synonyms = synonyms;
	}	
	
	public boolean isOneWay() {
		return oneWay;
	}
	
	public void setOneWay(boolean oneWay) {
		this.oneWay = oneWay;
	}
	
	public String getJson() {
		String json = null;
	
		if (oneWay == false) {
			 List<String> newArr = new ArrayList<String>();
			 newArr.add(this.word);
			 String[] arr = StringUtils.split(synonyms, ',');
			 if (arr != null) {					
				for (String s : arr){
					newArr.add(s.trim());
				}
			}
			json = new JSONSerializer().include("values.*").deepSerialize(newArr);
		} else {
			HashMap<String, List<String>> map = new HashMap<String, List<String>>();
			List<String> newArr = new ArrayList<String>();	
			if (StringUtils.isNotEmpty(synonyms)){			
				String[] arr = StringUtils.split(synonyms, ',');
				if (arr != null) {					
					for (String s:arr){
						newArr.add(s.trim());
					}
					map.put(this.word, newArr);
				}
				json = new JSONSerializer().include("values.*").deepSerialize(map);
			}
		}
		return json;
	}
}


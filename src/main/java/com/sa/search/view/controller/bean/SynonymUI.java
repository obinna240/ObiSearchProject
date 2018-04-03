package com.sa.search.view.controller.bean;


import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SynonymUI {
	String word;
	List<String> synonyms;
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public List<String> getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}
	public String getSynonymsDisplay() {
		return StringUtils.join(synonyms.toArray(), ", ");
	}
}

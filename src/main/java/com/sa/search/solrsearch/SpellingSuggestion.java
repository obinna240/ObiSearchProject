package com.sa.search.solrsearch;

public class SpellingSuggestion {
	String suggestion;
	String encodedUrl;
	private String paramUrl;

	public String getSuggestion() {
		return suggestion;
	}
	
	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public String getEncodedUrl() {
		return encodedUrl;
	}

	public void setEncodedUrl(String encodedUrl) {
		this.encodedUrl = encodedUrl;
	}

	public String getParamUrl() {
		return paramUrl;
	}

	public void setParamUrl(String paramUrl) {
		this.paramUrl = paramUrl;
	}
}

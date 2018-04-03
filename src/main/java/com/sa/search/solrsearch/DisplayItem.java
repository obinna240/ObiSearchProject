package com.sa.search.solrsearch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.sa.search.util.UIUtils;

@Component
public class DisplayItem {

	private String id;
	private String title;
	private String content;
	
	private List<String> contextList = new ArrayList<String>(); 
	private String classifications;
	private String url;
	
	List<String> highlightSnippetsTitle;
	List<String> highlightSnippetsContent;
	List<String> highlightSnippetsClassification;
	private int snippetLength;

	private Double latitude;
	private Double longitude;
	private Double distance;

	private boolean smartSuggest;
	private boolean recommendedLink;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getClassifications() {
		return classifications;
	}

	public void setClassifications(String classifications) {
		this.classifications = classifications;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getHighlightSnippetsTitle() {
		return highlightSnippetsTitle;
	}

	public void setHighlightSnippetsTitle(List<String> highlightSnippetsTitle) {
		this.highlightSnippetsTitle = highlightSnippetsTitle;
	}

	public List<String> getHighlightSnippetsContent() {
		return highlightSnippetsContent;
	}

	public void setHighlightSnippetsContent(List<String> highlightSnippetsContent) {
		this.highlightSnippetsContent = highlightSnippetsContent;
	}

	public List<String> getHighlightSnippetsClassification() {
		return highlightSnippetsClassification;
	}

	public void setHighlightSnippetsClassification(List<String> highlightSnippetsClassification) {
		this.highlightSnippetsClassification = highlightSnippetsClassification;
	}

	public int getSnippetLength() {
		return snippetLength;
	}

	public void setSnippetLength(int snippetLength) {
		this.snippetLength = snippetLength;
	}

	public boolean isSmartSuggest() {
		return smartSuggest;
	}

	public void setSmartSuggest(boolean smartSuggest) {
		this.smartSuggest = smartSuggest;
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

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public boolean isRecommendedLink() {
		return recommendedLink;
	}

	public void setRecommendedLink(boolean recommendedLink) {
		this.recommendedLink = recommendedLink;
	}

	public List<String> getContextList() {
		return contextList;
	}

	public void setContextList(List<String> contextList) {
		this.contextList = contextList;
	}
	
	
}

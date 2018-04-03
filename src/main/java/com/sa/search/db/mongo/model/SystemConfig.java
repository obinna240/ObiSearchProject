package com.sa.search.db.mongo.model;

import java.util.Map;

import org.springframework.data.annotation.Id;

import com.sa.search.db.mongo.model.SmartSuggestCategoryConfig;
import com.sa.search.db.mongo.model.config.PasswordConfig;

public class SystemConfig implements IPcgSearchConfig {
	@Id
	private String id;
	private String contextRoot = "";
	private String googleAnalyticsId = "";
	
	private PasswordConfig passwordConfig;
	private Map<String, SmartSuggestCategoryConfig> smartSuggestCatConfig;
	
	private boolean gazetteerSearchAllowed = true;
	private boolean recommendedLinksEnabled = true;
	private boolean testMode = true;
	private int maximumRecommendedLinks = 3;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContextRoot() {
		return contextRoot;
	}

	public void setContextRoot(String contextRoot) {
		this.contextRoot = contextRoot;
	}

	public String getGoogleAnalyticsId() {
		return googleAnalyticsId;
	}

	public void setGoogleAnalyticsId(String googleAnalyticsId) {
		this.googleAnalyticsId = googleAnalyticsId;
	}

	public Map<String, SmartSuggestCategoryConfig> getSmartSuggestCatConfig() {
		return smartSuggestCatConfig;
	}

	public void setSmartSuggestCatConfig(Map<String, SmartSuggestCategoryConfig> smartSuggestCatConfig) {
		this.smartSuggestCatConfig = smartSuggestCatConfig;
	}

	public PasswordConfig getPasswordConfig() {
		return passwordConfig;
	}

	public void setPasswordConfig(PasswordConfig passwordConfig) {
		this.passwordConfig = passwordConfig;
	}

	public boolean isGazetteerSearchAllowed() {
		return gazetteerSearchAllowed;
	}

	public void setGazetteerSearchAllowed(boolean gazetteerSearchAllowed) {
		this.gazetteerSearchAllowed = gazetteerSearchAllowed;
	}

	public boolean isRecommendedLinksEnabled() {
		return recommendedLinksEnabled;
	}

	public void setRecommendedLinksEnabled(boolean recommendedLinksEnabled) {
		this.recommendedLinksEnabled = recommendedLinksEnabled;
	}

	public boolean isTestMode() {
		return testMode;
	}

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}

	public int getMaximumRecommendedLinks() {
		return maximumRecommendedLinks;
	}

	public void setMaximumRecommendedLinks(int maximumRecommendedLinks) {
		this.maximumRecommendedLinks = maximumRecommendedLinks;
	}
	
}

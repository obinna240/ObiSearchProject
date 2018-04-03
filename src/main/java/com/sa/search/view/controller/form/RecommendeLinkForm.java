package com.sa.search.view.controller.form;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class RecommendeLinkForm {
	private String id;
	
	@NotEmpty	
	@Size(max=100)
	private String keyword;

	private String title;

	
	@NotEmpty
	@Size(min=1, max=100)
	private String description;

	
	@NotEmpty
	private String url;

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getKeyword() {
		return keyword;
	}


	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}	
}

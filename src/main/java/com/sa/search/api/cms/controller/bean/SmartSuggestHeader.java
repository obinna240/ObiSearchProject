package com.sa.search.api.cms.controller.bean;

import java.io.Serializable;

public class SmartSuggestHeader implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private String title; //Category Title
	private Integer num; //Number of results found for this category
	private Integer limit; //Number of results to show in smartsuggest dropdown
	
	public SmartSuggestHeader(String title, Integer num, Integer limit) {
		super();
		this.title = title;
		this.num = num;
		this.limit = limit;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

}

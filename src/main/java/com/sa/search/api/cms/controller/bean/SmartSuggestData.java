package com.sa.search.api.cms.controller.bean;

import java.io.Serializable;

public class SmartSuggestData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String primary; //Title
	private String secondary; //Description (Optional)
	private String image; //URL (Optional)	
	private String onclick; 	//JavaScript to execute when clicked (Optional). 
																	//Ideally this should be a link to the selected page/item 
	
	public SmartSuggestData(String primary) {
		super();
		this.primary = primary;
		this.setOnclick("javascript:setSearchValue('"+this.primary+"');");
	}
	
	public String getPrimary() {
		return primary;
	}
	
	public void setPrimary(String primary) {
		this.primary = primary;
		this.setOnclick("javascript:setSearchValue('"+this.primary+"');");
	}
	
	public String getSecondary() {
		return secondary;
	}
	
	public void setSecondary(String secondary) {
		this.secondary = secondary;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	public String getOnclick() {
		return onclick;
	}
	
	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}
	
	

}

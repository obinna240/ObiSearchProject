package com.sa.search.api.cms.json.packets;

import java.io.Serializable;

public class MetaDataPacket implements Serializable {

	private String conditions;
	private String needs;
	private String products;
	private String services;
	private String categories;
	
	public MetaDataPacket() {
		
	}
	
	public MetaDataPacket(String conditions, String needs, String products,
			String services, String categories) {
		super();
		this.conditions = conditions;
		this.needs = needs;
		this.products = products;
		this.services = services;
		this.categories = categories;
	}
	
	public String getConditions() {
		return conditions;
	}
	
	public void setConditions(String conditions) {
		this.conditions = conditions;
	}
	
	public String getNeeds() {
		return needs;
	}
	
	public void setNeeds(String needs) {
		this.needs = needs;
	}
	
	public String getProducts() {
		return products;
	}
	
	public void setProducts(String products) {
		this.products = products;
	}
	
	public String getServices() {
		return services;
	}
	
	public void setServices(String services) {
		this.services = services;
	}
	
	public String getCategories() {
		return categories;
	}
	
	public void setCategories(String categories) {
		this.categories = categories;
	}
	
	
	
}

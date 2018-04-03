package com.sa.search.view.controller.form;

import org.hibernate.validator.constraints.NotEmpty;


public class SystemConfigForm {
	@NotEmpty
	private String name;
	
	@NotEmpty
	private String type;
	
	private Object value;
	
	@NotEmpty
	private String fullPath;
	
	private String configId;
	
	private boolean editable;
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFullPath() {
		return fullPath;
	}
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
		
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public String getConfigId() {
		return configId;
	}
	public void setConfigId(String configId) {
		this.configId = configId;
	}
}

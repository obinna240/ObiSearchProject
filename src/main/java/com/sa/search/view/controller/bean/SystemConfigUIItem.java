package com.sa.search.view.controller.bean;

public class SystemConfigUIItem {
	private String name;
	private Object value;
	private boolean primitiveOrWrapper;
		
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
	public boolean isPrimitiveOrWrapper() {
		return primitiveOrWrapper;
	}
	public void setPrimitiveOrWrapper(boolean primitiveOrWrapper) {
		this.primitiveOrWrapper = primitiveOrWrapper;
	}
	public SystemConfigUIItem(String name, Object value,
			boolean primitiveOrWrapper) {
		super();
		this.name = name;
		this.value = value;
		this.primitiveOrWrapper = primitiveOrWrapper;
		
	}

}

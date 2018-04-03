package com.sa.search.api.cms.json;


public class AjaxResult {

	private AjaxStatus status;
	private String message;
	private String value;
	
	public AjaxStatus getStatus() {
		return status;
	}
	public void setStatus(AjaxStatus status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public AjaxResult() {}
	
	public AjaxResult(AjaxStatus status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
	public AjaxResult(AjaxStatus status, String message, String value) {
		super();
		this.status = status;
		this.message = message;
		this.value = value;
	}
	
}

package com.sa.search.view.controller.form;

import org.hibernate.validator.constraints.NotEmpty;

public class AdminSuspendUserForm {

	@NotEmpty
	private String username;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
}

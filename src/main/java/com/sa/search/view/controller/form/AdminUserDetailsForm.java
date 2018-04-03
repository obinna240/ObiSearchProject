package com.sa.search.view.controller.form;

import org.hibernate.validator.constraints.NotEmpty;

public class AdminUserDetailsForm {
	
	@NotEmpty(message = "Username is a required field")
	private String username;
	
	@NotEmpty(message = "Password is a required field")
	private String password;
	
	private String passwordcheck;	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordcheck() {
		return passwordcheck;
	}

	public void setPasswordcheck(String passwordcheck) {
		this.passwordcheck = passwordcheck;
	}

}

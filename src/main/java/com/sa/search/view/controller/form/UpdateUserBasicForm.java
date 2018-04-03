package com.sa.search.view.controller.form;

import org.hibernate.validator.constraints.NotBlank;

public class UpdateUserBasicForm {
	
	private String id;
	
	@NotBlank
	private String password;	
	@NotBlank
	private String newpassword;
	@NotBlank
	private String newpasswordcheck;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public String getNewpasswordcheck() {
		return newpasswordcheck;
	}

	public void setNewpasswordcheck(String newpasswordcheck) {
		this.newpasswordcheck = newpasswordcheck;
	}

}

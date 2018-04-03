package com.sa.search.service;

import javax.servlet.http.HttpServletRequest;

import com.sa.search.db.mongo.model.User;
import com.sa.search.user.SaUser;
import com.sa.search.view.controller.form.AdminUserDetailsForm;

public interface IUserService {

	public void changePassword(String username, String password);
	public void addAuthority(String auth);
	public void autoLogin(String username, String password, String authorities, HttpServletRequest request);
	public String generateSalt();
	public boolean passwordCheck(User user, String newpassword);
	public boolean passwordCheck(SaUser saUser, String currentPassword);

	public void suspendUserAccount(String userId);
	public void activateUserAccount(String userId);
	
	public boolean registerAdmin(AdminUserDetailsForm form);
}

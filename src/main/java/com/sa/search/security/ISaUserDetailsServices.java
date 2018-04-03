package com.sa.search.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.sa.search.db.mongo.model.User;
import com.sa.search.user.SaUser;

public interface ISaUserDetailsServices extends UserDetailsService {
	void changePassword(String username, String password);
	void autoLogin(String username, String password, String authorities, HttpServletRequest request);
	void switchUser(String username, String authorities, HttpServletRequest request);
	void addAuthority(String auth);
	boolean passwordCheck(SaUser saUser, String checkPassword);
	boolean passwordCheck(User user, String checkPassword);
}

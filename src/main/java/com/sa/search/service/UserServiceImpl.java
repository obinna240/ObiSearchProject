package com.sa.search.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sa.search.db.mongo.dao.UserDAO;
import com.sa.search.db.mongo.model.User;
import com.sa.search.security.ISaUserDetailsServices;
import com.sa.search.user.SaUser;
import com.sa.search.view.controller.form.AdminUserDetailsForm;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private ISaUserDetailsServices userServiceDao;
	@Autowired private UserDAO userDAO;
	
	private static Log m_log = LogFactory.getLog(UserServiceImpl.class); 

	@Override
	public void changePassword(String username, String password) {
		userServiceDao.changePassword(username, password);
	}

	@Override
	public void autoLogin(String username, String password, String authorities, HttpServletRequest request) {
		userServiceDao.autoLogin(username, password, authorities, request);
	}

	@Override
	public void addAuthority(String auth) {
		userServiceDao.addAuthority(auth);
	}
	
	@Override
	public String generateSalt() {

		Random r = new Random();
		return Long.toString(Math.abs(r.nextLong()), 36);

	}

	@Override
	public boolean passwordCheck(SaUser saUser, String checkPassword) {
		return userServiceDao.passwordCheck(saUser, checkPassword);
	}

	@Override
	public boolean passwordCheck(User user, String checkPassword) {
		return userServiceDao.passwordCheck(user, checkPassword);
	}
	
	@Override
	public void suspendUserAccount(String userId) {
		User user = userDAO.findOne(userId);
		user.setEnabled(false);

		Date now = new GregorianCalendar().getTime();
		user.setLastUpdated(now);

		userDAO.save(user);
	}

	@Override
	public void activateUserAccount(String userId) {
		User user = userDAO.findOne(userId);
		user.setEnabled(true);

		Date now = new GregorianCalendar().getTime();
		user.setLastUpdated(now);

		userDAO.save(user);
	}

	@Override
	public boolean registerAdmin(AdminUserDetailsForm form) {
		try {
			
			String salt = generateSalt();
			String encodedPassword = passwordEncoder.encodePassword(form.getPassword(), salt);
			String authorities = "ROLE_USER,ROLE_ADMIN";
			
			// Create user
			User user = new User();
			user.setId(form.getUsername());
			
	    	user.setPassword(encodedPassword);
	    	user.setSalt(salt);
	    	user.setEnabled(true);
	    	user.setAuthorities(authorities);
	    	Date now = new GregorianCalendar().getTime();
	    	user.setDateRegistered(now);
	    	user.setLastUpdated(now);
	    	
			userDAO.save(user);	
			
			return true;
		} catch (Exception e) {
			m_log.error(e.getMessage());
		}
		
		return false;
	}


}

package com.sa.search.setup;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.sa.search.db.mongo.dao.UserDAO;
import com.sa.search.db.mongo.model.User;
import com.sa.search.service.IUserService;


@RequestMapping("/setup/**")
@Controller
public class SetupController {
	
	private static Log m_log = LogFactory.getLog(SetupController.class);
	  
	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private UserDAO userDAO;
	@Autowired private IUserService userService;

	
	@RequestMapping(value = "/setup/initial", method = RequestMethod.GET)
    public String initialise(Model model, HttpSession session, HttpServletRequest request) {
//		
		
		// set up some users
		// 1. super user
		boolean check = userDAO.checkUserName("saadministrator");
		if (check == false) {
			registerAdminUser("&&sasupport&&", "ROLE_SA_USER", "saadministrator");
		}
		
		return "setup/complete";
		
	}
	
	private void registerAdminUser(String password, 
			String role, 
			String userName) {
		
		String salt = userService.generateSalt();
		String encodedPassword = passwordEncoder.encodePassword(password, salt);
		
		String authorities = "ROLE_USER,ROLE_ADMIN,"+role;
		
		// Create user
		User user = new User();
		user.setId(userName);
		
    	user.setPassword(encodedPassword);
    	user.setSalt(salt);
    	user.setEnabled(true);
    	user.setAuthorities(authorities);
    	Date now = new GregorianCalendar().getTime();
    	user.setDateRegistered(now);
    	user.setLastUpdated(now);
    	
		userDAO.save(user);	
	}
}

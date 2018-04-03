package com.sa.search.security;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.sa.search.config.SASpringContext;
import com.sa.search.db.mongo.dao.TemplateDAO;
import com.sa.search.db.mongo.dao.UserDAO;
import com.sa.search.db.mongo.model.User;
import com.sa.search.user.SaUser;

public class MongoUserDetailService implements ISaUserDetailsServices {

	@Autowired private PasswordEncoder passwordEncoder;
	@Autowired private SaltSource saltSource;
	@Autowired private UserDAO userDAO;
	
	private AuthenticationManager authenticationManager;
	private static Log m_log = LogFactory.getLog(MongoUserDetailService.class);
    
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException {
		return loadSAUserByUserName(userName);
    }

	@Override
	public void changePassword(String userName, String password) {
		
		UserDetails userDetails = loadUserByUsername(userName);
		String encodedPassword = passwordEncoder.encodePassword(password, saltSource.getSalt(userDetails));
		
		com.sa.search.db.mongo.model.User user = userDAO.findOne(userName);
		user.setPassword(encodedPassword);

		Date now = new GregorianCalendar().getTime();
		user.setLastUpdated(now);

		userDAO.save(user);
	}
	
	/**
	 * Automatic login after successful registration.
	 */
	public void autoLogin(String userName, String password, String authorities, HttpServletRequest request) {
		try {
			
			this.authenticationManager = SASpringContext.getAuthenticationManager();
			
			List<GrantedAuthority> grantedAuthority = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userName, "password", grantedAuthority);
			authRequest.setDetails(new WebAuthenticationDetails(request));
			updateContext(authRequest);
		    
		} catch (Exception e) {
		    SecurityContextHolder.getContext().setAuthentication(null);
		    m_log.error("Failure in autoLogin", e);
		}
	}

	public void switchUser(String userName, String authorities, HttpServletRequest request) {
		try {
			List<GrantedAuthority> grantedAuthority = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
			
			UserDetails userDetails = loadSAUserByUserName(userName);
			
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(createUserDetails(userName, userDetails, grantedAuthority), null, grantedAuthority);

			token.setDetails(new WebAuthenticationDetails(request));
			SecurityContextHolder.getContext().setAuthentication(token);
		} catch (Exception e) {
		    SecurityContextHolder.getContext().setAuthentication(null);
		    m_log.error("Failure in switchUser", e);
		}
	}

	protected UserDetails createUserDetails(String userName, UserDetails userDetails, List<GrantedAuthority> combinedAuthorities) {
		String returnUserName = userDetails.getUsername();
		//SaUser saUser = new SaUser(returnUserName, userDetails.getPassword(), userDetails.isEnabled(), true, true, true, userDetails.getAuthorities(), ((SaUser) userDetails).getSalt(),((SaUser) userDetails).getId());
		SaUser saUser = new SaUser(returnUserName, userDetails.getPassword(), userDetails.isEnabled(), true, true, true, combinedAuthorities, ((SaUser) userDetails).getSalt());
		return saUser;
	}
	

	private UserDetails loadSAUserByUserName(String userName) {

		com.sa.search.db.mongo.model.User user = userDAO.findOne(userName);
		
		if (user == null){
			throw new UsernameNotFoundException("User " + userName + " not found");
		}
	 
		List<GrantedAuthority> grantedAuths = AuthorityUtils.commaSeparatedStringToAuthorityList(user.getAuthorities());
	    SaUser saUser = new SaUser(user.getId(), user.getPassword(), user.isEnabled(), true, true, true, grantedAuths, user.getSalt());
	    
	    return saUser;
	
	}	
	
	private void updateContext(UsernamePasswordAuthenticationToken authRequest) {
		try {
			Authentication auth = authenticationManager.authenticate(authRequest);
			SecurityContextHolder.getContext().setAuthentication(auth);
		} catch (Exception e) {
			SecurityContextHolder.getContext().setAuthentication(null);
			m_log.error(e.getMessage(), e);
		}
	}

	@Override
	public void addAuthority(String auth) {

		this.authenticationManager = SASpringContext.getAuthenticationManager();
		List<GrantedAuthority> list = AuthorityUtils.commaSeparatedStringToAuthorityList(auth);

		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(
				SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
				SecurityContextHolder.getContext().getAuthentication().getCredentials(),
				list
			)
		);
	}
	
	public boolean passwordCheck(SaUser saUser, String checkPassword) {
		String encodedPassword;
		if (checkPassword.length()==32)
			encodedPassword = checkPassword;
		else
			encodedPassword = passwordEncoder.encodePassword(checkPassword, saUser.getSalt());
		if (encodedPassword.equals(saUser.getPassword())) {
			return true;
		}
		
		return false;
	}  
	
	public boolean passwordCheck(User user, String checkPassword) {
		String encodedPassword;
		if (checkPassword.length()==32)
			encodedPassword = checkPassword;
		else
			encodedPassword = passwordEncoder.encodePassword(checkPassword, user.getSalt());
		if (encodedPassword.equals(user.getPassword())) {
			return true;
		}
		
		return false;
	}  
}
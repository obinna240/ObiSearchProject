package com.sa.search.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.sa.search.db.mongo.dao.UserDAO;
import com.sa.search.db.mongo.model.User;


public class SaAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler{ 

	@Autowired private UserDAO userDAO;

	@Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

    	setDefaultFailureUrl("/content/login?login_error=t");
    	
    	super.onAuthenticationFailure(request, response, exception);
    }
}

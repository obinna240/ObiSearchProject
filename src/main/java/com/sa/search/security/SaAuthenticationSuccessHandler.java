package com.sa.search.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

public class SaAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler { 
	private static Log m_log = LogFactory.getLog(SaAuthenticationSuccessHandler.class);

   // protected final Log logger = LogFactory.getLog(this.getClass()); 
    private RequestCache requestCache = new HttpSessionRequestCache(); 

    @Override 
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws ServletException, IOException { 
       
    	//final SavedRequest savedRequest = requestCache.getRequest(request, response);
    	
    	// reset search Info 
		// there is an issue where the searchInfo does not seem to be cleared so do belts and braces approach
		request.getSession().setAttribute("searchInfo", null);
		request.getSession().removeAttribute("searchInfo");
    			    			
        String targetUrl = "/account/home"; 	
         
    	requestCache.removeRequest(request, response);
    	super.setDefaultTargetUrl(targetUrl);        
        super.onAuthenticationSuccess(request, response, authentication);
    
    }
    

}


package com.sa.search.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

public class SaLogoutSuccessHandler implements LogoutSuccessHandler { 
	
	private static Log m_log = LogFactory.getLog(SaLogoutSuccessHandler.class);

	
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) { 
						
    	//Redirect the user back to the 'you've logged off' page
    	try {
    		// reset search Info 
    		// there is an issue where the searchInfo does not seem to be cleared so do belts and braces approach
    		request.getSession().setAttribute("searchInfo", null);
    		request.getSession().removeAttribute("searchInfo");
    		
    		
    		String contextPath = request.getContextPath();
   		response.sendRedirect(contextPath + "/content/logoff");
		} catch (IOException e) {
			m_log.error("Failed to redirect user after logout", e);
		}
    }

}

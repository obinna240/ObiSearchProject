package com.sa.search.security;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.sa.search.db.mongo.dao.SystemConfigDAO;
import com.sa.search.db.mongo.model.SystemConfig;
import com.sa.search.util.UIUtils;

/**
 * Basic Security filter
 * Filters out common SQL and XSS attacks 
 * Validates form ID values
 */
public class SASecurityFilter extends OncePerRequestFilter {
	
	private List<String> allowedPOSTs;
	private List<String> whiteList;
	private List<String> allowedAPICalls;
	private final Random randomSource = new Random();
	private int maxParamLength = 100;
	
	private static String API_REQUEST = "/api/";
	
	@Autowired private SystemConfigDAO systemConfigDAO;
	private static Log m_log = LogFactory.getLog(SASecurityFilter.class);
	
	@Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		
		String ipAddress = request.getRemoteAddr();
		String requestUri = request.getRequestURI();
		
		//
		// Are we an api call
		//
		if (isApiCallAllowed(ipAddress, requestUri) == false ) {
			String errID = UIUtils.generateErrorID();
			String url = requestUri + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
			
			m_log.error("API call rejected [errorID = " + errID + "]: URL = " + url + ", IP Address = " + ipAddress);
			response.sendRedirect("/pcgsearch/error/badRequest?errID=" + errID + "&dest=" + requestUri);
			chain.doFilter(request, response);
			return;
		}
		
		//
		//Form submission validation
		//
		
		HttpSession session = request.getSession();
		
		//First time creation of a new UUID (nonce) available for use in any forms which need it 
		if ((String)session.getAttribute("FORM_UUID") == null){
			session.setAttribute("FORM_UUID", generateNonce());
		}
		
		//Check that any form POSTs contain the correct nonce UUID from the user's session
		//Assumes that all forms are POST only, and that all POSTS are form submits
		//These is an exclusion for the /ajax/ path - we may have to config this if we end up 
		//with more than a few exclusions 
		
		if (request.getMethod().equalsIgnoreCase("POST") && !isAllowedPOST(requestUri)){
		
			CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
			HttpServletRequest processedRequest = request;
			if (commonsMultipartResolver.isMultipart(processedRequest)) {
				// do this in the controller's post method
		}
		else {
				String submittedFormID = request.getParameter("FORM_UUID");
				
				//--- Exception testing. Uncomment this ONLY if you want ALL form submits to fail.
				//submittedFormID = submittedFormID + "XXX";
				//---
			
				String expectedFormID = (String)session.getAttribute("FORM_UUID");
				session.setAttribute("FORM_UUID", generateNonce());//new ID for next time - each ID is used/checked once only
				
				//--- ignore as only for testing index etc
//				if (submittedFormID == null || !submittedFormID.equals(expectedFormID)){
//					//
//					//Because we're in a filter we can't throw an exception here as we normally would.
//					//Instead we do the logging first then redirect to an error page.
//					//
//					String errID = UIUtils.generateErrorID();
//					String url = requestUri + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
//					
//					
//					m_log.error("FORM POST REJECTED [errorID = " + errID + "]: URL = " + url + ", IP Address = " + ipAddress);
//					response.sendRedirect("/pcgsearch/error/badRequest?errID=" + errID + "&dest=" + requestUri);
//					chain.doFilter(request, response);
//					return;
//				}
			}
		}
		
		//
		//Parameter cleanup
		//
		
		Enumeration parameterNames = request.getParameterNames();

		while (parameterNames.hasMoreElements()) {

			String parameterName = (String)parameterNames.nextElement();

			// You cannot update the parameter value in the parameter map
			// directly, so you have to use this roundabout 
			//request.getParameterValues() method.

			String[] parameterValues = request.getParameterValues(parameterName);

			for (int i = 0; i < parameterValues.length; i++) {
				String originalParamValue = parameterValues[i]; 
				
				//Skip all filters if incoming post includes tinyMCE
				if (!isWhiteListed(requestUri)) {			
					parameterValues[i] = StringUtils.left(parameterValues[i], getMaxParamLength());
					if (!parameterName.equalsIgnoreCase("homePhone") && 
							!parameterName.equalsIgnoreCase("workPhone") &&
							!parameterName.equalsIgnoreCase("mobilePhone") &&
							 !parameterName.equalsIgnoreCase("contactNumber")) {
					
						parameterValues[i] = cleanXSS(request, parameterName, parameterValues[i]);	
					}
				}
			
				if (!StringUtils.equals(originalParamValue, parameterValues[i])) {
					m_log.info("PARAMETER FILTERED: Path=" + requestUri + ", Parameter=" + parameterName);
					m_log.info("Original Value=" + originalParamValue + " , Filtered Value=" + parameterValues[i]);
				}
			}
		}

		
        chain.doFilter(request, response);		
	}
	
	/**
	 * Check submitted URL against a list of allowed API calls
	 */
	private boolean isApiCallAllowed(String ipAddress, String requestURI) {

		SystemConfig config = systemConfigDAO.getDefaultSystemConfig();
		if (config.isTestMode()) {
			return true;
		}
		
		
		// Are we from localhost
		// Check  IPv4 and IPv6 formats 
		if(ipAddress.equalsIgnoreCase("127.0.0.1") || ipAddress.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
			return true;
		}
		
		if (allowedAPICalls == null){
			return true;
		}
		
		if (StringUtils.contains(requestURI.toLowerCase(), API_REQUEST)){
		
			for (String  urlPart : allowedAPICalls) {
				
				if (StringUtils.contains(requestURI, urlPart)){
					m_log.debug("Allowing api call uri : " + requestURI);
					return true;
				}
			}

			return false;
		}
		
		return true;
	}
	
	
	/**
	 * Check submitted URL against a list which are allowed through without checking trheir UUID 
	 */
	private boolean isAllowedPOST(String requestURI) {

		if (allowedPOSTs == null){
			return false;
		}
		
		for (String  urlPart : allowedPOSTs) {
			
			if (StringUtils.contains(requestURI, urlPart)){
				m_log.debug("Allowing POST from exception list : " + requestURI);
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Check submitted URL against a white list which are allowed through without filtering html.
	 */
	private boolean isWhiteListed(String requestURI) {

		if (whiteList == null){
			return false;
		}
		
		for (String  urlPart : allowedAPICalls) {
			
			if (StringUtils.contains(requestURI, urlPart)){
				m_log.debug("Skipping filters, whitelist uri : " + requestURI);
				return true;
			}
		}

		return false;
	}
	
	/**
	 * XSS Cleanup. Removes/Escapes common HTML and escaped HTML sequences 
	 * Also removes 'eval', 'script' and 'javascript' from parameters so it 
	 * may cause unexpected side effects
	 */
	private String cleanXSS(HttpServletRequest request, String paramName, String paramVal) {

		if (!StringUtils.isEmpty(paramVal)){
			//URL decode first in case they have encoded the attack string
			//e.g.  %22%3e%3csCrIpT%3ealert(14475)%3c%2fsCrIpT%3e
			// ==   "><sCrIpT>alert(14475)</sCrIpT>

			try {
				paramVal = URLDecoder.decode(paramVal, "UTF-8");
			} catch (Exception e) {
				m_log.info(e);
			}
			
			paramVal = paramVal.replaceAll("<", "&lt;").replaceAll(">", "&gt;");  
			paramVal = paramVal.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");  
			//paramVal = paramVal.replaceAll("'", "&#39;");  
			paramVal = paramVal.replaceAll("\"", "&#34;");
			paramVal = paramVal.replaceAll("eval\\((.*)\\)", "");  
			paramVal = paramVal.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");  
			paramVal = paramVal.replaceAll("((?i)script)", ""); 
		}

		return paramVal;
	}
	
    /**
     * Generate a once time token (nonce) for authenticating subsequent
     * requests. This will also add the token to the session. The nonce
     * generation is a simplified version of ManagerBase.generateSessionId().
     * TW I nicked this code from the Tomcat source as I didn't have time 
     * to try and implement Tomcat's built in support. It may be worth returning 
     * to Tomcat if this DIY approach causes issues
     */
    protected String generateNonce() {
        byte random[] = new byte[16];

        // Render the result as a String of hexadecimal digits
        StringBuilder buffer = new StringBuilder();

        randomSource.nextBytes(random);
       
        for (int j = 0; j < random.length; j++) {
            byte b1 = (byte) ((random[j] & 0xf0) >> 4);
            byte b2 = (byte) (random[j] & 0x0f);
            if (b1 < 10)
                buffer.append((char) ('0' + b1));
            else
                buffer.append((char) ('A' + (b1 - 10)));
            if (b2 < 10)
                buffer.append((char) ('0' + b2));
            else
                buffer.append((char) ('A' + (b2 - 10)));
        }

        return buffer.toString();
    }

	public void setMaxParamLength(int maxParamLength) {
		this.maxParamLength = maxParamLength;
	}

	public int getMaxParamLength() {
		return maxParamLength;
	}

	public void setAllowedPOSTs(List<String> allowedPOSTs) {
		this.allowedPOSTs = allowedPOSTs;
	}

	public List<String> getAllowedPOSTs() {
		return allowedPOSTs;
	}

	public List<String> getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(List<String> whiteList) {
		this.whiteList = whiteList;
	}

	public List<String> getAllowedAPICalls() {
		return allowedAPICalls;
	}

	public void setAllowedAPICalls(List<String> allowedAPICalls) {
		this.allowedAPICalls = allowedAPICalls;
	}
	
}

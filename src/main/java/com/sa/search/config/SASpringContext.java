package com.sa.search.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;



/**
 * Listener class used to make the Spring webapp context available in classes where 
 * we cannot inject the required dependencies or access the request scope
 */
public class SASpringContext implements ServletContextListener {

	public static WebApplicationContext webAppContext = null;	
	private static String appContext;	
	private static AuthenticationManager authenticationManager;

	

	public static String CONFIG_ROOT_PATH = "C:/config"; 
	
	/* Application Startup Event */
	public void	contextInitialized(ServletContextEvent ce) {
	
		//Store the applicationContext so that we can look up the correct configuration file
		setAppContext(StringUtils.substringAfter(ce.getServletContext().getContextPath(), "/"));
		
		ServletContext servletContext = ce.getServletContext();

		webAppContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		authenticationManager = (ProviderManager) SASpringContext.getBeanFactory().getBean("authenticationManager");
		
		//Initialise in-memory caches
		CacheHandler.initCaches();	
	}

	/* Application Shutdown	Event */
	public void	contextDestroyed(ServletContextEvent ce) {}

	public static BeanFactory getBeanFactory(){
		return webAppContext;
	}

	

	public static String getAppContext() {
		return appContext;
	}

	public static void setAppContext(String appContext) {
		SASpringContext.appContext = appContext;
	}
	
	public static AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}
	
}

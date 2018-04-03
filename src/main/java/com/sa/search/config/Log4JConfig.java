package com.sa.search.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

/**
 * Initialize log4J from an external file  
 */
public class Log4JConfig implements ServletContextListener {
	
	private Timer updateTimer = null;
		
	public void	contextInitialized(ServletContextEvent ce) {
		
		//Initialize Log4J
		//Try to load config.properties from the standard Windows path.
		//If not available then use some default settings
		
		Properties props = loadProps(new File(SASpringContext.CONFIG_ROOT_PATH + "/pcgsearch_config.properties"));
		
		if (props == null){
			props = new Properties();
			props.put("log4j.rootlogger", "error, stdout");
			props.put("log4j.logger.com.sa", "info, stdout");
			props.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
			props.put("log4j.appender.stdout.layout","org.apache.log4j.PatternLayout");
			props.put("log4j.appender.stdout.layout.ConversionPattern","%d [%t] - %p - %C{1}.%M(%L) | %m%n");
		}
		
		PropertyConfigurator.configure(props);
	}

	private Properties loadProps(File propsFile){
		Properties props = new Properties();
		try {
			FileInputStream fis = new FileInputStream(propsFile);
			props.load(fis);
			fis.close();
			System.err.println("Logging config loaded from " + propsFile.getAbsolutePath());
			return props;
		} catch (IOException e) {
			System.err.println("Cannot load logging config from " + propsFile.getAbsolutePath());
			return null;
		}
	}    
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}

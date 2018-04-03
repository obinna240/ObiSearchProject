package com.sa.search.view.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;



@RequestMapping("/content/**")
@Controller
public class ContentController extends BaseController {
	private static Log m_log = LogFactory.getLog(ContentController.class);
	
	@RequestMapping("/content/login")
	public String login(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) { 
		return "content/login"; 
	}

	@RequestMapping("/content/logoff")
	public String logoff(ModelMap modelMap, HttpServletRequest request) { 
		return "content/logoff"; 
	}
}
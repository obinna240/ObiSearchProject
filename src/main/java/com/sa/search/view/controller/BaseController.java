package com.sa.search.view.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.sa.search.db.mongo.model.User;
import com.sa.search.solrsearch.SortOption;
import com.sa.search.user.SaUser;
import com.sa.search.view.controller.bean.DisplayBean;
import com.sa.search.db.mongo.dao.UserDAO;
import com.sa.search.db.mongo.dao.SystemConfigDAO;
import com.sa.search.db.mongo.model.SystemConfig;

public class BaseController {
	private static Log m_log = LogFactory.getLog(BaseController.class);

	@Autowired private SystemConfigDAO systemConfigDAO;
	@Autowired private UserDAO userDAO;

	
	public static String[] contexts = {"", "Internet", "Intranet", "Extranet"};

	@ModelAttribute("sortOptions")
	public String[] sortOptions() {
		SortOption[] options = SortOption.values();
		String[] sortOptions = new String[options.length];
		int x = 0;
		for (SortOption option : options) {
			sortOptions[x] = option.getOption();
			x++;
		}
		
		return sortOptions;
	}
	
	@ModelAttribute("contexts")
	public String[] getContexts() {
		return contexts;
	}
	
	protected Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
	
	protected SaUser getSaUser() {
		if (getAuthentication() != null && getAuthentication().getPrincipal() instanceof SaUser) {
			return (SaUser) getAuthentication().getPrincipal();
		}
		return null;
	}
	
	public User getUser(){
		SaUser saUser = getSaUser();
		
		if (saUser != null){
			return userDAO.findOne(saUser.getId());
		}
		
		return null;
	}
	
	
	@ModelAttribute("config")
	public SystemConfig getConfig() {
		return systemConfigDAO.getDefaultSystemConfig();
	}
	
		
}

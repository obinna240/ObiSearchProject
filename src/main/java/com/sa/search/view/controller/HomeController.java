package com.sa.search.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class HomeController extends BaseController {
	
	@RequestMapping(method=RequestMethod.GET)
	public String index(ModelMap modelMap) {
		
		return "index";
	}

}
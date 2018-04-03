package com.sa.search.view.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sa.search.db.mongo.model.User;
import com.sa.search.service.IUserService;
import com.sa.search.util.FlashMap;
import com.sa.search.view.controller.form.UpdateUserBasicForm;
import com.sa.search.view.controller.validator.PasswordValidator;
import com.sa.search.view.controller.validator.UserUpdateBasicValidator;


@RequestMapping("/account/**")
@Controller
public class AccountController extends BaseController {
	
	@Autowired private PasswordValidator passwordValidator;
	@Autowired private UserUpdateBasicValidator userUpdateBasicValidator;
	@Autowired private IUserService userService;

	@RequestMapping (value = "/account/home")
	public String home(ModelMap modelMap) {
		
		return "account/home";
	}
	
	 /**
     * Basic details change, setup form backing object and display input form
     */
    @RequestMapping(value="/account/update")
    public String editBasic(HttpServletRequest request, ModelMap modelMap, UpdateUserBasicForm form) {
    	
    	User user = getUser();
    	form.setId(user.getId());
    	
    	modelMap.put("passwordHelpText", passwordValidator.getPasswordHelpText());
    		
		return "account/update";
    }
    
    /**
     * Basic details form submission handler
     * @param modelMap
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(method=RequestMethod.POST, value="/account/update")
    public String editBasicPost(HttpServletRequest request, ModelMap modelMap, @Valid UpdateUserBasicForm form, BindingResult result, RedirectAttributes redirectAttributes) {
    	
    	userUpdateBasicValidator.validate(form, result);
    	
    	if (result.hasErrors()) {
    		
        	User user = getUser();
        	modelMap.put("passwordHelpText", passwordValidator.getPasswordHelpText());
        	
        	return "account/update";
    	}
    	
    	if (form.getNewpassword() != null && !form.getNewpassword().isEmpty()) {
	    	userService.changePassword(getSaUser().getUsername(), form.getNewpassword());
    	}
    	
    	FlashMap.setSuccessMessage("details_updated", redirectAttributes);

    	return "redirect:/account/home";
    }

}
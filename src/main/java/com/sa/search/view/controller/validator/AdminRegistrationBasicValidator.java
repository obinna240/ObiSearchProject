package com.sa.search.view.controller.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sa.search.db.mongo.dao.UserDAO;
import com.sa.search.view.controller.form.AdminUserDetailsForm;

@Component
public class AdminRegistrationBasicValidator implements Validator {
	
	@Autowired private UserDAO usersDAO;
	@Autowired private PasswordValidator passwordValidator;

	@Override
	public boolean supports(Class<?> clazz) {
		return AdminUserDetailsForm.class.isAssignableFrom(clazz);

	}

	@Override
	public void validate(Object target, Errors errors) {
		
		AdminUserDetailsForm adminUserDetailsForm = (AdminUserDetailsForm) target;
	
		//Username check
		if (usersDAO.checkUserName(adminUserDetailsForm.getUsername()) == true) {
			errors.rejectValue("username", "username.is.taken","Username already in use");
		}
		
		//Password check
		String password = adminUserDetailsForm.getPassword();
		String passwordCheck = adminUserDetailsForm.getPasswordcheck();
		
		if (StringUtils.isNotEmpty(password) && (!password.equals(passwordCheck))) {
			errors.rejectValue("passwordcheck", "passwords.must.match","Passwords must match");
		}
		
		passwordValidator.validate(adminUserDetailsForm.getPassword(), "password", errors);
	}

}

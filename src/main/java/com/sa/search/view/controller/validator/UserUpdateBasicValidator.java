package com.sa.search.view.controller.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sa.search.db.mongo.dao.UserDAO;
import com.sa.search.db.mongo.model.User;
import com.sa.search.service.IUserService;
import com.sa.search.user.SaUser;
import com.sa.search.view.controller.form.UpdateUserBasicForm;

@Component
public class UserUpdateBasicValidator implements Validator {
	
	@Autowired private UserDAO userDAO;
	@Autowired private PasswordValidator passwordValidator;
	@Autowired private IUserService userService;

	@Override
	public boolean supports(Class<?> clazz) {
		return UpdateUserBasicForm.class.isAssignableFrom(clazz);

	}

	@Override
	public void validate(Object target, Errors errors) {
		
		UpdateUserBasicForm updateUserBasicForm = (UpdateUserBasicForm) target;
	
		
		passwordValidator.validate(updateUserBasicForm.getNewpassword(), "newpassword", errors);
		//Password check		
		String pwd = updateUserBasicForm.getNewpassword();
		String pwdCheck = updateUserBasicForm.getNewpasswordcheck();
		
		if (!StringUtils.isBlank(pwd) && !pwd.equals(pwdCheck)) {
			errors.rejectValue("newpasswordcheck", "passwords.must.match", "Passwords must match");
		}
		
		//Check orig password?
		User user = userDAO.findOne(updateUserBasicForm.getId());
		
		if (StringUtils.isNotBlank(updateUserBasicForm.getPassword()) && !userService.passwordCheck(user, updateUserBasicForm.getPassword())) {
			errors.rejectValue("password", "password.incorrect", "Please enter your current password");
		}
	}
}

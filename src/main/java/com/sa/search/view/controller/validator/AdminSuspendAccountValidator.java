package com.sa.search.view.controller.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sa.search.db.mongo.dao.UserDAO;
import com.sa.search.db.mongo.model.User;
import com.sa.search.view.controller.form.AdminSuspendUserForm;

@Component
public class AdminSuspendAccountValidator implements Validator {

	@Autowired private UserDAO userDao;
	
	@Override
	public boolean supports(Class<?> arg0) {
		return AdminSuspendUserForm.class.isAssignableFrom(arg0);
	}

	@Override
	public void validate(Object target, Errors errors) {

		AdminSuspendUserForm adminUserDetailsForm = (AdminSuspendUserForm) target;
		if (StringUtils.isNotBlank(adminUserDetailsForm.getUsername())) {
			User users = userDao.findOne(adminUserDetailsForm.getUsername());
			
			if (users == null) {
				errors.rejectValue("username", "username.doesnt.exist" ,"This user does not exist.");
			}
		}
		
	}

}

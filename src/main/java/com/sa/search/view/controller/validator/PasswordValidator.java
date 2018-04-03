package com.sa.search.view.controller.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.sa.search.db.mongo.dao.SystemConfigDAO;
import com.sa.search.db.mongo.model.SystemConfig;
import com.sa.search.db.mongo.model.config.PasswordConfig;



public class PasswordValidator {
	@Autowired private SystemConfigDAO systemConfigDAO;
	
	public void validate(String input, String fieldName, Errors errors) {
		SystemConfig config = systemConfigDAO.getDefaultSystemConfig();
		PasswordConfig pConfig = config.getPasswordConfig();	
		
				
		if (!StringUtils.isBlank(input) && StringUtils.length(input) > pConfig.getPasswordMaxLength()) {
			errors.rejectValue(fieldName, "password.length.max", "Cannot be greater than "+pConfig.getPasswordMaxLength()+" characters");
		} else if (!StringUtils.isBlank(input) && StringUtils.length(input) < pConfig.getPasswordMinLength()) {
			errors.rejectValue(fieldName, "password.length.min", "Must be at least "+pConfig.getPasswordMinLength()+" characters long");
		}
		if (!StringUtils.isBlank(input) &&  pConfig.isEnforceAlphanumeric()){			
			if(!( input.matches(".*[a-zA-Z]+.*") && input.matches(".*[0-9]+.*"))) {
				errors.rejectValue(fieldName, "password.alphanumeric", "Must be alphanumeric");
			}				
		} 
		if (!StringUtils.isBlank(input) && pConfig.isEnforceCaseMix() ) {
			if (!( input.matches(".*[a-z]+.*") && input.matches(".*[A-Z]+.*")) ) {
				errors.rejectValue(fieldName, "password.case.mix", "Must contain both uppercase and lowercase letters");
			}
		} 		
	}
	
	public String getPasswordHelpText() {		
		SystemConfig config = systemConfigDAO.getDefaultSystemConfig();
		PasswordConfig pConfig = config.getPasswordConfig();
		
		StringBuffer helpText = new StringBuffer("Password should be minimum "+pConfig.getPasswordMinLength() +" characters");
		if (pConfig.isEnforceAlphanumeric()){
			helpText.append(", alphanumeric");
		}
		if (pConfig.isEnforceCaseMix()){
			helpText.append(", with both upper and lower case letters");
		}
		helpText.append(".");
		
		return helpText.toString();
	}
		
	public static void main(String[] args) {
		
		String s = "password1";
		
		if ( s.matches(".*[a-z]+.*") && s.matches(".*[A-Z]+.*") ) {			
		    System.out.println("Valid!");
		}else{
			System.out.println("Not Valid!");
		}
	}

}

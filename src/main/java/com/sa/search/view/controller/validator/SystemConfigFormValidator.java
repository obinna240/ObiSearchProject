package com.sa.search.view.controller.validator;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sa.search.view.controller.form.SystemConfigForm;


@Component
public class SystemConfigFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return SystemConfigForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		SystemConfigForm form = (SystemConfigForm)target;
		String type = form.getType();
		Object value = form.getValue();
		if (value != null && type != null){
			try {
				Class<?> cls = ClassLoader.getSystemClassLoader().loadClass(type);
				if (value!=null && ClassUtils.isPrimitiveOrWrapper(cls)){							
					String propName = cls.getName().toLowerCase();
						if (propName.contains("int"))
							Integer.parseInt(value.toString());
						else if (propName.contains("long"))
							Long.parseLong(value.toString());
						else if (propName.contains("double"))
							Double.parseDouble(value.toString());
						else if (propName.contains("float"))
							Float.parseFloat(value.toString());
						else if (propName.contains("boolean"))
							Boolean.parseBoolean(value.toString());				
			} }catch (Exception e) {
				errors.rejectValue("value", "value.invalid","Please enter a valid value for "+type);
			}		
		}

	}

}

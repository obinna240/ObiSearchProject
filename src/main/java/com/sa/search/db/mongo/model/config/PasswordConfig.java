package com.sa.search.db.mongo.model.config;

import com.sa.search.db.mongo.model.IPcgSearchConfig;

public class PasswordConfig implements IPcgSearchConfig {
	private int passwordMinLength = 6;
	private int passwordMaxLength = 100;
	private boolean enforceAlphanumeric = false;
	private boolean enforceCaseMix = false;

	public int getPasswordMinLength() {
		return passwordMinLength;
	}
	
	public void setPasswordMinLength(int passwordMinLength) {
		this.passwordMinLength = passwordMinLength;
	}
	
	public int getPasswordMaxLength() {
		return passwordMaxLength;
	}
	
	public void setPasswordMaxLength(int passwordMaxLength) {
		this.passwordMaxLength = passwordMaxLength;
	}
	
	public boolean isEnforceAlphanumeric() {
		return enforceAlphanumeric;
	}
	
	public void setEnforceAlphanumeric(boolean enforceAlphanumeric) {
		this.enforceAlphanumeric = enforceAlphanumeric;
	}
	
	public boolean isEnforceCaseMix() {
		return enforceCaseMix;
	}
	
	public void setEnforceCaseMix(boolean enforceCaseMix) {
		this.enforceCaseMix = enforceCaseMix;
	}
	
}

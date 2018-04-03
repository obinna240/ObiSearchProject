package com.sa.search.db.mongo.model;

public class Authority implements java.io.Serializable, Comparable<Authority> {

	private Long id;
	private String description;
	private String snacCode;
	private String accreditationEmail;
	private boolean enabled = true;
	private String approvalProcessLink;
 
	public Authority() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSnacCode() {
		return snacCode;
	}

	public void setSnacCode(String snacCode) {
		this.snacCode = snacCode;
	}

	public String getAccreditationEmail() {
		return accreditationEmail;
	}

	public void setAccreditationEmail(String accreditationEmail) {
		this.accreditationEmail = accreditationEmail;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getApprovalProcessLink() {
		return approvalProcessLink;
	}

	public void setApprovalProcessLink(String approvalProcessLink) {
		this.approvalProcessLink = approvalProcessLink;
	}
	
	public String toString() {
		return getIdAsString();
    }
	
	@Override
	public boolean equals(Object aThat) {
		//check for self-comparison
		if ( this == aThat ) return true;
		
		if (aThat instanceof Authority) {
		
			Authority that = (Authority)aThat;
			if (that.getId() == null) {
				return false;
			}
			if (this.getId().longValue() == that.getId().longValue()) {
				return true;
			}
		}
		
		return false;
	 	
	 }
	 
	 public String getIdAsString() {
	 	if (this.getId() == null) {
	 		return "";
	 	}
	 
		return new Long(this.id).toString();
	}

	@Override
	public int compareTo(Authority o) {
		
		Authority autComp = (Authority)o;
		
		if (this.getDescription() != null && autComp.getDescription() != null){
			return this.getDescription().toLowerCase().compareTo(autComp.getDescription().toLowerCase());
		}
		else {
			return 0;
		}
	}
}

package com.sa.search.api.cms.json.packets;

import java.util.Date;



public class AccreditationPacket {
		
	private Long accreditationId;
	private String accreditationtype;
	private String filePath;
	private Date validdatefrom;
	private Date validdateto;
	private boolean approved;
	private String title;
	private Boolean showIcon;
	private Boolean linkEnabled;
	
	public Long getAccreditationId() {
		return accreditationId;
	}
	public void setAccreditationId(Long accreditationId) {
		this.accreditationId = accreditationId;
	}
	public String getAccreditationtype() {
		return accreditationtype;
	}
	public void setAccreditationtype(String accreditationtype) {
		this.accreditationtype = accreditationtype;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Date getValiddatefrom() {
		return validdatefrom;
	}
	public void setValiddatefrom(Date validdatefrom) {
		this.validdatefrom = validdatefrom;
	}
	public Date getValiddateto() {
		return validdateto;
	}
	public void setValiddateto(Date validdateto) {
		this.validdateto = validdateto;
	}
	public boolean isApproved() {
		return approved;
	}
	public void setApproved(boolean approved) {
		this.approved = approved;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Boolean getShowIcon() {
		return showIcon;
	}
	public void setShowIcon(Boolean showIcon) {
		this.showIcon = showIcon;
	}
	public Boolean getLinkEnabled() {
		return linkEnabled;
	}
	public void setLinkEnabled(Boolean linkEnabled) {
		this.linkEnabled = linkEnabled;
	}
	
	public AccreditationPacket(Long accreditationId,
			String accreditationtype, String filePath, Date validdatefrom,
			Date validdateto, boolean approved, String title, Boolean showIcon,
			Boolean linkEnabled) {
		super();
		this.accreditationId = accreditationId;
		this.accreditationtype = accreditationtype;
		this.filePath = filePath;
		this.validdatefrom = validdatefrom;
		this.validdateto = validdateto;
		this.approved = approved;
		this.title = title;
		this.showIcon = showIcon;
		this.linkEnabled = linkEnabled;
	}
	
	/*public AccreditationPacket(VendorToAccreditation vta, String accreditationtype) {
		super();
		this.accreditationId = vta.getId();
		this.accreditationtype = accreditationtype;
		this.filePath = vta.getFilePath();
		this.validdatefrom = vta.getValidDateFrom();
		this.validdateto = vta.getValidDateTo();
		this.approved = vta.isApproved();
		this.title = vta.getTitle();
		this.showIcon = vta.getShowIcon();
		this.linkEnabled = vta.getLinkEnabled();
	}*/

}

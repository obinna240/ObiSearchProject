package com.sa.search.view.controller.bean;

import java.util.List;

public class DisplayBean {
	
	private String section;
	private String pageTitle;
	private String titleArgs;
	
	public DisplayBean(String section, String pageTitle) {
		super();
		this.section = section;
		this.pageTitle = pageTitle;
	}
	
	public DisplayBean(String section, String pageTitle, List<String> titleArgs) {
		super();
		this.section = section;
		this.pageTitle = pageTitle;
		if (titleArgs != null && titleArgs.size() > 0) {
			StringBuffer sb = new StringBuffer();
			int i = 1; for (String s : titleArgs) {
				if (i>1) sb.append(",");
				sb.append(s);
				i++;
			}
			this.titleArgs = sb.toString();
		}
	}
	
	public String getSection() {
		return section;
	}
	
	public void setSection(String section) {
		this.section = section;
	}
	
	public String getPageTitle() {
		return pageTitle;
	}
	
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	
	public String getTitleArgs() {
		return titleArgs;
	}
	
	public void setTitleArgs(String titleArgs) {
		this.titleArgs = titleArgs;
	}

}

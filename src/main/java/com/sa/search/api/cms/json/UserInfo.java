package com.sa.search.api.cms.json;

/**
 * This bean is stored in the CMS session please keep it as light as possible
 * @author hewitts
 *
 */
public class UserInfo {
	
	private String userName;
	private Long userId;
	private Long managedUserId;
	private boolean isLoggedIn;
	private String borough;
	private boolean pageInFavourites;
	private String jsessionid;
	private String postCode;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public boolean isLoggedIn() {
		return isLoggedIn;
	}
	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}
	public String getBorough() {
		return borough;
	}
	public void setBorough(String borough) {
		this.borough = borough;
	}
	public boolean isPageInFavourites() {
		return pageInFavourites;
	}	
	public void setPageInFavourites(boolean pageInFavourites) {
		this.pageInFavourites = pageInFavourites;
	}	
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public UserInfo() {
	}
	
	public UserInfo(String userName, boolean isLoggedIn, String borough) {
		super();
		this.userName = userName;
		this.isLoggedIn = isLoggedIn;
		this.borough = borough;
	}
	
	public String getJsessionid() {
		return jsessionid;
	}
	
	public void setJsessionid(String jsessionid) {
		this.jsessionid = jsessionid;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getManagedUserId() {
		return managedUserId;
	}
	
	public void setManagedUserId(Long managedUserId) {
		this.managedUserId = managedUserId;
	}
		
}

package com.sa.search.user;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;


//@Component
public class SaUser extends User {

	/**
	 * 
	 */
	private String salt;
	private String id;
	private String backLink;	

	public SaUser(String username, String password, boolean enabled, boolean accountNonExpired, 
			boolean credentialsNonExpired, boolean accountNonLocked, Collection<GrantedAuthority> authorities,
			String salt) {

		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

		this.salt = salt;
		this.id = username;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setBackLink(String backLink) {
		this.backLink = backLink;
	}

	public String getBackLink() {
		return backLink;
	}
}

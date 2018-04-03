package com.sa.search.db.mongo.dao;

import java.util.List;

import com.sa.search.db.mongo.model.Authority;

public interface AuthorityDAOCustom extends CustomDAO<Authority, Long> {
	List<Authority> getEnabledAuthorities(); 
	List<Authority> getDisabledAuthorities(); 
	
	
	public void createAuthorities(Authority auth); 
}

package com.sa.search.db.mongo.dao;

import com.sa.search.db.mongo.model.User;

public interface UserDAOCustom extends CustomDAO<User, String>{
		
	boolean checkUserName(String userName);
	boolean checkUserName(String userName, String role);	
	
}

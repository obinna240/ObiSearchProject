package com.sa.search.db.mongo.dao;

import com.sa.search.db.mongo.model.SystemConfig;

public interface SystemConfigDAOCustom extends CustomDAO<SystemConfig, String>{
	
	public SystemConfig getDefaultSystemConfig();
	public SystemConfig getWritableDefaultSystemConfig();
	public SystemConfig getSystemConfigForContext(String context);

	
}

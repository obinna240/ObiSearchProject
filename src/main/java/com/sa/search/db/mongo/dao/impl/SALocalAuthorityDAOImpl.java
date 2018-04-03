package com.sa.search.db.mongo.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;


import com.sa.search.db.mongo.dao.SALocalAuthorityDAOCustom;


public class SALocalAuthorityDAOImpl implements SALocalAuthorityDAOCustom {

	@Autowired	@Qualifier("mongoTemplateAddress") MongoOperations mongoOps;
	
	private static Log m_log = LogFactory.getLog(SALocalAuthorityDAOImpl.class);

	public SALocalAuthorityDAOImpl() {
		
	}
	
	
}

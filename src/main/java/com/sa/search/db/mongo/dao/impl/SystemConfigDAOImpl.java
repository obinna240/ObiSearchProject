package com.sa.search.db.mongo.dao.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.sa.search.config.SASpringContext;
import com.sa.search.db.mongo.dao.SystemConfigDAOCustom;
import com.sa.search.db.mongo.model.SystemConfig;

public class SystemConfigDAOImpl extends CustomDAOImpl<SystemConfig, String> implements SystemConfigDAOCustom {

	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;

	private static Log m_log = LogFactory.getLog(SystemConfigDAOImpl.class);

	public SystemConfigDAOImpl() {
		super(SystemConfig.class);
	}

	@Override
	public SystemConfig getDefaultSystemConfig() {
		Query query = null;
		try {
			String context = SASpringContext.getAppContext();

			query = new Query(where("_id").is(context));
			SystemConfig config = mongoOps.findOne(query, SystemConfig.class);
	
			return config;
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}

		return null;
	}

	@Override
	public SystemConfig getWritableDefaultSystemConfig() {

		Query query = null;
		try {
			String context = SASpringContext.getAppContext();
			query = new Query(where("_id").is(context));
			SystemConfig config = mongoOps.findOne(query, SystemConfig.class);
			return config;
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}

		return null;
	}

	@Override
	public SystemConfig getSystemConfigForContext(String context) {
		Query query = null;
		try {
			
			query = new Query(where("_id").is(context));
			SystemConfig config = mongoOps.findOne(query, SystemConfig.class);
			
			return config;
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}

		return null;
	}

}

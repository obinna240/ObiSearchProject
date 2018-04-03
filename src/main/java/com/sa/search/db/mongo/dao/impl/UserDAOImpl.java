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

import com.sa.search.db.mongo.dao.UserDAOCustom;
import com.sa.search.db.mongo.model.User;

public class UserDAOImpl extends CustomDAOImpl<User, String> implements UserDAOCustom {

	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;

	private static Log m_log = LogFactory.getLog(UserDAOImpl.class);

	public UserDAOImpl() {
		super(User.class);
	}

	@Override
	public boolean checkUserName(String userName) {
		if (StringUtils.isEmpty(userName)) {
			return false;
		}

		Query query = null;
		try {
			query = new Query(where("_id").regex("^" + userName + "$", "i")); 
			User user =  mongoOps.findOne(query, User.class);
			if (user != null) {
				return true;
			}
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}

		return false;
	}

	@Override
	public boolean checkUserName(String userName, String role) {
		if (StringUtils.isEmpty(userName)) {
			return false;
		}

		Query query = null;
		try {
			query = new Query(where("_id").regex("^" + userName + "$", "i")); 

			User user =  mongoOps.findOne(query, User.class);
			if (user != null) {
				return true;
			}
		}
		catch (Exception e){
			m_log.error("Error executing Mongo query : " + StringUtils.trim(query.toString()), e);
		}

		return false;
	}

}

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

import com.sa.search.db.mongo.dao.TemplateDAOCustom;
import com.sa.search.db.mongo.model.Template;

public class TemplateDAOImpl extends CustomDAOImpl<Template, String> implements TemplateDAOCustom {

	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;

	private static Log m_log = LogFactory.getLog(TemplateDAOImpl.class);

	public TemplateDAOImpl() {
		super(Template.class);
	}

}

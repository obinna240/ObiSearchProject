package com.sa.search.db.mongo.dao.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;

import com.sa.search.db.mongo.dao.RecommendedLinkDAOCustom;
import com.sa.search.db.mongo.model.RecommendedLink;
import com.sa.search.util.CacheHelper;

public class RecommendedLinkDAOImpl extends CustomDAOImpl<RecommendedLink, String> implements RecommendedLinkDAOCustom {

	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;
	@Autowired	@Qualifier("daoCache") CacheHelper cache;

	private static Log m_log = LogFactory.getLog(RecommendedLinkDAOImpl.class);

	public RecommendedLinkDAOImpl() {
		super(RecommendedLink.class);
	}

	
	@Override
	public List<RecommendedLink> findByKeyword(String keywords, int maxResults) {
		
		List<RecommendedLink> resultList = new ArrayList<RecommendedLink>();
		
		if (StringUtils.isNotBlank(keywords)){
			
			StringBuffer regex = new StringBuffer();

			//Trim and single-space the query text
			keywords = keywords.trim();
			keywords = keywords.replaceAll("[ ]+", " ");

			//Word/phrase is expected to be delimited by start-of-line and semicolon, semicolon and semicolon, or semicolon and end of line
			//Regex is (^|;)[ ]*keywords[ ]*($|;)
			
			
			regex.append("(^|;)[ ]*");//ignore accept 0 or more spaces around the keyword(s) 
			regex.append(keywords);
			regex.append("[ ]*($|;)");
			
			//Use a case insensitive regex to do word match on the keyword field
			resultList = mongoOps.find(query(where("keyword").regex(regex.toString(), "i")).limit(maxResults), RecommendedLink.class);

		}

		
		return resultList;
	}

}

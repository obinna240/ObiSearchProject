package com.sa.search.db.mongo.dao.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;

import com.sa.search.db.mongo.dao.SearchConfigDAOCustom;
import com.sa.search.db.mongo.model.SearchConfig;
import com.sa.search.util.CacheHelper;

public class SearchConfigDAOImpl extends CustomDAOImpl<SearchConfig, String> implements SearchConfigDAOCustom {

	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;
	@Autowired	@Qualifier("daoCache") CacheHelper cache;

	private static Log m_log = LogFactory.getLog(SearchConfigDAOImpl.class);

	public SearchConfigDAOImpl() {
		super(SearchConfig.class);
	}

	
	@Override
	public int findSnippetLength(int defaultVal) {
		
		String KEY = "snip_length";
		Integer cachedVal = cache.getIntValue(KEY);
		
		if (cachedVal != null){
			return cachedVal.intValue();
		}
		
		SearchConfig conf = null;
		
		try{
			conf = mongoOps.findOne(query(where("type").is(KEY)), SearchConfig.class);
		}
		catch (Throwable t){
			m_log.error("MongoDB connection error - using default values. Error message : " +  t.getMessage());
		}
			
		if (conf != null){
			cache.setValue(KEY, conf.getValue());
			return conf.getValue(); 
		}
		else {
			cache.setValue(KEY, new Integer(defaultVal));
			return defaultVal;
		}
	}

	@Override
	public int findResultsPerPage(int defaultVal) {

		String KEY = "results_per_page";
		Integer cachedVal = cache.getIntValue(KEY);
		if (cachedVal != null){
			return cachedVal.intValue();
		}

		SearchConfig conf = null;
		
		try{
			conf = mongoOps.findOne(query(where("type").is("results_per_page")), SearchConfig.class);
		}
		catch (Throwable t){
			m_log.error("MongoDB connection error - using default values. Error message : " +  t.getMessage());
		}
		
		if (conf != null){
			cache.setValue(KEY, conf.getValue());
			return conf.getValue(); 
		}
		else {
			cache.setValue(KEY, new Integer(defaultVal));
			return defaultVal;
		}
	}
	
	@Override
	public String getStandardSearchTemplate() {
		return getStringVal("standard_search_template");
	}
	
	@Override
	public String getSearchConstraintsTemplate() {
		return getStringVal("search_constraint_template");
	}
	
	@Override
	public String getSmartSuggestTemplate() {
		return getStringVal("standard_smartsuggest_template");
	}
	
	@Override
	public String getAdvancedAllTemplate() {
		return getStringVal("advanced_all_template");
	}

	@Override
	public String getAdvancedExactTemplate() {
		return getStringVal("advanced_exact_template");
	}

	@Override
	public String getAdvancedExcludeTemplate() {
		return getStringVal("advanced_exclude_template");
	}

	@Override
	public String getAdvancedOrTemplate() {
		return getStringVal("advanced_or_template");
	}

	@Override
	public String getAdvancedDateTemplate() {
		return getStringVal("advanced_date_template");
	}
	
	private String getStringVal(String typeId){

		String cachedVal = cache.getStringValue(typeId);
		
		if (cachedVal != null){
			return cachedVal;
		}

		SearchConfig conf = null;
		
		try {
			conf = mongoOps.findOne(query(where("type").is(typeId)), SearchConfig.class);
			cache.setValue(typeId, conf.getTextValue());
			return conf.getTextValue();
		}
		catch (Throwable t){
			m_log.error("MongoDB connection error. Error message : " +  t.getMessage());
			return null;
		}
	}

}

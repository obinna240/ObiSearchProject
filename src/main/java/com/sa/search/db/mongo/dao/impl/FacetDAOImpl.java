package com.sa.search.db.mongo.dao.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;

import com.sa.search.db.mongo.dao.FacetDAOCustom;
import com.sa.search.db.mongo.model.Facet;
import com.sa.search.util.CacheHelper;


public class FacetDAOImpl extends CustomDAOImpl<Facet, String> implements FacetDAOCustom {

	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;
	@Autowired	@Qualifier("daoCache") CacheHelper cache;

	private static Log m_log = LogFactory.getLog(FacetDAOImpl.class);
	

	public FacetDAOImpl() {
		super(Facet.class);
	}

	@Override
	public Facet findById(String id) {

		String KEY = "facet_" + id;
		Object cachedVal = cache.getValue(KEY);
		
		if (cachedVal != null){
			return (Facet)cachedVal;
		}
		else {
			Facet f = mongoOps.findOne(query(where("_id").is(id)), Facet.class);
			cache.setValue(KEY, f);
			return f;
		}
	}
	
	@Override
	public List<Facet> findAllEnabled() {

		String KEY = "facet_enabled";
		Object cachedVal = cache.getValue(KEY);
		
		if (cachedVal != null){
			return (List<Facet>)cachedVal;
		}
		else {
			List<Facet> f = mongoOps.find(query(where("enabled").is(true)), Facet.class);
			cache.setValue(KEY, f);
			return f;
		}
	}

	


}

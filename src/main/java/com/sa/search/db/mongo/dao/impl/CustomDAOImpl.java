package com.sa.search.db.mongo.dao.impl;

import java.io.Serializable;
import java.util.List;

//import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import com.sa.search.db.mongo.dao.CustomDAO;

public class CustomDAOImpl <T, ID extends Serializable> implements CustomDAO<T, ID> {
	@Autowired @Qualifier("mongoTemplate")  MongoOperations mongoOps;

	private Class<T> persistentClass;


	private static Log m_log = LogFactory.getLog(CustomDAOImpl.class);

	public CustomDAOImpl(Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}	

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		try {
			Query query = new Query();
			query.with(new Sort(Sort.Direction.ASC, "_id"));

			return mongoOps.find(query, getPersistentClass());
			
		}
		catch (Exception e) {
			m_log.error(e);
			return null;
		}
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> findAllOrderBy(String order) {
		try {
			Query query = new Query();
			query.with(new Sort(Sort.Direction.ASC, order));

			return mongoOps.find(query, getPersistentClass());
		}
		catch (Exception e) {
			m_log.error(e);
			return null;
		}
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> findEntries(int firstResult, int maxResults) {
		Query query = new Query();
		query.with(new Sort(Sort.Direction.ASC, "_id"));

		List<T> all = mongoOps.find(query, getPersistentClass());
    	int size = all.size();
    	int fromIndex  = firstResult;
    	int toIndex = firstResult;
    	if (firstResult+maxResults > size) {
    		toIndex = size;
    	}
    	else {
    		toIndex = firstResult+maxResults;
    	}
    	return all.subList(fromIndex, toIndex);
    	
    	
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> findEntries(int firstResult, int maxResults, String sortColumn) {
		Query query = new Query();
		query.with(new Sort(Sort.Direction.ASC, sortColumn));
		
		List<T> all = mongoOps.find(query, getPersistentClass());
    	int size = all.size();
    	int fromIndex  = firstResult;
    	int toIndex = firstResult;
    	if (firstResult+maxResults > size) {
    		toIndex = size;
    	}
    	else {
    		toIndex = firstResult+maxResults;
    	}
    	return all.subList(fromIndex, toIndex);
    	
    	
	}
	
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> findEntries(int firstResult, int maxResults, String sortColumn, boolean desc) {
		Query query = new Query();
		if (desc == true)  {
			query.with(new Sort(Sort.Direction.DESC, sortColumn));			
		} else { 
			query.with(new Sort(Sort.Direction.ASC, sortColumn));
		}
		List<T> all = mongoOps.find(query, getPersistentClass());
    	int size = all.size();
    	int fromIndex  = firstResult;
    	int toIndex = firstResult;
    	if (firstResult+maxResults > size) {
    		toIndex = size;
    	}
    	else {
    		toIndex = firstResult+maxResults;
    	}
    	return all.subList(fromIndex, toIndex);
    	
    	
	}
	
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public long count() {
		Query query = new Query();
    	long count = mongoOps.count(query, getPersistentClass());
    	
    	return count;
		    	
		    
	}
}

package com.sa.search.db.mongo.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.query.Order;

public interface CustomDAO<T, ID extends Serializable> {
	public List<T> findAll();
	public List<T> findAllOrderBy(String order);
	public List<T> findEntries(int firstResult, int maxResults);
	public List<T> findEntries(int firstResult, int maxResults, String sortColumn);
	public List<T> findEntries(int firstResult, int maxResults, String sortColumn, boolean desc);
	public long count();
}

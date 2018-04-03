package com.sa.search.db.mongo.dao;

import java.util.List;

import com.sa.search.db.mongo.model.Facet;

public interface FacetDAOCustom extends CustomDAO<Facet, String>{
		
	public Facet findById(String id);
	public List<Facet> findAllEnabled();
}

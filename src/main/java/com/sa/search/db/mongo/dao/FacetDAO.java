package com.sa.search.db.mongo.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.sa.search.db.mongo.model.Facet;

public interface FacetDAO extends PagingAndSortingRepository<Facet, String>, FacetDAOCustom {

}

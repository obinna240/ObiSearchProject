package com.sa.search.db.mongo.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.sa.search.db.mongo.model.SearchConfig;

public interface SearchConfigDAO extends PagingAndSortingRepository<SearchConfig, String>, SearchConfigDAOCustom {

}

package com.sa.search.db.mongo.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.sa.search.db.mongo.model.SearchLog;

public interface SearchLogDAO extends PagingAndSortingRepository<SearchLog, String>, SearchLogDAOCustom {

}

package com.sa.search.db.mongo.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.sa.search.db.mongo.model.SystemConfig;

public interface SystemConfigDAO extends PagingAndSortingRepository<SystemConfig, String>, SystemConfigDAOCustom {

}

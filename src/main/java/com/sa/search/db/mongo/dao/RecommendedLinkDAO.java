package com.sa.search.db.mongo.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.sa.search.db.mongo.model.RecommendedLink;

public interface RecommendedLinkDAO extends PagingAndSortingRepository<RecommendedLink, String>, RecommendedLinkDAOCustom {

}

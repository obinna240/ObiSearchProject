package com.sa.search.db.mongo.dao;

import java.util.List;

import com.sa.search.db.mongo.model.RecommendedLink;

public interface RecommendedLinkDAOCustom extends CustomDAO<RecommendedLink, String>{		
	public List<RecommendedLink> findByKeyword(final String keyword, final int maxResults);
}

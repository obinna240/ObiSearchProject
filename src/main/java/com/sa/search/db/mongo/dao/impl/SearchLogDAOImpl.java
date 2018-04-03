package com.sa.search.db.mongo.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sa.search.db.mongo.dao.SearchLogDAOCustom;
import com.sa.search.db.mongo.model.SearchLog;

public class SearchLogDAOImpl extends CustomDAOImpl<SearchLog, String> implements SearchLogDAOCustom {

	
	private static Log m_log = LogFactory.getLog(SearchLogDAOImpl.class);

	public SearchLogDAOImpl() {
		super(SearchLog.class);
	}

	
}

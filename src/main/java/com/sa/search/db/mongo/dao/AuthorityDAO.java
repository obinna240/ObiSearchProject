package com.sa.search.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.sa.search.db.mongo.model.Authority;

public interface AuthorityDAO extends CrudRepository<Authority, Long>, AuthorityDAOCustom {
	public Authority findBySnacCode(String snacCode);
}

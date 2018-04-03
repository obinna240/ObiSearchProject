package com.sa.search.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.sa.search.db.mongo.model.OsCodePoint;

public interface OsCodePointDAO extends CrudRepository<OsCodePoint, String>, OsCodePointDAOCustom {
}

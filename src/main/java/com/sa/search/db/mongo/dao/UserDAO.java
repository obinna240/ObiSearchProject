package com.sa.search.db.mongo.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.sa.search.db.mongo.model.User;

public interface UserDAO extends PagingAndSortingRepository<User, String>, UserDAOCustom {

}

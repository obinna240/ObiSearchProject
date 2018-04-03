package com.sa.search.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.sa.search.db.mongo.model.SALocalAuthority;

public interface SALocalAuthorityDAO extends CrudRepository<SALocalAuthority, Long>,SALocalAuthorityDAOCustom {


}

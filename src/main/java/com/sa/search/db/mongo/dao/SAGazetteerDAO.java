package com.sa.search.db.mongo.dao;

import org.springframework.data.repository.CrudRepository;

import com.sa.search.db.mongo.model.SAGazetteer;

public interface SAGazetteerDAO extends CrudRepository<SAGazetteer, Long>, SAGazetteerDAOCustom {


}

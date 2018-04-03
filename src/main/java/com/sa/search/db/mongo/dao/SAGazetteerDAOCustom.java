package com.sa.search.db.mongo.dao;

import java.util.List;

import com.sa.search.db.mongo.model.SAGazetteer;
import com.sa.search.db.mongo.model.SAGazetteer.Coverage;


public interface SAGazetteerDAOCustom {
	
	public SAGazetteer findByImportId(String importId);
	public List<SAGazetteer> findByLocation(String location, Coverage coverage);
}

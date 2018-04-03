package com.sa.search.db.mongo.dao.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;

import com.sa.search.db.mongo.dao.AuthorityDAO;
import com.sa.search.db.mongo.dao.SAGazetteerDAOCustom;
import com.sa.search.db.mongo.model.SAGazetteer.Coverage;
import com.sa.search.db.mongo.model.Authority;
import com.sa.search.db.mongo.model.SAGazetteer;



public class SAGazetteerDAOImpl implements SAGazetteerDAOCustom {

	@Autowired	@Qualifier("mongoTemplateAddress") MongoOperations mongoOps;
	@Autowired	AuthorityDAO authorityDAO;
	
	
	private static Log m_log = LogFactory.getLog(SAGazetteerDAOImpl.class);

	public SAGazetteerDAOImpl() {
		
	}

	
	@Override
	public SAGazetteer findByImportId(String importId) {
		Query query = new Query(where("importId").is(importId)); 
		SAGazetteer saGazetteer = mongoOps.findOne(query, SAGazetteer.class);
		return saGazetteer;
	}


	@Override
	public List<SAGazetteer> findByLocation(String location, Coverage coverage) {
		String query = "";
		try {
			query = getLocationCoverageQuery(location, coverage);
			return mongoOps.find(new BasicQuery(query), SAGazetteer.class);
		} catch (Exception e) {
			m_log.error("Error executing Mongo query : " + query, e);
		}

		return new ArrayList<SAGazetteer>();
	}
	
	
	private String getLocationCoverageQuery(String location, Coverage coverage){

		//@Query(value = "{'name': {$regex : '^?0$', $options: 'i'}}")
		StringBuilder jsonCommand = new StringBuilder();
		jsonCommand.append("{");
		jsonCommand.append(String.format("\"name\" : { \"$regex\" : \"%s\" , \"$options\" : \"i\"}", location));	
		
	
		if (coverage.equals(Coverage.UK)) {
			jsonCommand.append("}");
		} else if (coverage.equals(Coverage.LOCALAUTHORITIES)) {

			jsonCommand.append(",");

			StringBuilder jsonAuths = new StringBuilder();

			Iterable<Authority> auths = authorityDAO.getEnabledAuthorities();
			int authCount = 1;

			for (Authority authority : auths) {
				
				if (authCount > 1) {
					jsonAuths.append(",");
				}

				jsonAuths.append("{localGovtCode:{$regex:'^");
				jsonAuths.append(authority.getSnacCode());
				jsonAuths.append("'}}");
				authCount++;
			}

			if (authCount == 1) {
				jsonCommand.append(jsonAuths);
				jsonCommand.append("}");
			} else {
				jsonCommand.append("$or:[");
				jsonCommand.append(jsonAuths);
				jsonCommand.append("]}");
			}
			
		}
		
		return jsonCommand.toString();
	}
	
	
}

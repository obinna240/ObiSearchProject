package com.sa.search.db.mongo.dao.impl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.sa.search.db.mongo.dao.AuthorityDAO;
import com.sa.search.db.mongo.dao.OsCodePointDAOCustom;
import com.sa.search.db.mongo.model.Authority;
import com.sa.search.db.mongo.model.OsCodePoint;
import com.sa.search.db.mongo.model.OsCodePoint.Coverage;

import uk.me.jstott.jcoord.LatLng;


public class OsCodePointDAOImpl implements OsCodePointDAOCustom {

	@Autowired	@Qualifier("mongoTemplateAddress") MongoOperations mongoOps;
	@Autowired	AuthorityDAO authorityDAO;
	
	
	private static Log m_log = LogFactory.getLog(OsCodePointDAOImpl.class);

	public OsCodePointDAOImpl() {
		
	}
	
	/**
	 * Find a postcode constrained by the given coverage area
	 * A empty list is returned if the postcode either can't be found or is outside the coverage area, or is < 5 chars
	 */
	@Override
	public List<OsCodePoint> findByPostcode(String postcode, Coverage coverage) {
		
		String pcShort = getPCShort(postcode);
		
		if (pcShort.length() >= 5){
			
			String query = "";
			
			try {
				query = getPostCodeCoverageQuery(pcShort, coverage);
				return mongoOps.find(new BasicQuery(query), OsCodePoint.class);
			} catch (Exception e) {
				m_log.error("Error executing Mongo query : " + query, e);
			}
		}

		return new ArrayList<OsCodePoint>();
	}
	
	/**
	 * Find a lat lng constrained by the given coverage area
	 * A empty list is returned if the postcode either can't be found or is outside the coverage area, or is < 3 chars
	 */
	@Override
	public LatLng findLatLngByPostcode(String postcode, Coverage coverage) {
		
		String pcShort = getPCShort(postcode);
		
		if (pcShort.length() >= 2){
			try {
				DBCollection dbCollection = mongoOps.getCollection("osCodePoint");
				
			    Pattern regex = Pattern.compile("^"+pcShort.toUpperCase()); 
			   
			    DBObject query =  new BasicDBObject("_id", regex);
			    DBObject match = new BasicDBObject("$match", query);
			     
            
			    // build the $projection operation
			    DBObject fields = new BasicDBObject("latitude", 1);
			    fields.put("longtitude", 1);
			    fields.put("_id", 0);
			    DBObject project = new BasicDBObject("$project", fields );

			    
               DBObject groupFields = new BasicDBObject( "_id", "id");
               groupFields.put("latitudeAvg", new BasicDBObject( "$avg", "$latitude"));
               groupFields.put("longtitudeAvg", new BasicDBObject( "$avg", "$longtitude"));
               DBObject group = new BasicDBObject("$group", groupFields);
           
               List<DBObject> list = new ArrayList<DBObject>();
               list.add(match);
               list.add(project);
               list.add(group);
               AggregationOutput aggOutput = dbCollection.aggregate(list);
			    
			   //AggregationOutput aggOutput = dbCollection.aggregate(match, project, group);
			    
			    
			    Iterable<DBObject> results = aggOutput.results();
			    if (results != null && results.iterator().hasNext()) {
			    	DBObject result = results.iterator().next();
			    	Double lat = (Double)result.get("latitudeAvg");
			    	Double lng = (Double)result.get("longtitudeAvg");
			    	
			    	LatLng latLng = new LatLng(lat, lng);
			    	return latLng;
			    }
			
			} catch (Exception e) {
				m_log.error("Error executing Mongo query : ", e);
			}
		}

		return null;
	}
	
	private String getPCShort(String pc) {
		if (StringUtils.isBlank(pc)) {
			return "";
		} else {
			String pcShort = pc.replaceAll("\\s+","");
			return pcShort.toUpperCase();
		}
	}
	
	
	private String getPostCodeCoverageQuery(String pcShort, Coverage coverage){

		//Defensive code - expect at least 2 characters (i.e. a full outward postcode part)
		//That should restrict the regex to 1-200000 addresses. Any fewer characters than that 
		//and we're talking millions
		
		if (pcShort.length() < 2){
			pcShort = "XXX"; // will return 0 results
		}
		
		
		String query = "{_id:{$regex:'^" + pcShort + "'}";

		if (coverage.equals(Coverage.UK)) {
			query += "}";
		} else if (coverage.equals(Coverage.LOCALAUTHORITIES)) {

			StringBuilder jsonCommand = new StringBuilder(query);
			jsonCommand.append(",");

			StringBuilder jsonAuths = new StringBuilder();

			Iterable<Authority> auths = authorityDAO.getEnabledAuthorities();
			int authCount = 1;

			for (Authority authority : auths) {
				
				if (authCount > 1) {
					jsonAuths.append(",");
				}

				jsonAuths.append("{snacCode:{$regex:'^");
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
			query = jsonCommand.toString();
		}
		
		return query;
	}
}

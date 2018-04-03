package com.sa.search.db.mongo.dao;

import uk.me.jstott.jcoord.LatLng;

import java.util.List;

import com.sa.search.db.mongo.model.OsCodePoint;
import com.sa.search.db.mongo.model.OsCodePoint.Coverage;

public interface OsCodePointDAOCustom {
	public List<OsCodePoint> findByPostcode(String postcode, Coverage coverage);
	public LatLng findLatLngByPostcode(String postcode, Coverage coverage);
	
}

package com.sa.search.solrsearch;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sa.search.db.mongo.model.SAGazetteer;

public class Location extends SAGazetteer{

	private String locationUrl;
	private static Log m_log = LogFactory.getLog(Location.class);

	public Location(SAGazetteer saGazetteer) {
		try {
			BeanUtils.copyProperties(this, saGazetteer);
		} catch (Exception e) {
			m_log.error("Error initialising view bean ", e);
		}
	}

	public String getLocationUrl() {
		return locationUrl;
	}

	public void setLocationUrl(String locationUrl) {
		this.locationUrl = locationUrl;
	}
	
}

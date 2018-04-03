package com.sa.search.solrsearch;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.sa.search.api.cms.controller.bean.SmartSuggestCategory;
import com.sa.search.api.cms.json.CmsDocument;
import com.sa.search.db.mongo.dao.OsCodePointDAO;
import com.sa.search.db.mongo.dao.SAGazetteerDAO;
import com.sa.search.db.mongo.dao.SystemConfigDAO;
import com.sa.search.db.mongo.model.OsCodePoint;
import com.sa.search.db.mongo.model.SAGazetteer;

import uk.me.jstott.jcoord.LatLng;

import com.sa.search.db.mongo.model.SystemConfig;
import com.sa.search.db.mongo.model.OsCodePoint.Coverage;

@Component
public class SearchServices_old {

	@Autowired private ISearchProvider searchProvider;
	@Autowired private SolrSmartSuggester smartSuggester;
	@Autowired private SystemConfigDAO systemConfigDAO;
	@Autowired private SAGazetteerDAO saGazetteerDAO;
	@Autowired private OsCodePointDAO osCodePointDAO;
		
	public List<SmartSuggestCategory> doSmartSuggestSearch(HttpServletRequest request, ModelMap modelMap, SearchInfo searchInfo) {
		return smartSuggester.getSmartSuggestions(request, modelMap, searchInfo);
	}
	
	
	public void doSearch(HttpServletRequest request, ModelMap modelMap,  SearchInfo searchInfo, boolean smartSuggest) {
		
		if (searchInfo != null) {
			
			List<Location> locations = checkLocation(request, modelMap, searchInfo);
			if (locations != null) {
				SearchResult searchResult = new SearchResult();
				ResultLocation resultLocation = new ResultLocation();
				resultLocation.setLocations(locations);
				searchResult.setResultLocation(resultLocation);
				
				modelMap.put("searchResult", searchResult);
			} else {
				
		    	//
				//Geo search.
				//
				String searchPC = searchInfo.getPc();
				//String rad = searchInfo.getRadius();
				
				if (StringUtils.isNotBlank(searchPC)) {
					List<OsCodePoint> osCodePointList = osCodePointDAO.findByPostcode(searchPC, Coverage.UK);
					if (osCodePointList != null && osCodePointList.size() == 1) {
						OsCodePoint osCodePoint = osCodePointList.get(0);
						Double lat = osCodePoint.getLatitude();
						Double lng = osCodePoint.getLongtitude();
						
						searchInfo.setLatitude(lat);
						searchInfo.setLongitude(lng);

					} else {
						LatLng latLng = osCodePointDAO.findLatLngByPostcode(searchPC,com.sa.search.db.mongo.model.OsCodePoint.Coverage.UK);
						if (latLng != null) {
		    	        	Double lat = latLng.getLatitude();
		    	          	Double lng = latLng.getLongitude();
		    	          	
		    	          	searchInfo.setLatitude(lat);
							searchInfo.setLongitude(lng);
						}
						
					}
//					List<SAAddress> listAddresses = addressMasterUKDAO.findByPostcode(searchPC, com.sa.search.db.mongo.model.SAAddress.Coverage.UK);
//					if (listAddresses != null && listAddresses.size() > 0 ) {
//						// just use the first?
//						SAAddress addressmaster = listAddresses.get(0);
//						Double lat = addressmaster.getLatitude();
//						Double lng = addressmaster.getLongtitude();
//						
//						searchInfo.setLatitude(lat);
//						searchInfo.setLongitude(lng);
//						
//					} else {
//						LatLng latLng = addressMasterUKDAO.findLatLngByPostcode(searchPC,com.sa.search.db.mongo.model.SAAddress.Coverage.UK);
//						if (latLng != null) {
//		    	        	Double lat = latLng.getLatitude();
//		    	          	Double lng = latLng.getLongitude();
//		    	          	
//		    	          	searchInfo.setLatitude(lat);
//							searchInfo.setLongitude(lng);
//		    	        }
//					}
				}
		
				SearchResult searchResult = searchProvider.getSearchResults(request, modelMap, searchInfo, smartSuggest);
				modelMap.put("searchResult", searchResult);
			}
		}
	}

	private List<Location> checkLocation(HttpServletRequest request, ModelMap modelMap,  SearchInfo searchInfo) {
		
		String searchText = searchInfo.getSearchText();
		String location = "";
		String searchPC = searchInfo.getPc();
		
		SystemConfig systemConfig = getSystemConfig();
		if (systemConfig.isGazetteerSearchAllowed() == true) {
			
			if (StringUtils.isNotBlank(searchText)) {
				 String[] splits = searchText.split(" in | near ");
				 if (splits.length == 2) {
					 searchText = splits[0].trim();
					 location = splits[1].trim();
				 }
			}
		}
		
		if (StringUtils.isNotBlank(searchPC) || StringUtils.isNotBlank(location) ) {
			List<SAGazetteer> locations = null;
			// are we a town/city?
			// are we a town/city?
			if (StringUtils.isNotBlank(location) && StringUtils.isAlphanumericSpace(location) && !StringUtils.isAlphaSpace(location) ) {
				searchPC = location;
				searchInfo.setPc(location);
				searchInfo.setSearchText(searchText);
			} else if (StringUtils.isNotBlank(location) && StringUtils.isAlphaSpace(location)) {
				//if (getDefaultSearchPostcodeValidate() == true) {
				//	locations = saGazetteerDAO.findByLocation(location, com.sa.search.db.mongo.model.SAGazetteer.Coverage.LOCALAUTHORITIES);
				//} else {
					locations = saGazetteerDAO.findByLocation(location, com.sa.search.db.mongo.model.SAGazetteer.Coverage.UK);
				//}
				
			} else if (StringUtils.isNotBlank(searchPC) && StringUtils.isAlphaSpace(searchPC)) {
				//if (getDefaultSearchPostcodeValidate() == true) {
				//	locations = saGazetteerDAO.findByLocation(searchPC, com.sa.search.db.mongo.model.SAGazetteer.Coverage..LOCALAUTHORITIES);
				//} else {
					locations = saGazetteerDAO.findByLocation(searchPC, com.sa.search.db.mongo.model.SAGazetteer.Coverage.UK);
				//}
			} 
			
			
			if (locations != null && locations.size() == 1) {
				SAGazetteer saGazetteer = locations.get(0);
				if (StringUtils.isNotBlank(saGazetteer.getPostcode())) {
					searchPC = saGazetteer.getPostcode();
					searchInfo.setSearchText(searchText);
					searchInfo.setPc(searchPC);
					searchInfo.setRadius("5");
				}
			} else if (locations != null && locations.size() > 1 ){
				
				List<Location> locationList = new ArrayList<Location>();
				
				for (SAGazetteer saGazetteer : locations) {
					
					Location loc = new Location(saGazetteer);
					String locationUrl = String.format("/api/cms/search/doSearch?q=%s&pc=%s&radius=5", searchText, saGazetteer.getPostcode());
					loc.setLocationUrl(locationUrl);
					locationList.add(loc);
				}			
				
				return locationList;
			} 
		}
		
		return null;
	}
	
	public CmsDocument findDocument(String id) {
		CmsDocument cmsDocument = searchProvider.findById(id);
		return cmsDocument;
	}
		
	public SystemConfig getSystemConfig() {
		return systemConfigDAO.getDefaultSystemConfig();
	}
}
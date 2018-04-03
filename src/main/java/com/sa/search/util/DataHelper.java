package com.sa.search.util;

import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;

import com.sa.search.solrsearch.SearchInfo;

public class DataHelper {
	private static Log m_log = LogFactory.getLog(DataHelper.class);
	public static DecimalFormat dfPrice = new DecimalFormat("0.00");
	
	public static String searchInfoToUrl(SearchInfo searchInfo) {
		
		try {
			URIBuilder b = new URIBuilder();
				
			// search text
			String searchText = searchInfo.getSearchText();
			if (StringUtils.isNotBlank(searchText)) {
				b.addParameter("q", searchText);
			}
			
			// document type
			List<String> documentTypeList = searchInfo.getDocumentTypeList();
			if (documentTypeList != null && documentTypeList.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (String id : documentTypeList) {
					if (sb.length() > 0) {
						sb.append(";");
					}
					sb.append(id);
				}
				b.addParameter("t", sb.toString());
			}
			
			// metadata
			List<String> sectionIdList = searchInfo.getSectionIdList();
			if (sectionIdList != null && sectionIdList.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (String id : sectionIdList) {
					if (sb.length() > 0) {
						sb.append(";");
					}
					sb.append(id);
				}
				b.addParameter("sid", sb.toString());
			}
			
			List<String> classificationAndIdList = searchInfo.getClassificationAndIdList();
			if (classificationAndIdList != null && classificationAndIdList.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (String id : classificationAndIdList) {
					if (sb.length() > 0) {
						sb.append(";");
					}
					sb.append(id);
				}
				b.addParameter("caid", sb.toString());
			}
			
			List<String> classificationOrIdList = searchInfo.getClassificationOrIdList();
			if (classificationOrIdList != null && classificationOrIdList.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (String id : classificationOrIdList) {
					if (sb.length() > 0) {
						sb.append(";");
					}
					sb.append(id);
				}
				b.addParameter("coid", sb.toString());
			}
		
			
			List<String> searchableClassificationAndIdList = searchInfo.getSearchableClassificationAndIdList();
			if (searchableClassificationAndIdList != null && searchableClassificationAndIdList.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (String id : searchableClassificationAndIdList) {
					if (sb.length() > 0) {
						sb.append(";");
					}
					sb.append(id);
				}
				b.addParameter("scaid", sb.toString());
			}
			
			List<String> searchableClassificationOrIdList = searchInfo.getSearchableClassificationOrIdList();
			if (searchableClassificationOrIdList != null && searchableClassificationOrIdList.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (String id : searchableClassificationOrIdList) {
					if (sb.length() > 0) {
						sb.append(";");
					}
					sb.append(id);
				}
				b.addParameter("scoid", sb.toString());
			}
		
			List<String> boostClassificationOrIdList = searchInfo.getBoostClassificationOrIdList();
			if (boostClassificationOrIdList != null && boostClassificationOrIdList.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (String id : boostClassificationOrIdList) {
					if (sb.length() > 0) {
						sb.append(";");
					}
					sb.append(id);
				}
				b.addParameter("bcid", sb.toString());
			}
			// pagination Info
			Integer page = searchInfo.getPage();
			if (page != null && page != 0) {
				b.addParameter("p", page.toString());
			}
			
			Integer pageSize = searchInfo.getPageSize();
			if (pageSize != null && pageSize != 0) {
				b.addParameter("ps", pageSize.toString());
			}
			
			//sort
			String sortOption = searchInfo.getSortOption();
			if (StringUtils.isNotBlank(searchText)) {
				b.addParameter("so", sortOption);
			}
			
			String randomSortId = searchInfo.getRandomSortId();
			if (StringUtils.isNotBlank(randomSortId)) {
				b.addParameter("rs", randomSortId);
			}
	
			//spatial info
			String pc = searchInfo.getPc();
			if (StringUtils.isNotBlank(pc)) {
				b.addParameter("pc", pc);
			}
			
			Double latitude = searchInfo.getLatitude();
			if (latitude != null && latitude != 0) {
				b.addParameter("lat", latitude.toString());
			}
			
			Double longitude = searchInfo.getLongitude();
			if (longitude != null && longitude != 0) {
				b.addParameter("lng", longitude.toString());
			}
			
			String radius = searchInfo.getRadius();
			if (StringUtils.isNotBlank(radius)) {
				b.addParameter("r", radius);
			}
			
			//dates
			String dateFrom = searchInfo.getDateFrom();
			if (StringUtils.isNotBlank(dateFrom)) {
				b.addParameter("df", dateFrom);
			}
			
			String dateTo = searchInfo.getDateTo();
			if (StringUtils.isNotBlank(dateTo)) {
				b.addParameter("dt", dateTo);
			}
			
			List<String> dates = searchInfo.getDates();
			if (dates != null && dates.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (String id : dates) {
					if (sb.length() > 0) {
						sb.append(";");
					}
					sb.append(id);
				}
				b.addParameter("dates", sb.toString());
			}
			
			// price
			Double priceFrom = searchInfo.getPriceFrom();
			if (priceFrom != null && priceFrom != 0) {
				b.addParameter("pf", dfPrice.format(priceFrom));
			}
			
			Double priceTo = searchInfo.getPriceTo();
			if (priceTo != null && priceTo != 0) {
				b.addParameter("pt", dfPrice.format(priceFrom));
			}
			
			// atoz
			boolean atoz = searchInfo.isAtozSearch();
			if (atoz) {
				b.addParameter("atoz", "t");
			}
			
			// saMonitor
			boolean saMonitor = searchInfo.isSaMonitor();
			if (saMonitor) {
				b.addParameter("sa", "t");
			}
			
			String url = b.build().toString(); 
			return 	url;
		} catch (Exception e) {
			m_log.error(e.getCause());
			return null;
		}
	}
}

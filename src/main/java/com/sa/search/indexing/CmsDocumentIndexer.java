package com.sa.search.indexing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sa.search.api.cms.json.CmsDocument;
import com.sa.search.api.cms.json.packets.MetaDataPacket;
import com.sa.search.solrsearch.SolrConfig;

@Component
class CmsDocumentIndexer extends BaseIndexer {
	
	private static Log m_log = LogFactory.getLog(CmsDocumentIndexer.class);
	private Iterable<CmsDocument> cmsDocs;

		
	public CmsDocumentIndexer(){
	}
	
	public void init(SolrConfig solrConfig, Iterable<CmsDocument> cmsDocs, boolean bDeleteOthers){
		super.init(solrConfig, bDeleteOthers);
		this.cmsDocs = cmsDocs;
	}
	
	public void run() {
		
		/*List<Context> contexts = contextDAO.findAllEnabled();
		for(Context c : contexts) {
			contextList.put(c.getName(), c.getId());
		}*/
		
		for (CmsDocument cmsDoc : cmsDocs) {
			try {
			
				String cmsDocType = StringUtils.isEmpty(cmsDoc.getDocType()) ? "CMS_Generic" : cmsDoc.getDocType();
				
				m_log.debug("Indexing cmsDoc " + cmsDocType + cmsDoc.getUmbNodeId());
				
				HashMap<String,Object> docData = new HashMap<String,Object>();
	
				if (StringUtils.isNotBlank(cmsDoc.getId())) {
					docData.put("id", cmsDoc.getId());
				} else {
					docData.put("id", cmsDocType + cmsDoc.getUmbNodeId().toString());
				}
				docData.put("docType", cmsDocType);
				//docData.put("enabled", "true");
				
				String title = deHTML(cmsDoc.getPageTitle());
				docData.put("title", title);
				
				String content = deHTML(cmsDoc.getPageContent());
				if (StringUtils.isNotBlank(content)) {
					docData.put("content", content);
				}
				
				String pageUrl = cmsDoc.getPageUrl();
				if (StringUtils.isNotBlank(pageUrl)) {
					docData.put("pageUrl", pageUrl);
				}
				
				String mimetype = cmsDoc.getMimeType();
				if (StringUtils.isNotBlank(mimetype)) {
					docData.put("mimetype", mimetype);
				}
				
				String sitesection = cmsDoc.getSiteSection();
				if (StringUtils.isNotBlank(sitesection)) {
					docData.put("sitesection", sitesection);
				}
				
				String imagepath = cmsDoc.getImagePath();
				if (StringUtils.isNotBlank(imagepath)) {
					docData.put("imagepath", imagepath);
				}
				
				// get first letter of product title for atoz search
				String initial = StringUtils.substring(title, 0, 1);
				
				if (StringUtils.isNumeric(initial)) {
					docData.put("atoz", "0-9");
				} else if (StringUtils.isAlpha(initial)) {
					docData.put("atoz", initial.toUpperCase());
				} else {
					docData.put("atoz", "@");
				}
				
				// location
				if (cmsDoc.getLatitude() != null && cmsDoc.getLongitude() != null) {
					docData.put("location", cmsDoc.getLatitude().toString() + "," + cmsDoc.getLongitude().toString());
				}

				// contexts
				List<String> contextIDs = new ArrayList<String>();
				List<String> contexts = cmsDoc.getContexts();
				if (contexts != null && contexts.size() > 0 ) {
					for (String context : contexts) {
						if (context != null) {
							contextIDs.add(context);
						}
					}
 				} 
			
				int suffix = 1;
				for (String contextId : contextIDs) {
					docData.put("contextGroup." + suffix, contextId);	
					suffix++;
				}

				// classifications
				StringBuffer sbClassifications = new StringBuffer();
				List<String> andClassifications = cmsDoc.getAndClassifications();
				if (andClassifications != null && andClassifications.size() > 0) {
					addCategories("categoryAndTree", andClassifications, docData);
				
					for (String catId : andClassifications) {
						if (sbClassifications.length() > 0) {
							sbClassifications.append(" ");
						}
						sbClassifications.append(catId);
						Pattern pattern = Pattern.compile("\\s");
						Matcher matcher = pattern.matcher(catId);
						boolean found = matcher.find();
						if (found) {
							sbClassifications.append(" ");
							sbClassifications.append(catId.replaceAll("\\s",""));
						}
					}
				}
				
				List<String> orClassifications = cmsDoc.getOrClassifications();
				if (orClassifications != null && orClassifications.size() > 0) {
					addCategories("categoryOrTree", orClassifications, docData);
				
					for (String catId : orClassifications) {
						if (sbClassifications.length() > 0) {
							sbClassifications.append(" ");
						}
						sbClassifications.append(catId);
						Pattern pattern = Pattern.compile("\\s");
						Matcher matcher = pattern.matcher(catId);
						boolean found = matcher.find();
						if (found) {
							sbClassifications.append(" ");
							sbClassifications.append(catId.replaceAll("\\s",""));
						}
					}
				}
				
				if (sbClassifications.length() > 0) {
					docData.put("classification", sbClassifications.toString());
				}
				
				// dates
				if (cmsDoc.getDateFrom() != null) {
					addDates(cmsDoc.getDateFrom(), cmsDoc.getDateTo(), docData);
				} else {
					List<Date> dates = cmsDoc.getDates();
					if (dates != null && dates.size() > 0) {
						addDates(dates, docData);
					}
				}
				
				// prices
				Double priceFrom = cmsDoc.getPriceFrom();
				Double priceTo = cmsDoc.getPriceTo();
				if (priceFrom!= null) {
					docData.put("priceFrom", priceFrom);
					if (priceTo!= null) {
						docData.put("priceTo", priceTo);
					}
				}
				
				docData.put("smartSuggest", cmsDoc.getSmartSuggest());

				
				addDocToIndex(docData);
			}
			catch(Exception e) {
				m_log.error("Error indexing CMS document " + cmsDoc.getUmbNodeId());
			}
		}
		
		if (bDeleteOthers){
			deleteOldDocuments("+type:CMS_*");
		}
		
		m_log.info("Indexing complete");
	}

	/*private void addChildren(List<Long> catIDs, Long parentID) {
		
		Category category = categoryDAO.findOne(parentID);
		if (category!=null) {
			List<Category> childCategories = categoryDAO.getChildCategories(category);
			if (childCategories!=null) {
			
				for (Category childCat : childCategories) {
					
					catIDs.add(childCat.getId());
					
				}
			
			}
		}
	}*/
	
	public void removeCmsContent(CmsDocument cmsDoc) {
		
		String cmsDocType = StringUtils.isEmpty(cmsDoc.getDocType()) ? "CMS_Generic" : cmsDoc.getDocType();
		String solrId = cmsDocType + cmsDoc.getUmbNodeId().toString();
		
		String deleteQuery = "id:" + solrId;
		try {
			m_log.info("Deleting CMS content - " + deleteQuery );
			SolrClient solrClient = solrConfig.getSolrClient();
			solrClient.deleteByQuery(deleteQuery);
			solrClient.commit();
		} catch (Exception e) {
			m_log.error("Failure executing delete query " + deleteQuery, e);
		} 
		
	}
}

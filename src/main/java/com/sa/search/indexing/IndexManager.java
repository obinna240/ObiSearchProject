package com.sa.search.indexing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sa.search.indexing.CmsDocumentIndexer;
import com.sa.search.solrsearch.SolrConfig;
import com.sa.search.api.cms.json.CmsDocument;



public class IndexManager {
	private static final String SOLR_DATE_PATTERN = "yyyy-MM-dd'T'kk:mm:ss'Z'";
	
	private static Log m_log = LogFactory.getLog(IndexManager.class);
	
	@Autowired
	@Qualifier("MainSolrIndex")
	private SolrConfig solrConfig;
	@Autowired private CmsDocumentIndexer cmsDocumentIndexer;


	public IndexManager(){
	}
	
	public void indexCmsDocument(CmsDocument doc) {
		List<CmsDocument> docList = new ArrayList<CmsDocument>();
		docList.add(doc);
		indexCmsDocuments(docList, false);
	}

	public void indexCmsDocumentList(List<CmsDocument> docList) {
		indexCmsDocuments(docList, false);
	}
	public void indexAllCms(){
		/*SystemConfig config = getSystemConfig();
		String url = config.getCmsConfig().getCmsUrl() + config.getCmsConfig().getCmsReIndexUrl();
		HttpMethod  get = new GetMethod(url);
		HttpClient httpClient = new HttpClient();
		int response;
		try {
			m_log.debug("Reindexing all CMS content.");
			response = httpClient.executeMethod(get);
			m_log.info("CMS content reindex response : "+response);
		} catch (HttpException e) {
			m_log.error(e);
		} catch (IOException e) {
			m_log.error(e);
		}*/

	}

	public void removeCmsContent(CmsDocument cmsDoc) {
		m_log.debug("Removing CMS content based on unpublish event.");
		cmsDocumentIndexer.init(solrConfig, null, false);
		cmsDocumentIndexer.removeCmsContent(cmsDoc);
	}
	
	public void removeDocument(String id) {
		m_log.debug("deleting document");
		cmsDocumentIndexer.init(solrConfig, null, false);
		cmsDocumentIndexer.deleteDocument(id);
	}


	private synchronized void indexCmsDocuments(Iterable<CmsDocument> cmsDocs, boolean bDeleteOthers){

		if (cmsDocs == null){
			return;
		}

		cmsDocumentIndexer.init(solrConfig, cmsDocs, bDeleteOthers);
		cmsDocumentIndexer.startIndexing();
	}
}

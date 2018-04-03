package com.sa.search.solrsearch;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;

import com.sa.search.config.SearchConstants;
import com.sa.search.db.mongo.dao.RecommendedLinkDAO;
import com.sa.search.db.mongo.dao.SystemConfigDAO;
import com.sa.search.db.mongo.model.RecommendedLink;
import com.sa.search.db.mongo.model.SystemConfig;
import com.sa.search.solrsearch.ResultPagination.ResultPaginationEntry;
import com.sa.search.util.DataHelper;

public class BaseSearchBean {

	private static Log m_log = LogFactory.getLog(BaseSearchBean.class);
	private SolrConfig solrConfig;
	private SolrClient solrClient;
	
	@Autowired private RecommendedLinkDAO recommendedLinkDAO;
	@Autowired private SystemConfigDAO systemConfigDAO;
	
	public SolrClient getSolrClient(){

		if (solrClient == null){
			try {
				
				m_log.info("Connecting to search server at " + solrConfig.getSolrURL());
				//solrClient = new HttpSolrClient(solrConfig.getQueryURL());
				solrClient = new HttpSolrClient.Builder(solrConfig.getQueryURL()).build();
				m_log.info("Connection successful" +solrConfig.getQueryURL());
				
			} catch (Exception e) {
				m_log.error("Search system initilisation failed");
				solrClient = null;//this will force a retry next time round
			}
		}
		return this.solrClient;
	}

	public void setSolrConfig(SolrConfig solrConfig) {
		this.solrConfig = solrConfig;
	}

	protected QueryResponse makeSolrQuery(SolrQuery q) throws SolrServerException, IOException {
		long t1 = System.currentTimeMillis();
		m_log.debug("Performing Solr search: " + URLDecoder.decode(q.toString(), "UTF-8"));
		QueryResponse response = getSolrClient().query(q);
		m_log.debug("Search took " + (System.currentTimeMillis() - t1) + "ms");
		return response;
	}


	public SolrConfig getSolrConfig() {
		return solrConfig;
	}

	public void setSolrClient(SolrClient solrClient) {
		this.solrClient = solrClient;
	}

	protected void populatePaginationInfo(SearchInfo searchInfo, ISearchResult result, Integer page, Integer pageSize, SolrDocumentList solrResultPageDocs) {
	
		int totalResults = solrResultPageDocs == null? 0 : (int) solrResultPageDocs.getNumFound();
		result.setTotalResults(totalResults);
		
		ResultPagination resultPagination = new ResultPagination();
		
		resultPagination.setCurrentPage(page);
		resultPagination.setPageSize(pageSize);
		int lastPage = (totalResults / pageSize) + ((totalResults % pageSize) > 0 ? 1 : 0);
		resultPagination.setLastPage(lastPage);

		int firstNavPage = Math.max(page - SearchConstants.NAV_PAGE_COUNT / 2, 1);
		int lastNavPage = Math.min(lastPage, firstNavPage + SearchConstants.NAV_PAGE_COUNT - 1);
		resultPagination.setFirstNavPage(firstNavPage);
		resultPagination.setLastNavPage(lastNavPage);

		int firstResult = ((page - 1) * pageSize) + 1;
		resultPagination.setFirstResult(firstResult);
		int lastResult = Math.min(firstResult + pageSize - 1, totalResults);
		resultPagination.setLastResult(lastResult);
		
		ResultPaginationEntry resultPaginationEntry = resultPagination. new ResultPaginationEntry();
		resultPaginationEntry.setPage(firstResult);
		String jsonLink = generatePaginationEncodedLink(searchInfo, firstNavPage);
		resultPaginationEntry.setEncodedUrl(jsonLink);
		String urlLink = generatePaginationUrlLink(searchInfo, firstNavPage);
		resultPaginationEntry.setParamUrl(urlLink);
		resultPagination.setFirstNavPageEntry(resultPaginationEntry);
		

		resultPaginationEntry = resultPagination. new ResultPaginationEntry();
		resultPaginationEntry.setPage(lastResult);
		jsonLink = generatePaginationEncodedLink(searchInfo, lastNavPage);
		resultPaginationEntry.setEncodedUrl(jsonLink);
		urlLink = generatePaginationUrlLink(searchInfo, lastNavPage);
		resultPaginationEntry.setParamUrl(urlLink);
		resultPagination.setLastNavPageEntry(resultPaginationEntry);
	
		List<ResultPaginationEntry> entries = resultPagination.getEntries();
		
		for (int count = firstNavPage; count <= lastNavPage; count ++) {
			resultPaginationEntry = resultPagination. new ResultPaginationEntry();
			resultPaginationEntry.setPage(count);
			
			jsonLink = generatePaginationEncodedLink(searchInfo, count);
			resultPaginationEntry.setEncodedUrl(jsonLink);
			urlLink = generatePaginationUrlLink(searchInfo, count);
			resultPaginationEntry.setParamUrl(urlLink);

			entries.add(resultPaginationEntry);
		}
		
		result.setResultPagination(resultPagination);
	}
	
	private String generatePaginationEncodedLink(SearchInfo searchInfo, Integer page) {
		
		if (searchInfo != null) {
			
			String json = searchInfo.toJson();
			SearchInfo searchInfoTn  = SearchInfo.fromJson(json);
			searchInfoTn.setPage(page);
			byte[]   bytesEncoded = Base64.encodeBase64(searchInfoTn.toJson().getBytes());
			String encoded = new String(bytesEncoded);
			return encoded;
		}
		
		return null;
	}
	private String generatePaginationUrlLink(SearchInfo searchInfo, Integer page) {
		
		if (searchInfo != null) {
			
			String json = searchInfo.toJson();
			SearchInfo searchInfoTn  = SearchInfo.fromJson(json);
			searchInfoTn.setPage(page);
			String paramUrl = DataHelper.searchInfoToUrl(searchInfoTn);
			return paramUrl;
		}
		return null;
	}
	
	/**
	 * Escape special Solr characters
	 */
	protected String escapeSearchText(String searchText) {
		
		// Escape / remove some items which might nobble the Solr queries
		searchText = searchText.replaceAll(" OR ", " ");
		searchText = searchText.replaceAll(" AND ", " ");
		searchText = searchText.replaceAll(" NOT ", " ");
		
		for (String solrChar : SearchConstants.SOLR_SPECIAL_CHARS) {
			searchText = StringUtils.replace(searchText, solrChar, "\\" + solrChar);
		}
		
		return searchText;
	}
	
	/**
	 * Helper method for building up a search query string 
	 */
	protected void addQueryClause(StringBuilder queryBuff, String clause) {

		if (StringUtils.isNotBlank(clause)){
			if (queryBuff.length() > 0) {
				queryBuff.append(" ");
			}
			queryBuff.append(clause);
		}
	}
	
	/**
	 * Helper method for building up a search query string 
	 */
	protected void addQueryClause(StringBuilder queryBuff, String clause, String operator) {
		if (StringUtils.isNotBlank(clause)){
			if (queryBuff.length() > 1) {
				queryBuff.append(" " + operator + " ");
			}
	
			queryBuff.append(clause);
		}
	}
	
	protected List<String> tokenizeSearchWords(String searchText) {

		List<String> searchWords = new ArrayList<String>();

		searchText = escapeSearchText(searchText);
		// N.B. This will turn " into \", hence the use of \\\" in the quoted
		// phrase code below

		// Individual words - quoted phrases count as a single word
		String[] words = StringUtils.split(searchText);

		if (!StringUtils.contains(searchText, "\"")) {
			searchWords = Arrays.asList(words);
		} else {
			// Quoted terms in the search text
			searchWords = new ArrayList<String>();

			boolean inQuotes = false;
			StringBuffer quotedWord = new StringBuffer();

			for (int i = 0; i < words.length; i++) {

				if (words[i].startsWith("\"")) {

					if (inQuotes) {
						// Already in quotes - this shouldn't happen. We'll
						// count this as the word end
						inQuotes = false;
						quotedWord.append("\"");
						searchWords.add(quotedWord.toString());
						searchWords.add(StringUtils.remove(words[i], "\""));
					} else {
						// Start a new quoted word

						if (i == (words.length - 1)) {
							// End of the text and we're still in quotes -
							// must be mismatched, so ditch quotes
							searchWords.add(StringUtils.remove(words[i], "\""));
						} else {
							inQuotes = true;
							quotedWord = new StringBuffer();
							quotedWord.append(words[i]);
						}
					}

				} else if (inQuotes && words[i].endsWith("\"")) {
					inQuotes = false;
					quotedWord.append(" ");
					quotedWord.append(words[i]);
					searchWords.add(quotedWord.toString());
				} else if (inQuotes) {
					quotedWord.append(" ");
					quotedWord.append(words[i]);

					if (i == (words.length - 1)) {
						// End of the text and we're still in quotes - must
						// be mismatched, so ditch it
						searchWords.add(StringUtils.remove(quotedWord.toString(), "\""));
					}
				} else {
					searchWords.add(words[i]);
				}
			}
		}

		return searchWords;
	}
	
	public String getSolrServerUrl() {
		return this.solrConfig.getQueryURL();
	}
	
	
	protected void setRecommendedLinks(SearchResult result, int pageNum, String searchText) {
		if (searchText != null && pageNum <= 1) {

			List<DisplayItem> recommendedLinkResults  = new ArrayList<DisplayItem>();
		
			searchText = searchText.trim();
			SystemConfig config = systemConfigDAO.getDefaultSystemConfig();
			
			List<RecommendedLink> recommendedLinks = recommendedLinkDAO.findByKeyword(searchText, config.getMaximumRecommendedLinks());
			
			if (recommendedLinks != null && recommendedLinks.size() > 0) {
				
				for (RecommendedLink link : recommendedLinks) {
					
					String keyWord = link.getKeyword();
					
					String[] phrases = StringUtils.split(keyWord, ",");
					
					boolean match = false;
					for (String phrase : phrases) {
						if ((phrase.trim().startsWith("\"") && phrase.trim().endsWith("\"")) || 
								(phrase.trim().startsWith("'") && phrase.trim().endsWith("'"))) {
							//Quoted keyword phrase - only use this link if the whole phrase is matched
							//Remove spaces as these aren't considered relevant for matching
							
							String phraseTrim = phrase.trim();
							String queryText = "";
							if (phraseTrim.startsWith("\"")) {
								queryText = StringUtils.removeStart(phraseTrim, "\"");  
								queryText = StringUtils.removeEnd(queryText, "\"");
							} else {
								queryText = StringUtils.removeStart(phraseTrim, "'");  
								queryText = StringUtils.removeEnd(queryText, "'");
							}
							
							queryText = StringUtils.remove(queryText, " ");
							
							if (!queryText.equalsIgnoreCase(StringUtils.remove(searchText, " "))){
								//Not a phrase match - don't recommend this link
								continue;
							} else {
								match = true;
							}
						}	else {
							// 
							Pattern p = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
						    Matcher m = p.matcher(phrase);
						    if (m.find() == true) {
						    	match = true;
							}
						}
					}
					
					if (match == false) {
						continue;
					}
					
					DisplayItem di = new DisplayItem();
					di.setUrl(link.getUrl());
					di.setTitle(link.getTitle());
					di.setContent(link.getDescription());
					di.setRecommendedLink(true);
					
					recommendedLinkResults.add(di);
				}
			}
		
			//Add recommended links
			
			result.setRecommendedLinks(recommendedLinkResults);
		}
	}
}

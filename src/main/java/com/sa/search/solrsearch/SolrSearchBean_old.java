package com.sa.search.solrsearch;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;

import com.sa.search.config.SearchConstants;
import com.sa.search.db.mongo.dao.FacetDAO;
import com.sa.search.db.mongo.dao.SearchConfigDAO;
import com.sa.search.db.mongo.model.Facet;
import com.sa.search.db.mongo.model.Facet.FacetCategory;
import com.sa.search.solrsearch.ResultFacet_old.ResultFacetEntry_old;
public class SolrSearchBean_old extends BaseSearchBean {
	private static Log m_log = LogFactory.getLog(SolrSearchBean_old.class);

	//@Autowired DaoService daoService;
	@Autowired private SearchConfigDAO searchConfigDAO;
	@Autowired private FacetDAO facetDAO;
	
	public void setSolrConfig(SolrConfig solrConfig) {
		super.setSolrConfig(solrConfig);
	}
	
	public SearchResult getSearchResults(String searchText) {
		SearchResult searchResult = new SearchResult();
		QueryResponse queryResponse = null;
		SolrQuery query = new SolrQuery();
		query.setQuery(searchText);
		try {
			queryResponse = makeSolrQuery(query);

		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SolrDocumentList solrResponseDocs = queryResponse.getResults();
		searchResult.setItems(new ArrayList<DisplayItem>());
		int hitLen = solrResponseDocs.size();
		// Populate pagination & other search metadata
		int totalResults = (int) solrResponseDocs.getNumFound();
		//searchResult.setTotalResults(totalResults);
		searchResult.setSearchText(searchText);

		return searchResult;
	}

	public List<Suggestion> getSuggesterSearchResults(String searchText, Integer maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	public SearchResult getSearchResults(SearchInfo searchInfo, Integer page, Integer pageSize, String sortOption, String facetTotal, Map<String, ArrayList<String>> facetValues, String dateFrom, String dateTo, boolean bRestricted,
			String accessSchools, String accessGroups, String accessRoles) {

		String searchText = searchInfo.getSearchText();
		SearchResult result = new SearchResult();
		SolrQuery q = new SolrQuery();
		String originalSearchText = searchText;

		//
		// Construct search string
		//
		StringBuilder queryBuff = new StringBuilder();
		List<String> searchWords = new ArrayList<String>();

		if (StringUtils.isNotBlank(searchText)) {

			searchWords = tokenizeSearchWords(searchText);

			StringBuilder wordClauseBuff = new StringBuilder();

			for (String word : searchWords) {
				if (StringUtils.isNotBlank(word)) {
					addQueryClause(wordClauseBuff, word, "_QUERY_TYPE_");
				}
			}

			queryBuff.append(String.format(getStandardSearch(), "(" + wordClauseBuff.toString() + ")"));

		} else {
			queryBuff.append("*:*");
			q.addOrUpdateSort("docdate", ORDER.desc);
		}

		// Add any additional constraints (ignore specific doc types etc )
		if (StringUtils.isNotBlank(getSearchConstraints())) {
			queryBuff.append(getSearchConstraints());
		}

		if (bRestricted) {

			StringBuilder rBuff = new StringBuilder();

			if (StringUtils.isNotEmpty(accessSchools)) {
				rBuff.append("(");
				String[] arrSchools = accessSchools.split(",");
				for (int s = 0; s < arrSchools.length; s++) {
					if (s > 0) {
						rBuff.append(" OR ");
					}
					rBuff.append("accesscontrol.school:" + arrSchools[s]);
				}
				rBuff.append(")");
			}

			if (rBuff.length() > 0) {
				rBuff.append(" AND ");
			}

			if (StringUtils.isNotEmpty(accessGroups)) {
				rBuff.append("(");
				String[] arrGroups = accessGroups.split(",");
				for (int s = 0; s < arrGroups.length; s++) {
					if (s > 0) {
						rBuff.append(" OR ");
					}
					rBuff.append("accesscontrol.group:" + arrGroups[s]);
				}
				rBuff.append(")");
			}

			if (rBuff.length() > 0) {
				rBuff.append(" AND ");
			}

			if (StringUtils.isNotEmpty(accessRoles)) {
				rBuff.append("(");
				String[] arrRoles = accessRoles.split(",");
				for (int s = 0; s < arrRoles.length; s++) {
					if (s > 0) {
						rBuff.append(" OR ");
					}
					rBuff.append("accesscontrol.role:" + arrRoles[s]);
				}
				rBuff.append(")");
			}

			// TW add in additional override which bypasses the accesscontrol
			// groups
			// so that the result can be returned to the front end and a message
			// displayed to the end user instead
			q.addFilterQuery("(" + rBuff.toString() + ") OR (noaccess:true)");
		}

		String mainQuery = queryBuff.toString();

		// highlighting
		int snippetLength = searchConfigDAO.findSnippetLength(250);
		boolean highlighting = false;

		if (StringUtils.isNotBlank(searchText)) {
			q.setHighlight(true);
			q.setHighlightSnippets(1);

			q.setHighlightFragsize(snippetLength);
			q.setParam("hl.fl", "title, content, relatedcontent");
			q.setParam("hl.simple.pre", "<strong>");
			q.setParam("hl.simple.post", "</strong>");
			highlighting = true;
		}

		// Page (counting starts at 1)
		if (page == null || page < 1) {
			page = 1;
		}

		if (pageSize == null || pageSize < 1) {
			pageSize = SearchConstants.DEFAULT_PAGE_SIZE;
		}

		// Facetting
		if (facetValues.size() > 0) {

			q.setFacet(true);
			q.addFacetField("docdate");
			q.add("f.docdate.facet.missing", "true");

			Set<String> facetNames = facetValues.keySet();
			for (String facetName : facetNames) {

				// Apply any selected facet filters. Filters are tagged so that
				// they can be excluded from subsequent facet counts
				ArrayList<String> arrFacetValues = facetValues.get(facetName);

				if (facetName.equals("docdate")) {

					if (arrFacetValues != null && arrFacetValues.size() > 0) {

						// date facet is single select only
						String facetValue = arrFacetValues.get(0);

						if (StringUtils.isNotBlank(facetValue) && !"all".equalsIgnoreCase(facetValue)) {

							if (facetValue.equalsIgnoreCase("custom")) {

								if (StringUtils.isNotEmpty(dateFrom) && StringUtils.isNotEmpty(dateTo)) {

									String solrDateFrom = parseSolrDate(dateFrom, SearchConstants.START_DAY_TIME);
									String solrDateTo = parseSolrDate(dateTo, SearchConstants.END_DAY_TIME);

									q.addFilterQuery("{!tag=" + facetName + "}" + facetName + ":[" + solrDateFrom + " TO " + solrDateTo + "]");
								}
							} else {
								if ("THISMONTH".equalsIgnoreCase(facetValue)) {
									q.addFilterQuery("{!tag=" + facetName + "}" + facetName + ":[NOW/MONTH TO NOW/MONTH+1MONTHS]");
								} else if ("LASTMONTH".equalsIgnoreCase(facetValue)) {
									q.addFilterQuery("{!tag=" + facetName + "}" + facetName + ":[NOW/MONTH-1MONTHS TO NOW/MONTH]");
								}
								// Sticking these boys in for future use
								else if ("THISYEAR".equalsIgnoreCase(facetValue)) {
									q.addFilterQuery("{!tag=" + facetName + "}" + facetName + ":[NOW/YEAR TO NOW/MONTH+1MONTHS]");
								} else if ("LASTYEAR".equalsIgnoreCase(facetValue)) {
									q.addFilterQuery("{!tag=" + facetName + "}" + facetName + ":[NOW/YEAR-1YEARS TO NOW/YEAR]");
								} else {
									q.addFilterQuery("{!tag=" + facetName + "}" + facetName + ":[NOW/MONTH+1MONTHS-" + facetValue + " TO NOW/MONTH+1MONTHS]");
								}

							}
						}
					} else {
						// q.addFacetField("" + facetName);
					}
					// facet=true&facet.range=docdate&facet.range.start=NOW-6MONTHS&facet.range.end=NOW&facet.range.gap=%2B3MONTHS
					// facetValue is expected to be a Solr-compatible date range
					// variable, e.g. NOW, 28DAYS etc
					q.add("facet.range", "{!ex=docdate}docdate");
					q.add("facet.range.start", "NOW/MONTH-2YEARS");
					q.add("facet.range.end", "NOW/MONTH+1MONTHS");
					q.add("facet.range.gap", "+1MONTHS");
					q.add("facet.range.other", "all");
				} else {

					if (arrFacetValues != null && arrFacetValues.size() > 0) {
						Boolean filterFound = false;
						String filterQuery = "";
						for (String filterValue : arrFacetValues) {
							if (filterFound) {
								filterQuery += " OR " + facetName + ":\"" + filterValue + "\"";
							} else {
								filterQuery = "{!tag=" + facetName + "}" + facetName + ":\"" + filterValue + "\"";
							}
							filterFound = true;
						}

						if (filterFound) {
							q.addFilterQuery(filterQuery);
							q.addFacetField("{!ex=" + facetName + "}" + facetName);
						} else {
							q.addFacetField("" + facetName);
						}

					} else {
						q.addFacetField("" + facetName);
					}
				}

			}

		}

		// spelling checker
		StringBuilder spellCheckBuff = new StringBuilder();
		spellCheckBuff.append(searchText);

		if (spellCheckBuff.length() > 0) {
			q.add("spellcheck", "true");
			q.add("spellcheck.q", spellCheckBuff.toString());
			q.add("spellcheck.collate", "true");
		}

		q.setStart((page - 1) * pageSize);
		q.setRows(pageSize);
		
		//
		// Get the results
		//
		QueryResponse solrResponse = null;
		SolrDocumentList solrResultPageDocs = null;

		try {
			// Do the query
			q.setQuery(StringUtils.replace(mainQuery, "_QUERY_TYPE_", "AND"));
			solrResponse = makeSolrQuery(q);
			solrResultPageDocs = solrResponse.getResults();

			// do we have any results? If not then modify query and try again
			if (solrResultPageDocs.size() == 0) {
				// repeat search but with OR operator
				q.setQuery(StringUtils.replace(mainQuery, "_QUERY_TYPE_", "OR"));
				solrResponse = makeSolrQuery(q);
				solrResultPageDocs = solrResponse.getResults();
			}

			//
			// Result processing
			//
			processSearchResults(searchText, page, pageSize, sortOption, result, originalSearchText, snippetLength, highlighting, solrResponse, solrResultPageDocs, false, facetTotal, facetValues);

		} catch (Exception t) {
			// Log it and carry on. This means that we will get 0 search
			// results, but at least it won't blow up

			String params = "?";
			try {
				params = String.format("(%s, %s, %s, %s)", searchText, page, pageSize, sortOption);
			} catch (Throwable t2) {
			}

			m_log.error("SearchResults search failed. Params = " + params, t);
		}

		// Do additional geo search if we think that the user is looking for one
		// of our known feed types
		// result.setGeoSearchResult(geoSearchBean.phraseSearch(originalSearchText,
		// null, 3));

		return result;
	}

	@SuppressWarnings("unchecked")
	private void processSearchResults(String searchText, Integer page, Integer pageSize, String sortOption, SearchResult result, String originalSearchText, int snippetLength, boolean highlighting, QueryResponse solrResponse,
			SolrDocumentList solrResultPageDocs, boolean advancedSearch, String facetTotal, Map<String, ArrayList<String>> facetValues) {

		// Extract facet counts
		List<FacetField> resultFacets = solrResponse.getFacetFields();

		long nullDocDateCount = 0;

		if (resultFacets != null) {

			// Get docdate facets with no value. Used later in the ALL value for
			// docdate
			for (FacetField facetField : resultFacets) {

				if ("docdate".equalsIgnoreCase(facetField.getName())) {
					List<Count> values = facetField.getValues();
					for (Count count : values) {
						if (StringUtils.isEmpty(count.getName())) {
							nullDocDateCount = count.getCount();
						}
					}
				}

			}

			for (FacetField facetField : resultFacets) {

				// Get name mappings from db
				Facet f = facetDAO.findById(facetField.getName());

				if (f != null && f.isEnabled()) {
					long totalCount = 0;
					ResultFacet_old displayFacet = new ResultFacet_old(f.getId(), f.getDisplayName(), f.getDisplayOrder());

					String facetURLParams = "";
					/*
					 * if (facetValues != null){ Set<String> facetNames =
					 * facetValues.keySet(); for (String facetName : facetNames)
					 * { if (!facetName.equals(facetField.getName())){
					 * facetURLParams += "&" + facetName + "=" +
					 * facetValues.get(facetName); } } }
					 */

					List<Count> values = facetField.getValues();
					for (Count count : values) {
						String name = count.getName();
						String displayName = name;
						int displayOrder = -1;

						for (FacetCategory facetCategory : f.getCategories()) {
							if (facetCategory.getId().equals(name)) {
								displayName = facetCategory.getDisplayName();
								displayOrder = facetCategory.getDisplayOrder();
								break;
							}
						}
						ResultFacetEntry_old facetEntry = displayFacet.new ResultFacetEntry_old(name, displayName, count.getCount(), displayOrder, facetURLParams + "&" + facetField.getName() + "=" + name);

						if (count.getCount() > 0)
							displayFacet.getEntries().add(facetEntry);

						totalCount += count.getCount();
					}

					ResultFacetEntry_old facetEntry = displayFacet.new ResultFacetEntry_old("all", "All", totalCount, 0, facetURLParams + "&" + facetField.getName() + "=all");
					// displayFacet.getEntries().add(facetEntry);

					Collections.sort(displayFacet.getEntries(), new Comparator() {
						public int compare(Object o1, Object o2) {
							ResultFacetEntry_old r1 = (ResultFacetEntry_old) o1;
							ResultFacetEntry_old r2 = (ResultFacetEntry_old) o2;
							return new Long(r1.getDisplayOrder()).compareTo(new Long(r2.getDisplayOrder()));
						}
					});

					//result.getResultFacets().add(displayFacet);
				}
			}
		}

		// Extract facet range for docdate

		// Range facets - treat docdate as a special case
		// We're expecting monthly counts
		List<RangeFacet> facetRanges = solrResponse.getFacetRanges();

		if (facetRanges != null) {
			for (RangeFacet rangeFacet : facetRanges) {

				Facet f = facetDAO.findById(rangeFacet.getName());

				if (f != null && f.isEnabled()) {

					if (rangeFacet.getName().equals("docdate")) {

						ResultFacet_old displayFacet = new ResultFacet_old(f.getId(), f.getDisplayName(), f.getDisplayOrder());

						// Gather monthly counts as an incrementing figure going
						// backwards, so that we can display results 'in the
						// last x months'
						Map<String, Long> countMap = new HashMap<String, Long>();
						List<org.apache.solr.client.solrj.response.RangeFacet.Count> counts = rangeFacet.getCounts();

						String countKey = "MONTHS";
						long total = 0;

						// Having to fudge this routine to compensate for solr
						// date
						for (int lastNMonths = 1; lastNMonths <= counts.size(); lastNMonths++) {
							org.apache.solr.client.solrj.response.RangeFacet.Count count = counts.get(counts.size() - lastNMonths);
							total += count.getCount();
							if (lastNMonths == 1) {
								countMap.put("THISMONTH", new Long(count.getCount()));
							} else if (lastNMonths == 2) {
								countMap.put("LASTMONTH", new Long(count.getCount()));
							} else if (lastNMonths == counts.size()) {
								// Ah, but hang on a sec. Need to grab results
								// outside of the range plud those with a null
								// value.
								long ultimateTotal = total;
								ultimateTotal += rangeFacet.getBefore().longValue() + rangeFacet.getAfter().longValue();
								ultimateTotal += nullDocDateCount;
								countMap.put("ALL", ultimateTotal);
							} else {
								countMap.put((lastNMonths) + countKey, total);
							}
						}
						/*
						 * for (int lastNMonths = 1; lastNMonths <=
						 * counts.size(); lastNMonths ++){
						 * org.apache.solr.client.solrj.response.RangeFacet.
						 * Count count = counts.get(counts.size() -
						 * lastNMonths); total += count.getCount();
						 * countMap.put(lastNMonths + countKey, total); }
						 */

						// Add display facets
						String facetURLParams = "";
						if (facetValues != null) {
							Set<String> facetNames = facetValues.keySet();
							for (String facetName : facetNames) {
								if (!facetName.equals("docdate")) {
									facetURLParams += "&" + facetName + "=" + facetValues.get(facetName);
								}
							}
						}
						ResultFacetEntry_old facetEntry = displayFacet.new ResultFacetEntry_old("all", "All", countMap.get("ALL"), 0, facetURLParams + "&" + f.getId() + "=all");
						displayFacet.getEntries().add(facetEntry);

						List<FacetCategory> categories = f.getCategories();
						for (FacetCategory facetCategory : categories) {
							facetEntry = displayFacet.new ResultFacetEntry_old(facetCategory.getId(), facetCategory.getDisplayName(), countMap.get(facetCategory.getId()), facetCategory.getDisplayOrder(),
									facetURLParams + "&" + f.getId() + "=" + facetCategory.getId());
							displayFacet.getEntries().add(facetEntry);
						}

						Collections.sort(displayFacet.getEntries(), new Comparator() {
							public int compare(Object o1, Object o2) {
								ResultFacetEntry_old r1 = (ResultFacetEntry_old) o1;
								ResultFacetEntry_old r2 = (ResultFacetEntry_old) o2;
								return new Long(r1.getDisplayOrder()).compareTo(new Long(r2.getDisplayOrder()));
							}
						});

						//result.getResultFacets().add(displayFacet);
					}
				}
			}
		}

		// Sort the facet for display
//		Collections.sort(result.getResultFacets(), new Comparator() {
//			public int compare(Object o1, Object o2) {
//				ResultFacet r1 = (ResultFacet) o1;
//				ResultFacet r2 = (ResultFacet) o2;
//				return new Long(r1.getDisplayOrder()).compareTo(new Long(r2.getDisplayOrder()));
//			}
//		});

		// Populate result list
		result.setItems(new ArrayList<DisplayItem>());
		int hitLen = solrResultPageDocs == null ? 0 : solrResultPageDocs.size();

		for (int i = 0; i < hitLen; i++) {
			SolrDocument doc = solrResultPageDocs.get(i);
			DisplayItem di = new DisplayItem();

			populateDisplayItem(di, doc, solrResponse, snippetLength, highlighting);

			result.getItems().add(di);

		}

		if (!advancedSearch) {

			StringBuilder suggestionBuff = new StringBuilder();
			SpellCheckResponse spellingResponse = solrResponse.getSpellCheckResponse();
			if (spellingResponse != null) {

				List<Suggestion> suggestions = spellingResponse.getSuggestions();
				String collatedResult = spellingResponse.getCollatedResult();

				if (StringUtils.isNotBlank(collatedResult)) {

					suggestionBuff.append(collatedResult);
					suggestionBuff.append(" ");

				} else {

					if (suggestions.size() > 0) {
						for (Suggestion suggestion : suggestions) {
							String suggestedWord = suggestion.getAlternatives().get(0);
							suggestionBuff.append(suggestedWord);
							suggestionBuff.append(" ");
						}
					}

				}

				//result.setSpellingSuggestion(suggestionBuff.toString());

			}
		}

		// Populate pagination & other search metadata
		result.setSearchText(originalSearchText);
		// result.setSearchSection(searchSection);
		// result.setSearchContentType(searchContentType);
		result.setSortOption(sortOption);

		populatePaginationInfo(null, result, page, pageSize, solrResultPageDocs);
		result.setSearchType(advancedSearch ? "advanced" : "simple");
	}



	public List<String> getAutocompleteSuggestions(String searchText, Integer maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	public DisplayItem getResultByURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}



	public String getStandardSearch() {
		return searchConfigDAO.getStandardSearchTemplate();
	}

	public String getSearchConstraints() {
		return searchConfigDAO.getSearchConstraintsTemplate();
	}

	public String getSmartSuggest() {
		return searchConfigDAO.getSmartSuggestTemplate();
	}

	private String parseSolrDate(String date, String timeSuffix) {

		try {
			Date parsedInputDate = SearchConstants.DATE_INPUT_FORMAT.parse(date);
			String formattedOutputDate = SearchConstants.SOLR_QUERY_DATE_FORMAT.format(parsedInputDate);
			return formattedOutputDate + timeSuffix;

		} catch (ParseException e) {
			return "*"; // Solr date wildcard
		}
	}

	private void populateDisplayItem(DisplayItem di, SolrDocument doc, QueryResponse solrResponse, int snippetLength, boolean highlighting) {

		SimpleDateFormat sdfResults = new SimpleDateFormat("dd-MM-yyyy");
		Map<String, Map<String, List<String>>> highlightMap = solrResponse.getHighlighting();

		//List idList = (List) doc.getFieldValue("id");
		String id = (String) doc.getFieldValue("id");

		di.setId(id);

		// XXX TODO config this
		String url = (String) doc.getFieldValue("pageUrl");

		if (StringUtils.isNotEmpty(url) && url.contains("~/media")) {

			String protocol = StringUtils.substringBefore(url, "//");
			String path = StringUtils.substringAfter(url, "//");
			String domain = StringUtils.substringBefore(path, "/");
			String mediaPath = StringUtils.substringAfter(path, "~");
			url = protocol + "//" + domain + "/~" + mediaPath;
		}
		// XXX

		di.setUrl(url);
//		di.setMimetype((String) doc.getFieldValue("mimetype"));
//		di.setSitesection((String) doc.getFieldValue("sitesection"));
//		di.setContentType((String) doc.getFieldValue("contenttype"));
//		di.setScore((Float) doc.getFieldValue("score"));

		// Ensure that we have a title
		String title = (String) doc.getFieldValue("title_display");
		List<String> highlightSnippets = new ArrayList<String>();

		if (highlighting && highlightMap != null && highlightMap.get(url) != null) {

			highlightSnippets = solrResponse.getHighlighting().get(url).get("content");

			// No content snippets, try related content (= text from
			// associated PDFs e.g. for Fact Files)
			if (highlightSnippets == null) {
				highlightSnippets = solrResponse.getHighlighting().get(url).get("relatedcontent");
			}

			if (highlightSnippets == null) {
				// OK, just the first chunk of content then.
				String content = (String) doc.getFieldValue("description_display");
				highlightSnippets = new ArrayList<String>();
				highlightSnippets.add(StringUtils.substring(content, 0, snippetLength));
			}

			// Highlights in title
			List<String> highlightSnippetsTitle = solrResponse.getHighlighting().get(url).get("title");

			if (highlightSnippetsTitle != null) {
				title = highlightSnippetsTitle.get(0);
			}

		} else {

			// No highlight snippets (probably due to blank search) - use first
			// chunk of content
			String content = (String) doc.getFieldValue("description_display");
			di.setContent(content);
			highlightSnippets = new ArrayList<String>();
			highlightSnippets.add(StringUtils.substring(content, 0, snippetLength));

		}

		List<String> cleanedHighlightSnippets = new ArrayList<String>();

		for (String snippet : highlightSnippets) {

			if (snippet!=null) {
				// Remove any non-ASCII chars
				String cleanSnippet = snippet.replaceAll("\\u00A0", " ").replaceAll("[^\\x00-\\x7F]", "");
	
				// Try to start on a word rather than some punctuation
				if (cleanSnippet.length() > 0 && !Character.isLetterOrDigit(cleanSnippet.charAt(0)) && !(cleanSnippet.charAt(0) == '<')) {
					cleanSnippet = StringUtils.substringAfter(cleanSnippet, " ");
				}
	
				cleanedHighlightSnippets.add(cleanSnippet);
			}

		}

		di.setHighlightSnippetsContent(cleanedHighlightSnippets);

		// p.setImagePath((String)doc.getFieldValue("imagepath"));
		di.setSnippetLength(snippetLength);

//		if (StringUtils.isBlank(title)) {
//			title = getBestAnchor(doc);
//		} else if (StringUtils.equals(di.getMimetype(), "application/pdf")) {
//			// eLog 11726, 11662
//			// Avoid pdf titles like 'untitled' and
//			// '\376\377\000P\000r\000o\000g\000r\000a\000m\0001\0001\000-\0001\0002\000-\000o\000u\000t\000e\000r\0'
//
//			if (StringUtils.endsWithIgnoreCase(title, "untitled") || StringUtils.countMatches(title, "\\") > 5) {
//				title = getBestAnchor(doc);
//			}
//		}
//
//		di.setTitle(title);
//
//		if (doc.getFieldValue("docdate") != null) {
//			di.setDate(sdfResults.format((Date) doc.getFieldValue("docdate")));
//		}

	}

	private String getBestAnchor(SolrDocument doc) {
		String bestAnchor = null;

		ArrayList<String> anchors = (ArrayList<String>) doc.getFieldValue("anchor");

		if (anchors != null && anchors.size() > 0) {

			// Choose first non-blank anchor
			for (String anchor : anchors) {
				if (StringUtils.isNotBlank(anchor) && StringUtils.length(anchor) > 2) {
					bestAnchor = anchor;
					break;
				}
			}
		}

		if (bestAnchor == null) {
			bestAnchor = "Untitled";
		}

		return bestAnchor;
	}
}

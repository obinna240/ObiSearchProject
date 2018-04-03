package com.sa.search.db.mongo.dao;

import com.sa.search.db.mongo.model.SearchConfig;

public interface SearchConfigDAOCustom extends CustomDAO<SearchConfig, String>{
	public int findSnippetLength(int defaultVal);
	public int findResultsPerPage(int defaultVal);
	public String getStandardSearchTemplate();
	public String getSearchConstraintsTemplate();
	public String getSmartSuggestTemplate();
	public String getAdvancedAllTemplate();
	public String getAdvancedExactTemplate();
	public String getAdvancedExcludeTemplate();
	public String getAdvancedOrTemplate();
	public String getAdvancedDateTemplate();
}

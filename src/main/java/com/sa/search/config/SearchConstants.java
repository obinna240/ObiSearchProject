package com.sa.search.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flexjson.JSONSerializer;
import flexjson.transformer.HtmlEncoderTransformer;

public final class SearchConstants {
	public static final SimpleDateFormat SOLR_QUERY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat DATE_INPUT_FORMAT = new SimpleDateFormat("dd/MM/yy");
	public static final String START_DAY_TIME = "T00:00:00Z";
	public static final String END_DAY_TIME = "T23:59:59Z";
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int NAV_PAGE_COUNT = 5;
	public static final List<String> SOLR_SPECIAL_CHARS;
	
	static {
		SOLR_SPECIAL_CHARS = new ArrayList<String>();
		SOLR_SPECIAL_CHARS.add("+");
		SOLR_SPECIAL_CHARS.add("-");
		SOLR_SPECIAL_CHARS.add("&&");
		SOLR_SPECIAL_CHARS.add("||");
		SOLR_SPECIAL_CHARS.add("!");
		SOLR_SPECIAL_CHARS.add("(");
		SOLR_SPECIAL_CHARS.add(")");
		SOLR_SPECIAL_CHARS.add("{");
		SOLR_SPECIAL_CHARS.add("}");
		SOLR_SPECIAL_CHARS.add("[");
		SOLR_SPECIAL_CHARS.add("]");
		SOLR_SPECIAL_CHARS.add("^");		
		SOLR_SPECIAL_CHARS.add("~");
		SOLR_SPECIAL_CHARS.add("*");
		SOLR_SPECIAL_CHARS.add("?");
		SOLR_SPECIAL_CHARS.add(":");
	}
	
	public static final Map<String, JSONSerializer> SERIALIZERS;
	static {
		SERIALIZERS = new HashMap<String, JSONSerializer>();
		SERIALIZERS.put("DEFAULT", new JSONSerializer().exclude("*.class"));		
	}
	
	public static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	
}


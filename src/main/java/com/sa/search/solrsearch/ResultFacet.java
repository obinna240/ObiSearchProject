package com.sa.search.solrsearch;

import java.util.ArrayList;
import java.util.List;


public class ResultFacet {

	private String name;
	private List<ResultFacetEntry> entries = new ArrayList<ResultFacetEntry>();
	
	// Used in range facets
	private String start;
    private String end;
    private String gap;

    private Number before;
    private Number after;
    private Number between;
    
    // Used in interval facets

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ResultFacetEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<ResultFacetEntry> entries) {
		this.entries = entries;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getGap() {
		return gap;
	}

	public void setGap(String gap) {
		this.gap = gap;
	}

	public Number getBefore() {
		return before;
	}

	public void setBefore(Number before) {
		this.before = before;
	}

	public Number getAfter() {
		return after;
	}

	public void setAfter(Number after) {
		this.after = after;
	}

	public Number getBetween() {
		return between;
	}

	public void setBetween(Number between) {
		this.between = between;
	}

	public class ResultFacetEntry {
		private String name;
		private long count;
		private String encodedUrl;
		private String paramUrl;
		private boolean selected;
		private boolean disabled;
		
		public ResultFacetEntry() {
			
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public long getCount() {
			return count;
		}
		
		public void setCount(long count) {
			this.count = count;
		}

		public String getEncodedUrl() {
			return encodedUrl;
		}

		public void setEncodedUrl(String encodedUrl) {
			this.encodedUrl = encodedUrl;
		}

		public String getParamUrl() {
			return paramUrl;
		}

		public void setParamUrl(String paramUrl) {
			this.paramUrl = paramUrl;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public boolean isDisabled() {
			return disabled;
		}

		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}

		
	}
}

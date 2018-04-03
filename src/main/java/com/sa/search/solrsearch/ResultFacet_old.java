package com.sa.search.solrsearch;

import java.util.ArrayList;
import java.util.List;

public class ResultFacet_old {
	private String id;
	private String displayName;
	private int displayOrder;

	private List<ResultFacetEntry_old> entries = new ArrayList<ResultFacetEntry_old>();
	
	public ResultFacet_old(String id, String displayName, int displayOrder) {
		super();
		this.id = id;
		this.displayName = displayName;
		this.setDisplayOrder(displayOrder);
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	public List<ResultFacetEntry_old> getEntries() {
		return entries;
	}

	public void setEntries(List<ResultFacetEntry_old> entries) {
		this.entries = entries;
	}
	
	public long getTotalCounts(){
		long total = 0;
		for (ResultFacetEntry_old entry : entries) {
			total += entry.getCount();
		}
		return total;
	}


	public class ResultFacetEntry_old {
		
		private String id;
		private String displayName;
		private long count;
		private long displayOrder;
		private String facetURLParams;

		public ResultFacetEntry_old(String id, String displayName, long count, long displayOrder, String urlParams) {
			this.id = id;
			this.displayName = displayName;
			this.count = count;
			this.setDisplayOrder(displayOrder);
			this.facetURLParams = urlParams;
		}
		
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getDisplayName() {
			return displayName;
		}
		
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		
		public long getCount() {
			return count;
		}
		
		public void setCount(long count) {
			this.count = count;
		}
		
		public long getDisplayOrder() {
			return displayOrder;
		}

		public void setDisplayOrder(long displayOrder) {
			this.displayOrder = displayOrder;
		}
		
		public String getFacetURLParams() {
			return facetURLParams;
		}
		
		public void setFacetURLParams(String facetURLParams) {
			this.facetURLParams = facetURLParams;
		}
	}
}

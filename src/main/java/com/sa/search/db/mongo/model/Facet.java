package com.sa.search.db.mongo.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class Facet {
	
	@Id
	private String id;
	private String displayName;
	private int displayOrder;
	private boolean allowMultipleCategories;
	private boolean enabled;
	private List<FacetCategory> categories = new ArrayList<Facet.FacetCategory>();

	public Facet() {
	}

	public Facet(String id, String displayName, int displayOrder, boolean allowMultipleCategories, boolean enabled) {
		this.id = id;
		this.displayName = displayName;
		this.displayOrder = displayOrder;
		this.allowMultipleCategories = allowMultipleCategories;
		this.enabled = enabled;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayName() {
		return this.displayName;
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

	public boolean isAllowMultipleCategories() {
		return allowMultipleCategories;
	}

	public void setAllowMultipleCategories(boolean allowMultipleCategories) {
		this.allowMultipleCategories = allowMultipleCategories;
	}

	public List<FacetCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<FacetCategory> categories) {
		this.categories = categories;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public class FacetCategory {

		//Display attributes
		private String id;
		private String displayName;
		private int displayOrder;

		//Indexing attributes
		private boolean isDefault;
		private List<String> urlPrefixes = new ArrayList<String>();
		private List<String> mimeTypes = new ArrayList<String>();
		private String indexField;

		public FacetCategory() {
		}

		public FacetCategory init (String id, String displayName, boolean isDefault, int displayOrder){
			this.id = id;
			this.displayName = displayName;
			this.isDefault = isDefault;
			this.displayOrder = displayOrder;
			return this;
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

		public boolean isDefault() {
			return isDefault;
		}

		public void setDefault(boolean isDefault) {
			this.isDefault = isDefault;
		}

		public List<String> getUrlPrefixes() {
			return urlPrefixes;
		}

		public void setUrlPrefixes(List<String> urlPrefixes) {
			this.urlPrefixes = urlPrefixes;
		}

		public List<String> getMimeTypes() {
			return mimeTypes;
		}

		public void setMimeTypes(List<String> mimeTypes) {
			this.mimeTypes = mimeTypes;
		}

		public String getIndexField() {
			return indexField;
		}

		public void setIndexField(String indexField) {
			this.indexField = indexField;
		}

		public int getDisplayOrder() {
			return displayOrder;
		}

		public void setDisplayOrder(int displayOrder) {
			this.displayOrder = displayOrder;
		}

	}

}

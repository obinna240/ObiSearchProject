package com.sa.search.solrsearch;


public enum SortOption {
	
	RELEVANCY("Relevancy"), 
	ALPHABETICALLY("Alphabetically"), 
	DISTANCE("Distance"), 
	DATE("Date"), 
	PRICE("Price"), 
	RANDOM("Random");
	
	private final String option;
	
	private SortOption(String option) {
		this.option = option;
	}
	
	public String getOption() {
	    return this.option;
	}
	
	public static SortOption findByOption(String option) {
		SortOption[] vals = SortOption.values();
		for (SortOption val : vals) {
			if (val.getOption().equalsIgnoreCase(option)){
				return val;
			}
		}
		return null;
    }

}

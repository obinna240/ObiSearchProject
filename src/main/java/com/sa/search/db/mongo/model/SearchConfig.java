package com.sa.search.db.mongo.model;

import org.springframework.data.annotation.Id;

public class SearchConfig {

	 @Id
     private String id;
     private String name;
     private int value;
     private String type;
     private String textValue;

 	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
    public SearchConfig() {
    }

    public SearchConfig(String name, int value, String type) {
       this.name = name;
       this.value = value;
       this.type = type;
    }
   
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public int getValue() {
        return this.value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}




}



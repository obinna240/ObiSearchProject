package com.sa.search.db.mongo.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Id;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class RecommendedLink {
	
	 @Id
     private String id;
     private String keyword;
     private String title;
     private String description;
     private String url;

    public RecommendedLink() {
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public RecommendedLink(String keyword, String title, String description, String url) {
       this.keyword = keyword;
       this.title = title;
       this.description = description;
       this.url = url;
    }
   
    public String getKeyword() {
        return this.keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static RecommendedLink fromJsonToRecommendedLink(String json) {
        return new JSONDeserializer<RecommendedLink>().use(null, RecommendedLink.class).deserialize(json);
    }

	public static String toJsonArray(Collection<RecommendedLink> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<RecommendedLink> fromJsonArrayToRecommendedLinks(String json) {
        return new JSONDeserializer<List<RecommendedLink>>().use(null, ArrayList.class).use("values", RecommendedLink.class).deserialize(json);
    }
}



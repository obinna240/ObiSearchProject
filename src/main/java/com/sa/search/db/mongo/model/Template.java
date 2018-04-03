package com.sa.search.db.mongo.model;

import org.springframework.data.annotation.Id;

public class Template {
	
	@Id
	private String id;
    private String templateName;
    private String viewName;
    private boolean defaultView;

    public Template() {
    }

    public Template(String templateName, String viewName, boolean defaultView) {
       this.templateName = templateName;
       this.viewName = viewName;
       this.defaultView = defaultView;
    }
   
    public String getId() {
        return this.id;
    }
    
    public void setId(String templateId) {
        this.id = templateId;
    }
    public String getTemplateName() {
        return this.templateName;
    }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    public String getViewName() {
        return this.viewName;
    }
    
    public void setViewName(String styleName) {
        this.viewName = styleName;
    }
    public boolean isDefaultView() {
        return this.defaultView;
    }
    
    public void setDefaultView(boolean defaultView) {
        this.defaultView = defaultView;
    }
}



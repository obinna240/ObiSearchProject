package com.sa.search.view.controller.bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.util.ClassUtils;

import com.sa.search.db.mongo.model.IPcgSearchConfig;

public class SystemConfigUI {
	private LinkedHashMap<String, SystemConfigUIItem> fieldsMap;
	private boolean indexedProperty;
	private boolean mappedProperty;
	
	public SystemConfigUI(LinkedHashMap<String, SystemConfigUIItem> fieldsMap){
		this.fieldsMap = fieldsMap;
	}
			
	public SystemConfigUI(Object obj ){		
		fieldsMap = new LinkedHashMap<String, SystemConfigUIItem>();
		
			try {				
				if (obj instanceof IPcgSearchConfig) {
					Map<String, Object> map = PropertyUtils.describe(obj);	
					if (map != null && map.containsKey("class")){
						map.remove("class");
					}
					for (String key:map.keySet()){
						Object o = map.get(key);
						boolean canDisplay = (o!=null)?(o.getClass().equals(String.class) || ClassUtils.isPrimitiveOrWrapper(o.getClass())):false;
						if (canDisplay && o instanceof String){
							o = deHTML(o.toString());
						}
						SystemConfigUIItem item = new SystemConfigUIItem(key, o, canDisplay);	
						fieldsMap.put(key, item);
					}
				}
				else if (obj instanceof List) {	
					this.setIndexedProperty(true);
					Iterator i = ((List) obj).iterator();
					
					while (i.hasNext()){
						Object o = i.next();
						Method mthd;
						
						mthd = o.getClass().getDeclaredMethod("getType", (Class[])null);				
						String type = (String)mthd.invoke(o, (Object[])null);
						boolean canDisplay = (o!=null)?(o.getClass().equals(String.class)|| ClassUtils.isPrimitiveOrWrapper(o.getClass())):false;
						if (canDisplay && o instanceof String){
							o = deHTML(o.toString());
						}
						SystemConfigUIItem item = new SystemConfigUIItem(type, o, canDisplay);	
						fieldsMap.put(type, item);
						
					}
				}
				else if (obj instanceof Map) {
						this.setMappedProperty(true);
						Map<String, Object> m= ((Map<String, Object>) obj);			
						for (String s:m.keySet()){
							Object o = m.get(s);			
							boolean canDisplay = (o!=null)?(o.getClass().equals(String.class) || ClassUtils.isPrimitiveOrWrapper(o.getClass())):false;			
							if (canDisplay && o instanceof String){
								o = deHTML(o.toString());
							}	
							SystemConfigUIItem item = new SystemConfigUIItem(s, o, canDisplay);	
							fieldsMap.put(s, item);
							
						}
					}
				
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	
	public HashMap<String, SystemConfigUIItem> getFieldsMap() {
		return fieldsMap;
	}

	public void setFieldsMap(LinkedHashMap<String, SystemConfigUIItem> fieldsMap) {
		this.fieldsMap = fieldsMap;
	}
	
	public boolean isIndexedProperty() {
		return indexedProperty;
	}
	public void setIndexedProperty(boolean indexedProperty) {
		this.indexedProperty = indexedProperty;
	}
	public boolean isMappedProperty() {
		return mappedProperty;
	}
	public void setMappedProperty(boolean mappedProperty) {
		this.mappedProperty = mappedProperty;
	}
	
	/**
	 * Basic HTML tag removal 
	 */
	String deHTML(String html){
		if (html != null) {
			Document doc = Jsoup.parse(html);
			return doc.text();
		
			//String deHTMLdString = html.replaceAll("\\<.*?>","");
			//deHTMLdString = deHTMLdString.replaceAll("\\r\\n", "");
			//return deHTMLdString;
		} else {
			return "";
		}
	}
}

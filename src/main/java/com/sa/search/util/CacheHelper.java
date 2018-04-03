package com.sa.search.util;

import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.ehcache.EhCacheCacheManager;

/**
 * Helper for ehcache access 
 * Caches are named and configured in META-INF/spring/ehcache.xml
 */
public class CacheHelper {
	
	private EhCacheCacheManager cacheManager;
	private String cacheName;
	
	public CacheHelper(EhCacheCacheManager cacheManager, String cacheName){
		this.cacheManager = cacheManager;
		this.cacheName = cacheName;
	}
	
	public void setValue(String key, Object value){
		cacheManager.getCache(cacheName).put(key, value);
	}
	
	public Object getValue(String key){
		ValueWrapper valueWrapper = cacheManager.getCache(cacheName).get(key);
		
		if (valueWrapper == null){
			return null;
		}
		else {
			return valueWrapper.get();
		}
	}

	public String getStringValue(String key){
		return (String)getValue(key);
	}
	
	public Integer getIntValue(String key){
		return (Integer)getValue(key);
	}	

}

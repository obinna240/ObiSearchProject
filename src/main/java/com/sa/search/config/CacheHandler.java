/**
 * @author Tony Wilson
 * Created on 20 Dec 2013 at 10:24:19
 * Copyright (c) 2013 System Associates Ltd
 */

package com.sa.search.config;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Wrapper class for some ehcache caches
 */
public class CacheHandler {
	
	private static Log m_log = LogFactory.getLog(CacheHandler.class);
	
	public enum CacheName {
		STATICINFO
	}
	
	public static void initCaches() throws CacheException {
		
    	CacheManager cm = CacheManager.create();
      	cm.addCache(new Cache(CacheName.STATICINFO.toString(), 200, false, true, 0, 0));//200 items, eternal
      		m_log.info("Caches initialised");
	}
	
	
	public static void flushAllCaches() throws CacheException {
		CacheName[] cacheNames = CacheName.values();
		for (CacheName cacheName : cacheNames) {
			flushCache(cacheName);
		}
	}

	public static void flushCache(CacheName cacheName) throws CacheException {
		
		Cache cache = CacheManager.getInstance().getCache(cacheName.toString());
		cache.flush();
    	m_log.info("Cache " + cacheName + "  flushed");
	}

	
	public static void cacheStringVal (CacheName cacheName, String key, String value){
		cacheVal(cacheName, key, value);
	}
	
	public static void cacheVal (CacheName cacheName, String key, Serializable value){
		
		try {
			Cache cache = CacheManager.getInstance().getCache(cacheName.toString());
			cache.put(new Element(key, value));
		} catch (IllegalStateException e) {
			m_log.error("Cache failure ", e);
		} catch (CacheException e) {
			m_log.error("Cache failure ", e);
		}
	}
	
	public static Integer getCachedIntegerVal (CacheName cacheName, String key){
		return (Integer)getCachedVal(cacheName, key);
	}	

	public static String getCachedStringVal (CacheName cacheName, String key){
		return (String)getCachedVal(cacheName, key);
	}	
	
	public static Object getCachedVal (CacheName cacheName, String key){
	
		Object value = null;
			
		try {
			Cache cache = CacheManager.getInstance().getCache(cacheName.toString());
			if (cache != null){
				Element cacheVal = cache.get(key);
				if (cacheVal != null){
					value = cacheVal.getObjectValue();
				}
			}
		} catch (IllegalStateException e) {
			m_log.error("Cache failure ", e);
		} catch (CacheException e) {
			m_log.error("Cache failure ", e);
		}
		return value;
	}
}

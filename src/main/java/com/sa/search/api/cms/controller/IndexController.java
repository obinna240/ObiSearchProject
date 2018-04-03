package com.sa.search.api.cms.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import com.sa.search.api.cms.controller.IndexController;
import com.sa.search.api.cms.json.AjaxResult;
import com.sa.search.api.cms.json.AjaxStatus;
import com.sa.search.api.cms.json.CmsDocument;
import com.sa.search.indexing.IndexManager;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@RequestMapping("/api/cms/index/**")
@Controller
public class IndexController {
	
	@Autowired private IndexManager indexManager;
	
	private static Log m_log = LogFactory.getLog(IndexController.class);
	
	private static Map<String, JSONSerializer> serializers;
	static {
		serializers = new HashMap<String, JSONSerializer>();
		serializers.put("DEFAULT", new JSONSerializer().exclude("*.class"));
	}

	@RequestMapping(value = "/add")
	public @ResponseBody String addCmsDocumentToIndexPost(@RequestBody String json) {
		
		try {
			CmsDocument doc = new JSONDeserializer<CmsDocument>().use(null, CmsDocument.class).deserialize(json);
			
			indexManager.indexCmsDocument(doc);
			
			AjaxResult ar = new AjaxResult(AjaxStatus.OK, "Document added to solr index");
			String result = serializers.get("DEFAULT").serialize(ar);
			return result;
			
		} catch (Exception e) {
			m_log.error(e.getMessage());
			AjaxResult ar = new AjaxResult(AjaxStatus.ERROR, "Error adding document to solr index");
			String result = serializers.get("DEFAULT").serialize(ar);
			return result;
		}
 
	}
	
	@RequestMapping(value = "/encoded/add")
	public @ResponseBody String addEncodeCmsDocumentToIndexPost(@RequestBody String encodedJSON) {
		
		try {
			byte[] valueDecoded= Base64.decodeBase64(encodedJSON.getBytes());
			String json = new String(valueDecoded);
			
			CmsDocument doc = new JSONDeserializer<CmsDocument>().use(null, CmsDocument.class).deserialize(json);
			
			indexManager.indexCmsDocument(doc);
			
			AjaxResult ar = new AjaxResult(AjaxStatus.OK, "Document added to solr index");
			String result = serializers.get("DEFAULT").serialize(ar);
			return result;
			
		} catch (Exception e) {
			m_log.error(e.getMessage());
			AjaxResult ar = new AjaxResult(AjaxStatus.ERROR, "Error adding document to solr index");
			String result = serializers.get("DEFAULT").serialize(ar);
			return result;
		}
 
	}
	
	@RequestMapping(value = "/removeByDoc")
	public @ResponseBody String removeCmsDocument(@RequestBody String json) {
		
		try {
			CmsDocument doc = new JSONDeserializer<CmsDocument>().use(null, CmsDocument.class).deserialize(json);
			
			indexManager.removeCmsContent(doc);
			
			AjaxResult ar = new AjaxResult(AjaxStatus.OK, "Document removed from solr index");
			String result = serializers.get("DEFAULT").serialize(ar);
			return result;
			
		} catch (Exception e) {
			m_log.error(e.getMessage(), e);
			AjaxResult ar = new AjaxResult(AjaxStatus.ERROR, "Error removing document from solr index");
			String result = serializers.get("DEFAULT").serialize(ar);
			return result;
		}
 
	}
	
	
	@RequestMapping(value = "/encoded/removeByDoc")
	public @ResponseBody String removeEncodedCmsDocument(@RequestBody String encodedJSON) {
		
		try {
			byte[] valueDecoded= Base64.decodeBase64(encodedJSON.getBytes());
			String json = new String(valueDecoded);
		
			CmsDocument doc = new JSONDeserializer<CmsDocument>().use(null, CmsDocument.class).deserialize(json);
			
			indexManager.removeCmsContent(doc);
			
			AjaxResult ar = new AjaxResult(AjaxStatus.OK, "Document removed from solr index");
			String result = serializers.get("DEFAULT").serialize(ar);
			return result;
			
		} catch (Exception e) {
			m_log.error(e.getMessage(), e);
			AjaxResult ar = new AjaxResult(AjaxStatus.ERROR, "Error removing document from solr index");
			String result = serializers.get("DEFAULT").serialize(ar);
			return result;
		}
 
	}
	
	@RequestMapping(value = "/removeById")
	public @ResponseBody String removeDocument(@RequestBody String id) {
		
		try {
			indexManager.removeDocument(id);
			
			AjaxResult ar = new AjaxResult(AjaxStatus.OK, "Document removed from solr index");
			String result = serializers.get("DEFAULT").serialize(ar);
			return result;
			
		} catch (Exception e) {
			m_log.error(e.getMessage(), e);
			AjaxResult ar = new AjaxResult(AjaxStatus.ERROR, "Error removing document from solr index");
			String result = serializers.get("DEFAULT").serialize(ar);
			return result;
		}
 
	}
}


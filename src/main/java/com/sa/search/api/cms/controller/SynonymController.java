package com.sa.search.api.cms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.sa.search.api.cms.controller.SynonymController;
import com.sa.search.api.cms.json.AjaxResult;
import com.sa.search.api.cms.json.AjaxStatus;
import com.sa.search.api.cms.json.CmsDocument;
import com.sa.search.api.cms.json.SynonymDocument;
import com.sa.search.config.SearchConstants;
import com.sa.search.indexing.IndexManager;
import com.sa.search.solrsearch.SearchResult;
import com.sa.search.solrsearch.SolrSearchBean;
import com.sa.search.util.SynonymHelper;
import com.sa.search.view.controller.bean.SynonymUI;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@RequestMapping("/api/cms/synonym/**")
@Controller
public class SynonymController extends CmsController{
	
	@Autowired private SolrSearchBean solrSearcher;
	
	private static Log m_log = LogFactory.getLog(SynonymController.class);
	private static Map<String, JSONSerializer> serializers;
	static {
		serializers = new HashMap<String, JSONSerializer>();
		serializers.put("DEFAULT", new JSONSerializer().exclude("*.class"));
	}
	
	@RequestMapping(method=RequestMethod.POST, value = "/add")
	public @ResponseBody String addSynonymToIndexPost(@RequestBody String json) {
		
		try {
			
			String url = solrSearcher.getSolrServerUrl() +"schema/analysis/synonyms/english";	
			//String url = solrSearcher.getSolrServerUrl() +"synonyms.json";
			SynonymDocument doc = new JSONDeserializer<SynonymDocument>().use(null, SynonymDocument.class).deserialize(json);
		
			//First delete the old synonym and then add new one 
			SynonymHelper.removeSynonym(url, doc.getWord());	
			List<String> synonyms = doc.getSynonymList();
			if (synonyms != null){			
				for (String s:synonyms){
					SynonymHelper.removeSynonym(url, s);	
				}
			}
		
			int statusCode = SynonymHelper.addSynonym(url, getSolrJson(doc.getWord(), doc.getSynonymList(), doc.isOneWay()));
	
			if (statusCode != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + statusCode);
			}
			
			// relaod core
			//http://localhost:8983/solr/admin/cores?action=RELOAD&core=search
			StringBuffer reloadUrl = new StringBuffer(solrSearcher.getSolrConfig().getCoresActionURL());
			reloadUrl.append("RELOAD&core=");
			reloadUrl.append(solrSearcher.getSolrConfig().getSolrCore());
			statusCode = SynonymHelper.reloadCore(reloadUrl.toString());
			if (statusCode != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + statusCode);
			}
			AjaxResult ar = new AjaxResult(AjaxStatus.OK, "Synonym added to solr index");
			return serializers.get("DEFAULT").serialize(ar);
			
		} catch (Exception e) {
			m_log.error(e.getMessage());
			AjaxResult ar = new AjaxResult(AjaxStatus.ERROR, "Error adding synonym to solr index");
			return serializers.get("DEFAULT").serialize(ar);
		}
 
	}
	
	public String getSolrJson(String word, List<String> synonymList, boolean oneWay) {
		String json = null;
		

		if (oneWay == false) {
			 List<String> newArr = new ArrayList<String>();
			 newArr.add(word);
			 if (synonymList != null) {					
				for (String s : synonymList){
					newArr.add(s.trim());
				}
			}
			json = new JSONSerializer().include("values.*").deepSerialize(newArr);
		} else {
	
			HashMap<String, List<String>> map = new HashMap<String, List<String>>();
			List<String> newArr = new ArrayList<String>();	
		
			if (synonymList != null) {					
				for (String s : synonymList){
					newArr.add(s.trim());
				}
				map.put(word, newArr);
			}
			json = new JSONSerializer().include("values.*").deepSerialize(map);
		}
	
		return json;
	}

	
	@RequestMapping(value = "/remove")
	public @ResponseBody String removeDocument(@RequestBody String word) {
		String url = solrSearcher.getSolrServerUrl() +"schema/analysis/synonyms/english";	
		//String url = solrSearcher.getSolrServerUrl() +"synonyms.json";
		HashMap<String, List<String>> managedSynonyms = SynonymHelper.getSynonymList(url);
		AjaxResult ar;
		if (StringUtils.isNotEmpty(word) && managedSynonyms != null){						
			int statusCode = SynonymHelper.removeSynonym(url, word);
				if (statusCode == 200) {
				// relaod core
				StringBuffer reloadUrl = new StringBuffer(solrSearcher.getSolrConfig().getCoresActionURL());
				reloadUrl.append("RELOAD&core=");
				reloadUrl.append(solrSearcher.getSolrConfig().getSolrCore());
				statusCode = SynonymHelper.reloadCore(reloadUrl.toString());
			}
			
			if (statusCode != 200) {
				ar = new AjaxResult(AjaxStatus.OK, "error removing synonym from solr index");
			}else{
				ar = new AjaxResult(AjaxStatus.OK, "Synonym removed from solr index");
			}
		} else {
			ar = new AjaxResult(AjaxStatus.OK, "error removing synonym from solr index");
		}
		
		return serializers.get("DEFAULT").serialize(ar);
 
	}
	
	@RequestMapping(value = "/get")
	public @ResponseBody String getSearchResults(HttpServletRequest request, HttpServletResponse response) {
		String url = solrSearcher.getSolrServerUrl() +"schema/analysis/synonyms/english";
		//String url = solrSearcher.getSolrServerUrl() +"synonyms.json";
		HashMap<String, List<String>> managedSynonyms = SynonymHelper.getSynonymList(url);
		List<SynonymDocument> synonymDocumentList = new ArrayList<SynonymDocument>();
		for (String key:managedSynonyms.keySet()){
			SynonymDocument synonymDocument = new SynonymDocument();
			synonymDocument.setWord(key.toString());
			synonymDocument.setSynonymList(managedSynonyms.get(key));
			synonymDocumentList.add(synonymDocument);
		}
		
		String retVal = SearchConstants.SERIALIZERS.get("DEFAULT").deepSerialize(synonymDocumentList); 
		
		//String returnVal = unicodeEscape(SynonymDocument.toJsonArray(synonymDocumentList));
		
		return unicodeEscape(retVal);
	}
}


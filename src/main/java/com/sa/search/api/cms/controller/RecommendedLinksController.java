package com.sa.search.api.cms.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sa.search.api.cms.json.AjaxResult;
import com.sa.search.api.cms.json.AjaxStatus;
import com.sa.search.db.mongo.dao.RecommendedLinkDAO;
import com.sa.search.db.mongo.model.RecommendedLink;

import flexjson.JSONSerializer;

@RequestMapping("/api/cms/recommendedLinks/**")
@Controller
public class RecommendedLinksController extends CmsController{
	
	@Autowired private RecommendedLinkDAO recommendedLinkDAO;

	
	private static Log m_log = LogFactory.getLog(RecommendedLinksController.class);
	private static Map<String, JSONSerializer> serializers;
	static {
		serializers = new HashMap<String, JSONSerializer>();
		serializers.put("DEFAULT", new JSONSerializer().exclude("*.class"));
	}
	
	@RequestMapping(method=RequestMethod.POST, value = "/add")
	public @ResponseBody String addRecommendedLinkPost(@RequestBody String json) {
		
		try {
			RecommendedLink recommendedlink = RecommendedLink.fromJsonToRecommendedLink(json);
			String id = recommendedlink.getId();
			if (StringUtils.isBlank(id)) {
				AjaxResult ar = new AjaxResult(AjaxStatus.ERROR, "Error adding recommended link no id specified");
				return serializers.get("DEFAULT").serialize(ar);
			}
			recommendedLinkDAO.save(recommendedlink);
			AjaxResult ar = new AjaxResult(AjaxStatus.OK, "Recommended link saved");
			return serializers.get("DEFAULT").serialize(ar);
			
		} catch (Exception e) {
			m_log.error(e.getMessage());
			AjaxResult ar = new AjaxResult(AjaxStatus.ERROR, "Error adding recommended link");
			return serializers.get("DEFAULT").serialize(ar);
		}
 	}
	
	@RequestMapping(value = "/remove")
	public @ResponseBody String removeRecommendedLink(@RequestBody String id) {
		AjaxResult ar;
		if (StringUtils.isNotEmpty(id)){	
			recommendedLinkDAO.delete(id);
			ar = new AjaxResult(AjaxStatus.OK, "Recommended link removed");
		} else {
			ar = new AjaxResult(AjaxStatus.OK, "error removing recommended link");
		}
		
		return serializers.get("DEFAULT").serialize(ar);
 	}
	
	@RequestMapping(value = "/get")
	public @ResponseBody String getRecommendedLinks(HttpServletRequest request, HttpServletResponse response) {
		List<RecommendedLink> recommendedlinks = recommendedLinkDAO.findAll();
		return RecommendedLink.toJsonArray(recommendedlinks);
	}
}


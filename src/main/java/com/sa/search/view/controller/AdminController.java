package com.sa.search.view.controller;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sa.search.api.cms.json.CmsDocument;
import com.sa.search.config.CacheHandler;
import com.sa.search.db.mongo.dao.RecommendedLinkDAO;
import com.sa.search.db.mongo.dao.OsCodePointDAO;
import com.sa.search.db.mongo.dao.SystemConfigDAO;
import com.sa.search.db.mongo.dao.UserDAO;
import com.sa.search.db.mongo.model.IPcgSearchConfig;
import com.sa.search.db.mongo.model.RecommendedLink;
import com.sa.search.db.mongo.model.OsCodePoint;
import com.sa.search.db.mongo.model.OsCodePoint.Coverage;
import com.sa.search.db.mongo.model.SystemConfig;
import com.sa.search.db.mongo.model.User;
import com.sa.search.indexing.IndexManager;
import com.sa.search.service.IUserService;
import com.sa.search.solrsearch.DisplayItem;
import com.sa.search.solrsearch.ResultFacet;
import com.sa.search.solrsearch.ResultFacet.ResultFacetEntry;
import com.sa.search.solrsearch.SearchInfo;
import com.sa.search.solrsearch.SearchResult;
import com.sa.search.solrsearch.SearchServices;
import com.sa.search.solrsearch.SolrSearchBean;
import com.sa.search.solrsearch.SortOption;
import com.sa.search.util.FlashMap;
import com.sa.search.util.MapPin;
import com.sa.search.util.SynonymHelper;
import com.sa.search.view.controller.bean.ConfigUI;
import com.sa.search.view.controller.bean.SynonymUI;
import com.sa.search.view.controller.bean.SystemConfigUI;
import com.sa.search.view.controller.bean.SystemConfigUIItem;
import com.sa.search.view.controller.form.AdminSuspendUserForm;
import com.sa.search.view.controller.form.AdminUserDetailsForm;
import com.sa.search.view.controller.form.DocumentForm;
import com.sa.search.view.controller.form.RecommendeLinkForm;
import com.sa.search.view.controller.form.SearchForm;
import com.sa.search.view.controller.form.SynonymForm;
import com.sa.search.view.controller.form.SystemConfigForm;
import com.sa.search.view.controller.validator.AdminRegistrationBasicValidator;
import com.sa.search.view.controller.validator.AdminSuspendAccountValidator;
import com.sa.search.view.controller.validator.PasswordValidator;
import com.sa.search.view.controller.validator.SystemConfigFormValidator;
import com.sa.search.view.helper.AZInfo;

import flexjson.JSONDeserializer;
import uk.me.jstott.jcoord.LatLng;


@RequestMapping("/admin/**")
@Controller
public class AdminController extends BaseController {

	@Autowired private UserDAO userDAO;
	@Autowired private RecommendedLinkDAO recommendedLinkDAO;
	@Autowired private IUserService userService;
	@Autowired private AdminSuspendAccountValidator adminSuspendAccountValidator;
	@Autowired private AdminRegistrationBasicValidator adminRegistrationBasicValidator;
	@Autowired private PasswordValidator passwordValidator;
	@Autowired private IndexManager indexManager;
	
	@Autowired private SolrSearchBean solrSearcher;
	@Autowired private SearchServices searchService;
	@Autowired private SystemConfigDAO systemConfigDAO;
	
	@Autowired private OsCodePointDAO osCodePointDAO;
	@Autowired private SystemConfigFormValidator systemConfigFormValidator;
	
	//Property to the Configuration Names
	@Resource(name="systemConfig")
	private Properties systemConfigProp;
	//Constant for holding the ID Attribute
	private final static String ID = "id";
	//Constant for the Class Attribute
	private final static String CLASS = "class";
	//Constant for the systemConfig
	private final static String  SYSTEMCONFIG  = "systemConfig";
	//Constant for the System
	private final static String SYSTEM = "System";
	private final String SYSTEM_CONFIG_FIELD_SEPARATOR = "-";
	private final static String SYSTEMCONFIG_UI = "systemConfigUI";
	private final static String FIELD_NAME = "fieldName";
	private static final String CONFIG_ID = "configId";

		
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private static Log m_log = LogFactory.getLog(AdminController.class);

	public static String[] classifications1 = {"", "Mammals", "Birds", "Fish", "Reptiles", "Amphibians"};
	public static String[] classifications2 = {"", "Africa", "Antarctica", "Asia", "Australia", "Europe", "North America", "South America"};
	private static String[] allAlpha = {"0-9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

	@ModelAttribute("sortOptions")
	public String[] sortOptions() {
		SortOption[] options = SortOption.values();
		String[] sortOptions = new String[options.length];
		int x = 0;
		for (SortOption option : options) {
			sortOptions[x] = option.getOption();
			x++;
		}
		
		return sortOptions;
	}
	
	@ModelAttribute("classifications1")
	public String[] getClassifications1() {
		return classifications1;
	}
	
	@ModelAttribute("classifications2")
	public String[] getClassifications2() {
		return classifications2;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/admin/manageAdmin/list")
	public String adminUserList(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "size", required = false) Integer size, 
			ModelMap modelMap, HttpSession session, HttpServletRequest request) {

		User user = getUser();
		modelMap.addAttribute("currentUser", user);
		
		int sizeNo = size == null ? 10 : size.intValue();
		List<User> users = userDAO.findEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo, "_id");
		modelMap.addAttribute("users", users);
		
		long count = userDAO.count();
		float nrOfPages = (float)count / sizeNo;
		modelMap.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));

		return "admin/manageadmin/list";
	}

	@RequestMapping (value = "/admin/admin/suspend/{uid}")
	public String suspendAdmin(@PathVariable("uid") String userId, ModelMap modelMap, HttpServletRequest request) {

		User user = userDAO.findOne(userId);
	
		modelMap.put("user", user);

		return "admin/suspend/confirm";
	}

	@RequestMapping (method = RequestMethod.POST, value = "/admin/admin/suspend")
	public String suspendAdminPost(ModelMap modelMap, @Valid AdminSuspendUserForm form, BindingResult result, HttpServletRequest request) {

		adminSuspendAccountValidator.validate(form, result);

		if (result.hasErrors()) {
			return "admin/suspend/"+form.getUsername();
		}

		User user = userDAO.findOne(form.getUsername());
		modelMap.put("user", user);
	
		return "admin/user/suspend/confirm";
	}

	@RequestMapping (value = "/admin/admin/suspend/confirm/{uid}")
	public String suspendAdminConfirmation(@PathVariable("uid") String userId, RedirectAttributes redirect) {

		userService.suspendUserAccount(userId);
		FlashMap.setSuccessMessage("service_account_suspended", redirect);

		return "redirect:/admin/manageAdmin/list";
	}

	@RequestMapping (value = "/admin/admin/activate/{uid}")
	public String activateAdmin(@PathVariable("uid") String userId, ModelMap modelMap, HttpServletRequest request) {

		User user = userDAO.findOne(userId);
		modelMap.put("user", user);

		return "/admin/activate/confirm";
	}

	@RequestMapping (value = "/admin/admin/activate/confirm/{uid}")
	public String activateAdminConfirmation(@PathVariable("uid") String userId, RedirectAttributes redirect) {

		userService.activateUserAccount(userId);
		FlashMap.setSuccessMessage("service_account_active", redirect);

		return "redirect:/admin/manageAdmin/list";
	}
	
	@RequestMapping (value = "/admin/add/adminuser")
	public String addAminUser(AdminUserDetailsForm form, ModelMap modelMap, HttpServletRequest request) {
		
		modelMap.put("passwordHelpText", passwordValidator.getPasswordHelpText());
	
		return "admin/add/user";
	}

	@RequestMapping (method=RequestMethod.POST, value = "/admin/add/adminuser")
	public String addAdminUserPost(ModelMap modelMap, @Valid AdminUserDetailsForm form, BindingResult result, HttpServletRequest request, RedirectAttributes redirect) {

		adminRegistrationBasicValidator.validate(form, result);

		if (result.hasErrors()) {
			modelMap.put("passwordHelpText", passwordValidator.getPasswordHelpText());
			return "admin/add/user";
		}


		userService.registerAdmin(form);
		FlashMap.setSuccessMessage("admin_account_new_form_complete", redirect);

		return "redirect:/account/home";
	}
	

	@RequestMapping(method = RequestMethod.GET, value = "/admin/synonyms/list")
	public String synonymList(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, ModelMap modelMap, HttpServletRequest request) {
		try {		
			String url = solrSearcher.getSolrServerUrl() +"schema/analysis/synonyms/english";	
			HashMap<String, List<String>> managedSynonyms = SynonymHelper.getSynonymList(url);
			List<SynonymUI> uiList = new ArrayList<SynonymUI>();
			for (String key:managedSynonyms.keySet()){
				SynonymUI ui = new SynonymUI();
				ui.setWord(key.toString());
				ui.setSynonyms(managedSynonyms.get(key));
				uiList.add(ui);
			}
			modelMap.addAttribute("managedSynonyms", uiList);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return "admin/managedSynonyms/list";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/admin/synonyms/edit")
	public String editSynonym(@RequestParam(value = "word", required = true) String word, HttpServletRequest request, ModelMap modelMap, SynonymForm form) {
		String url = solrSearcher.getSolrServerUrl() +"schema/analysis/synonyms/english";
		HashMap<String, List<String>> managedSynonyms = SynonymHelper.getSynonymList(url);
		if (StringUtils.isNotEmpty(word) && managedSynonyms != null){			
			form.setSynonyms(StringUtils.join(managedSynonyms.get(word),','));
			form.setWord(word);		

		}
		return "admin/managedSynonyms/edit";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/admin/synonyms/addNew")
	public String addNewSynonym(HttpServletRequest request, ModelMap modelMap, SynonymForm form) {		
		return "admin/managedSynonyms/edit";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/admin/synonyms/save")
	public String saveSynonym(HttpServletRequest request, ModelMap modelMap, @Valid SynonymForm form, BindingResult result, RedirectAttributes redirect) {

		if (result.hasErrors()) {
			return editSynonym(form.getWord(), request, modelMap, form);
		}

		//String url = "http://localhost:8983/solr/collection1/schema/analysis/synonyms/english";
		String url = solrSearcher.getSolrServerUrl() +"schema/analysis/synonyms/english";		
		try{	    			    			    		
			//First delete the old synonym(s) and then add new one 
			if (StringUtils.isNotEmpty(form.getWord())) {// In case of edit
				
				deleteSynonym(form.getWord(), request, modelMap, null);    	
				String synonyms = form.getSynonyms();
				if (StringUtils.isNotEmpty(synonyms)){			
					String[] arr = StringUtils.split(synonyms, ',');
					if (arr != null) {					
						for (String s:arr){
							deleteSynonym(s, request, modelMap, null);  
						}
					}
				}
			}
		
			int statusCode = SynonymHelper.addSynonym(url, form.getJson());
	
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
					
		}catch(Exception e) {
			e.printStackTrace();
		}


		if (redirect != null) {
			FlashMap.setSuccessMessage("synonym_updated", redirect);
		}
		return "redirect:/admin/synonyms/list";				
	}


	@RequestMapping(method = RequestMethod.GET, value = "/admin/synonyms/delete")
	public String deleteSynonym(@RequestParam(value = "word", required = true) String word, HttpServletRequest request, ModelMap modelMap, RedirectAttributes redirect) {
		String url = solrSearcher.getSolrServerUrl() +"schema/analysis/synonyms/english";	
		HashMap<String, List<String>> managedSynonyms = SynonymHelper.getSynonymList(url);
		if (StringUtils.isNotEmpty(word) && managedSynonyms != null){						
			int statusCode = SynonymHelper.removeSynonym(url, word);

			if (statusCode != 200) {
				//throw new RuntimeException("Failed : HTTP error code : " + statusCode);
			}else{
				if (redirect != null) { //do not reload or reindex if delete is being called from edit method. 'editSynonym' has its own reloadCore and reindex.
					//scheduleDAO.scheduleJob("REINDEX_ALL");  
				}	            	     	         	
			}
		}
		if (redirect != null) {		
			com.sa.search.util.FlashMap.setSuccessMessage("synonym_updated", redirect);
		}
		return "redirect:/admin/synonyms/list";				
	}	

	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/document/add")
	public String addDocument(HttpServletRequest request, ModelMap modelMap, DocumentForm form) {	
				
		form.setSmartSuggest(true);
		return "admin/document/add";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/admin/document/add")
	public String addDocumentPost(HttpServletRequest request, ModelMap modelMap, @Valid DocumentForm form, BindingResult result, RedirectAttributes redirect) {
	
		if (result.hasErrors()) {
			return "admin/document/add";
		}
		
		CmsDocument cmsDocument = new CmsDocument();
		
		String id = form.getId();
		if (StringUtils.isBlank(id)) {
			Random r = new Random();
			int Low = 3;
			int High = 999999;
			int n = r.nextInt(High-Low) + Low;
			
			cmsDocument.setUmbNodeId(n);
		} else {
			cmsDocument.setId(id);
		}
			
		cmsDocument.setPageTitle(form.getPageTitle());
		cmsDocument.setPageContent(form.getPageContent());
		cmsDocument.setPageUrl(form.getPageUrl());
		cmsDocument.setSmartSuggest(form.getSmartSuggest());
		cmsDocument.setDocType(form.getDocType());
	
		cmsDocument.setMimeType(form.getMimeType());;
		cmsDocument.setSiteSection(form.getSiteSection());
		cmsDocument.setImagePath(form.getImagePath());;
		
		// location
		String postcode = form.getPostcode();
		if (StringUtils.isNotBlank(postcode)) {
			List<OsCodePoint> osCodePointList = osCodePointDAO.findByPostcode(postcode, Coverage.UK);
			if (osCodePointList != null && osCodePointList.size() == 1) {
				OsCodePoint osCodePoint = osCodePointList.get(0);
				//String longitude =  Double.toString(osCodePoint.getLongtitude());
				
				cmsDocument.setLatitude(osCodePoint.getLatitude());
				cmsDocument.setLongitude(osCodePoint.getLongtitude());
			
			} else {
				LatLng latLng = osCodePointDAO.findLatLngByPostcode(postcode, com.sa.search.db.mongo.model.OsCodePoint.Coverage.UK);
				if (latLng != null) {
    	        	Double lat = latLng.getLatitude();
    	          	Double lng = latLng.getLongitude();
    	          	
    				cmsDocument.setLatitude(lat);
    				cmsDocument.setLongitude(lng);
				}
			}
//			List<SAAddress> listAddresses = addressMasterUKDAO.findByPostcode(postcode, Coverage.UK);
//			if (listAddresses != null && listAddresses.size() > 0 ) {
//				// just use the first?
//				SAAddress addressmaster = listAddresses.get(0);
//				//String latitude = Double.toString(addressmaster.getLatitude());
//				String longitude =  Double.toString(addressmaster.getLongtitude());
//				
//				cmsDocument.setLatitude(addressmaster.getLatitude());
//				cmsDocument.setLongitude(addressmaster.getLongtitude());
//			}
		}

		//dates
		String dateFrom = form.getDateFrom();
		String dateTo = form.getDateTo();
		if (StringUtils.isNotEmpty(dateFrom)) {
			try {
				cmsDocument.setDateFrom(sdf.parse(dateFrom));
				if (StringUtils.isNotEmpty(dateTo)) {
					cmsDocument.setDateTo(sdf.parse(dateTo));
				}
			} catch (Exception e) {
				m_log.error(e.getCause());
			}
		} else {
			String dateList = form.getDateList();
			if (StringUtils.isNotEmpty(dateList))  {
				String[] dates = dateList.split(",");
				if (dates != null) {
					List<Date> cmsDates = cmsDocument.getDates();
					for (String date : dates) {
						try {
							cmsDates.add(sdf.parse(date));
						} catch (ParseException e) {
							m_log.error(e.getCause());
						}
					}
				}
			}
		}
	
		//prices
		Double priceFrom = form.getPriceFrom();
		Double priceTo = form.getPriceTo();
		if (priceFrom != null) {
			cmsDocument.setPriceFrom(priceFrom);
			if (priceTo != null) {
				cmsDocument.setPriceTo(priceTo);
			}
		}
		
		// contexts
		List<String> contexts =  form.getContext();
		cmsDocument.setContexts(contexts);
		
		// classifications
		List<String> andClassifications = new ArrayList<String>();
		
		String classifications1 = form.getClassification1();
		if (form.getClassification1() != null) {
			andClassifications.add(classifications1);
		}
		cmsDocument.setAndClassifications(andClassifications);
		
		List<String> orClassifications = new ArrayList<String>();
		List<String> classifications2 = form.getClassification2();
		if (classifications2 != null) {
			for (String l : classifications2) {
				orClassifications.add(l);
			}
		} 
		cmsDocument.setOrClassifications(orClassifications);
				
		indexManager.indexCmsDocument(cmsDocument);
		
		if (redirect != null) {
			FlashMap.setSuccessMessage("document_added", redirect);
		}
		return "redirect:/account/home";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/document/edit")
	public String editDocument(@RequestParam(value = "id", required = true) String id,
			HttpServletRequest request, ModelMap modelMap, DocumentForm form) {		
		
		//String[] s = id.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
		
		CmsDocument cmsDocument = searchService.findDocument(id);
		if (cmsDocument != null) {
			form.setId(id);
			form.setPageTitle(cmsDocument.getPageTitle());
			form.setPageContent(cmsDocument.getPageContent());
			form.setPageUrl(cmsDocument.getPageUrl());
			form.setSmartSuggest(cmsDocument.getSmartSuggest());
			form.setContext(cmsDocument.getContexts());
			
			List<String> andClassifications = cmsDocument.getAndClassifications();
			if (andClassifications != null) {
				for (String classification : andClassifications) {
					
					for (String c : classifications1) {
						if (c.equalsIgnoreCase(classification)) {
							form.setClassification1(c);
							break;
						}
					}
				}
			}
			
			List<String> orClassifications = cmsDocument.getOrClassifications();
			if (orClassifications != null) {
				for (String classification : orClassifications) {
					
					for (String c : classifications2) {
						if (c.equalsIgnoreCase(classification)) {
							form.getClassification2().add(c);
							break;
						}
					}
				}
			}
		}
			
		return "admin/document/edit";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/document/remove")
	public String removeDocument(@RequestParam(value = "id", required = true) String id,
			HttpServletRequest request, ModelMap modelMap, SearchForm form, RedirectAttributes redirect) {		
		
		indexManager.removeDocument(id);
		
		FlashMap.setSuccessMessage("document_removed", redirect);
		
		return "admin/search";
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/admin/search")
	public String search(HttpServletRequest request, ModelMap modelMap, SearchForm form) {		
		return "admin/search";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/admin/search")
	public String searchPost(HttpServletRequest request, ModelMap modelMap, @Valid SearchForm form, BindingResult result, RedirectAttributes redirect) {
		
		SearchInfo searchInfo = new SearchInfo();
		
		searchInfo.setSearchText(form.getSearchText());
		searchInfo.setPc(form.getLocation());
		Integer radius = form.getRadius();
		if (radius != null) {
			searchInfo.setRadius(radius.toString());
		}
		
		String fromDate = form.getDateFrom();
		if (fromDate != null) {
			searchInfo.setDateFrom(fromDate);
		}
		String toDate = form.getDateTo();
		if (toDate != null) {
			searchInfo.setDateTo(toDate);
		}
		
		searchInfo.setContextIdList(form.getContext());
		searchInfo.setSortOption(form.getSortOption());
		searchInfo.setSearchableClassificationOrIdList(form.getSearchableClassifications());
		searchInfo.setBoostClassificationOrIdList(form.getBoostClassifications());
		
		//searchInfo.setPageSize(2);
		
		searchService.doSearch(request, modelMap, searchInfo, false);
		
		SearchResult searchResult = (SearchResult)modelMap.get("searchResult");
		
		// get Atoz facets
		configureAtoZ(modelMap, searchResult);

		if (searchResult != null && searchInfo.getLatitude() != null) {
			configureMapResults(modelMap, searchResult.getItems(), searchInfo.getLatitude().toString(), searchInfo.getLongitude().toString());
		}
		
		// 
		if (form.isShowAllClassifications()) {
			showAllClassifications(searchResult);
		}
		
		request.getSession().setAttribute("searchInfo", null);
		request.getSession().removeAttribute("searchInfo");
		request.getSession().setAttribute("searchInfo", searchInfo);
			
		return "admin/search";
	}
	
	@RequestMapping(value = "/admin/atozSearch")
	public String atozSearchPost(@RequestParam(value = "atoz", required = true) String atoz,
			HttpServletRequest request, ModelMap modelMap, SearchForm form) {
		
		SearchInfo searchInfo = new SearchInfo();
		searchInfo.setAtozSearch(true);
		searchInfo.setSearchText(atoz);
		
		searchService.doSearch(request, modelMap, searchInfo, false);
		
		SearchResult searchResult = (SearchResult)modelMap.get("searchResult");
		
		// get Atoz facets
		configureAtoZ(modelMap, searchResult);

		
		return "admin/search";
	}
		
	@RequestMapping(method = RequestMethod.GET, value = "/admin/recommendedLinks/list")
	public String recommendedLinksList(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, ModelMap modelMap, HttpServletRequest request) {


		int sizeNo = size == null ? 10 : size.intValue();
		List<RecommendedLink> recommendedlinks = recommendedLinkDAO.findEntries( page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo);

		modelMap.addAttribute("recommendedLinks", recommendedlinks);
		long count = recommendedLinkDAO.count();
		float nrOfPages = (float)count / sizeNo;
		modelMap.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));

		return "admin/recommendedLinks/list";
	}

	@RequestMapping(value="/admin/recommendedLinks/new")
	public String newRecommendlink(ModelMap modelMap, RecommendeLinkForm form, HttpServletRequest request) {

		return "admin/recommendedLinks/edit";
	}

	@RequestMapping(value="/admin/recommendedLinks/edit")
	public String editRecommendLink(@RequestParam(value = "id", required = true) String id,
			ModelMap modelMap, RecommendeLinkForm form, HttpServletRequest request) {

		RecommendedLink recommendedlink = recommendedLinkDAO.findOne(id);

		form.setId(id);
		form.setKeyword(recommendedlink.getKeyword());
		form.setTitle(recommendedlink.getTitle());
		form.setDescription(recommendedlink.getDescription());
		form.setUrl(recommendedlink.getUrl());


		return "admin/recommendedLinks/edit";
	}


	@RequestMapping(method = RequestMethod.POST, value="/admin/recommendedLinks/edit")
	public String editRecommendedLinkPost (HttpServletRequest request, ModelMap modelMap, @Valid RecommendeLinkForm form, BindingResult result, RedirectAttributes redirect) {

		if (result.hasErrors()) {		 
			return "admin/recommendedLinks/edit";
		}


		RecommendedLink recommendedlink;

		if (form.getId() != null) {
			recommendedlink = recommendedLinkDAO.findOne(form.getId());

		} else {
			recommendedlink = new RecommendedLink();
		}

		if (recommendedlink == null){
			recommendedlink = new RecommendedLink();
		}

		recommendedlink.setKeyword(form.getKeyword());
		recommendedlink.setTitle(form.getTitle());
		recommendedlink.setDescription(form.getDescription());
		recommendedlink.setUrl(form.getUrl());

		recommendedLinkDAO.save(recommendedlink);

		FlashMap.setSuccessMessage("details_updated", redirect);

		return "redirect:/admin/recommendedLinks/list";
	}

	@RequestMapping(value="/admin/recommendedLinks/delete")
	public String deleteRecommendedLink(@RequestParam(value = "id", required = true) String id,
			ModelMap modelMap, RecommendeLinkForm form, RedirectAttributes redirect) {

		RecommendedLink recommendedlink = recommendedLinkDAO.findOne(id);

		recommendedLinkDAO.delete(recommendedlink);

		FlashMap.setSuccessMessage("details_updated", redirect);

		return "redirect:/admin/recommendedLinks/list";
	}
	
	
	public static void configureAtoZ(ModelMap modelMap, SearchResult searchResult) {
		
		List<ResultFacet> resultFacets = searchResult.getResultFacets();
		
		if (resultFacets != null && resultFacets.size() > 0) {
			
			List<ResultFacetEntry> entries = null;
			
			for (ResultFacet resultFacet : resultFacets) {
				String facetName = resultFacet.getName();
				if ("AtoZ".equalsIgnoreCase(facetName)) {
					entries = resultFacet.getEntries();
					break;
				}
			}
			
			if (entries != null) {
				
				Map<String, Long> atozFacets = new HashMap<String, Long>();
				for (ResultFacetEntry entry : entries) {
					atozFacets.put(entry.getName(), entry.getCount());
				}
				
				ArrayList<AZInfo> alphaList = new ArrayList<AZInfo>();
				for(int i=0; i < allAlpha.length; i++){
					AZInfo curList = new AZInfo();
					Long count = atozFacets.get(allAlpha[i]);
					curList.setLetter(allAlpha[i]);

					if (count == null){
						curList.setCount("0");
					}
					else {
						curList.setCount(count.toString());
					}

					alphaList.add(i, curList);
				}
				modelMap.put("alphaNumConfig", alphaList);
			}
		}
	}

	public static void configureMapResults(ModelMap modelMap, List<DisplayItem> items, String lat, String lng) {

		modelMap.put("showmap", true);
		modelMap.put("lat", lat.toString());
		modelMap.put("lng", lng.toString());
		modelMap.put("zoom","12"); 
		

		if (items != null) {
			List<MapPin> mapPins = new ArrayList<MapPin>();
			int index = 1;

			for (DisplayItem di : items) {
				Double latitude = di.getLatitude();
				Double longitude = di.getLongitude();
				if (lat != null && lng != null) {
					MapPin mapPin = new MapPin();
					mapPin.setLatitude(latitude.toString());
					mapPin.setLongtitude(longitude.toString());

					StringBuffer icon;
					icon = new StringBuffer("/pcgsearch/images/maps/pinburgandy.png"); 
					icon.insert(icon.indexOf(".png"), index);
					mapPin.setIcon(icon.toString());

					mapPin.setTitle(di.getTitle());
					mapPins.add(mapPin);
					index++;
				}
			}

			if (mapPins.size() > 0) {
				modelMap.put("showmap", true);
			}

			String jsonPins = MapPin.toJsonArray(mapPins);
			modelMap.put("jsonPins", jsonPins);
		}

		
	}

	public static void showAllClassifications(SearchResult searchResult) {
		
		List<ResultFacet> resultFacets = searchResult.getResultFacets();
		
		List<ResultFacetEntry> entries = null;
		
		if (resultFacets != null && resultFacets.size() > 0) {
			for (ResultFacet resultFacet : resultFacets) {
				String facetName = resultFacet.getName();
				if ("Must have".equalsIgnoreCase(facetName)) {
					entries = resultFacet.getEntries();
					for (String id: classifications1) {
						boolean found = false;
						for (ResultFacetEntry entry : entries) {
							String name = entry.getName();
							if (id.equalsIgnoreCase(name)) {
								found = true;
							}
							break;
						}
						if (StringUtils.isNotBlank(id) && found == false) {
							ResultFacetEntry resultFacetEntry = resultFacet.new ResultFacetEntry();
							resultFacetEntry.setName(id);
							resultFacetEntry.setCount(0);
							resultFacetEntry.setDisabled(true);
							entries.add(resultFacetEntry);
						}
					}
					
				} else if ("Optional".equalsIgnoreCase(facetName)) {
					entries = resultFacet.getEntries();
					for (String id: classifications2) {
						boolean found = false;
						for (ResultFacetEntry entry : entries) {
							String name = entry.getName();
							if (id.equalsIgnoreCase(name)) {
								found = true;
							}
							break;
						}
						if (StringUtils.isNotBlank(id) && found == false) {
							ResultFacetEntry resultFacetEntry = resultFacet.new ResultFacetEntry();
							resultFacetEntry.setName(id);
							resultFacetEntry.setCount(0);
							resultFacetEntry.setDisabled(true);
							entries.add(resultFacetEntry);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Utility method for multi context system configs
	 * @param request
	 * @param modelMap
	 * @param redirect
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET, value="/admin/systemConfig/list")
	public String getAllSystemConfig(HttpServletRequest request, ModelMap modelMap,RedirectAttributes redirect){
		List<ConfigUI> configList = new ArrayList<ConfigUI>();
		List<SystemConfig> configContexts = systemConfigDAO.findAll();
		if(!configContexts.isEmpty() && configContexts.size() >1 ){
			for(SystemConfig sysConfig : configContexts) {
				ConfigUI config = new ConfigUI();
				config.setConfigId(sysConfig.getId());
				String display = systemConfigProp.getProperty(sysConfig.getId());
				if(!StringUtils.isBlank(display)){
					config.setConfigDesc(display);
				}else {
					config.setConfigDesc(sysConfig.getId());
				}
				configList.add(config);
			}
			modelMap.addAttribute("configList", configList);	
			List<String> args = new ArrayList<String>();
			args.add(SYSTEM);
			return "admin/systemConfig/listAllContextsConfig";
		}
		return "redirect:/admin/systemConfig/getConfig";
	}


	/**
	 * Helper method to get All Non primitive types of List,Map or IAssistConfig configuration for display from SystemConfig.
	 * 
	 * @param request
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(method=RequestMethod.GET, value="/admin/systemConfig/getConfig")
	public String getConfig(HttpServletRequest request, ModelMap modelMap,
			@RequestParam(value="configId",required=false) String configId,
			@RequestParam(value="flushAllContexts",required=false) Boolean flushAllContexts) {
		List<ConfigUI> configList = new ArrayList<ConfigUI>();
		List<String> args = new ArrayList<String>();
		SystemConfig configs = null;
		if( !StringUtils.isEmpty(configId)){
			configs = systemConfigDAO.getSystemConfigForContext(configId);
			args.add(configId);
			modelMap.addAttribute(CONFIG_ID, configId);
		}else {
			configs = systemConfigDAO.getDefaultSystemConfig();
			args.add(SYSTEM);
			configId = StringUtils.EMPTY;
			modelMap.addAttribute(CONFIG_ID, configId);
		}
		modelMap.addAttribute(SYSTEMCONFIG, configs);

		try {
			Map<String, Object> map =  PropertyUtils.describe(configs);
			for (String key:map.keySet()){
				Object o = map.get(key);
				if(o instanceof IPcgSearchConfig ||o instanceof List || o instanceof Map || o instanceof Class ) {
					ConfigUI config = new ConfigUI();
					String display = "";

					//This is required to get the SystemConfig configurations,stored under the KEY "class" in Map
					if(o instanceof Class){
						display = systemConfigProp.getProperty(SYSTEMCONFIG);
						config.setPriority(1);
					}else{
						display = systemConfigProp.getProperty(key);
						config.setPriority(0);
					}
					//Fix - If property is missing in systemConfig property file add key name
					if(!StringUtils.isBlank(display)){
						config.setConfigDesc(display);
					}else {
						config.setConfigDesc(key);
					}
					config.setConfigId(key);
					configList.add(config);
					Collections.sort(configList,ConfigUI.ConfigNameComparator);

				}
			}
		} catch (IllegalAccessException e) {
			m_log.error("Failure while getting the Config Details ", e);
		} catch (InvocationTargetException e) {
			m_log.error("Failure while  getting the Config Details ", e);
		} catch (NoSuchMethodException e) {
			m_log.error("Failure while  getting the Config Details NO Such method ", e);
		}


		modelMap.addAttribute("configList", configList);	

		return "admin/systemConfig/list";
	}

	@RequestMapping(method=RequestMethod.GET, value="/admin/systemConfig/edit/{fieldName}")
	public String editConfig(HttpServletRequest request, ModelMap modelMap, @PathVariable(FIELD_NAME) String fieldName,
			@RequestParam(value="configId",required=false) String configId, SystemConfigForm systemConfigForm){		

		SystemConfig config= null;
		if( !StringUtils.isEmpty(configId)){
			config = systemConfigDAO.getSystemConfigForContext(configId);
			modelMap.addAttribute(CONFIG_ID, configId);
		}else {
			//set empty for system config having no context
			config = systemConfigDAO.getDefaultSystemConfig();
			configId = StringUtils.EMPTY;
			modelMap.addAttribute(CONFIG_ID, configId);
		}
		List<String> args = new ArrayList<String>();
		Object innerConfig = null;

		try {
			//This is required to get only the Primitive type fields under SystemConfig
			if(fieldName.equalsIgnoreCase(CLASS)){
				LinkedHashMap<String, SystemConfigUIItem> fieldsMap = new LinkedHashMap<String, SystemConfigUIItem>();

				Map<String, Object> map = PropertyUtils.describe(config);	
				//Remove the id attribute as its non editable field
				if (map != null && map.containsKey(ID)){
					map.remove(ID);
				}
				if (map != null && map.containsKey(CLASS)){
					for (String key:map.keySet()){
						Object o = map.get(key);
						boolean canDisplay = (o!=null)?(o.getClass().equals(String.class) || ClassUtils.isPrimitiveOrWrapper(o.getClass())):false;
						if (canDisplay ){
							if( o instanceof String){
								o = deHTML(o.toString());
							}
							SystemConfigUIItem item = new SystemConfigUIItem(key, o, canDisplay);	
							fieldsMap.put(key, item);
						}
					}

					SystemConfigUI systemConfigUI = new SystemConfigUI(fieldsMap);
					args.add(SYSTEM);
					modelMap.addAttribute(SYSTEMCONFIG_UI, systemConfigUI);
					//FieldName is not required for primitive types under SystemConfig,So setting empty
					modelMap.addAttribute(FIELD_NAME,StringUtils.EMPTY);			

					return "admin/systemConfig/view";

				}
			}

			String fullPath = fieldName.replaceAll(SYSTEM_CONFIG_FIELD_SEPARATOR, ".");		
			String innerFieldName = fieldName.substring(fieldName.lastIndexOf(SYSTEM_CONFIG_FIELD_SEPARATOR) + 1);
			String partialPath = (fullPath.indexOf(".") >-1)?fullPath.substring(0, fullPath.lastIndexOf(".")):fullPath;

			if (partialPath != null && !partialPath.equalsIgnoreCase(innerFieldName)){				
				innerConfig = PropertyUtils.getProperty(config,partialPath );
			}
			Object value = null;
			PropertyDescriptor pd = null;
			if (innerConfig != null) {
				pd = PropertyUtils.getPropertyDescriptor(innerConfig,innerFieldName);
				if (pd != null) {
					value=PropertyUtils.getProperty(innerConfig,innerFieldName);
				}else{ //This is a special case for directoryInfos
					value=extractValue(innerConfig, innerFieldName);
				}
			}else{				
				value = PropertyUtils.getProperty(config,fullPath );	
				pd = PropertyUtils.getPropertyDescriptor(config,fullPath);
			}
			systemConfigForm.setFullPath(fullPath);
			systemConfigForm.setValue(value);
			systemConfigForm.setType((value!=null)?value.getClass().getName():(pd!=null)?pd.getPropertyType().getName():null);
			systemConfigForm.setName(fieldName.substring(fieldName.lastIndexOf(SYSTEM_CONFIG_FIELD_SEPARATOR) + 1));


			if(!StringUtils.isEmpty(configId)) {systemConfigForm.setConfigId(configId); }
			Class cls = (value!=null)?value.getClass():(pd!=null)?pd.getPropertyType():String.class;
			if (!(cls.equals(String.class) || ClassUtils.isPrimitiveOrWrapper(cls))) {				
				SystemConfigUI systemConfigUI = new SystemConfigUI(systemConfigForm.getValue());

				modelMap.addAttribute(SYSTEMCONFIG_UI, systemConfigUI);	
				modelMap.addAttribute(FIELD_NAME,fieldName);				
				args.add(fieldName.toUpperCase());

				return "admin/systemConfig/view";
			}else{
				args.add(fullPath);
				systemConfigForm.setEditable(true);
			}

		} catch (Exception e) {
			m_log.error("Failure while editing the Config Details ", e);
		}
		modelMap.addAttribute("systemConfigForm", systemConfigForm);

		return "admin/systemConfig/edit"; 
	}


	private Object extractValue(Object config, String innerFieldName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {	
		if (config instanceof List) {
			List sConfig = (ArrayList)config;
			int indx = Integer.parseInt(innerFieldName);
			return PropertyUtils.getIndexedProperty(sConfig,innerFieldName, indx);
		}else
			if (config instanceof Map){
				return ((Map)config).get(innerFieldName);
			}else
				if (config instanceof IPcgSearchConfig){
					return config;
				}

		return null;
	}

	@RequestMapping(method=RequestMethod.POST, value="/admin/systemConfig/edit")
	public String editConfigPost(HttpServletRequest request, ModelMap modelMap, @Valid SystemConfigForm systemConfigForm,BindingResult result, RedirectAttributes redirect){
		String configId = null;
		List<SystemConfig> configContexts = systemConfigDAO.findAll();
		Boolean flushAllContexts = false;
		try {			
			String fullPath = systemConfigForm.getFullPath();
			Object value = systemConfigForm.getValue();
			configId = systemConfigForm.getConfigId();

			systemConfigFormValidator.validate(systemConfigForm, result);
			if (result.hasErrors()) {				
				return editConfig( request,modelMap, fullPath.replace(".",SYSTEM_CONFIG_FIELD_SEPARATOR), configId,systemConfigForm);
			}

			SystemConfig sysConfig = null;
			if( StringUtils.isNotBlank(configId)){
				sysConfig = systemConfigDAO.getSystemConfigForContext(configId);
				modelMap.addAttribute(CONFIG_ID, configId);
			}else {
				sysConfig = systemConfigDAO.getDefaultSystemConfig();
				configId = StringUtils.EMPTY;
				modelMap.addAttribute(CONFIG_ID,configId);
			}

			PropertyDescriptor desc = PropertyUtils.getPropertyDescriptor(sysConfig, fullPath);
			if (desc != null) { //Property exists
				if (value!=null && ClassUtils.isPrimitiveOrWrapper(desc.getPropertyType())){							
					//String propName = desc.getPropertyType().getName().toLowerCase();
					Class cls = desc.getPropertyType();
					if (int.class.equals(cls) || Integer.class.equals(cls))
						PropertyUtils.setProperty(sysConfig, fullPath, Integer.parseInt(value.toString()));
					else if (long.class.equals(cls) || Long.class.equals(cls))
						PropertyUtils.setProperty(sysConfig, fullPath, Long.parseLong(value.toString()));
					else if (double.class.equals(cls) || Double.class.equals(cls))
						PropertyUtils.setProperty(sysConfig, fullPath, Double.parseDouble(value.toString()));
					else if (float.class.equals(cls) || Float.class.equals(cls))
						PropertyUtils.setProperty(sysConfig, fullPath, Float.parseFloat(value.toString()));
					else if (boolean.class.equals(cls) ||Boolean.class.equals(cls))
						PropertyUtils.setProperty(sysConfig, fullPath, Boolean.parseBoolean(value.toString()));
				}else{
					PropertyUtils.setProperty(sysConfig, fullPath, value);
				}
			}else{
				String innerFieldName = fullPath.substring(fullPath.lastIndexOf(".") + 1);
				String partialPath = (fullPath.indexOf(".") >-1)?fullPath.substring(0, fullPath.lastIndexOf(".")):fullPath;
				Object prop = PropertyUtils.getProperty(sysConfig, partialPath);
				if (prop instanceof Map ){
					PropertyUtils.setMappedProperty(sysConfig, partialPath, innerFieldName, value);
				}else if (prop instanceof List){
					PropertyUtils.setProperty(sysConfig, fullPath, value);					
				}
			}

			systemConfigDAO.save(sysConfig);

			if (configContexts != null && configContexts.size() == 1) {
				CacheHandler.flushAllCaches();
			} else {
				flushAllContexts = true;
			}

		} catch (Exception e) {
			m_log.error("Failure while Saving the Config Details ", e);
		}
	
		return "redirect:/admin/systemConfig/getConfig?configId="+configId+"&flushAllContexts="+flushAllContexts; 
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
	
	@RequestMapping(method=RequestMethod.GET, value="admin/flush")
	public @ResponseBody String flushCaches() {
		m_log.debug("flush cache method called>>>>>>>>>");
		CacheHandler.flushAllCaches();
		return "Caches flushed";
	}
}
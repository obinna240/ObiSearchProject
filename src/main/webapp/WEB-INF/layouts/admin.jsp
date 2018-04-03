<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sa" tagdir="/WEB-INF/tags/sa" %>
<!doctype html>
<!--[if lt IE 7]><html class="no-js lt-ie9 lt-ie8 lt-ie7" lang="en"> <![endif]-->
<!--[if (IE 7)&!(IEMobile)]><html class="no-js lt-ie9 lt-ie8" lang="en"><![endif]-->
<!--[if (IE 8)&!(IEMobile)]><html class="no-js lt-ie9" lang="en"><![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"><!--<![endif]-->
<head>
	<meta charset="utf-8">
	<c:set var="_pT" value="${empty _pT ? 'Welcome' : _pT}"></c:set>
	<title>Search application</title>
	<meta name="description" content="">
	<meta name="author" content="">
	<meta name="HandheldFriendly" content="True">
	<meta name="MobileOptimized" content="320">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	
	<link href='//fonts.googleapis.com/css?family=Open+Sans+Condensed:300,700' rel='stylesheet' type='text/css'>
	<link href='//fonts.googleapis.com/css?family=Open+Sans:400,600' rel='stylesheet' type='text/css'>
	
	<!-- styles -->
	<spring:url var="css_bootstrap_url" value="/css/bootstrap/bootstrap.min.css" />
	<spring:url var="css_core_url" value="/css/core/core.css" />
	<spring:url var="css_override_url" value="/css/override.css" />
	<spring:url var="css_contrast_url" value="/css/core/contrast.css" />
	<spring:url var="css_jquery_plugins_url" value="/css/core/jquery.plugins.css" />
	<spring:url var="css_jqueryui_url" value="/css/core/jquery-ui.css" />
	
    <link href="${css_bootstrap_url}" rel="stylesheet"/>
    <link href="${css_core_url}" rel="stylesheet"/>
	<link href="${css_override_url}" rel="stylesheet"/>
    <link rel="alternate stylesheet" title="contrast" data-type="contrast" class="alt-style" type="text/css" media="screen" href="${css_contrast_url}" />
    <link href="${css_jquery_plugins_url}" rel="stylesheet" />
	<link href="${css_jqueryui_url}" rel="stylesheet" />

	<!-- Respond for all those old IE users out there, loaded early to prevent unstyled IE content pop-ups -->
	<spring:url var="js_respond_url" value="/js/core/static/respond.min.js" />
	<script src="${js_respond_url}"></script>
	
	<!-- Javascript, have jquery up here so we can do some inline scripts -->
	<script src="//cdn.jquerytools.org/1.2.7/full/jquery.tools.min.js"></script>
	
	<!-- Modernizr for the html5shim -->
	<spring:url var="modernizr_url" value="/js/core/static/modernizr-2.5.3-min.js" />
	<script src="${modernizr_url}"></script>

	<!-- For iPhone 4 -->
	<!-- <link rel="apple-touch-icon-precomposed" sizes="114x114" href="img/h/apple-touch-icon.png"> -->
	<!-- For iPad 1-->
	<!-- <link rel="apple-touch-icon-precomposed" sizes="72x72" href="img/m/apple-touch-icon.png"> -->
	<!-- For the new iPad -->
	<!-- <link rel="apple-touch-icon-precomposed" sizes="144x144" href="img/h/apple-touch-icon-144x144-precomposed.png"> -->
	<!-- For iPhone 3G, iPod Touch and Android -->
	<!-- <link rel="apple-touch-icon-precomposed" href="img/l/apple-touch-icon-precomposed.png"> -->
	<!-- For Nokia -->
	<!-- <link rel="shortcut icon" href="img/l/apple-touch-icon.png"> -->
	<!-- For everything else -->
	<spring:url var="favicon_url" value="/favicon.ico" />
	<link rel="shortcut icon" href="${favicon_url}" />
	  
	<!-- iOS -->
	<!-- <meta name="apple-mobile-web-app-capable" content="yes"> -->
	<!-- <meta name="apple-mobile-web-app-status-bar-style" content="black"> -->
	<!-- <link rel="apple-touch-startup-image" href="img/splash.png"> -->

	<!-- <script>(function(){var p,l,r=window.devicePixelRatio;if(navigator.platform==="iPad"){p=r===2?"img/startup/startup-tablet-portrait-retina.png":"img/startup/startup-tablet-portrait.png";l=r===2?"img/startup/startup-tablet-landscape-retina.png":"img/startup/startup-tablet-landscape.png";document.write('<link rel="apple-touch-startup-image" href="'+l+'" media="screen and (orientation: landscape)"/><link rel="apple-touch-startup-image" href="'+p+'" media="screen and (orientation: portrait)"/>');}else{p=r===2?"img/startup/startup-retina.png":"img/startup/startup.png";document.write('<link rel="apple-touch-startup-image" href="'+p+'"/>');}})()</script> -->
	<!--Microsoft -->

	<!-- Prevents links from opening in mobile Safari -->
	<!-- <script>(function(a,b,c){if(c in b&&b[c]){var d,e=a.location,f=/^(a|html)$/i;a.addEventListener("click",function(a){d=a.target;while(!f.test(d.nodeName))d=d.parentNode;"href"in d&&(d.href.indexOf("http")||~d.href.indexOf(e.host))&&(a.preventDefault(),e.href=d.href)},!1)}})(document,window.navigator,"standalone")</script> -->
	<meta http-equiv="cleartype" content="on">
		
</head>

<body class="clearfix">
	<tiles:insertAttribute name="header" ignore="true" />
 	
 	
 	<div class="container content">
		<div class="clearfix">
			<div class="row">
				<div class="col-sm-3" id="sub-nav"><tiles:insertAttribute name="subnav" /></div>
		    	<div class="col-sm-9" id="body"><tiles:insertAttribute name="body"/></div> 
	    	</div>
   		</div>			   		
    </div>
    
    <tiles:insertAttribute name="footer" ignore="true"/>

	<!-- Analytics
	    ================================================== -->
		<c:if test="${!empty config.googleAnalyticsId}">	
		<script type="text/javascript">
		   var _url = document.location.href;
		   var _gaq = _gaq || [];
		  _gaq.push(['_setAccount', '${config.googleAnalyticsId}']);
		  _gaq.push(['_trackPageview', _url]);   
		  
		  (function() {
			var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
			ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
			var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
		  })();
		
		</script> 
		</c:if> 
	
		<div id="ajaxMessage"></div>   
	    <div id="greyscale"></div>
	   
	   	<c:url var="js_bootstrap_url" value="/js/bootstrap/bootstrap.min.js" />
		<c:url var="js_jquery_backup_url" value="/js/core/static/jquery-1.11.0.min.js" /> 
		<c:url var="js_jquery_search_url" value="/js/core/jquery-search.js" />
		<c:url var="js_bing_url" value="https://www.bing.com/mapspreview/sdk/mapcontrol?callback=loadMapScenario" />
		<c:url var="js_map_url" value="/js/core/jquery-map.js" /> 
		<c:url var="js_plugins_url" value="/js/core/static/jquery-plugins.js" />
		<c:url var="js_jqueryui_url" value="/js/core/static/jquery-ui.min.js" />
	    
	    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
	    <script>window.jQuery || document.write('<script src="${js_jquery_backup_url}"><\/script>')</script>
	    <!-- Include all compiled plugins (below), or include individual files as needed -->
	    <script src="${js_jqueryui_url}"></script>
	    <script src="${js_plugins_url}"></script>
	    <script src="${js_bootstrap_url}"></script>
	    <script src="${js_jquery_search_url}"></script>


		<script>
	    	jQuery(function() {
	        	$.Search({
	            });
	         });

	    </script>
	    
	    
	    <c:if test="${showmap == true}">
		   	<c:set var="map_lat" value="${lat}" /><c:if test="${empty map_lat}"><c:set var="map_lat" value="null" /></c:if>
	    	<c:set var="map_lng" value="${lng}" /><c:if test="${empty map_lng}"><c:set var="map_lng" value="null" /></c:if>
	    	<c:set var="map_zoom" value="${zoom}" /><c:if test="${empty map_zoom}"><c:set var="map_zoom" value="9" /></c:if>
	    	<c:set var="map_pins" value="${jsonPins}" /><c:if test="${empty map_pins}"><c:set var="map_pins" value="null" /></c:if>
	 		<c:set var="map_show" value="${showmaps}" /><c:if test="${empty map_show}"><c:set var="map_show" value="${useMaps}" /></c:if><c:if test="${empty map_show}"><c:set var="map_show" value="false" /></c:if>
  			<c:set var="map_hideMapResults" value="${hideMapResults}" /><c:if test="${empty hideMapResults}"><c:set var="map_hideMapResults" value="false" /></c:if>
 
			<script src="${js_bing_url}"></script>
		   	<script src="${js_map_url}"></script>
		   	<script type='text/javascript'>
		   	function loadMapScenario() {
	    		jQuery(function() {
		 			$('#search-map').AssistMap({
						latitude:${map_lat},
						longitude:${map_lng},
						context:'pcgsearch',
						zoom:${map_zoom},
						pins: ${map_pins},
						hideMap: ${map_hideMapResults}
					});
	    		});	
		  	}
            	
        	</script>
		   	
	
	    </c:if>
	</body>

</html>

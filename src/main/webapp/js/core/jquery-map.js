$.fn.AssistMap = function (options) {

	var latitude,
	longitude,
	context,
	zoom,
	pins,
	hideMap,
	map,
	mouseOverPin,
	$this

	//Constructor
	options = $.extend({
		latitude: null,
		longitude: null,
		context: null,
		zoom: 9,
		pins: null,
		hideMap : false
		
	}, options);

	$this = this;
	latitude = options.latitude;
	longitude = options.longitude;
	context = options.context;
	zoom = options.zoom;
	pins = options.pins;
	hideMap = options.hideMap;
	mouseOverPin = false;
	
	if (latitude != null && longitude != null && this.length) {
		var mapId = $this.attr('id');
		loadMap($this.attr('id'));
	}
	
	function loadMap(id) {
	
		map = new Microsoft.Maps.Map(document.getElementById(id), {
         credentials: 'AklzLajIModK9bhLuY2tN4kN3qXYcrkm60qZKek6u4k5so9danDneypraoFIAhr0',
         center: new Microsoft.Maps.Location(latitude, longitude),
         zoom: zoom
		});
     	               
     	var data = pins;	
		if (data.latitude != null) {
			var lat = data.latitude;
			var lng = data.longtitude;
			var title = data.title;
			var desc = data.description;
			
			var loc = new Microsoft.Maps.Location(lat, lng);
			var pushpin;
				if (data.icon != null) {
				var icon = '/'+ context + dataicon;
				pushpin = new Microsoft.Maps.Pushpin(loc, { icon: icon, anchor: new Microsoft.Maps.Point(12, 12) });
				} else {
					pushpin = new Microsoft.Maps.Pushpin(loc, null);
				}
				
			if (title != null) {
				var infobox = new Microsoft.Maps.Infobox(loc, { title: title,
                    description: desc, visible: false });
                infobox.setMap(map);
                Microsoft.Maps.Events.addHandler(pushpin, 'click', function () {
                    infobox.setOptions({ visible: true });
                });
			}
			
            map.entities.push(pushpin);
          	if (data.icon != null) {
         		Microsoft.Maps.Events.addHandler(pushpin, 'mouseover', function () { highlight(pushpin, "pinburgandy", "pinyellow"); });
          	}
				
		} else {
			pinIds = new Array ();
			var index = 1;

			$.each(data, function(i) {
				
				var lat = this.latitude;
				var lng = this.longtitude;
				var title = this.title;
				var desc = this.description;
				
				var loc = new Microsoft.Maps.Location(lat, lng);
				var pushpin;
				if (this.icon != null) {
   					var icon = this.icon;
   					pushpin = new Microsoft.Maps.Pushpin(loc, { icon: icon, anchor: new Microsoft.Maps.Point(12, 12) });
				} else {
					pushpin = new Microsoft.Maps.Pushpin(loc, null);
				}
				
				if (title != null) {
   					var infobox = new Microsoft.Maps.Infobox(loc, { title: title,
   	                    description: desc, visible: false });
   	                infobox.setMap(map);
   	                Microsoft.Maps.Events.addHandler(pushpin, 'click', function () {
   	                    infobox.setOptions({ visible: true });
   	                });
				}
				
                map.entities.push(pushpin);
         		if (this.icon != null) {
             		Microsoft.Maps.Events.addHandler(pushpin, 'mouseover', function () { highlight(pushpin, "pinburgandy", "pinyellow"); });      
         		}
         		index++;
			});
		}
			
		// hide/show map
		if (hideMap == true) {
			//$('.ch-listing').hide();
			$('.ch-listing').slideToggle('fast');
			$('.location').addClass("closed");
			$('.location').removeClass("open");
			$('.location').text("Show results on the map");
		}
	}
	
	function highlight(pushpin, colour1, colour2) {
		if (mouseOverPin == false) {
    		mouseOverPin = true;
    		var icon = pushpin.getIcon();
			var i = icon.lastIndexOf('/');
			var i2 = icon.indexOf('.png');
			var c1 = icon.charAt(i2-1);
			if(c1 == '0' || c1 == '1' || c1 == '2' || c1 == '3' || c1 == '4' || c1 == '5' ||
					c1 == '6' || c1 == '7' ||  c1 == '8' || c1 == '9') {
				var derivedId = icon.substring(i2-1, i2); 
				
				var customIcon = icon.replace(colour1, colour2);
				pushpin.setOptions({icon : customIcon});
				
				// replace panel image                
				var panelImageId = "panelpin" + derivedId;
				var oldPanelImage = document.getElementById(panelImageId).src;
				var customIcon2 = oldPanelImage.replace(colour1, colour2);
				document.getElementById(panelImageId).src=customIcon2;
				
				setTimeout(function () { 
					var customIcon = icon.replace(colour2, colour1);
					pushpin.setOptions({icon : customIcon});
					var customIcon2 = oldPanelImage.replace(colour2, colour1);
					document.getElementById(panelImageId).src=customIcon2;
					mouseOverPin = false;
				}, 1500);
			}
		}
	}
	
	function listOnMouseOver(index, page){

		var pid = index + ((page-1)*10);
		panelImageId = "panelpin" + index;
		panelOldPin = document.getElementById(panelImageId).src;
		var myNewString = panelOldPin.replace("pinburgandy", "pinyellow");
		document.getElementById(panelImageId).src=myNewString;
		if(vemap){
			var pinMouseOver2 = vemap.GetShapeByID(pinIds[pid]);
			if (pinMouseOver2 != null) {
				var icon = pinMouseOver2.GetCustomIcon();
				var image = icon.Image;
				var newString = image.replace("pinburgandy", "pinyellow");
				pinMouseOver2.SetCustomIcon(newString);
			}
		}
	}
}
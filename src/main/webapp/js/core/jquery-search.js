(function ($) {
	$.Search = function(options) {
		//$("#searchText").on('input propertychange paste', function() {
		//	alert('c');
		//});
		// smart suggest
		if ($('#searchText').length) $("#searchText").smartSuggest({src: '/pcgsearch/api/cms/ui/search/getSmartSuggestionsUI', 
			resultsText:'',
			minChars:2,
			fillBox: true});
		
		
		$('.datepicker-mon').datepicker( { dateFormat: "dd/mm/yy",firstDay: 1 });
			var dateSelected = $('.datepicker-mon').val(); 
			if (dateSelected != null) {
			$("#dateFrom").datepicker({dateFormat: 'dd/mm/yy'}).datepicker("setDate", dateSelected);
		}
	  
		// multi date picker
		var dates = $('.datepickerMultiVal').val(); 
		if (dates != null) {
			$('.datepickerMulti').datepick(
					{
						dateFormat: 'dd/mm/yy', 
						multiSelect: 999,  
						minDate: 1,
						altField: inlineDatepickerVal
					}
			);

			var ds = dates.split(','); 
			$('.datepickerMulti').datepick('setDate', ds); 
		}
	}
	
	/*
	$.fn.Map = function (options) {
		
  	
		//Constructor
		options = $.extend({
			latitude: null,
			longitude: null,
			zoom: 12,
			pins: null,
			hideMap : false
		}, options);

		$this = this;
		latitude = options.latitude;
		longitude = options.longitude;
		zoom = options.zoom;
		pins = options.pins;
		hideMap = options.hideMap;
		pinMouseOver = null;
		panelOldPin = "";
		panelImageId = "";



		if (latitude != null && longitude != null && this.length) {
			var mapId = $this.attr('id');
			var interval = setInterval(function() {
				if ((eval("typeof VEMap") != "undefined")
						&& (document.getElementById(mapId).attachEvent != undefined)) { clearInterval(interval); LoadMap($this.attr('id')); }}, 10);
		}
		
		var latitude,
		longitude,
		zoom,
		pins,
		vemap,
		slDrawing,
		pinIds,
		pinMouseOver,
		panelOldPin,
		panelImageId,
		hideMap;


		var $this;

   
		
		var LoadMap = function (id) {
			vemap = new VEMap(id);
			//vemap = new VEMap('search-map');
			slDrawing = new VEShapeLayer();

			vemapOptions =  new VEMapOptions();
			vemapOptions.EnableBirdseye = false;
			vemapOptions.DashboardColor = 'black';
			vemapOptions.EnableClickableLogo = false;
			vemapOptions.UseEnhancedRoadStyle = true;
			vemapOptions.EnableSearchLogo = false;
			
			var veLatLong = new VELatLong( latitude, longitude );

			vemap.LoadMap(veLatLong, 
					zoom, 
					VEMapStyle.Road, false, 
					VEMapMode.Mode2D, true, 0, vemapOptions);
			vemap.AddShapeLayer(slDrawing);
			slDrawing.DeleteAllShapes();

			var data = pins;	
			if (data.latitude != null) {
				var icon = data.icon;
				var veLatLong = new VELatLong(data.latitude, data.longtitude);
				var shape = new VEShape(
						VEShapeType.Pushpin, veLatLong);

				var title = data.title;
				shape.SetTitle(title);

				var desc = data.description;
				shape.SetDescription(desc);

				if (icon != null) {
					shape.SetCustomIcon(icon);
				}

				var photoURL = data.photoURL;
				if (photoURL != null) {
					shape.SetPhotoURL(photoURL);
				}
				slDrawing.AddShape(shape); 

			} else {
				pinIds = new Array ();
				var index = 1;

				$.each(data, function(i) {
					var context = this.context;
					var icon = this.icon;
					var veLatLong = new VELatLong( this.latitude, this.longtitude);
					var shape = new VEShape(
							VEShapeType.Pushpin, veLatLong);

					var title = this.title;
					shape.SetTitle(title);

					if (icon != null) {
						shape.SetCustomIcon("/pcgsearch"+icon);
					}
					
					slDrawing.AddShape(shape);
					pinIds[index] = shape.GetID();
					index++;
				});
			}
		
		};
		
		
	}
	 */
	
})(jQuery);
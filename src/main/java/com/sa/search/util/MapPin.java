package com.sa.search.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class MapPin {

	private String longtitude;
	private String latitude;
	private String title;
	private String description;
	private String icon;
	private String photoURL;
	private String context;
	
	public String getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}


	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }

	public static MapPin fromJsonToMapPin(String json) {
        return new JSONDeserializer<MapPin>().use(null, MapPin.class).deserialize(json);
    }

	public static String toJsonArray(Collection<MapPin> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }

	public static Collection<MapPin> fromJsonArrayToMapPins(String json) {
        return new JSONDeserializer<List<MapPin>>().use(null, ArrayList.class).use("values", MapPin.class).deserialize(json);
    }
}
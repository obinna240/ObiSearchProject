package com.sa.search.view.controller.bean;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * Data bean for holding List of system-wide configuration data
 *  
 */

public class ConfigUI  {

	private String configId;
	private String configDesc;
	private int priority;


	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}



	public String getConfigDesc() {
		return configDesc;
	}
	public void setConfigDesc(String configDesc) {
		this.configDesc = configDesc;
	}

	public ConfigUI(){

	}
	public ConfigUI(String configId, String configDesc,int priority) {
		super();
		this.configId = configId;
		this.configDesc = configDesc;
		this.priority = priority;
	}


	public String getConfigId() {
		return configId;
	}
	public void setConfigId(String configId) {
		this.configId = configId;
	}
	//Sort the List first by priority then by configDisplay Name
	public static Comparator<ConfigUI> ConfigNameComparator 
	= new Comparator<ConfigUI>() {
		public int compare(ConfigUI o1, ConfigUI o2) {
			   return new CompareToBuilder()
               .append(o2.getPriority(), o1.getPriority())
               .append(o1.getConfigDesc(), o2.getConfigDesc()).toComparison();
		}
	};

}

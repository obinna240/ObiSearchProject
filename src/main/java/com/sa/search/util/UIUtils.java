package com.sa.search.util;

import org.apache.commons.lang.StringUtils;

public class UIUtils {

	
	public static String encodeStringToHex(String sourceText) {
		if (sourceText == null){
			return "";
		}
		
		byte[] rawData = sourceText.getBytes();
		StringBuffer hexText = new StringBuffer();
		String initialHex = null;
		int initHexLength = 0;

		for (int i = 0; i < rawData.length; i++) {
			int positiveValue = rawData[i] & 0x000000FF;
			initialHex = Integer.toHexString(positiveValue);
			initHexLength = initialHex.length();
			while (initHexLength++ < 2) {
				hexText.append("0");
			}
			hexText.append(initialHex);
		}
		return hexText.toString();
	}
	
	public static String hexStringToString(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return new String(data);
	}

	/**
	 * Generate ID to reference an error.
	 * Designed to be put in the error logs and also displayed on the front end if necessary. 
	 * Use the last 8 Hex digits of the current time, which should be unique within a
	 * 50 day period or so but not be too big for a user to read out  
	 */
	public static String generateErrorID(){
		String tHex = Long.toHexString(System.currentTimeMillis());
		return StringUtils.left(tHex, 8).toUpperCase();
	}
}
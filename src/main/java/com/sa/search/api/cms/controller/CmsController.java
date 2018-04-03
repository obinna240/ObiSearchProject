package com.sa.search.api.cms.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;

import com.sa.search.config.SearchConstants;


@Controller
public class CmsController {
	private static Log m_log = LogFactory.getLog(CmsController.class);

	protected static String unicodeEscape(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ((c >> 7) > 0) {
				sb.append("\\u");
				sb.append(SearchConstants.HEX_CHAR[(c >> 12) & 0xF]); // append the hex character
														// for the left-most
														// 4-bits
				sb.append(SearchConstants.HEX_CHAR[(c >> 8) & 0xF]); // hex for the second group
													// of 4-bits from the left
				sb.append(SearchConstants.HEX_CHAR[(c >> 4) & 0xF]); // hex for the third group
				sb.append(SearchConstants.HEX_CHAR[c & 0xF]); // hex for the last group, e.g.,
												// the right most 4-bits
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
}

package com.sa.search.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.sa.search.config.SearchConstants;

public class DateHelper {

	public static List<Date> getDaysBetweenDates(Date startdate, Date enddate)
	{
			
	    List<Date> dates = new ArrayList<Date>();
	    Calendar calendar = new GregorianCalendar();
	    calendar.setTime(startdate);
	    calendar.set(Calendar.HOUR, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);

	    do
	    {
	        Date result = calendar.getTime();
	        dates.add(result);
	        calendar.add(Calendar.DATE, 1);
	    } while (calendar.getTime().before(enddate) || calendar.getTime().equals(enddate) );
        
	    return dates;
	}

	public static String convertDateToString(Date date , String pattern) throws ParseException{
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}
	
	public static String convertDateToString(Date date , DateFormat df) throws ParseException{
		return df.format(date);
	}
	
	public static String convertDateToString(String date , String patternFrom, String patternTo) throws ParseException{
		DateFormat dfFrom = new SimpleDateFormat(patternFrom);
		DateFormat dfTo = new SimpleDateFormat(patternTo);
		
		Date d = dfFrom.parse(date);
		
		return dfTo.format(d);
	}
	
	public static String parseSolrDate(String date, String timeSuffix) {

		try {
			Date parsedInputDate = SearchConstants.DATE_INPUT_FORMAT.parse(date);
			String formattedOutputDate = SearchConstants.SOLR_QUERY_DATE_FORMAT.format(parsedInputDate);
			return formattedOutputDate + timeSuffix;

		} catch (ParseException e) {
			return "*"; // Solr date wildcard
		}
	}
}

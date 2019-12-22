package com.pengtoolbox.cfw.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.pengtoolbox.cfw._main.CFW;

/**************************************************************************************************************
 * 
 * @author Reto Scheiwiller, © 2019 
 * @license Creative Commons: Attribution-NonCommercial-NoDerivatives 4.0 International
 **************************************************************************************************************/
public class CFWTime {
	
	public static final String TIME_FORMAT = "YYYY-MM-dd'T'HH:mm:ss.SSS";
	public static final SimpleDateFormat dateFormatter = new SimpleDateFormat(CFWTime.TIME_FORMAT);
	
	public static String currentTimestamp(){
		
		return CFWTime.formatDate(new Date());
	}
	
	public static String formatDate(Date date){
		
		return dateFormatter.format(date);
	}
	
	/********************************************************************************************
	 * 
	 ********************************************************************************************/
	public static Date getCurrentDateWithOffset(int years, int days, int hours, int minutes) {
		Calendar calendar = Calendar.getInstance();
		
		calendar.add(Calendar.YEAR, years);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		calendar.add(Calendar.MINUTE, minutes);
		
		return calendar.getTime();
	}
	
	/********************************************************************************************
	 * 
	 ********************************************************************************************/
	public static Timestamp offsetTimestamp(Timestamp timestamp, int years, int days, int hours, int minutes) {
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTimeInMillis(timestamp.getTime());
		calendar.add(Calendar.YEAR, years);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		calendar.add(Calendar.MINUTE, minutes);
		
		return new Timestamp(calendar.getTimeInMillis());
	}
	
	/********************************************************************************************
	 * 
	 ********************************************************************************************/
	public static Timestamp getCurrentTimestampWithOffset(int years, int days, int hours, int minutes) {
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.add(Calendar.YEAR, years);
		calendar.add(Calendar.DAY_OF_YEAR, days);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		calendar.add(Calendar.MINUTE, minutes);
		
		return new Timestamp(calendar.getTimeInMillis());
	}
	
	/********************************************************************************************
	 * 
	 ********************************************************************************************/
	public static Timestamp getDefaultAgeOutTime(int granularityMinutes) {
		Timestamp ageOutOffset = null;
		
		if		(granularityMinutes < 5) 		{ ageOutOffset = CFW.Time.getCurrentTimestampWithOffset(0, 0, 0, -1); }
		else if (granularityMinutes < 10) 		{ ageOutOffset = CFW.Time.getCurrentTimestampWithOffset(0, 0, -0, -5); }
		else if (granularityMinutes < 30) 		{ ageOutOffset = CFW.Time.getCurrentTimestampWithOffset(0, 0, -0, -10); }
		else if (granularityMinutes < 60) 		{ ageOutOffset = CFW.Time.getCurrentTimestampWithOffset(0, -1, 0, 0); }
		else if (granularityMinutes < 120) 		{ ageOutOffset = CFW.Time.getCurrentTimestampWithOffset(0, -7, 0, 0); }
		else if (granularityMinutes < 360) 		{ ageOutOffset = CFW.Time.getCurrentTimestampWithOffset(0, -14, 0, 0); }
		else if (granularityMinutes < 1440) 	{ ageOutOffset = CFW.Time.getCurrentTimestampWithOffset(0, -30, 0, 0); }
		else  									{ ageOutOffset = CFW.Time.getCurrentTimestampWithOffset(0, -90, 0, 0); }

		return ageOutOffset;
	}
	


}

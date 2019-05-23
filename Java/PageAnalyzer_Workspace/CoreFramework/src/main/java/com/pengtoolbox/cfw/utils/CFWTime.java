package com.pengtoolbox.cfw.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CFWTime {
	
	public static final String TIME_FORMAT = "YYYY-MM-dd'T'HH:mm:ss.SSS";
	public static final SimpleDateFormat dateFormatter = new SimpleDateFormat(CFWTime.TIME_FORMAT);
	
	public static String currentTimestamp(){
		
		return CFWTime.formatDate(new Date());
	}
	
	public static String formatDate(Date date){
		
		return dateFormatter.format(date);
	}
}

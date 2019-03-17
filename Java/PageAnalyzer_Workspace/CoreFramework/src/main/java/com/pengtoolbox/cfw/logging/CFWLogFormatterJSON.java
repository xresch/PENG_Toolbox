package com.pengtoolbox.cfw.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.pengtoolbox.cfw._main.CFW;

public class CFWLogFormatterJSON extends Formatter {
	
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(CFW.TIME_FORMAT);
	@Override
	public String format(LogRecord rec) {
		
		StringBuffer buf = new StringBuffer(1000);
		CFWLog log = (CFWLog)rec.getParameters()[0];
		buf.append("{");
		
			//-------------------------
			// Timestamp
			buf.append("\"timestamp\":\"");
			buf.append(CFW.formatDate(new Date(rec.getMillis())));
			buf.append("\"");
			
			//-------------------------
			// Level
			buf.append(", \"level\":\"");
			buf.append(rec.getLevel());
			buf.append("\"");
			
			//-------------------------
			// user
			buf.append(", \"user\":\"");
			buf.append(log.userID);
			buf.append("\"");
			
			//-------------------------
			// URL
			buf.append(", \"deltaStartMillis\":\"");
			buf.append(log.deltaStartMillis);
			buf.append("\"");
			

			
			//-------------------------
			// URL
			buf.append(", \"webURL\":\"");
			buf.append(log.webURL);
			buf.append("\"");
			
			//-------------------------
			// URL
			buf.append(", \"webParams\":\"");
			buf.append(log.queryString);
			buf.append("\"");
			
			//-------------------------
			// Class
			buf.append(", \"class\":\"");
			buf.append(log.sourceClass);
			buf.append("\"");
			
			//-------------------------
			// Method
			buf.append(", \"method\":\"");
			buf.append(log.sourceMethod);
			buf.append("\"");
			
			//-------------------------
			// RequestID
			buf.append(", \"requestID\":\"");
			buf.append(log.requestID);
			buf.append("\"");
			
			//-------------------------
			// SessionID
			buf.append(", \"sessionID\":\"");
			buf.append(log.sessionID);
			buf.append("\"");
		
			//-------------------------
			// Response Size Bytes
			buf.append(", \"sizeChars\":\"");
			buf.append(log.estimatedResponseSizeChars);
			buf.append("\"");

			//-------------------------
			// message
			buf.append(", \"message\":\"");
			buf.append(rec.getMessage());
			buf.append("\"");
			
			//-------------------------
			// Duration Millisecond
			if(log.durationMillis != -1){
				buf.append(", \"durationMillis\":\"");
				buf.append(log.durationMillis);
				buf.append("\"");
			}
			
			//-------------------------
			// Exception
			if(log.exception != null){
				buf.append(", \"exception\":\"");
				buf.append(log.exception);
				buf.append("\"");
			}
			
			
		buf.append("}\n");
		
		
		return buf.toString();
	}

}

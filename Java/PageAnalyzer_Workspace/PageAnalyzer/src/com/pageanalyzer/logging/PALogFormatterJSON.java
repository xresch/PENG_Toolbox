package com.pageanalyzer.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.pageanalyzer._main.PA;

public class PALogFormatterJSON extends Formatter {
	
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(PA.TIME_FORMAT);
	@Override
	public String format(LogRecord rec) {
		
		StringBuffer buf = new StringBuffer(1000);
		PALogger log = (PALogger)rec.getParameters()[0];
		buf.append("{");
		
			//-------------------------
			// Timestamp
			buf.append("\"timestamp\":\"");
			buf.append(PA.formatDate(new Date(rec.getMillis())));
			buf.append("\"");
			
			//-------------------------
			// URL
			buf.append(", \"deltaStartMillis\":\"");
			buf.append(log.deltaStartMillis);
			buf.append("\"");
			
			//-------------------------
			// Level
			buf.append(", \"level\":\"");
			buf.append(rec.getLevel());
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

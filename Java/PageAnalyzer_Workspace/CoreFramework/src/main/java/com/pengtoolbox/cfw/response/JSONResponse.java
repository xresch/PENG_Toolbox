package com.pengtoolbox.cfw.response;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

public class JSONResponse extends AbstractTemplateJSON {

	private boolean success = true;

	public JSONResponse() {
		super();
	}

	@Override
	public StringBuffer buildResponse() {
		
		StringBuffer builder = new StringBuffer("{"); 
		
		//----------------------------
		// Success
		builder.append("\"success\": ").append(success).append(",");
		
		//----------------------------
		// Messages
		builder.append("\"messages\": ")
			   .append(CFW.Context.Request.getAlertsAsJSONArray())
			   .append(",");
		
		//----------------------------
		// Messages
		if (content.length() == 0) {
			content.append("null");
		}
		builder.append("\"payload\": ")
			   .append(content);
		//----------------------------
		// Close and Return
		builder.append("}"); 
		return builder;
	}
	
	@Override
	public int getEstimatedSizeChars() {
		
		int size = this.content.length();
		
		return size;
	}
	
	public void addAlert(MessageType type, String message) {
		CFW.Context.Request.addAlertMessage(type, message);
	}

}

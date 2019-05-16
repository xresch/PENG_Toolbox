package com.pengtoolbox.cfw._main;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.AbstractTemplate;

public class CFWLocalization {

	public static void writeLocalized(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		AbstractTemplate template = CFW.getTemplate(request);
		
		if(template != null){
	
			//TODO: Make language handling dynamic
			ResourceBundle bundle = ResourceBundle.getBundle("language",
											new Locale("en", "US"), 
											CFW.urlClassLoader);
			
			StringBuffer sb = template.buildResponse();
			
			int fromIndex = 0;
			int leftIndex = 0;
			int rightIndex = 0;
			int length = sb.length();
			
			while(fromIndex < length && leftIndex < length){
			
				leftIndex = sb.indexOf(CFW.LOCALE_LB, fromIndex);
				
				if(leftIndex != -1){
					rightIndex = sb.indexOf(CFW.LOCALE_RB, leftIndex);
					
					if(rightIndex != -1 && (leftIndex+CFW.LOCALE_LB_SIZE) < rightIndex){
	
						String propertyName = sb.substring(leftIndex+CFW.LOCALE_LB_SIZE, rightIndex);
						if(bundle.containsKey(propertyName)){
							sb.replace(leftIndex, rightIndex+CFW.LOCALE_RB_SIZE, bundle.getString(propertyName));
						}
						//start again from leftIndex
						fromIndex = leftIndex+1;
						
					}else{
						//TODO: Localize message
						new CFWLog(CFW.logger, request)
						.method("writeLocalized")
						.warn("Localization Parameter was missing the right bound");
					
						break;
					}
					
				}else{
					//no more stuff found to replace
					break;
				}
			}
			
			response.getWriter().write(sb.toString());
			
		}
	
	}

}

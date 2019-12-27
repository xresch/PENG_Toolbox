package com.pengtoolbox.cfw.datahandling;

import java.util.logging.Logger;

import com.pengtoolbox.cfw._main.CFW;
import com.pengtoolbox.cfw.logging.CFWLog;
import com.pengtoolbox.cfw.response.bootstrap.AlertMessage.MessageType;

public class CFWAutocompleteHandlerDefault extends CFWAutocompleteHandler {

	private static Logger logger = CFWLog.getLogger(CFWAutocompleteHandlerDefault.class.getName());
	private Class<? extends CFWObject> clazz;
	
	public CFWAutocompleteHandlerDefault(Class<? extends CFWObject> clazz) {
		this.setMaxResults(10);
		this.clazz = clazz;
	}
	
	public CFWAutocompleteHandlerDefault(Class<? extends CFWObject> clazz, int maxResults) {
		super(maxResults);
		this.clazz = clazz;
	}
	@Override
	public String getAutocompleteData(String inputValue)  {
		
		CFWField parent = this.getParent();
		String fieldname = parent.getName();
		String[] result = null;
		if( !(parent.getValue() instanceof Object[])) {
			try {
				result = clazz.newInstance()
					.select(fieldname)
					.like(fieldname, "%"+inputValue+"%")
					.limit(this.getMaxResults())
					.getAsStringArray(fieldname);
			} catch (Exception e) {
				new CFWLog(logger)
				.method("getAutocompleteData")
				.severe("Exception occured while trying to instanciate a CFWObject.", e);
			} 
		}else {
			
		}

		return CFW.JSON.toJSON(result);
	}

}

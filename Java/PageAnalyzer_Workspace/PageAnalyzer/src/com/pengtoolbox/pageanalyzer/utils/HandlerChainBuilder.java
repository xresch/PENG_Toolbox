package com.pengtoolbox.pageanalyzer.utils;

import org.eclipse.jetty.server.handler.HandlerWrapper;

public class HandlerChainBuilder {
	
	private HandlerWrapper current = null;
	
	public HandlerChainBuilder(HandlerWrapper handler) {
		current = handler;
	}
	
	public HandlerChainBuilder chain(HandlerWrapper handler) {
		
		if(current != null) {
			current.setHandler(handler);
		}
		
		current = handler;
		
		return this;
	}

}

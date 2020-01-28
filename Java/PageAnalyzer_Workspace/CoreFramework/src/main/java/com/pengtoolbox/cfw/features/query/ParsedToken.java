package com.pengtoolbox.cfw.features.query;

public class ParsedToken {

	private String token;
	
	public enum TokenType{
		DEFAULT,
		QUOTED_TEXT,
		EMBRACED_TEXT,
		KEYWORD,
	}
	
	public ParsedToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}	
	
}

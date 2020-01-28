package com.pengtoolbox.cfw.features.query;

import java.text.ParseException;

public class ContextualTokenizer {
	
	private String textToParse;
	private char splitterChar;
	
	private char[] charArray;
	private int startPosition = 0;
	
	private boolean handleQuotes = true;
	
	public ContextualTokenizer(String textToParse, char splitterChar) {
		this.textToParse = textToParse.trim();
		charArray = textToParse.toCharArray(); 
		this.splitterChar = splitterChar;
	}
	
	public ParsedToken getNextToken() throws ParseException {
		int currentPos = startPosition;
		for( ;currentPos < charArray.length; currentPos++) {
			
			switch(charArray[currentPos]) {
				case '"': 
					if(handleQuotes) {
						currentPos = skipQuotedText('"', currentPos);
					}
					break;
				case '\'': 
					if(handleQuotes) {
						currentPos = skipQuotedText('\'', currentPos);
					}
					break;	
				default: 
					if(charArray[currentPos] == splitterChar) {
						ParsedToken result = new ParsedToken(textToParse.substring(startPosition, currentPos-1).trim());
						startPosition = currentPos+1;
						return result;
					}
					
					break;
			}
			
		}
		
		//------------------------------
		// End of String
		if(startPosition != currentPos && startPosition < charArray.length) {
			ParsedToken result = new ParsedToken(textToParse.substring(startPosition, currentPos-2));
			startPosition = currentPos+1;
			return result;
		}
		//------------------------------
		// Return null if no more data
		return null;
	}

	private int skipQuotedText(char quoteChar, int currentPos) throws ParseException {
		boolean isQuoteOpen = true;
		while(isQuoteOpen && currentPos < charArray.length-1) {
			currentPos++;
			if(charArray[currentPos] == quoteChar) {
				int backslashCount = 0;
				int tempPos = currentPos-1; 
				while(charArray[tempPos] == '\\') {
					backslashCount++;
					tempPos--;
				}
				
				if(backslashCount % 2 == 0) {
					isQuoteOpen = false;
				}
			}
			if(currentPos >= charArray.length) {
				throw new ParseException("Unbalanced Quotes!", currentPos);							
			}
		}
		
		return currentPos;
	}
}

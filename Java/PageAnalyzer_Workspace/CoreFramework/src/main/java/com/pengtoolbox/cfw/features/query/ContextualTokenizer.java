package com.pengtoolbox.cfw.features.query;

import java.text.ParseException;
import java.util.ArrayList;

import com.pengtoolbox.cfw.utils.CFWArrayUtils;

public class ContextualTokenizer {
	
	private String textToParse;
	private ArrayList<Character> splitterChars;
	
	private char[] charArray;
	private int startPosition = 0;
	
	private boolean handleQuotes = true;
	
	public ContextualTokenizer(String textToParse) {
		this.textToParse = textToParse.trim();
		charArray = textToParse.toCharArray(); 

	}
	
	public ArrayList<CFWToken> getTokensbyDelimiters(ArrayList<Character> splitterChars) throws ParseException {
		
		CFWToken token = this.getNextToken(splitterChars);

		ArrayList<CFWToken> tokensArray = new ArrayList<CFWToken>();
		
		while(token != null) {
			tokensArray.add(token);
			token = this.getNextToken(splitterChars);
		}
		
		return tokensArray;
	}
	public CFWToken getNextToken(ArrayList<Character> splitterChars) throws ParseException {
		int currentPos = startPosition;
		for( ;currentPos < charArray.length; currentPos++) {
			
			switch(charArray[currentPos]) {
				case '"':  		if(handleQuotes) { currentPos = skipQuotedText('"', currentPos); }
								break;
				case '\'': 		if(handleQuotes) { currentPos = skipQuotedText('\'', currentPos); }
								break;	
				default: 
					if(splitterChars.contains(charArray[currentPos]) ) {
						CFWToken result = new CFWToken(textToParse.substring(startPosition, currentPos-1).trim());
						startPosition = currentPos+1;
						
						//-------------------------
						//Skip subsequent splitters
						while(splitterChars.contains(charArray[startPosition]) ) {
							startPosition++;
						}
						return result;
					}
					
					break;
			}
			
		}
		
		//------------------------------
		// End of String
		if(startPosition != currentPos && startPosition < charArray.length) {
			CFWToken result = new CFWToken(textToParse.substring(startPosition, currentPos-2));
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

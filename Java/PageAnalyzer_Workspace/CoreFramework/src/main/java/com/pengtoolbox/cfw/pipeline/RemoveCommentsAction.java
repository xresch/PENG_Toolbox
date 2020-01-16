package com.pengtoolbox.cfw.pipeline;

public class RemoveCommentsAction extends PipelineAction<String, String>{

	private boolean isBlockCommentOpen = false;
	
	@Override
	void execute() throws Exception {
		boolean isBlockComment = false;
		while(!inQueue.isEmpty()) {
			String line = getInQueue().poll();
			
			//=================================================
			// Handle Single Line Comments
			//=================================================
			if(line.trim().startsWith("//")) {
				continue;
			}
			
			//=================================================
			// Handle Open Block comments
			//=================================================
			if(isBlockCommentOpen) {
				
				while(!inQueue.isEmpty() && !line.contains("*/")) {
					line = getInQueue().poll();
				}
				
				if(line.contains("*/")) {
					isBlockCommentOpen = false;
					line = line.substring(line.indexOf("*/")+2);
					if(line.trim().isEmpty()) {
						continue;
					}
				}
			}
			
			//=================================================
			// Handle Block comments
			//=================================================
			if(line.contains("/*")) {
				
				//----------------------------
				//remove all inline block comments
				while(line.contains("*/")) {
					line = line.substring(0, line.indexOf("/*")) + " " + line.substring(line.indexOf("*/")+2);
				}
				
				if(line.trim().isEmpty()) {
					continue;
				}
				
				//----------------------------
				//remove starting inline block comments
				if(line.contains("/*")) {
					
					//------------------------
					// Check is in Quotes

					line = line.substring(0, line.indexOf("/*"));
					if(!line.trim().isEmpty()) {
						outQueue.add(line);
					}
					isBlockCommentOpen = true;
					continue;
				}
				
			}
			//=================================================
			// Handle lines without comments
			//=================================================
			outQueue.add(line);
		}
		this.setDoneIfPreviousDone();
		
	}
	
	private static int firstUnquotedIndexOf(String text, String searchTerm, int offset) {
		
		if(offset >= text.length() -1) {
			return -1;
		}
		
		int doubleQuoteIndex = 9999;
		int singleQuoteIndex = 9999;
		
		while(singleQuoteIndex >= 0 || doubleQuoteIndex >= 0) {
			doubleQuoteIndex = text.indexOf("\"", offset);
			singleQuoteIndex = text.indexOf("\'", offset);
			//---------------------------------------
			// Check Double Quotes
			//---------------------------------------
			if( doubleQuoteIndex >= 0
			&& (singleQuoteIndex == -1 || singleQuoteIndex > doubleQuoteIndex) ) {
				
				int commentIndex = text.indexOf(searchTerm, doubleQuoteIndex);
				if(commentIndex < doubleQuoteIndex) {
					return commentIndex;
				}
				if( doubleQuoteIndex < commentIndex
				 && commentIndex < text.indexOf("\"", commentIndex)) {
					offset = text.indexOf("\"", commentIndex)+1;
					
					continue;
				}else {
					return commentIndex;
				}
			}
			
			//---------------------------------------
			// Check Single Quotes
			//---------------------------------------
			if( singleQuoteIndex >= 0
			&& (doubleQuoteIndex == -1 || doubleQuoteIndex > singleQuoteIndex) ) {
				
				int commentIndex = text.indexOf(searchTerm, singleQuoteIndex);
				if(commentIndex < singleQuoteIndex) {
					return commentIndex;
				}
				if( singleQuoteIndex < commentIndex
				 && commentIndex < text.indexOf("'", commentIndex)) {
					offset = text.indexOf("'", commentIndex)+1;
					
					continue;
				}else {
					return commentIndex;
				}
			}
		}
		
		return -1;
	}

}

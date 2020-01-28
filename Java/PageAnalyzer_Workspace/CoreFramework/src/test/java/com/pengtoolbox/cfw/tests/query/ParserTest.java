package com.pengtoolbox.cfw.tests.query;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.pengtoolbox.cfw.features.query.ContextualTokenizer;
import com.pengtoolbox.cfw.features.query.ParsedToken;

public class ParserTest {
	
	@Test
	public void testTokenizeIgnoreDoubleQuotedText() throws IOException {
		
		ContextualTokenizer tokenizer = new ContextualTokenizer(" find User where text=\"this is | a piped text\" "
		+ "| grep \"2\""
		+ "| table test, bla, blub "
		+ "| singlebackslash \" dont split this  \\\" | \\\" dont split this \" "
		+ "| multibackslash \"split this \\\\\" | \"split this\" "
		, '|');
	
		ParsedToken token;
		try {
			token = tokenizer.getNextToken();

			ArrayList<String> tokensArray = new ArrayList<String>();
			
			while(token != null) {
				System.out.println(token.getToken());
				tokensArray.add(token.getToken());
				token = tokenizer.getNextToken();
			}
			
			Assertions.assertEquals("find User where text=\"this is | a piped text\"", tokensArray.get(0));
			Assertions.assertEquals("grep \"2\"", tokensArray.get(1));
			Assertions.assertEquals("table test, bla, blub", tokensArray.get(2));
			Assertions.assertEquals("singlebackslash \" dont split this  \\\" | \\\" dont split this \"", tokensArray.get(3));
			Assertions.assertEquals("multibackslash \"split this \\\\\"", tokensArray.get(4));
			Assertions.assertEquals("\"split this\"", tokensArray.get(5));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test
	public void testTokenizeIgnoreSingleQuotedText() throws IOException {
		
		ContextualTokenizer tokenizer = new ContextualTokenizer(" find User where text='this is | a piped text' "
		+ "| grep '2'"
		+ "| table test, bla, blub "
		+ "| singlebackslash ' dont split this  \\' | \\' dont split this ' "
		+ "| multibackslash 'split this \\\\' | 'split this' "
		, '|');
	
		ParsedToken token;
		try {
			token = tokenizer.getNextToken();

			ArrayList<String> tokensArray = new ArrayList<String>();
			
			while(token != null) {
				System.out.println(token.getToken());
				tokensArray.add(token.getToken());
				token = tokenizer.getNextToken();
			}
			
			Assertions.assertEquals("find User where text='this is | a piped text'", tokensArray.get(0));
			Assertions.assertEquals("grep '2'", tokensArray.get(1));
			Assertions.assertEquals("table test, bla, blub", tokensArray.get(2));
			Assertions.assertEquals("singlebackslash ' dont split this  \\' | \\' dont split this '", tokensArray.get(3));
			Assertions.assertEquals("multibackslash 'split this \\\\'", tokensArray.get(4));
			Assertions.assertEquals("'split this'", tokensArray.get(5));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

}

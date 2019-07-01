package com.pengtoolbox.cfw.utils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.logging.Logger;

import com.pengtoolbox.cfw.logging.CFWLog;

public class CFWEncryption {

	// internal salt to make it even more complicated to recreate a password
	// Don't change this if you don't want to mess up existing passwords!
	public static final String INTERNAL_SALT = "1a@2v#3r%9s&7k?";
	
	public static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345678901234567890+*%&/()=?!{}[]><:;.,-_+*%&/()=?!{}[]><:;.,-_";
		
	private static Logger logger = CFWLog.getLogger(CFWEncryption.class.getName());
	
	/******************************************************************************
	 * Creates a salted SHA512 password hash and returns a string of 127 or less bytes.
	 * Removes the first character of the resulting hash string. This adds as 
	 * well some more complexity to the hashing algorithm.
	 * 
	 * @param password
	 * @param salt
	 * @return hash with 127 or less bytes
	 ******************************************************************************/
	public static String createPasswordHash(String password, String salt) {
		
		try { 
            // getInstance() method is called with algorithm SHA-512 
            MessageDigest md = MessageDigest.getInstance("SHA-512"); 
  
            byte[] messageDigest = md.digest((INTERNAL_SALT+password+salt).getBytes()); 
  
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
            
            return hashtext.substring(1); 
        } 
  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
        	new CFWLog(logger).severe("Error creating password hash.", e);
            throw new RuntimeException(e); 
        }
	}
	
	/******************************************************************************
	 * Creates a random Salt for a Password.
	 * 
	 * @param byteCount number of bytes to create
	 * @return
	 ******************************************************************************/
	public static String createPasswordSalt(int byteCount) {

		StringBuilder builder = new StringBuilder();
		
		Random random = new Random();
		for(int i = 0; i < byteCount; i++) {
			builder.append(CHARS.charAt(random.nextInt(CHARS.length()-1)));
		}
		
		return builder.toString();
	    
	}
	
}

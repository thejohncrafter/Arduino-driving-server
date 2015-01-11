package com.ArduinoDrivingServer.web;

/**
 * This class statically contains tools for JSP pages.
 * 
 * @author thejohncrafter
 * 
 */
public class JSPUtil {
	
	/**
	 * This method is used to make a given <code>String</code> don't have
	 * a line length superior than the given length.
	 * @param s The <code>String</code> to format.
	 * @param maxLength The maximum line length.
	 * @return The created <code>String</code>.
	 */
	public static String maxLineLength(String s, int maxLength){
		
		int max = s.length();
		String toReturn = "";
		
		for(int i = 0; i < max; i+=maxLength){
			
			if(i == 0)
				continue;
			
			toReturn += s.substring(0,maxLength) + "<br>";
			s = s.substring(maxLength);
			
		}
		
		toReturn += s;
		
		return toReturn;
		
	}
	
}

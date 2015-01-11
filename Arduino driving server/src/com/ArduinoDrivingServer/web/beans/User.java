package com.ArduinoDrivingServer.web.beans;

/**
 * This bean represents a user.
 * 
 * @author Julien Marquet
 *
 */
public class User {
	
	/**
	 * User's name.
	 */
	private String name;
	
	/**
	 * This method is used to get user's name.
	 * @return User's name.
	 */
	public String getName() { return name; }
	
	/**
	 * This method is used to set user's name.
	 * @param name User's name.
	 */
	public void setName(String name) { this.name = name; }
	
}

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
	 * User's id.
	 */
	private int id;
	
	/**
	 * This method is used to get user's name.
	 * @return User's name.
	 */
	public String getName(){return name;}
	
	/**
	 * This method is used to set user's name.
	 * @param name User's name.
	 */
	public void setName(String name){this.name = name;}
	
	/**
	 * This method is used to get user's id.
	 * @return User's id.
	 */
	public int getId(){return id;}
	
	/**
	 * This method is used to set user's id.
	 * @param name User's id.
	 */
	public void setId(int id){this.id = id;}
	
}

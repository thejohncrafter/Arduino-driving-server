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
	 * This field stores the user's permissions group name.
	 */
	private String permissionsGroup;
	
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
	
	/**
	 * This method is used to get user's permissions group name.
	 * @return User's id.
	 */
	public String getPermissionsGroup(){return permissionsGroup;}
	
	/**
	 * This method is used to set user's permissions group name.
	 * @param name User's permissions group name.
	 */
	public void setPermissionsGroup(String permissionsGroup){this.permissionsGroup = permissionsGroup;}
	
}

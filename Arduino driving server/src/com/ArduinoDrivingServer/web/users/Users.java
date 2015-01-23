package com.ArduinoDrivingServer.web.users;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.ArduinoDrivingServer.web.beans.User;
import com.ArduinoDrivingServer.web.servlets.ArduinoDriving;

/**
 * This class statically contains methods related to users.
 * 
 * @author Julien Marquet
 *
 */
public class Users {
	
	/**
	 * This field stores all the users.
	 */
	private static HashMap<Integer, User> users = new HashMap<Integer, User>();
	
	/**
	 * This field stores all the passswords.
	 */
	private static HashMap<Integer, String> passwords = new HashMap<Integer, String>();
	
	/**
	 * This method loads all the users from the file <code>/WEB-INF/users.xml</code> and 
	 * stores it in <code>users</code>.
	 * @throws JDOMException If a <code>JDOMException</code> occurs.
	 * @throws IOException If a <code>IOException</code> occurs.
	 */
	public static void load() throws JDOMException, IOException{
		
		System.out.println("Loading all users form /WEB-INF/users.xml...");
		
		File usrFile = new File(ArduinoDriving.getRealPath("WEB-INF/users.xml"));
		SAXBuilder builder = new SAXBuilder();
		Document document = (Document) builder.build(usrFile);
		Element rootNode = document.getRootElement();
		
		List<?> usersList = rootNode.getChildren();
		
		for(int i = 0; i < usersList.size(); i++){
			
			Element userElem = (Element) usersList.get(i);
			String name = userElem.getAttributeValue("name");
			String group = userElem.getChild("permissions").getAttributeValue("group");
			
			System.out.println("Creating user " + name + "...");
			
			// can't call newUser() because user's datas will be re-written.
			User u = new User();
			u.setName(name);
			u.setId(i);
			u.setPermissionsGroup(group);
			
			users.put(i, u);
			passwords.put(i, userElem.getAttributeValue("password"));
			
		}
		
		System.out.println("Done loading users.");
		
	}
	
	/**
	 * This method is used to add a new user.<br>
	 * It will throw nothing if the user can't be found.
	 * @param name The username.
	 * @param password The password.
	 * @throws IOException Should never happen.
	 * @throws JDOMException Should never happen.
	 */
	public static void newUser(String name, String password, String group) throws JDOMException, IOException{
		
		User u = new User();
		u.setName(name);
		u.setId(users.size());
		u.setPermissionsGroup(group);
		users.put(u.getId(), u);
		passwords.put(u.getId(), password);
		
		File usrFile = new File(ArduinoDriving.getRealPath("WEB-INF/users.xml"));
		SAXBuilder builder = new SAXBuilder();
		Document document = (Document) builder.build(usrFile);
		Element rootNode = document.getRootElement();
		
		Element usrElem = new Element("user");
		usrElem.setAttribute("name", name);
		usrElem.setAttribute("password", password);
		
		Element groupElem = new Element("permissions");
		groupElem.setAttribute("group", group);
		
		usrElem.addContent(groupElem);
		rootNode.addContent(usrElem);
		
		XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
		output.output(document, new FileOutputStream(ArduinoDriving.getRealPath("WEB-INF/users.xml")));
		
	}
	
	/**
	 * This method is used to change a username to another username.<br>
	 * It also automatically changes the "name" property in the user and the password access key.<br>
	 * It will throw nothing if the user can't be found.
	 * @param oldUsername The old username.
	 * @param newUsername The new username.
	 * @throws IOException Should never happen.
	 * @throws JDOMException Should never happen.
	 */
	public static void changeUsername(String oldUsername, String newUsername) throws JDOMException, IOException{
		
		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);
		
		for(int index : keys){
			
			if(users.get(index).getName().equals(oldUsername)){
				
				users.get(index).setName(newUsername);
				
				File usrFile = new File(ArduinoDriving.getRealPath("WEB-INF/users.xml"));
				SAXBuilder builder = new SAXBuilder();
				Document document = (Document) builder.build(usrFile);
				Element rootNode = document.getRootElement();
				
				List<Element> allUsers = rootNode.getChildren();
				
				for(int i = 0; i < allUsers.size(); i++){
					
					Element e = allUsers.get(i);
					
					if(e.getAttributeValue("name").equals(oldUsername)){
						
						e.setAttribute("name", newUsername);
						
					}
					
				}
				
				XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
				output.output(document, new FileOutputStream(ArduinoDriving.getRealPath("WEB-INF/users.xml")));
				
			}
			
		}
		
	}
	
	/**
	 * This method is used to remove a given user.<br>
	 * It will throw nothing if the user can't be found.
	 * @param user The name of the user to remove.
	 * @throws IOException Should never happen.
	 * @throws JDOMException Should never happen.
	 */
	public static void deleteUser(String user) throws JDOMException, IOException{
		
		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);
		
		for(int index : keys){
			
			if(users.get(index).getName().equals(user)){
				
				users.remove(index);
				passwords.remove(index);
				
				File usrFile = new File(ArduinoDriving.getRealPath("WEB-INF/users.xml"));
				SAXBuilder builder = new SAXBuilder();
				Document document = (Document) builder.build(usrFile);
				Element rootNode = document.getRootElement();
				
				List<Element> allUsers = rootNode.getChildren();
				
				for(int i = 0; i < allUsers.size(); i++){
					
					Element e = allUsers.get(i);
					
					if(e.getAttributeValue("name").equals(user)){
						
						rootNode.removeContent(e);
						
					}
					
				}
				
				XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
				output.output(document, new FileOutputStream(ArduinoDriving.getRealPath("WEB-INF/users.xml")));
				
			}
			
		}
		
	}
	
	/**
	 * This method is used to set the password of the user which as the given name.
	 * @param username The username.
	 * @param password The password.
	 * @throws IOException Should never happen.
	 * @throws JDOMException Should never happen.
	 */
	public static void setPassword(String user, String password) throws JDOMException, IOException{
		
		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);
		
		for(int index : keys){
			
			if(users.get(index).getName().equals(user)){
				
				passwords.put(index, password);
				
				File usrFile = new File(ArduinoDriving.getRealPath("WEB-INF/users.xml"));
				SAXBuilder builder = new SAXBuilder();
				Document document = (Document) builder.build(usrFile);
				Element rootNode = document.getRootElement();
				
				List<Element> allUsers = rootNode.getChildren();
				
				for(int i = 0; i < allUsers.size(); i++){
					
					Element e = allUsers.get(i);
					
					if(e.getAttributeValue("name").equals(user)){
						
						e.setAttribute("password", password);
						
					}
					
				}
				
				XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
				output.output(document, new FileOutputStream(ArduinoDriving.getRealPath("WEB-INF/users.xml")));
				
			}
			
		}
		
	}
	
	/**
	 * This method is used to change a given user's group.
	 * @param user The user to edit.
	 * @param group The new group.
	 * @throws IOException Should never happen.
	 * @throws JDOMException Should never happen.
	 */
	public static void changeGroup(String user, String group) throws JDOMException, IOException{
		
		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);
		
		for(int index : keys){
			
			if(users.get(index).getName().equals(user)){
				
				users.get(index).setPermissionsGroup(group);
				File usrFile = new File(ArduinoDriving.getRealPath("WEB-INF/users.xml"));
				SAXBuilder builder = new SAXBuilder();
				Document document = (Document) builder.build(usrFile);
				Element rootNode = document.getRootElement();
				
				List<Element> allUsers = rootNode.getChildren();
				
				for(int i = 0; i < allUsers.size(); i++){
					
					Element e = allUsers.get(i);
					
					if(e.getAttributeValue("name").equals(user)){
						
						e.getChild("permissions").setAttribute("group", group);
						
					}
					
				}
				
				XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
				output.output(document, new FileOutputStream(ArduinoDriving.getRealPath("WEB-INF/users.xml")));
				
			}
			
		}
		
	}
	
	/**
	 * This method is used to get all the users contained by a given group.
	 * @param gName The name of the user group to get.
	 * @return The users contained by the group.
	 */
	public static User[] getUsersByGroup(String gName){
		
		ArrayList<User> group = new ArrayList<User>();
		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);
		
		for(int i : keys){
			
			if(users.get(i).getPermissionsGroup().equals(gName))
				group.add(users.get(i));
			
		}
		
		return group.toArray(new User[group.size()]);
		
	}
	
	/**
	 * This method is used to get the user which as the given name.<br>
	 * It will return null if there is no such user.
	 * @param username The username.
	 * @return The user.
	 */
	public static User getUser(String username){
		
		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);
		
		for(int i : keys){
			
			if(users.get(i).getName().equals(username))
				return users.get(i);
			
		}
		
		return null;
		
	}
	
	/**
	 * This method is used to get the password of the user which as the given name.
	 * @param username The username.
	 * @return The password.
	 */
	public static String getPassword(String username){
		
		Integer[] keys = users.keySet().toArray(new Integer[users.size()]);
		
		for(int i : keys){
			
			if(users.get(i).getName().equals(username))
				return passwords.get(i);
			
		}
		
		return null;
		
	}
	
	/**
	 * This method is used to get the users list.
	 * @return The users list.
	 * @see users
	 */
	public static HashMap<Integer, User> getUsers(){return users;}
	
}

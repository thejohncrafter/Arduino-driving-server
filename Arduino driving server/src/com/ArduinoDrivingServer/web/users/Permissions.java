package com.ArduinoDrivingServer.web.users;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.ArduinoDrivingServer.web.beans.User;
import com.ArduinoDrivingServer.web.servlets.ArduinoDriving;

/**
 * This class statically contains all the methods related to permissions.
 * 
 * @author Julien Marquet
 *
 */
public class Permissions {
	
	/**
	 * This field represents the "nothing" (not even read) permission.
	 */
	public static final int NONE = 0;
	
	/**
	 * This field represents the "read only" permission.
	 */
	public static final int READ = 1;
	
	/**
	 * This field represents all (read + write) permissions.
	 */
	public static final int ALL = 2;
	
	/**
	 * This <code>HashMap</code> stores all the user's permissions.
	 */
	private static HashMap<Integer, HashMap<String, Integer>> groups = new HashMap<Integer, HashMap<String, Integer>>();
	
	/**
	 * This <code>HashMap</code> stores all the group names.
	 */
	private static HashMap<Integer, String> groupNames = new HashMap<Integer, String>();
	
	/**
	 * This array stores all the permission names.
	 */
	private static String[] permissionNames;
	
	/**
	 * This method is used to load the permissions.
	 * @throws IOException Should never happen.
	 * @throws JDOMException Should never happen.
	 * @see loadPermissions
	 * @see loadGroups
	 */
	public static void load() throws JDOMException, IOException{
		
		loadPermissions();
		loadGroups();
		
	}
	
	/**
	 * This method is called at startup and is used to load all the permission names.
	 * @throws IOException Should never happen.
	 * @throws JDOMException Should never happen.
	 */
	private static void loadPermissions() throws JDOMException, IOException{
		
		System.out.println("Loading all permission names form /WEB-INF/ADS-cfg.xml...");
		
		Element rootNode = ArduinoDriving.getConfigElement("permissions");
		List<Element> perms = rootNode.getChild("names").getChildren();
		permissionNames = new String[perms.size()];
		
		for(int i = 0; i < perms.size(); i++){
			
			Element perm = perms.get(i);
			System.out.println("\t" + perm.getAttributeValue("name"));
			permissionNames[i] = perm.getAttributeValue("name");
			
		}
		
		System.out.println("Done.");
		
	}
	
	/**
	 * This method loads the permissions groups from file (permisssions.xml in /WEB-INF/).
	 * @throws JDOMException If a <code>JDOMExcaption</code> occurs.
	 * @throws IOException If an <code>IOExcaption</code> occurs.
	 */
	private static void loadGroups() throws JDOMException, IOException{
		
		System.out.println("Loading permission groups from /WEB-INF/ADS-cfg.xml...");
		
		Element rootNode = ArduinoDriving.getConfigElement("permissions");
		List<Element> groups = rootNode.getChild("groups").getChildren();
		
		for(int i = 0; i < groups.size(); i++){
			
			Element group = groups.get(i);
			String name = group.getAttributeValue("name");
			
			groupNames.put(i, name);
			Permissions.groups.put(i, new HashMap<String, Integer>());
			
			System.out.println("Group :");
			System.out.println("\tName : " + name);
			System.out.println("\tPermissions :");
			
			List<Element> perms = group.getChildren();
			// use HashMap because it is simpler to use
			HashMap<String, Boolean> allPerms = new HashMap<String, Boolean>();
			
			for(String p : permissionNames)
				allPerms.put(p, false);
			
			for(int i2 = 0; i2 < perms.size(); i2++){
				
				Element perm = perms.get(i2);
				String pName = perm.getAttributeValue("name");
				String pValue_str = perm.getAttributeValue("value");
				int pValue;
				
				switch(pValue_str){
				
				case "ALL" :
					pValue = ALL;
					break;
				case "READ" :
					pValue = READ;
					break;
				case "NONE" :
					pValue = NONE;
					break;
				default :
					pValue = NONE;
					System.out.println("Error : bad value in attribute \"permission\" "
							+ "from permissions.groups(name = " + name + ").permission(name = " + pValue_str + "). Setting value to NONE.");
					pValue_str = "NONE"; // used for logs
					break;
				
				}
				
				System.out.println("\t\t" + pName + " : " + pValue);
				Permissions.groups.get(i).put(pName, pValue);
				allPerms.remove(pName);
				
			}
			
			if(allPerms.size() != 0){
				
				System.out.println("\tWarning : missing permission(s). Filling it with default value (NONE)");
				
				for(String p : permissionNames){
					
					Element permElem = new Element("permission");
					permElem.setAttribute("name", p);
					permElem.setAttribute("value", "NONE");
					
					rootNode.getChild("names").addContent(permElem);
					Permissions.groups.get(i).put(p, Permissions.NONE);
					
				}
				
				XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
				output.output(group.getDocument(), new FileOutputStream(ArduinoDriving.getRealPath("WEB-INF/permissions.xml")));
				
			}
			
		}
		
	}
	
	/**
	 * This method is used to register a permissions group.
	 * <div style="color:red;">
	 * 	Make sure you gave a correct <code>HashMap</code> because validity won't be tested.
	 * </div>
	 * @param name The name of the group.
	 * @param perms A <code>HashMap</code> used to store the permissions.
	 * @throws IOException Should never happen.
	 * @throws JDOMException Should never happen.
	 */
	public static void createGroup(String name, HashMap<String, Integer> perms) throws JDOMException, IOException{

		groupNames.put(groups.size(), name);
		groups.put(groups.size(), perms);
		
		Element rootNode =  ArduinoDriving.getConfigElement("permissions");
		List<Element> groups = rootNode.getChild("groups").getChildren();
		Element group = new Element("group");
		String[] keys = perms.keySet().toArray(new String[perms.size()]);
		
		group.setAttribute("name", name);
		
		for(String key : keys){
			
			Element perm = new Element("permission");
			perm.setAttribute("name", key);
			perm.setAttribute("value", getPermissionAsString(perms.get(key)));
			
			group.addContent(perm);
			
		}
		
		groups.add(group);
		
		XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
		output.output(rootNode.getDocument(), new FileOutputStream(ArduinoDriving.getRealPath("WEB-INF/ADS-cfg.xml")));
		
	}
	
	/**
	 * This method is used to remove a given group.
	 * @param name The name of the group to remove.
	 * @throws IOException Should never happen.
	 * @throws JDOMException Should never happen.
	 */
	public static void removeGroup(String name) throws FileNotFoundException, IOException, JDOMException{
		
		groups.remove(getGroupIndex(name));
		groupNames.remove(getGroupIndex(name));
		
		Element rootNode =  ArduinoDriving.getConfigElement("permissions");
		
		List<Element> groups = rootNode.getChild("groups").getChildren();
		
		for(int i = 0; i < groups.size(); i++){
			
			if(groups.get(i).getAttributeValue("name").equals(name))
				rootNode.getChild("groups").removeContent(groups.get(i));
			
		}
		
		XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
		output.output(rootNode.getDocument(), new FileOutputStream(ArduinoDriving.getRealPath("WEB-INF/ADS-cfg.xml")));
		
	}
	
	/**
	 * This method is used to edit a given group.
	 * @param oldName The old name of the group (if renaming).
	 * @param name The new name of the group (if renaming).
	 * @param perms The new permissions.
	 * @throws IOException Should never happen.
	 * @throws JDOMException Should never happen.
	 */
	public static void editGroup(String oldName, String name, HashMap<String, Integer> perms) throws JDOMException, IOException{
		
		removeGroup(oldName);
		createGroup(name, perms);
		
	}
	
	/**
	 * This method is used to get a user's permission. It will always return <code>Permissions.ALL</code> 
	 * if the user's permissions group is "sudo". If the permission is null, it returns <code>Permissions.NONE</code>.
	 * @param user The user.
	 * @param permission The permission name.
	 * @return The permission.
	 */
	public static int getPermission(User user, String permission){
		
		if(user.getPermissionsGroup().equals("sudo"))
			return ALL;
		// else
		
		Integer perm = getGroup(user.getPermissionsGroup()).get(permission);
		
		if(perm == null)
			return NONE;
		// else
		
		return perm;
		
	}
	
	/**
	 * This method is used to get the group (a <code>HashMap<String, Integer></code> 
	 * that stores all the permissions) which have the given name.
	 * @param name The group name.
	 * @return The group.
	 */
	public static HashMap<String, Integer> getGroup(String name){
		
		return groups.get(getGroupIndex(name));
		
	}
	
	/**
	 * This method is used to get the index (the identifier) of a given group.
	 * @param gName The name of the group.
	 * @return The index of the group.
	 */
	public static int getGroupIndex(String gName){
		
		Integer[] keys = groupNames.keySet().toArray(new Integer[groupNames.size()]);
		
		for(int i : keys){
			
			if(groupNames.get(i).equals(gName))
				return i;
			
		}
		
		return -1;
		
	}
	
	/**
	 * This method is used to get a given permission as a String.<br>
	 * It returns the given permission a <code>"ALL", "NONE" or "READ"</code>.
	 * @param perm The permission to get as String.
	 * @return The permission as String.
	 * @see Permissions#NONE
	 * @see Permissions#READ
	 * @see Permissions#ALL
	 */
	public static String getPermissionAsString(int perm){
		
		String val = "ALL";
		
		if(perm == Permissions.NONE)
			val = "NONE";
		else if(perm == Permissions.READ)
			val = "READ";
		
		return val;
		
	}
	
	/**
	 * This method is used to get all the permission names.
	 * @return All the permission names.
	 */
	public static String[] getPermissionNames(){return permissionNames;}
	
	/**
	 * This method is used to get all the groups.<br>
	 * If you want to get a special group, it is better to use <code>getGroup()</code>.
	 * @return All the groups.
	 * @see Permissions#getGroup(String)
	 */
	public static HashMap<Integer, HashMap<String, Integer>> getGroups(){return groups;}
	
	/**
	 * This method is used to get all the group names.<br>
	 * If you want to get a special group, it is better to use <code>getGroup()</code>.
	 * @return All the group names.
	 * @see Permissions#getGroup(String)
	 */
	public static HashMap<Integer, String> getGroupNames(){return groupNames;}
	
}

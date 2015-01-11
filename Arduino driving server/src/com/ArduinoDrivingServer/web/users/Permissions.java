package com.ArduinoDrivingServer.web.users;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
 * This class statically contains all the methods related to permissions.
 * 
 * @author thejohncrafter
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
	private static HashMap<String, HashMap<String, Integer>> permissions = new HashMap<String, HashMap<String, Integer>>();
	
	/**
	 * This array stores all the permission names.
	 */
	private static String[] permissionNames;
	
	/**
	 * This method is called at startup and is used to load all the permission names.
	 * @throws IOException Should never happen.
	 * @throws JDOMException Should never happen.
	 */
	public static void loadPermissions() throws JDOMException, IOException{
		
		System.out.println("Loading all permission names form /WEB-INF/permissions.xml...");
		
		File permFile = new File(ArduinoDriving.getRealPath("WEB-INF/permissions.xml"));
		SAXBuilder builder = new SAXBuilder();
		Document document = (Document) builder.build(permFile);
		Element rootNode = document.getRootElement();
		
		List<?> perms = rootNode.getChildren();
		permissionNames = new String[perms.size()];
		
		for(int i = 0; i < perms.size(); i++){
			
			Element perm = (Element) perms.get(i);
			System.out.println("\t" + perm.getAttributeValue("name"));
			permissionNames[i] = perm.getAttributeValue("name");
			
		}
		
		System.out.println("Done.");
		
	}
	
	/**
	 * This method is used to get a user's permission. It will always return <code>Permissions.ALL</code> 
	 * if the user's name is "sudo". If the permission is null, it returns <code>Permissions.NONE</code>.
	 * @param user The user.
	 * @param permission The permission name.
	 * @return The permission.
	 */
	public static int getPermission(User user, String permission){
		
		if(user.getName().equals("sudo"))
			return ALL;
		// else
		
		Integer perm = Permissions.permissions.get(user.getName()).get(permission);
		
		if(perm == null)
			return NONE;
		// else
		
		return perm;
		
	}
	
	/**
	 * This method fills a <code>HashMap</code> where all the given user's permissions are stored 
	 * and returns it.
	 * <div style="color:red">
	 * 	Warning : usrElem (param 2) MUST be contained by a document because this function can 
	 * 	try to save the users file.
	 * </div>
	 * @param user The username.
	 * @return The permissions.
	 * @throws FileNotFoundException If the user file isn't there (should never happen).
	 * @throws IOException If a <code>IOException</code> occurs (if it occurs, it is maybe because 
	 * <code>usrElem</code> isn't contained by a document).
	 */
	public static void fillPermissions(String user, Element usrElem) throws FileNotFoundException, IOException{
		
		System.out.println("Getting " + user + "'s permissions...");
		
		HashMap<String,Integer> permissions = new HashMap<String,Integer>();
		
		List<?> perms = usrElem.getChildren();
		
		// use HashMap because it is simpler to use
		HashMap<String, Boolean> allPerms = new HashMap<String, Boolean>();
		
		for(String p : permissionNames)
			allPerms.put(p, false);
		
		for(int i = 0; i < perms.size(); i++){
			
			Element permElem = (Element) perms.get(i);
			
			String permName = permElem.getAttributeValue("name");
			String permString = permElem.getAttributeValue("permission");
			int perm = NONE;
			
			switch(permString){
			
			case "ALL" :
				perm = ALL;
				break;
			case "READ" :
				perm = READ;
				break;
			case "NONE" :
				perm = NONE;
				break;
			default :
				// perm = NONE
				System.out.println("Error : bad value in attribute \"permission\" "
						+ "from user.permissions.permission(name = " + permName + "). Setting value to NONE.");
				permString = "NONE"; // used for logs
				break;
			
			}
			
			System.out.println("\t" + permName + " = " + permString);
			
			permissions.put(permName, perm);
			allPerms.remove(permName);
			
		}
		
		if(allPerms.size() != 0){
			
			System.out.println("\tWarning : missing permission(s). Filling it with default value (NONE)");
			
			for(String p : permissionNames){
				
				Element permElem = new Element("permission");
				permElem.setAttribute("name", p);
				permElem.setAttribute("permission", "NONE");
				
				usrElem.addContent(permElem);
				permissions.put(p, Permissions.NONE);
				
			}
			
			XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
			output.output(usrElem.getDocument(), new FileOutputStream(ArduinoDriving.getRealPath("WEB-INF/users.xml")));
			
		}
		
		Permissions.permissions.put(user, permissions);
		
		System.out.println("Done getting permissions for " + user + ".");
		
	}
	
}

package com.ArduinoDrivingServer.web.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.ArduinoDrivingServer.bridge.AbstractBridge;
import com.ArduinoDrivingServer.bridge.Bridge;
import com.ArduinoDrivingServer.bridge.BridgeException;
import com.ArduinoDrivingServer.web.beans.User;
import com.ArduinoDrivingServer.web.users.Users;

/**
 * This servlet is used for parameter edition.
 * 
 * @author Julien Marquet
 *
 */
public class EditServlet extends HttpServlet {
	
	/**
	 * Used by Serializable.
	 */
	private static final long serialVersionUID = -4484658732795268873L;
	
	/**
	 * Used by the <code>Servlet</code> system.
	 */
	private ServletConfig cfg;
	
	public void init(ServletConfig config) throws ServletException{
		
		cfg = config;
		
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		this.getServletContext().getRequestDispatcher("/ADS").forward(request, response);
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//TODO : synchronized blocks for multithreading ?
		BufferedReader reader = request.getReader();
		PrintWriter out = response.getWriter();
		
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
		
	    String params = "";
		String line;
		
		while((line = reader.readLine()) != null)
			params += line;
	    
		try {
			
			JSONObject object = new JSONObject(params);
			JSONStringer stringer = new JSONStringer();
			
			{
				
				User user = Users.getUser(object.getJSONObject("user").getString("name"));
				
				if(user == null){
					
					out.print("{\"error\":\"Bad username !\"}");
					return;
					
				}else if(!Users.getPassword(user.getName()).equals(object.getJSONObject("user").getString("password"))){
					
					out.print("{\"error\":\"Bad password !\"}");
					return;
					
				}
				
			}
			
			String action = object.getString("action");
			
			switch(action){
			
			case "users.new" :
				{
					
					String name = StringEscapeUtils.escapeHtml(object.getJSONObject("newUser").getString("name"));
					String password = StringEscapeUtils.escapeHtml(object.getJSONObject("newUser").getString("password"));
					
					System.out.println("New user :");
					System.out.println("\t username : " + name);
					System.out.println("\t password : " + password);
					
					if(Users.getUser(name) != null){
						
						System.out.println("Error : a user is already named " + name);
						out.print("{\"error\":\"The given username already exists !\"}");
						
					}if(name.length() == 0 || name.trim().length() == 0){
						
						System.out.println("Error : the new username is blank !");
						out.print("{\"error\":\"The new username is blank !\"}");
						
					}else if(password.length() == 0){
						
						System.out.println("Error : the new password is blank !");
						out.print("{\"error\":\"The new password is blank !\"}");
						
					}else{
						
						try {
							
							Users.newUser(name, password);
							
						} catch (JDOMException e) {
							
							System.out.println("Error when creating user :");
							e.printStackTrace();
							out.print(stringer.object().key("error").value("Internal error : " + e.getMessage()).endObject().toString());
							
						}
						
						out.print("{\"answer\":\"OK.\"}");
						
					}
					
					System.out.println("Done.");
					
				}
				break;
			case "users.edit" :
				{ // for variables
					
					String name = StringEscapeUtils.escapeHtml(object.getString("oldUser"));
					String newName = StringEscapeUtils.escapeHtml(object.getJSONObject("newUser").getString("name"));
					String newPassword = StringEscapeUtils.escapeHtml(object.getJSONObject("newUser").getString("password"));
					
					System.out.println("Edition : user");
					System.out.println("\told username : " + name);
					System.out.println("\tnew username : " + newName);
					System.out.println("\tnew password : " + newPassword);
					
					if(name.equals("sudo") && !newName.equals("sudo")){
						
						System.out.println("Error : attemt to rename sudo !");
						out.print("{\"error\":\"Atempt to rename sudo !\"}");
						
					}else if(!name.equals(newName) && Users.getUser(newName) != null){
						
						System.out.println("Error : a user is already named " + newName);
						out.print("{\"error\":\"The given username already exists !\"}");
						
					}else if(newName.length() == 0 || newName.trim().length() == 0){
						
						System.out.println("Error : the new username is blank !");
						out.print("{\"error\":\"The new username is blank !\"}");
						
					}else if(newPassword.length() == 0){
						
						System.out.println("Error : the new password is blank !");
						out.print("{\"error\":\"The new password is blank !\"}");
						
					}else{
						
						try {
							
							Users.changeUsername(name, newName);
							Users.setPassword(newName, newPassword);
							
						} catch (JDOMException e) {
							
							System.out.println("Exception :");
							e.printStackTrace();
							System.out.println("Canceling...");
							out.print(stringer.object().key("error").value("Internal error : " + e.getMessage()).endObject().toString());
							break;
							
						}
						
						out.print("{\"answer\":\"OK.\"}");
						System.out.println("Done.");
						
					}
					
				}
				break;
			case "users.remove" :
				{ // for variables
					
					String user = StringEscapeUtils.escapeHtml(object.getString("toRemove"));
					
					if(user.equals("sudo")){
						
						System.out.println("Attempt to delete sudo !");
						out.print("{\"error\":\"Can't delete sudo !\"}");
						break;
						
					}
					
					System.out.println("Deleting user " + user);
					
					try{
						
						Users.deleteUser(user);
						
					} catch (JDOMException e) {
						
						System.out.println("Exception :");
						e.printStackTrace();
						System.out.println("Canceling...");
						out.print(stringer.object().key("error").value("Internal error : " + e.getMessage()).endObject().toString());
						break;
						
					}
					
					
					out.print("{\"answer\":\"OK.\"}");
					System.out.println("Done.");
					
				}
				break;
			case "bridge.open" :
				if(Bridge.isOpened()){
					
					out.print("{\"error\":\"Already opened.\"}");
					
				}else{
					
					System.out.println("Openeing bridge...");
					System.out.println("Writing data in bridge.xml...");
					
					Element rootNode;
					
					try{
						
						File bridgesfile = new File(ArduinoDriving.getRealPath("WEB-INF/bridge.xml"));
						SAXBuilder builder = new SAXBuilder();
						Document document = (Document) builder.build(bridgesfile);
						rootNode = document.getRootElement();
						
						rootNode.setAttribute("opened", "true");
						
						XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
						output.output(document, new FileOutputStream(ArduinoDriving.getRealPath("WEB-INF/bridge.xml")));
						
					}catch(IOException | JDOMException e){
						
						System.out.println("Exception :");
						e.printStackTrace();
						System.out.println("Canceling...");
						out.print(stringer.object().key("error").value("Internal error : " + e.getMessage()).endObject().toString());
						break;
						
					}
					
					System.out.println("Calling Bridge.init()...");
					
					try {
						
						Bridge.init();
						
					} catch (BridgeException e) {
						
						System.out.println("Error : ");
						e.printStackTrace();
						out.print(stringer.object().key("error").value("Internal error : " + e.getMessage()).endObject().toString());
						
					}
					
					out.print("{\"answer\":\"OK.\"}");
					System.out.println("Done.");
					
				}
				break;
			case "bridge.close" :
				if(!Bridge.isOpened()){
					
					out.print("{\"error\":\"Already closed.\"}");
					
				}else{
					
					System.out.println("Closing bridge...");
					System.out.println("Writing data in bridge.xml...");
					
					try{
						
						File bridgesfile = new File(ArduinoDriving.getRealPath("WEB-INF/bridge.xml"));
						SAXBuilder builder = new SAXBuilder();
						Document document = (Document) builder.build(bridgesfile);
						Element rootNode = document.getRootElement();
						
						rootNode.setAttribute("opened", "false");
						
						XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
						output.output(document, new FileOutputStream(ArduinoDriving.getRealPath("WEB-INF/bridge.xml")));
						
					}catch(IOException | JDOMException e){
						
						System.out.println("Exception :");
						e.printStackTrace();
						System.out.println("Canceling...");
						out.print(stringer.object().key("error").value("Internal error : " + e.getMessage()).endObject().toString());
						break;
						
					}
					
					System.out.println("Calling Bridge.destroy()...");
					
					try {
						
						Bridge.destroy();
						
					} catch (BridgeException e) {
						
						System.out.println("Error : ");
						e.printStackTrace();
						out.print(stringer.object().key("error").value("Internal error : " + e.getMessage()).endObject().toString());
						
					}
					
					out.print("{\"answer\":\"OK.\"}");
					System.out.println("Done.");
					
				}
				break;
			case "bridges.edit" :
				{
					
					JSONObject newDatas = object.getJSONObject("bridge");
					String oldName = object.getString("oldName");
					boolean activated = newDatas.getBoolean("activated");
					String name = StringEscapeUtils.escapeHtml(newDatas.getString("name"));
					String desc = StringEscapeUtils.escapeHtml(newDatas.getString("desc"));
					
					System.out.println("Editing bridge :");
					System.out.println("\tOld name : " + oldName);
					System.out.println("\tActivated : " + activated);
					System.out.println("\tName : " + name);
					System.out.println("\tDescription : " + desc);
					
					if(!oldName.equals(name) && Bridge.getBridges().get(name) != null){
						
						System.out.println("Error : the given name already exists !");
						out.print("{\"error\":\"The given name already exists !\"}");
						break;
						
					}else if(name.length() == 0 || name.trim().length() == 0){
						
						System.out.println("Error : the given name is blank !");
						out.print("{\"error\":\"The given name is blank !\"}");
						break;
						
					}else if(desc.length() == 0 || desc.trim().length() == 0){
						
						System.out.println("Error : the given description is blank !");
						out.print("{\"error\":\"The given description is blank !\"}");
						break;
						
					}else{
						
						try{
							
							File bridgesfile = new File(ArduinoDriving.getRealPath("WEB-INF/bridge.xml"));
							SAXBuilder builder = new SAXBuilder();
							Document document = (Document) builder.build(bridgesfile);
							List<Element> bridges = document.getRootElement().getChild("bridges").getChildren();
							
							for(int i = 0; i < bridges.size(); i++){
								
								if(bridges.get(i).getAttributeValue("name").equals(oldName)){
									
									Element bridge = bridges.get(i);
									bridge.setAttribute("name", name);
									bridge.getChild("desc").setText(desc);
									
								}
								
							}
							
							XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
							output.output(document, new FileOutputStream(ArduinoDriving.getRealPath("WEB-INF/bridge.xml")));
							
						}catch(IOException | JDOMException e){
							
							System.out.println("Exception :");
							e.printStackTrace();
							System.out.println("Canceling...");
							out.print(stringer.object().key("error").value("Internal error : " + e.getMessage()).endObject().toString());
							break;
							
						}
						
						AbstractBridge bridge = Bridge.getBridges().get(oldName);
						
						if(activated != bridge.isActivated()){
							
							bridge.setActivated(activated);
							
							if(activated){
								
								Bridge.initBridge(bridge);
								
							}else{
								
								try {
									
									Bridge.destroyBridge(bridge);
									
								} catch (BridgeException e) {
									
									System.out.println("Exception :");
									e.printStackTrace();
									System.out.println("Canceling...");
									out.print(stringer.object().key("error").value("Internal error : " + e.getMessage()).endObject().toString());
									break;
									
								}
								
							}
							
						}
						
						bridge.setDesc(desc);
						Bridge.changeBridgeName(bridge, name);
						Bridge.getBridges().put(name, Bridge.getBridges().remove(oldName));
						
						out.print("{\"answer\":\"Done.\"}");
						System.out.println("Done.");
						
					}
					
				}
				break;
			default:
				out.print("{\"error\":\"No such action.\"}");
				break;
			
			}
			
		} catch (JSONException e) {
			
			out.print("{\"error\":\"" + e.getMessage().replace("\"", "\\\"").replace("\n", "\\n") + "\"}"); // replace " by \"
			
		}
		
	}
	
	public String getServletInfo(){
		
		return "Part of Arduino driving server - used for data edition.";
		
	}
	
	public ServletConfig getServletConfig(){ return cfg; }
	
}

package com.ArduinoDrivingServer.web.servlets;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom2.JDOMException;

import com.ArduinoDrivingServer.bridge.Bridge;
import com.ArduinoDrivingServer.web.users.Permissions;
import com.ArduinoDrivingServer.web.users.Users;


/**
 * This servlet is the main servlet of the program.
 * 
 * @author Julien Marquet
 *
 */
public class ArduinoDriving extends HttpServlet {
	
	/**
	 * Used by Serializable.
	 */
	private static final long serialVersionUID = 6370473352382504816L;
	
	/**
	 * Used by the <code>Servlet</code> system.
	 */
	private ServletConfig cfg;
	
	/**
	 * This is used to store the instance of the Servlet.
	 */
	private static ArduinoDriving instance;
	
	public void init(ServletConfig config) throws ServletException{
		
		cfg = config;
		
		instance = this;
		
		System.out.println("========================");
		System.out.println("|arduino driving server|");
		System.out.println("|by Julien Marquet     |");
		System.out.println("|created on            |");
		System.out.println("|20 october 2014       |");
		System.out.println("========================");
		
		System.out.println("Loading users...");
		
		try {
			
			Permissions.loadPermissions();
			Users.loadUsers();
			
		} catch (JDOMException | IOException e) {
			
			System.out.println("An exception has occured while loading users :");
			e.printStackTrace();
			System.out.println("Exiting...");
			return;
			
		}
		
		System.out.println("Done.");
		
		System.out.println("Initializing bridge...");
		Bridge.init();
		System.out.println("Done.");
		
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException{
		
		this.getServletContext().getRequestDispatcher("/index.jsp").forward(req, response);
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		this.getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
		
	}
	
	/**
	 * This method is used to get the real path to the given file.
	 * @param path The path to get.
	 * @return The real path.
	 */
	public static String getRealPath(String path){
		
		return instance.getServletContext().getRealPath(path);
		
	}
	
	public String getServletInfo(){
		
		return "Arduino driving server is used to drive Arduinos.";
		
	}
	
	public ServletConfig getServletConfig(){ return cfg; }
	
}

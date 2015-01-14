package com.ArduinoDrivingServer.web.servlets;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ArduinoDrivingServer.web.beans.User;
import com.ArduinoDrivingServer.web.users.Users;

/**
 * This servlet is used for session hadling.
 * 
 * @author Julien Marquet
 *
 */
public class SessionServlet extends HttpServlet {
	
	/**
	 * USed by Serializable.
	 */
	private static final long serialVersionUID = -5188411500623891680L;
	
	/**
	 * Used by the <code>Servlet</code> system.
	 */
	private ServletConfig cfg;
	
	public void init(ServletConfig config) throws ServletException{
		
		cfg = config;
		
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		response.getOutputStream().print("<html><head><script type=\"text/javascript\" src=\"script.js\"></script></head>"
				+ "<body><script>post('ADS', 'get', {});</script></body></html>");
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		
		String act = request.getParameter("act");
		
		if(act == null){
			
			response.getOutputStream().print("<html><head><script type=\"text/javascript\" src=\"script.js\"></script></head>"
					+ "<body>"
					+ "Missing act parameter."
					+ "<a href=\"javascript:post('ADS', 'get', {});\">Go to main page</a>"
					+ "</body></html>");
			
		}else if(act.equals("connect")){
			
			User user;
			String name = request.getParameter("username");
			String password = request.getParameter("password");
			
			if(!(name == null || name.trim().length() == 0) && !(password == null || password.trim().length() == 0)){
				
				System.out.println("Someone attemps to connect with username \"" + name + "\" and password \"" + password + "\".");
				
				user = Users.getUser(name);
				
				if(user != null){
					
					if(password.equals(Users.getPassword(name))){
						
						System.out.println("Someone is connected as " + name + " !");
						
						user = new User();
						user.setName(name);
						
						session.setAttribute("user", user);
						
					}
					
				}
				
			}
			
			response.getOutputStream().print("<html><head><script type=\"text/javascript\" src=\"script.js\"></script></head>"
					+ "<body><script>post('ADS', 'get', {});</script></body></html>");
			
		}else if(act.equals("disconnect")){
			
			System.out.println(((User) session.getAttribute("user")).getName() + " is disconnected !");
			session.invalidate();
			
			response.getOutputStream().print("<html><head><script type=\"text/javascript\" src=\"script.js\"></script></head>"
					+ "<body><script>post('ADS', 'get', {});</script></body></html>");
			
		}else{
			
			response.getOutputStream().print("<html><head><script type=\"text/javascript\" src=\"script.js\"></script></head>"
					+ "<body>"
					+ "Bad value of parameter act."
					+ "<a href=\"javascript:post('ADS', 'get', {});\">Go to main page</a>"
					+ "</body></html>");
			
		}
		
	}
	
	public String getServletInfo(){
		
		return "Part of Arduino driving server - used for sessions.";
		
	}
	
	public ServletConfig getServletConfig(){ return cfg; }
	
}

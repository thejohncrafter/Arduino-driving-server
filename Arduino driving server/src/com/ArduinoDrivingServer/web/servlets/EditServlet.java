package com.ArduinoDrivingServer.web.servlets;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom2.JDOMException;

import com.ArduinoDrivingServer.web.users.Users;

/**
 * This servlet is used for parameter edition.
 * 
 * @author thejohncrafter
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
		
		String action = request.getParameter("action");
		
		switch(action){
		
		case "users.new" :
			{
				
				String name = request.getParameter("new_username");
				String password = request.getParameter("new_password");
				
				System.out.println("New user :");
				System.out.println("\t username : " + name);
				System.out.println("\t password : " + password);
				
				if(name == null || password == null){
					
					response.getOutputStream().print("<html><body><H1>Missing argument(s) !</H1></body></html>");
					break;
					
				}
				
				if(name.length() == 0 || name.trim().length() == 0){
					
					System.out.println("Error : the new username is blank !");
					response.getOutputStream().print(makeAnswerGotoNewuser(true, "The new username is blank !"));
					
				}else if(password.length() == 0){
					
					System.out.println("Error : the new password is blank !");
					response.getOutputStream().print(makeAnswerGotoNewuser(true, "The new password is blank !"));
					
				}else{
					
					try {
						
						Users.newUser(name, password);
						
					} catch (JDOMException e) {
						
						System.out.println("Error when creating user :");
						e.printStackTrace();
						response.getOutputStream().print(makeAnswerGotoNewuser(true, "Internal error."));
						
					}
					
					response.getOutputStream().print(makeAnswerGotoNewuser(false, ""));
					
				}
				
				System.out.println("Done.");
				
			}
			break;
		case "users.edit" :
			{ // for variables
				
				String name = request.getParameter("arg:user");
				String newName = request.getParameter("new_username");
				String newPassword = request.getParameter("new_password");
				
				System.out.println("Edition : user");
				System.out.println("\told username : " + name);
				System.out.println("\tnew username : " + newName);
				System.out.println("\tnew password : " + newPassword);
				
				if(name == null || newName == null || newPassword == null){
					
					response.getOutputStream().print("<html><body><H1>Missing argument(s) !</H1></body></html>");
					break;
					
				}
				
				if(Users.getUser(newName) != null){
					
					System.out.println("Error : a user is already named " + newName);
					response.getOutputStream().print(makeAnswerGotoEdituser(newName, true, "A user is already named" + newName + "."));
					
				}else if(newName.length() == 0 || newName.trim().length() == 0){
					
					System.out.println("Error : the new username is blank !");
					response.getOutputStream().print(makeAnswerGotoEdituser(newName, true, "The new username is blank."));
					
				}else if(newPassword.length() == 0){
					
					System.out.println("Error : the new password is blank !");
					response.getOutputStream().print(makeAnswerGotoEdituser(newName, true, "The new username is blank."));
					
				}else{
					
					try {
						
						Users.changeUsername(name, newName);
						Users.setPassword(newName, newPassword);
						
					} catch (JDOMException e) {
						
						System.out.println("Exception :");
						e.printStackTrace();
						System.out.println("Canceling...");
						response.getOutputStream().print(makeAnswerGotoEdituser(newName, true, "Internal error."));
						break;
						
					}
					
					response.getOutputStream().print(makeAnswerGotoEdituser(newName, false, "" + newName));
					System.out.println("Done.");
					
				}
				
			}
			break;
		case "users.remove" :
			{ // for variables
				
				String user = request.getParameter("user");
				
				if(user == null){
					
					response.getOutputStream().print("<html><body><H1>Missing argument !</H1></body></html>");
					break;
					
				}
				
				if(user.equals("sudo")){
					
					System.out.println("Attempt to delete sudo !");
					response.getOutputStream().print(makeAnswerGotoEdituser(user, true, "Can't delete sudo !"));
					break;
					
				}
				
				System.out.println("Deleting user " + user);
				
				try{
					
					Users.deleteUser(user);
					
				} catch (JDOMException e) {
					
					System.out.println("Exception :");
					e.printStackTrace();
					System.out.println("Canceling...");
					response.getOutputStream().print(makeAnswerGotoEdituser(user, true, "An exception has occured."));
					break;
					
				}
				
				System.out.println("Done.");
				
				response.getOutputStream().print("<html><head><script type=\"text/javascript\" src=\"script.js\"></script></head>"
						+ "<body><script>post('ADS', 'post', {file:'admin/users.jsp', edit:'', action:'users.list.edit'});</script></body></html>");
				
			}
			break;
		default:
			this.getServletContext().getRequestDispatcher("/ADS").forward(request, response);
			break;
		
		}
		
	}
	
	private String makeAnswerGotoEdituser(String user, boolean hasError, String error){
		
		String answ = "<html><head><script type=\"text/javascript\" src=\"script.js\"></script></head>"
				+ "<body></body><script>post('ADS', 'post', {file:'admin/users.jsp', edit:'', action:'users.edit', user:'" + user + "'";
		
		if(hasError)
			answ += ", error:'" + error.replace('"', '\'') + "'";
		
		answ += "});</script></html>";
		
		return answ;
		
	}
	
	private String makeAnswerGotoNewuser(boolean hasError, String error){
		
		String answ = "<html><head><script type=\"text/javascript\" src=\"script.js\"></script></head>"
				+ "<body></body><script>post('ADS', 'post', {file:'admin/users.jsp', edit:'', action:'users.new'";
				
				if(hasError)
					answ += ", error:'" + error.replace('"', '\'') + "'";
				
				answ += "});</script></html>";
		
		return answ;
		
	}
	
	public String getServletInfo(){
		
		return "Part of Arduino driving server - used for data edition.";
		
	}
	
	public ServletConfig getServletConfig(){ return cfg; }
	
}

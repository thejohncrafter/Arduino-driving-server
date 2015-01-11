package com.ArduinoDrivingServer.web.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.ArduinoDrivingServer.bridge.AbstractBridge;
import com.ArduinoDrivingServer.bridge.Bridge;


/**
 * This servlet is the main servlet of the program.
 * 
 * @author Julien Marquet
 *
 */
public class RemoteServlet extends HttpServlet {
	
	/**
	 * Used by Serializable.
	 */
	private static final long serialVersionUID = 944893213980199371L;
	
	/**
	 * Used by the <code>Servlet</code> system.
	 */
	private ServletConfig cfg;
	
	public void init(ServletConfig config) throws ServletException{
		
		cfg = config;
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		BufferedReader reader = request.getReader();
		PrintWriter out = response.getWriter();
		
		response.setContentType("text/html");
	    response.setCharacterEncoding("UTF-8");
		
		String params = "";
		String line;
		
		while((line = reader.readLine()) != null)
			params += line;
		
		try{

			JSONStringer stringer = new JSONStringer();
			JSONObject object = new JSONObject(params); // will go outside try if this isn't readable JSON data
			
			String reqType = object.getString("request");
			
			if(reqType != null){
				
				switch(reqType){
				
				case "SEND" :
					{
						
						String port = object.getString("port");
						String data = object.getString("data");
						
						// optional datas
						boolean wait = false;
						long timeout = -1;
						
						try{
							
							wait = object.getBoolean("wait");
							timeout = object.getLong("timeout");
							
						}catch(JSONException e){ /* don't care */}
						
						AbstractBridge bridge = Bridge.getPortBridge(port);
						
						if(bridge == null){
							
							out.print("{\"error\":\"There is any bridge matching to the given port name.\"}");
							break;
							
						}
						
						try{
							
							if(wait){
								
								if(timeout == -1)
									out.print(stringer.object().key("answer").value(bridge.readLine(data)).endObject().toString());
								else
									out.print(stringer.object().key("answer").value(bridge.readLine(data, timeout)).endObject().toString());
								
							}else{
								
								bridge.send(data);
								out.print("{\"answer\":\"Sent.\"}");
								
							}
							
						}catch(Exception e){
							
							out.print(stringer.object().key("error").value("Error whan sending : " + e.getMessage()).endObject().toString());
							break;
							
						}
						
					}
					break;
				
				}
				
			}else{
				
				out.print("{\"error\":\"Missing reqType.\"}");
				
			}
			
		}catch(JSONException e){
			
			out.print("{\"error\":\"" + e.getMessage().replace("\"", "\\\"").replace("\n", "\\n") + "\"}"); // replace " by \"
			
		}
		
	}
	
	public String getServletInfo(){
		
		return "Part of Arduino driving server - used as a remote.";
		
	}
	
	public ServletConfig getServletConfig(){ return cfg; }
	
}

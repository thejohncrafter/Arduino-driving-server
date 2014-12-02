package com.ArduinoDrivingServer.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import com.ArduinoDrivingServer.bridge.Bridge;
import com.ArduinoDrivingServer.bridge.PortBridge;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * This class implements <code>HttpHandler</code> and is used to handle 
 * requestes on the <code>context /requestes</code>.
 * @author thejohncrafter
 *
 */
@SuppressWarnings("restriction")
public class RequestHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exc) throws IOException {
		
		System.out.println("Request !");
		
		@SuppressWarnings("unchecked")
		Map<String, Object> args = (Map<String, Object>) exc.getAttribute("parameters");
		
		String request = (String) args.get("r"); // r = request
		
		if(request != null){
			
			System.out.println("Got request : " + request + " !");
			
		}else{
			
			System.out.println("No request !");
			answer(exc, "NOREQUEST");
			return;
			
		}
		
		switch(request){
		
		case "CHECK" :
			answer(exc, "SUCCEED");
			break;
		case "HIDS" :
			
			String answer = "{ \"HIDS\" : [";
			
			String[][] datas;
			PortBridge[] bridges = Bridge.getbridges().values().toArray(new PortBridge[Bridge.getbridges().size()]);
			
			String[] ports    = new String[Bridge.getbridges().size()];
			String[] hids     = new String[Bridge.getbridges().size()];
			String[] names    = new String[Bridge.getbridges().size()];
			String[] creators = new String[Bridge.getbridges().size()];
			
			for(int i = 0; i < bridges.length; i++){
				
				ports[i]    = bridges[i].getPort().getName();
				hids[i]     = bridges[i].getHID().hid;
				names[i]    = bridges[i].getHID().name;
				creators[i] = bridges[i].getHID().creator;
				
			}
			
			datas = new String[Bridge.getbridges().size()][4];
			
			for(int i = 0; i < datas.length; i++){
				
				answer += "{\"PORT\" : \"" + 	ports[i] + "\",";
				answer += "\"HID\" : \"" + 		hids[i] + "\",";
				answer += "\"NAME\" : \"" + 	names[i] + "\",";
				answer += "\"CREATOR\" : \"" + 	creators[i] + "\"}";
				
				if(i != datas.length - 1)
					answer += ',';
				
			}
			
			answer += "]}";
			
			answer(exc, answer);
			
			break;
		case "SEND" :
			
			String port = (String) args.get("p"); // p = port
			
			if(port == null){
				
				answer(exc, "MISSING");
				System.out.println("Error : missing arg 0 (port)");
				break;
				
			}
			
			String params = (String) args.get("a"); // a = args
			
			if(params == null){
				
				answer(exc, "MISSING");
				System.out.println("Error : missing arg 1 (parameters)");
				break;
				
			}
			
			PortBridge bridge = Bridge.getPortBridge(port);
			
			if(bridge == null){
				
				answer(exc, "NOPORT");
				System.out.println("Error : arg 2 don't refer to a port !");
				break;
				
			}
			
			System.out.println("Sending " + params);
			bridge.send(params);
			System.out.println("Succeed");
			answer(exc, "SUCCEED");
			
			break;
		default :
			answer(exc, "NOSUCH");
			break;
		
		}
		
	}
	
	/**
	 * This method sends the given <code>String</code> to the  given <code>HttpExchange</code>.
	 * @param exc The exchange.
	 * @param answ The answer to send.
	 * @throws IOException If an <code>I/O error</code> occurates.
	 */
	private void answer(HttpExchange exc, String answ) throws IOException{
		
		answ = "<!DOCTYPE HTML>\n<html><head><meta charset=\"UTF-8\"></head><body><div id=\"RESPONSE\">" + answ + "</div></body></html>";
	    exc.sendResponseHeaders(200, answ.length());
	    OutputStream os = exc.getResponseBody();
	    os.write(answ.getBytes());
	    os.close();
		
	}
	
}

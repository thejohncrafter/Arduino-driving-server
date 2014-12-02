package com.ArduinoDrivingServer.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * this class is used to create a handler that refers to a given file.
 * 
 * @author thejohncrafter
 *
 */
@SuppressWarnings("restriction")
public class FileHandler implements HttpHandler {
	
	/**
	 * This field stores <code>File</code> to send.
	 */
	private File path;
	
	/**
	 * This constructor defines the path to the file to send.
	 * @param path
	 */
	public FileHandler(String path){
		
		this.path = new File(path);
		
	}
	
	/**
	 * Found <a href="http://www.rgagnon.com/javadetails/java-have-a-simple-http-server.html">here</a>.
	 */
	public void handle(HttpExchange t) {
		
		System.out.println("Sending file " + path + "...");
		
		try {
			
			File file = path;
			byte [] bytearray  = new byte [(int)file.length()];
			FileInputStream fis;
			fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(bytearray, 0, bytearray.length);
			
			t.sendResponseHeaders(200, file.length());
			OutputStream os = t.getResponseBody();
			os.write(bytearray,0,bytearray.length);
			os.close();
			bis.close();
			
			System.out.println("Successful !");
			
		} catch (IOException e) {
			
			System.out.println("An error has occured in the FileHandler of " + path + " :");
			e.printStackTrace();
			System.out.println("Relmoving context...");
			HTTPServer.removeContext(t.getHttpContext());
			
		}
		
	}
	
}

package com.ArduinoDrivingServer.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

/**
 * This class is statically used to handle the <code>HTTP server</code> 
 * of the program.
 * 
 * @author thejohncrafter
 *
 */
@SuppressWarnings("restriction")
public class HTTPServer {
	
	/**
	 * This <code>ArrayList</code> stores the list of all the <code>ServerEventListener</code>s.
	 * @see ArrayList
	 * @see ServerEventListener
	 */
	private static ArrayList<ServerEventListener> listeners = new ArrayList<ServerEventListener>();
	
	/**
	 * This field stores the <code>HTTP server</code>.
	 */
	private static HttpServer server;
	
	/**
	 * This <code>Thread</code> refreshes the HTTP server files every 10 seconds.
	 */
	private static Thread refresher;
	
	/**
	 * This boolean is used by the method <code>isClosed()</code>.<br>
	 * If it is true, the server is closed, else it is opened.
	 * @see HTTPServer#isClosed()
	 */
	private static boolean closed = true;
	
	/**
	 * This method initializes the server
	 */
	public static boolean open(){
		
		System.out.println("Initializing HTTP server...");
		
		try {
			
			server = HttpServer.create(new InetSocketAddress(8080), 0);
			
		} catch (IOException e) {
			
			System.out.println("Error when initializing server :");
			e.printStackTrace();
			return false;
			
		}
		
		System.out.println("Creating refresher...");
		
		refresher = new Thread(new Runnable(){

			@Override
			public void run() {
				
				while(true){
					
					System.out.println("Refreshing files...");
					
					HttpContext context = server.createContext("/requestes", new RequestHandler());
					context.getFilters().add(new ParameterFilter());
					server.createContext("/", new FileHandler(System.getProperty("user.dir") + "/website/home.html"));
					
					try{
						
						loadAllFromFile(new File(System.getProperty("user.dir") + "/website"));
						
					}catch(IllegalArgumentException e){
						
						System.out.println("Exception when refreshing :");
						e.printStackTrace();
						System.out.println("closing server...");
						close();
						
					}
					
					System.out.println("Done !");
					System.out.println("Waiting for 10 seconds before next refresh...");
					
					try {
						
						Thread.sleep(10000);
						
					} catch (InterruptedException e) {
						
						System.out.println("Refresher interrupted !");
						break;
						
					}
					
				}
				
			}
			
		}, "HTTPServer files refresher");
		
		System.out.println("Starting server...");
		
		refresher.start();
		server.start();
		
		System.out.println("Notifying listeners...");
		
		closed = false;
		
		for(int i = 0; i < listeners.size(); i++)
			listeners.get(i).fireServerOpened();
		
		System.out.println("Successful !");
		
		return true;
		
	}
	
	/**
	 * This method is used to close the server.
	 */
	public static void close(){
		
		System.out.println("Closing server...");
		
		refresher.interrupt();
		server.stop(5);
		
		System.out.println("Notifying listeners...");
		
		closed = true;
		
		for(int i = 0; i < listeners.size(); i++)
			listeners.get(i).fireServerClosed();
		
		System.out.println("Successful !");
		
	}
	
	/**
	 * This method is used to load all the files in a given directory and push it to the server.<br>
	 * It exactly like calling <code>loadAllFromFile(file, "");</code>.
	 * @param file The directory where getting all files.
	 * @see HTTPServer#loadAllFromFile(File, String)
	 */
	private static void loadAllFromFile(File file){
		
		loadAllFromFile(file, "");
		
	}
	
	/**
	 * This method is used to load all the files in a given directory and push it to the server.
	 * @param file The directory where getting all files.
	 * @param path The path where pushing the file in the server.<br>
	 * Will be like this :<br>
	 * <code>path + / + file name</code>
	 */
	private static void loadAllFromFile(File file, String path){
		
		System.out.println("loading " + file.getAbsolutePath());
		
		if(!file.isDirectory())
			throw new IllegalArgumentException("The given file isn't a directory : " + file.getAbsolutePath());
		
		path += '/';
		
		File[] subs = file.listFiles();
		
		for(File f : subs){
			
			if(f.isDirectory()){
				
				System.out.println("File " + f.getAbsolutePath() + " is a directory !");
				loadAllFromFile(f, path + f.getName());
				continue;
				
			}
			
			server.createContext(path + f.getName(), new FileHandler(f.getAbsolutePath()));
			
		}
		
	}
	
	/**
	 * This method is used by <code>FileHandler</code> to remove a given conext from the server.
	 * @param context The context to remove.
	 */
	static void removeContext(HttpContext context){
		
		server.removeContext(context);
		
	}
	
	/**
	 * This method is used to add a <code>ServerEventListener</code>.
	 * @param l The <code>ServerEventListener</code> to add.
	 */
	public static void addServerEventListener(ServerEventListener l){listeners.add(l);}
	
	/**
	 * This method is used to remove a <code>ServerEventListener</code>.
	 * @param l The <code>ServerEventListener</code> to remove.
	 */
	public static void removeServerEventListener(ServerEventListener l){listeners.remove(l);}
	
	/**
	 * This Method is used to know if the <code>Server</code> is closed.
	 * @return True if it is closed, otherwise false.
	 * @see HTTPServer#closed
	 */
	public static boolean isClosed() {return closed;}
	
}

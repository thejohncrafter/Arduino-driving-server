package com.ArduinoDrivingServer.bridge;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.ArduinoDrivingServer.bridge.USB.PortBridge;
import com.ArduinoDrivingServer.web.servlets.ArduinoDriving;

/**
 * This class is the <code>bridge</code> between the software and the hardware.
 * 
 * @author Julien Marquet
 *
 */
public class Bridge {
	
	/**
	 * This <code>HashMap</code> stores all the available <code>PortBridge</code>s.
	 * @see HashMap
	 */
	private static HashMap<String, AbstractBridge> bridges = new HashMap<String, AbstractBridge>();
	
	/**
	 * This field is used by the method <code>isClosed()</code>
	 */
	private static boolean closed = true;
	
	/**
	 * This method is used to initialize the <code>Bridge</code>.
	 * @throws IOException If an error occurates when creating a <code>PortBridge</code> 
	 * (but should rarely happen).
	 * @see PortBridge
	 */
	public static void init() throws BridgeException {
		
		System.out.println("Opening bridge...");
		
		if(!closed)
			throw new IllegalStateException("Bridge already opened !");
		
		Element rootNode;
		
		try{
			
			File bridgesfile = new File(ArduinoDriving.getRealPath("WEB-INF/bridges.xml"));
			SAXBuilder builder = new SAXBuilder();
			Document document = (Document) builder.build(bridgesfile);
			rootNode = document.getRootElement();
			
		}catch(IOException | JDOMException e){
			
			throw new BridgeException(e);
			
		}
		
		List<Element> cfgs = rootNode.getChildren();
		
		for(int i = 0; i < cfgs.size(); i++){
			
			Element cfg = cfgs.get(i);
			Boolean activated = false;
			String activated_str = cfg.getAttributeValue("activated");
			
			if(activated_str == null)
				throw new BridgeException("Error in bridges.xml : in bridge " + i + " : missing activated attribute !");
			
			switch(activated_str){
			
			case "true" :
				activated = true;
				// break;
			case "false" :
				// activated = false;
				break;
			default:
				throw new BridgeException("Error in bridges.xml : in bridge " + i + " : state isn't valid !");
			
			}
			
			if(activated){
				
				Element classloader = cfg.getChild("classloader");
				String classname = classloader.getChildText("classname");
				
				if(classname == null)
					throw new BridgeException("Error in bridges.xml : in bridge " + i + " : in classloader : missing classname !");
				
				switch(classloader.getAttributeValue("type")){
				
				case "JAR" : //TODO : implement JAR load
					{
						String file = classloader.getChildText("file");
						
						if(file == null)
							throw new BridgeException("Error in bridges.xml : in bridge " + i + " : in classloader : missing file !");
						
						Class<?> c;
						
						try{
							
							URLClassLoader loader = new URLClassLoader(new URL[]{new File(ArduinoDriving.getRealPath("/WEB-INF/bridges/" + file))
																.toURI().toURL()}, Bridge.class.getClassLoader());
							c = Class.forName(classname, true, loader);
							
						}catch(ClassNotFoundException | MalformedURLException e){
							
							throw new BridgeException("Error in bridges.xml : in bridge " + i + " : in classloader : in classname : Can't load !", e);
							
						}
						
						try{
							
							Method method = c.getDeclaredMethod("setup");
							Object object = c.newInstance();
							method.invoke(object);
							
						}catch(NoSuchMethodException | InstantiationException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException e){
							
							throw new BridgeException(e);
							
						}
						
					}
					break;
				case "NATIVE" :
					{
						
						Class<?> c;
						
						try{
							
							c = Class.forName(classname);
							
						}catch(ClassNotFoundException e){
							
							throw new BridgeException("Error in bridges.xml : in bridge " + i + " : in classloader : in classname : no such class !", e);
							
						}
						
						try{
							
							Method method = c.getDeclaredMethod("setup");
							Object object = c.newInstance(); 
							method.invoke(object);
							
						}catch(NoSuchMethodException | InstantiationException | IllegalAccessException
								| IllegalArgumentException | InvocationTargetException e){
							
							throw new BridgeException(e);
							
						}
						
					}
					break;
				default :
					throw new BridgeException("Error in bridges.xml : in bridge " + i + " : in classloader : type isn't valid !");
				
				}
				
			}
			
		}
		
		System.out.println("Done.");
		closed = false;
		
	}
	
	/**
	 * This method is used to destroy the bridge.
	 * @throws Exception If an exception occurs when closing a bridge.
	 */
	public static void destroy() throws Exception{
		
		System.out.println("destroying bridge...");
		
		closed = true;
		
		PortBridge[] bridges = Bridge.bridges.values().toArray(new PortBridge[Bridge.bridges.size()]);
		
		for(int i = 0; i < bridges.length; i++)
			bridges[i].close();
		
		Bridge.bridges.clear();
		
		System.out.println("Successful !");
		
	}
	
	/**
	 * This method is used to get all the available <code>PortBridge</code>s.
	 * @throws IOException If an error occurates when creating a <code>PortBridge</code> 
	 * (but should rarely happen).
	 */
	public static void updateBridges() {
		
		//TODO : implement this method
		
	}
	
	/**
	 * This method is used to get a <code>PortBridge</code> by getting its name.
	 * @param port The name of the <code>PortBridge</code> to get.
	 * @return The <code>PortBridge</code>.
	 * @see bridges
	 */
	public static AbstractBridge getPortBridge(String port){
		
		return bridges.get(port);
		
	}
	
	/**
	 * This method is used to add a <code>bridge</code> to the list of bridges.<br>
	 * It is exactly like calling <code>Bridge.getbridges().put(bridge.getName(), bridge);</code>.
	 * @param bridge The bridge to add.
	 */
	public static void addPortBridge(AbstractBridge bridge){
		
		bridges.put(bridge.getPortName(), bridge);
		
	}
	
	/**
	 * This method is called by <code>PortBridge</code> when the hardware is disconnected.
	 * @param pb The <code>PortBridge</code>.
	 */
	public static void fireDisconnected(AbstractBridge pb){
		
		try{
			
			pb.close();
			
		}catch (Exception e){
			
			System.out.println("Can't close port " + pb.getPortName());
			e.printStackTrace();
			
		}finally{
			
			bridges.remove(pb.getPortName());
			
		}
		
	}
	
	/**
	 * This method is used to get the list of the available bridges.
	 * @return The list of the available bridges.
	 */
	public static HashMap<String, AbstractBridge> getbridges(){return bridges;}
	
	/**
	 * This Method is used to know if the <code>Bridge</code> is closed.
	 * @return True if it is closed, otherwise false.
	 * @see Bridge#closed
	 */
	public static boolean isClosed(){return closed;}
	
}

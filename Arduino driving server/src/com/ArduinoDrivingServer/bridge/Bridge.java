package com.ArduinoDrivingServer.bridge;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.ArduinoDrivingServer.bridge.USB.USBBridgeInterface;
import com.ArduinoDrivingServer.web.servlets.ArduinoDriving;

/**
 * This class is the <code>bridge</code> between the software and the hardware.
 * 
 * @author Julien Marquet
 *
 */
public class Bridge {
	
	/**
	 * This <code>HashMap</code> stores all the available <code>AbstractBridgeInteface</code>s.
	 * @see HashMap
	 */
	private static HashMap<String, AbstractBridgeInterface> ifaces = new HashMap<String, AbstractBridgeInterface>();
	
	/**
	 * This <code>HashMap</code> stores the available <code>AbstractBridge</code>s.
	 */
	private static HashMap<String, AbstractBridge> bridges = new HashMap<String, AbstractBridge>();
	
	/**s
	 * This field is used by the method <code>isClosed()</code>
	 */
	private static boolean closed = true;
	
	/**
	 * This method is used to initialize the <code>Bridge</code>.
	 * @throws IOException If an error occurates when creating a <code>PortBridge</code> 
	 * (but should rarely happen).
	 * @see USBBridgeInterface
	 */
	public static void init() throws BridgeException {
		
		if(!closed)
			throw new IllegalStateException("Bridge is already opened !");
		
		Element rootNode;
		
		try{
			
			File bridgesfile = new File(ArduinoDriving.getRealPath("WEB-INF/bridge.xml"));
			SAXBuilder builder = new SAXBuilder();
			Document document = (Document) builder.build(bridgesfile);
			rootNode = document.getRootElement();
			
		}catch(IOException | JDOMException e){
			
			throw new BridgeException(e);
			
		}
		
		String opened = rootNode.getAttributeValue("opened");
		
		if(opened == null)
			throw new BridgeException("Missing \"opened\" attribute in Bridge tag (bridge.xml) !");
		
		if(opened.equals("false")){
			
			System.out.println("Not opening bridge : opened attriute in bridge tag (bridge.xml) isn't true.");
			return;
			
		}else if(!opened.equals("true"))
			throw new BridgeException("Bad value of \"opened\" attribute in Bridge tag (bridge.xml) !");
		
		System.out.println("Opening bridge...");
		
		if(!closed)
			throw new IllegalStateException("Bridge already opened !");
		
		List<Element> cfgs = rootNode.getChild("bridges").getChildren();
		
		for(int i = 0; i < cfgs.size(); i++){
			
			Element cfg = cfgs.get(i);
			Boolean activated = false;
			String activated_str = cfg.getAttributeValue("activated");
			Element classloader = cfg.getChild("classloader");
			String classname = classloader.getChildText("classname");
			Class<?> bridgeClass;
			String name = cfg.getAttributeValue("name");
			String desc = cfg.getChildText("desc");
			
			if(activated_str == null)
				throw new BridgeException("Error in bridge.xml : in bridge " + i + " : missing activated attribute !");
			
			switch(activated_str){
			
			case "true" :
				activated = true;
				// break;
			case "false" :
				// activated = false;
				break;
			default:
				throw new BridgeException("Error in bridge.xml : in bridge " + i + " : state isn't valid !");
			
			}
			
			if(classname == null)
				throw new BridgeException("Error in bridge.xml : in bridge " + i + " : in classloader : missing classname !");
			
			if(name == null)
				throw new BridgeException("Error in bridge.xml : in bridge " + i + " : missing attribute name !");
			
			if(desc == null)
				throw new BridgeException("Error in bridge.xml : in bridge " + i + " : missing <desc> child !");
			
			switch(classloader.getAttributeValue("type")){
			
			case "JAR" :
				{
					
					String file = classloader.getChildText("file");
					
					if(file == null)
						throw new BridgeException("Error in bridge.xml : in bridge " + i + " : in classloader : missing file !");
					
					try{
						
						URLClassLoader loader = new URLClassLoader(new URL[]{new File(ArduinoDriving.getRealPath("/WEB-INF/bridges/" + file))
															.toURI().toURL()}, Bridge.class.getClassLoader());
						bridgeClass = Class.forName(classname, true, loader);
						
					}catch(ClassNotFoundException | MalformedURLException e){
						
						throw new BridgeException("Error in bridge.xml : in bridge " + i + " : in classloader : in classname : Can't load !", e);
						
					}
					
				}
				break;
			case "NATIVE" :
				{
					
					try{
						
						bridgeClass = Class.forName(classname);
						
					}catch(ClassNotFoundException e){
						
						throw new BridgeException("Error in bridge.xml : in bridge " + i + " : in classloader : in classname : no such class !", e);
						
					}
					
				}
				break;
			default :
				throw new BridgeException("Error in bridge.xml : in bridge " + i + " : in classloader : type isn't valid !");
			
			}
			
			AbstractBridge bridge;
			
			try{
				
				bridge = (AbstractBridge) bridgeClass.newInstance();
				
			}catch(InstantiationException | IllegalAccessException | IllegalArgumentException | ClassCastException e){
				
				throw new BridgeException("Can't instantiate " + bridgeClass.getName(), e);
				
			}
			
			bridge.setName(name);
			bridge.setDesc(desc);
			bridge.setActivated(activated);
			
			bridges.put(name, bridge);
			
			if(activated){
				
				System.out.println("Initializing bridge" + name + "...");
				
				initBridge(bridge);
				
			}
			
		}
		
		System.out.println("Done.");
		closed = false;
		
	}
	
	/**
	 * This method is used to destroy the bridge.
	 * @throws BridgeException If an exception occurs when closing a bridge.
	 */
	public static void destroy() throws BridgeException{
		
		if(closed)
			throw new IllegalStateException("Bridge is already closed !");
		
		System.out.println("destroying bridge...");
		
		closed = true;
		
		String[] ifacesKeys = ifaces.keySet().toArray(new String[ifaces.size()]);
		
		for(String key : ifacesKeys)
			ifaces.remove(key).close();
		
		String[] bridgesKeys = bridges.keySet().toArray(new String[bridges.size()]);
		
		for(String key : bridgesKeys)
			bridges.remove(key).destroy();
		
		Bridge.ifaces.clear();
		
		System.out.println("Successful !");
		
	}
	
	/**
	 * This method is used to get all the available <code>PortBridge</code>s.<br>
	 * This method automatically destroys the invalid bridges.
	 * @throws BridgeException If an exception occurs when closing an 
	 * invalid bridge, or if it can't be closed.
	 */
	public static void updateBridges() throws BridgeException {
		
		System.out.println("Updating bridges...");
		
		for(int i = 0; i < bridges.size(); i++){
			
			bridges.get(i).update();
			
		}
		
	}
	
	/**
	 * This method is used to initialize a given bridge. It automatically stores the created 
	 * <code>AbstractBridgeInterface</code> in the <code>ifaces</code> field.
	 * @param bridge
	 */
	public static void initBridge(AbstractBridge bridge){
		
		HashMap<String, AbstractBridgeInterface> ifaces = bridge.init();
		String[] keys = ifaces.keySet().toArray(new String[ifaces.size()]);
		
		for(String key : keys){
			
			Bridge.ifaces.put(key, ifaces.get(key));
			
		}
		
	}
	
	/**
	 * This method is used to close the given bridge.<br>
	 * If closes the bridge, then all the associated <code>AbstractBridgeinterface</code>s.
	 * @param bridge The bridge to close.
	 * @throws BridgeException If an exception occurs when closing the bridge.
	 */
	public static void destroyBridge(AbstractBridge bridge) throws BridgeException{
		
		bridge.destroy();
		
		String[] keys = ifaces.keySet().toArray(new String[ifaces.size()]);
		
		for(String key : keys){
			
			AbstractBridgeInterface iface = ifaces.get(key);
			
			if(iface.getBridgeName().equals(bridge.getName())){
				
				if(!ifaces.get(key).close())
					throw new BridgeException("Can't close iface " + iface.getPortName() + " !");
				
			}
			
		}
		
	}
	
	/**
	 * This method is used to change the name of a given AbstractBridge to a given name.
	 * <div style="color:red;">This method does not edit bridge.xml !</div>
	 * @param bridge The bridge where changing name.
	 * @param name The new name.
	 */
	public static void changeBridgeName(AbstractBridge bridge, String name){
		
		String[] keys = ifaces.keySet().toArray(new String[ifaces.size()]);
		
		for(String key : keys){
			
			AbstractBridgeInterface iface = ifaces.get(key);
			
			if(iface.getBridgeName().equals(bridge.getName())){
				
				ifaces.get(key).setBridgeName(name);
				
			}
			
		}
		
		bridge.setName(name);
		
	}
	
	/**
	 * This method is called by <code>PortBridge</code> when the hardware is disconnected.
	 * @param pb The <code>PortBridge</code>.
	 */
	public static void fireDisconnected(AbstractBridgeInterface pb){
		
		try{
			
			pb.close();
			
		}catch (Exception e){
			
			System.out.println("Can't close port " + pb.getPortName());
			e.printStackTrace();
			
		}finally{
			
			ifaces.remove(pb.getPortName());
			
		}
		
	}
	
	/**
	 * This method is used to know if a driver exists for the given HID.
	 * @param hid The HID to test.
	 * @return True if the driver exists, otherwise false.
	 */
	public static boolean hasDriver(String hid){return new File(ArduinoDriving.getRealPath("drivers/" + hid.replace(" ", "_") + "/driver.jsp")).exists();}
	
	/**
	 * This method is used to get a <code>PortBridge</code> by getting its name.
	 * @param port The name of the <code>PortBridge</code> to get.
	 * @return The <code>PortBridge</code>.
	 * @see bridges
	 */
	public static AbstractBridgeInterface getPortBridge(String port){return ifaces.get(port);}
	
	/**
	 * This method is used to add a <code>bridge</code> to the list of bridges.<br>
	 * It is exactly like calling <code>Bridge.getbridges().put(bridge.getName(), bridge);</code>.
	 * @param bridge The bridge to add.
	 */
	public static void addPortBridge(AbstractBridgeInterface bridge){ifaces.put(bridge.getPortName(), bridge);}
	
	/**
	 * This method is used to get the list of the available interfaces.
	 * @return The list of the available interfaces.
	 */
	public static HashMap<String, AbstractBridgeInterface> getIFaces(){return ifaces;}
	
	/**
	 * This method is used to get the list of available bridges.
	 * @return The list of available bridges.
	 */
	public static HashMap<String, AbstractBridge> getBridges(){return bridges;}
	
	/**
	 * This Method is used to know if the <code>Bridge</code> is opened.
	 * @return True if it is opened, otherwise false.
	 * @see Bridge#closed
	 */
	public static boolean isOpened(){return !closed;}
	
}

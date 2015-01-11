package com.ArduinoDrivingServer.bridge;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import com.ArduinoDrivingServer.bridge.HID.InvalidHIDException;

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
	public static void init() {
		
		System.out.println("Opening bridge...");
		
		if(!closed)
			System.out.println("Bridge already opened. Reloading...");
		
		System.out.println("Setting up the available ports list...");
		updatePortBridges();
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
	 * @see PortBridge
	 */
	public static void updatePortBridges() {
		
		System.out.println("updating PortBridges...");
		
		String[] portNames = SerialPortList.getPortNames();
		
		mainLoop : for(String id : portNames){
			
			System.out.println("Checking " + id);
			
			if(Bridge.bridges.containsKey(id)){
				
				System.out.println("A PortBridge is already created for the port " + id);
				
				try {
					
					AbstractBridge b = bridges.get(id);
					b.updateHID();
					
				} catch (Exception e) {
					
					System.out.println("Error when getting HID :");
					e.printStackTrace();
					System.out.println("Removing " + id);
					bridges.remove(id);
					continue;
					
				}
				
				System.out.println("Keeping it !");
				continue;
				
			}
			
			System.out.println("registering port " + id + "...");
			System.out.println("Creating PortBridge...");
			
			SerialPort port = null;
			
			while(true){
				
				port = new SerialPort(id);
				
				try {
					
					port.openPort();
					
				} catch (SerialPortException e) {
					
					System.out.println("Error when openeing port :");
					e.printStackTrace();
					System.out.println(id + " isn't registered.");
					continue mainLoop;
					
				}
				
				break;
				
			}
			
			try {
				
				bridges.put(id, new PortBridge(port, id));
				System.out.println("port " + id + " is registered");
				
			} catch (IOException e){
				
				System.out.println("Error when initializing PortBridge :");
				e.printStackTrace();
				System.out.println("port " + id + " isn't registered.");
				
			}catch (TimeoutException e) {
				
				System.out.println("Error : timeout ended before getting HID !");
				System.out.println("port " + id + " isn't registered.");
				
			}catch(InvalidHIDException e){
				
				System.out.println("Error : HID is invalid !");
				System.out.println("port " + id + " isn't registered.");
				
			} catch (SerialPortException e) {
				
				System.out.println("Error : " + e.getMessage());
				System.out.println("port " + id + " isn't registered.");
				
			} catch (InterruptedException e) {
				
				System.out.println("Error : thread interrupted when waiting for HID !");
				System.out.println("port " + id + " isn't registered.");
				
			}
			
		}
		
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

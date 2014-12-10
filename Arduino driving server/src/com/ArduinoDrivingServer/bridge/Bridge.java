package com.ArduinoDrivingServer.bridge;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import javax.swing.JOptionPane;

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
	private static HashMap<String, AbstractPortBridge> bridges = new HashMap<String, AbstractPortBridge>();
	
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
		
		System.out.println("Setting up the available ports list...");
		updatePortBridges();
		System.out.println("Done.");
		closed = false;
		
	}
	
	/**
	 * This method is used to destroy the bridge.
	 */
	public static void destroy(){
		
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
		
		Enumeration<?> ids = CommPortIdentifier.getPortIdentifiers();
		
		mainLoop : while(ids.hasMoreElements()){
			
			CommPortIdentifier id = (CommPortIdentifier) ids.nextElement();
			
			System.out.println("Checking " + id.getName());
			
			if(id.getPortType() == CommPortIdentifier.PORT_SERIAL){
				
				if(Bridge.bridges.containsKey(id.getName())){
					
					System.out.println("A PortBridge is already created for the port " + id.getName());
					
					try {
						
						AbstractPortBridge b = bridges.get(id.getName());
						
						if(b instanceof PortBridge)
							((PortBridge) b).updateHID();
						
					} catch (TimeoutException | InvalidHIDException e) {
						
						System.out.println("Error when getting HID :");
						e.printStackTrace();
						System.out.println("Removing " + id.getName());
						bridges.remove(id.getName());
						continue;
						
					}
					
					System.out.println("Keeping it !");
					continue;
					
				}
				
				System.out.println("The port " + id.getName() + " is a serial port !");
				System.out.println("registering it...");
				
				System.out.println("Creating PortBridge for port " + id.getName() + "...");
				
				SerialPort port = null;
				
				while(true){
					
					try {
						
						port = (SerialPort) id.open(id.getName(), 9600);
						
					} catch (PortInUseException e) { // because id.isCurrentlyOwned() don't work on Windows (I don't know for the others systems)
						
						System.out.println("The port is currently owned !");
						
						int resp = JOptionPane.showConfirmDialog(null, "The port " + id.getName() + " is "
								+ "currently owned by another program.\n"
								+ "Please shut it down and press OK,\n"
								+ "or press cancel.", "alert", JOptionPane.OK_CANCEL_OPTION);
						
						if(resp == JOptionPane.OK_OPTION)
							continue;
						// else
						System.out.println("User has choosed to cancel action.");
						continue mainLoop;
						
					}
					
					break;
					
				}
				
				try {
					
					bridges.put(id.getName(), new PortBridge(port, id.getName()));
					System.out.println("port " + id.getName() + " is registered");
					
				} catch (IOException e){
					
					System.out.println("Error when initializing PortBridge :");
					e.printStackTrace();
					System.out.println("port " + id.getName() + " isn't registered.");
					
				}catch (TimeoutException e) {
					
					System.out.println("Error : timeout ended before getting HID !");
					System.out.println("port " + id.getName() + " isn't registered.");
					
				}catch(InvalidHIDException e){
					
					System.out.println("Error : HID is invalid !");
					System.out.println("port " + id.getName() + " isn't registered.");
					
				}
				
			}
			
		}
		
	}
	
	/**
	 * This method is used to get a <code>PortBridge</code> by getting its name.
	 * @param port The name of the <code>PortBridge</code> to get.
	 * @return The <code>PortBridge</code>.
	 * @see bridges
	 */
	public static AbstractPortBridge getPortBridge(String port){
		
		return bridges.get(port);
		
	}
	
	/**
	 * This method is used to add a <code>bridge</code> to the list of bridges.<br>
	 * It is exactly like calling <code>Bridge.getbridges().put(bridge.getName(), bridge);</code>.
	 * @param bridge The bridge to add.
	 */
	public static void addPortBridge(AbstractPortBridge bridge){
		
		bridges.put(bridge.getPortName(), bridge);
		
	}
	
	/**
	 * This method is called by <code>PortBridge</code> when the hardware is disconnected.
	 * @param pb The <code>PortBridge</code>.
	 */
	public static void fireDisconnected(AbstractPortBridge pb){
		
		pb.close();
		bridges.remove(pb.getPortName());
		
	}
	
	/**
	 * This method is used to get the list of the available bridges.
	 * @return The list of the available bridges.
	 */
	public static HashMap<String, AbstractPortBridge> getbridges(){return bridges;}
	
	/**
	 * This Method is used to know if the <code>Bridge</code> is closed.
	 * @return True if it is closed, otherwise false.
	 * @see Bridge#closed
	 */
	public static boolean isClosed(){return closed;}
	
}

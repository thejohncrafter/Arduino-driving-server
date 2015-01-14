package com.ArduinoDrivingServer.bridge.USB;

import java.util.HashMap;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import com.ArduinoDrivingServer.bridge.AbstractBridge;
import com.ArduinoDrivingServer.bridge.AbstractBridgeInterface;
import com.ArduinoDrivingServer.bridge.Bridge;
import com.ArduinoDrivingServer.bridge.BridgeException;

/**
 * This class is used to create the USB bridge.
 * @author Julien Marquet
 *
 */
public class USBBridge extends AbstractBridge {
	
	/**
	 * This method sets up the USB bridge.<br>
	 * It creates a PortBridge for each valid USB port.
	 */
	public HashMap<String, AbstractBridgeInterface> init(){
		
		System.out.println("Opening USBBridge...");
		
		HashMap<String, AbstractBridgeInterface> bridges = new HashMap<String, AbstractBridgeInterface>();
		String[] portNames = SerialPortList.getPortNames();
		
		mainLoop : for(String id : portNames){
			
			System.out.println("registering port " + id + "...");
			System.out.println("Creating PortBridge...");
			
			SerialPort port = null;
			
			port = new SerialPort(id);
			
			try {
				
				port.openPort();
				
			} catch (SerialPortException e) {
				
				System.out.println("Error when openeing port :");
				e.printStackTrace();
				System.out.println(id + " isn't registered.");
				continue mainLoop;
				
			}
			
			try {
				
				USBBridgeInterface bridge = new USBBridgeInterface(port, id);
				
				if(!Bridge.hasDriver(bridge.getHID().hid)){
					
					System.out.println("Missing driver for " + bridge.getHID().hid);
					System.out.println(bridge.getPortName() + " isn't registered.");
					System.out.println("Closing " + bridge.getPortName() + "...");
					
					if(bridge.close())
						System.out.println("Ok.");
					else
						System.out.println("Can't close bridge " + bridge.getPortName() + " !");
					
				}else{
					
					bridges.put(id, bridge);
					System.out.println("port " + id + " is registered");
					
				}
				
			}catch(BridgeException e){
				
				System.out.println("Can't register port : " + e.getMessage());
				
			}
			
		}
		
		return bridges;
		
	}

	@Override
	public HashMap<String, AbstractBridgeInterface> update() {
		
		System.out.println("Updating USBBridge...");
		
		HashMap<String, AbstractBridgeInterface> bridges = new HashMap<String, AbstractBridgeInterface>();
		String[] portNames = SerialPortList.getPortNames();
		
		mainLoop : for(String id : portNames){
			
			System.out.println("Checking " + id);
			
			if(Bridge.getIFaces().containsKey(id)){
				
				System.out.println("A PortBridge is already created for the port " + id);
				continue;
				
			}
			
			System.out.println("registering port " + id + "...");
			System.out.println("Creating PortBridge...");
			
			SerialPort port = null;
			
			port = new SerialPort(id);
			
			try {
				
				port.openPort();
				
			} catch (SerialPortException e) {
				
				System.out.println("Error when openeing port :");
				e.printStackTrace();
				System.out.println(id + " isn't registered.");
				continue mainLoop;
				
			}
			
			try {
				
				USBBridgeInterface bridge = new USBBridgeInterface(port, id);
				
				if(!Bridge.hasDriver(bridge.getHID().hid)){
					
					System.out.println("Missing driver for " + bridge.getHID().hid);
					System.out.println(bridge.getPortName() + " isn't registered.");
					System.out.println("Closing " + bridge.getPortName() + "...");
					
					if(bridge.close())
						System.out.println("Ok.");
					else
						System.out.println("Can't close bridge " + bridge.getPortName() + " !");
					
				}else{
					
					bridges.put(id, bridge);
					System.out.println("port " + id + " is registered");
					
				}
				
			}catch(BridgeException e){
				
				System.out.println("Can't register port : " + e.getMessage());
				
			}
			
		}
		
		return bridges;
		
	}
	
	@Override
	public void destroy(){/* Nothing to do. */}
	
}

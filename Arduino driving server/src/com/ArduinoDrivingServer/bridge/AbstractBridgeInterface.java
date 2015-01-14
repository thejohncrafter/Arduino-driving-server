package com.ArduinoDrivingServer.bridge;

import com.ArduinoDrivingServer.bridge.HID.HID;

/**
 * This class is used to create <code>PortBridge</code>s and <code>VirtualPort</code>s.
 * @author Julien Marquet
 *
 */
public abstract class AbstractBridgeInterface {
	
	/**
	 * This field stores the type of bridge where the interface 
	 * was created (USB, bluetooth,...).
	 */
	private String bridgeName;
	
	/**
	 * This constructor just defines the <code>virtual</code> boolean.
	 * @param vrtual This boolean is used to know if the created <code>PortBridge</code> is virtual or not.
	 * @see virtual
	 */
	public AbstractBridgeInterface(String bridgeType){
		
		this.bridgeName = bridgeType;
		
	}
	
	/**
	 * This method is used to get the HID (HardwareIDentifier) of the port.
	 * @return The HID of the port.
	 * @see HIDGetter
	 * @see HIDGetter#getHID()
	 * @see HID 
	 */
	public abstract HID getHID();
	
	/**
	 * This method is used to read a line from the input stream.<br>
	 * It waits for data and returns the read line.
	 * @return The read line.
	 * @throws Exception If an error occurs.
	 */
	public abstract String readLine() throws BridgeException;

	/**
	 * This method is used to read a line from the input stream until a given timeout.<br>
	 * It waits for data and returns the read line.
	 * @param timeout The maximum time to wait in milliseconds.
	 * @return The read line.
	 * @throws Exception If an error occurs.
	 */
	public abstract String readLine(long timeout) throws BridgeException;

	/**
	 * This method is used to read a line from the input stream.<br>
	 * It sends the given line, waits for data and returns the read line.<br>
	 * Calling this method is the same as calling <code>send(line); realLine();</code>.
	 * @param lin The line to send.
	 * @return The read line.
	 * @throws Exception If an error occurs.
	 */
	public abstract String readLine(String line) throws BridgeException;
	/**
	 * This method is used to read a line from the input stream until a given timeout.<br>
	 * It sends the given line, waits for data and returns the read line.
	 * @param lin The line to send.
	 * @param timeout The maximum time to wait in milliseconds.
	 * @return The read line.
	 * @throws Exception If an error occurs.
	 */
	public abstract String readLine(String line, long timeout) throws BridgeException;
	
	/**
	 * This method is used to send a request through the port.
	 * @param request The request to send.
	 * @throws Exception If an error occurs.
	 */
	public abstract void send(String request) throws BridgeException;
	
	/**
	 * This method is used to close the <code>PortBridge</code>.
	 * @return True if the bridge has been succesdfully closed, otherwise false.
	 * @throws Exception If an error occurs.
	 */
	public abstract boolean close() throws BridgeException;
	
	/**
	 * This method is used to update the HID.
	 * @throws Exception if an exception occurs.
	 */
	public abstract void updateHID() throws BridgeException;
	
	/**
	 * This method is used to get the port's name.
	 * @return The port's name.
	 */
	public abstract String getPortName();
	
	/**
	 * This method is used to get the name of the bridge where the interface 
	 * was created (USB, bluetooth,...).
	 * @return The parent bridge name.
	 */
	public String getBridgeName(){return bridgeName;}
	
	/**
	 * This method is used to set the name of the bridge where the interface 
	 * was created (USB, bluetooth,...). It is called when the bridge name changes.
	 * @param name The new parent bridge name.
	 */
	public void setBridgeName(String name){this.bridgeName = name;}
	
}

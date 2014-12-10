package com.ArduinoDrivingServer.bridge;

import com.ArduinoDrivingServer.bridge.HID.HID;
import com.ArduinoDrivingServer.bridge.HID.HIDGetter;

/**
 * This class is used to create <code>PortBridge</code>s and <code>VirtualPort</code>s.
 * @author Julien Marquet
 *
 */
public abstract class AbstractPortBridge {
	
	/**
	 * This boolean is used to know if the created <code>PortBridge</code> is virtual or not.
	 */
	private boolean virtual;
	
	/**
	 * This constructor just defines the <code>virtual</code> boolean.
	 * @param vrtual This boolean is used to know if the created <code>PortBridge</code> is virtual or not.
	 * @see virtual
	 */
	public AbstractPortBridge(boolean virtual){
		
		this.virtual = virtual;
		
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
	 * This method is used to get the port's name.
	 * @return The port's name.
	 */
	public abstract String getPortName();
	
	/**
	 * This method is used to read a line from the input stream.<br>
	 * It waits for data and returns the read line.
	 * @return The read line.
	 * @throws InterruptedException If the thread is interrupted when waiting for data.
	 */
	public abstract String readLine() throws InterruptedException;

	/**
	 * This method is used to read a line from the input stream until a given timeout.<br>
	 * It waits for data and returns the read line.
	 * @param timeout The maximum time to wait in milliseconds.
	 * @return The read line.
	 * @throws InterruptedException If the thread is interrupted when waiting for data.
	 */
	public abstract String readLine(long timeout) throws InterruptedException;

	/**
	 * This method is used to read a line from the input stream.<br>
	 * It sends the given line, waits for data and returns the read line.<br>
	 * Calling this method is the same as calling <code>send(line); realLine();</code>.
	 * @param lin The line to send.
	 * @return The read line.
	 * @throws InterruptedException If the thread is interrupted when waiting for data.
	 */
	public abstract String readLine(String line) throws InterruptedException;
	/**
	 * This method is used to read a line from the input stream until a given timeout.<br>
	 * It sends the given line, waits for data and returns the read line.<br>
	 * Calling this method is the same as calling <code>send(line); realLine();</code>.
	 * @param lin The line to send.
	 * @param timeout The maximum time to wait in milliseconds.
	 * @return The read line.
	 * @throws InterruptedException If the thread is interrupted when waiting for data.
	 */
	public abstract String readLine(String line, long timeout) throws InterruptedException;
	
	/**
	 * This method is used to send a request through the port.
	 * @param request The request to send.
	 */
	public abstract void send(String request);
	
	/**
	 * This method is used to close the <code>PortBridge</code>.
	 */
	public abstract void close();
	
	/**
	 * This boolean is used to know if the created <code>PortBridge</code> is virtual or not.
	 * @return <code>true</code> if the <code>PortBridge</code>, otherwise <code>false</code>.
	 */
	public boolean isVirtual(){
		
		return virtual;
		
	}
	
}

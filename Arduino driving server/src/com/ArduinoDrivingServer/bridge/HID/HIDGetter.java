package com.ArduinoDrivingServer.bridge.HID;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.ArduinoDrivingServer.bridge.PortBridge;

/**
 * This class is used to get the HID of a port.
 * @see HID
 * 
 * @author Julien Marquet
 *
 */
public class HIDGetter {
	
	/**
	 * This field stores the handled <code>SerialPort</code>.
	 */
	private PortBridge bridge;
	
	/**
	 * This constructor needs the <code>in</code> and <code>out</code> stream of the port where getting HID.
	 * @param in The input stream.
	 * @param out The output stream.
	 * @throws IOException If an error occurates when getting <code>in</code> an <code>out stream</code>s (should rarely happen).
	 */
	public HIDGetter(PortBridge port) {
		
		this.bridge = port;
		
	}
	
	/**
	 * This method is used to get the HID of the given port until a timeout of 5 seconds.
	 * @return The HID of the hardware.
	 * @throws TimeoutException If the timeout ends.
	 * @throws InvalidHIDException If the HID is invalid.
	 */
	public HID getHID() throws TimeoutException, InvalidHIDException {
		
		System.out.println("getting HID...");
		System.out.println("sending request...");
		
		String hid = null;
		
		try {
			
			hid = bridge.readLine("HID", 5000);
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			
		}
		
		if(hid == null)
			throw new TimeoutException("Timeout ended before hardware's response.");
		
		if(hid.indexOf('#') == -1)
			throw new InvalidHIDException("The HID don't contains a \"#\"");
		
		System.out.println("Got a valid answer !");
		
		String creator = hid.substring(0, hid.indexOf('#'));
		String name = hid.substring(hid.indexOf('#') + 1);
		
		HID theHID = new HID();
		theHID.hid = hid;
		theHID.creator = creator;
		theHID.name = name;
		
		System.out.println("Successful.");
		
		return theHID;
		
	}
	
}

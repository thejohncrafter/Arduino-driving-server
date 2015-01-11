package com.ArduinoDrivingServer.bridge.HID;

import java.util.concurrent.TimeoutException;

import jssc.SerialPortException;

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
	 * This method is used to get the HID of the given port until a timeout of 5 seconds.
	 * @return The HID of the hardware.
	 * @throws TimeoutException If the timeout ends.
	 * @throws InvalidHIDException If the HID is invalid.
	 */
	public static HID getHID(PortBridge bridge) throws TimeoutException, InvalidHIDException, SerialPortException, InterruptedException {
		
		System.out.println("getting HID...");
		System.out.println("sending request...");
		
		String hid = null;
		
		hid = bridge.readLine("HID", 5000);
		
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

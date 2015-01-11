package com.ArduinoDrivingServer.bridge.HID;

/**
 * This class is used to stores the datas of a HID (HardwareIDentifier).<br>
 * A HID is created like this : <code>&lsaquo;creator&rsaquo;&#135;&lsaquo;hardware name&rsaquo;</code>.
 * 
 * @author Julien Marquet
 *
 */
public class HID {
	
	/**
	 * The HID (HardwareIDentifier).
	 */
	public String hid;
	
	/**
	 * The name of the hardware.
	 */
	public String name;
	
	/**
	 * The name of the creator.
	 */
	public String creator;
	
	public static HID createHID(String hid) throws InvalidHIDException{
		
		if(!hid.contains("#"))
			throw new InvalidHIDException();
		
		String creator = hid.substring(0, hid.indexOf('#'));
		String name = hid.substring(hid.indexOf('#') + 1);
		
		HID theHID = new HID();
		theHID.hid =  hid;
		theHID.creator = creator;
		theHID.name = name;
		
		return theHID;
		
	}
	
	/**
	 * Returns the <code>HID</code>.
	 * @see hid
	 */
	public String toString(){
		
		return hid;
		
	}
	
}

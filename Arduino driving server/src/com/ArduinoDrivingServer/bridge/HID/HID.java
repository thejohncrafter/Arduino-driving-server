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
	
}

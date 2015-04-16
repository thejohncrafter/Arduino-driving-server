package com.ArduinoDrivingServer.bridge;

import java.io.File;

import com.ArduinoDrivingServer.bridge.HID.HID;

/**
 * This class is used to create a <code>"virtual"</code> port :
 * a port fully handled by the porgramm (used for driver creation).
 * @author Julien Marquet
 * 
 */
public class VirtualPort extends AbstractBridgeInterface {
	
	//TODO : fully implement this class
	/**
	 * This <code>File</code> is used to log.
	 */
	private File logFile;
	
	/**
	 * This field stores the <code>HID</code> of emulated hardware.
	 * @see HID
	 */
	private HID hid;
	
	/**
	 * This field stores the emulated hardware's port name.
	 * @see portName
	 */
	private String portName;
	
	/**
	 * This constructor defines the name and the HID of the port.
	 * It also defines <code>virtual</code> to <code>true</code> on <code>super</code>.
	 * @param name The name of the emulated port.
	 * @param hid the port's HID.
	 */
	public VirtualPort(String portName, HID hid) {
		
		super("virtual port");
		
		this.portName = portName;
		this.hid = hid;
		
		Bridge.getInstance().addPortBridge(this);
		
	}
	
	/**
	 * This constructor defines the name, the HID and the log file of the port.
	 * It also defines <code>virtual</code> to <code>true</code> on <code>super</code>.
	 * @param name The name of the emulated port.
	 * @param hid the port's HID.
	 * @param logFile the file where logging.
	 */
	public VirtualPort(String portName, HID hid, File logFile) {
		
		super("virtual port");
		
		this.portName = portName;
		this.hid = hid;
		this.logFile = logFile;
		
		Bridge.getInstance().addPortBridge(this);
		
	}

	@Override
	public HID getHID() {
		
		return hid;
		
	}
	
	@Override
	public String getPortName() {
		
		return portName;
		
	}
	
	@Override
	public String readLine() {
		
		return null;
		
	}
	
	@Override
	public String readLine(long timeout) {
		
		return null;
		
	}
	
	@Override
	public String readLine(String line) {
		
		return null;
		
	}
	
	@Override
	public String readLine(String line, long timeout) {
		
		return null;
		
	}
	
	@Override
	public void send(String request) {
		
		
		
	}
	
	/**
	 * Closes the assigned <code>VirtualPortView</code>.
	 */
	@Override
	public boolean close(){
		
		return true;
		
	}
	
	/**
	 * This method is used to get the <code>log file</code>.
	 * @return The <code>log file</code>.
	 * @see logFile
	 */
	public File getLogFile(){
		
		return logFile;
		
	}
	
	@Override
	public void updateHID() {/* Nothing to do... */}
	
}

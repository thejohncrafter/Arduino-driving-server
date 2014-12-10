package com.ArduinoDrivingServer.bridge;

import com.ArduinoDrivingServer.bridge.HID.HID;
import com.ArduinoDrivingServer.gui.VirtualPort.VirtualPortView;

/**
 * This class is used to create a <code>"virtual"</code> port :
 * a port fully handled by the porgramm (used for driver creation).
 * @author Julien Marquet
 * 
 */
public class VirtualPort extends AbstractPortBridge {
	
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
	 * This field stores the port's current view.
	 * @see VirtualPortView
	 */
	private VirtualPortView view;
	
	/**
	 * This constructor defines the name of the port.
	 * It also defines <code>virtual</code> to <code>true</code> on <code>super</code>.
	 * @param name The name of the emulated port.
	 */
	public VirtualPort(String portName, HID hid) {
		
		super(true);
		
		this.portName = portName;
		this.hid = hid;
		
		Bridge.addPortBridge(this);
		
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
	public String readLine() throws InterruptedException {
		
		return view.dataNeeded("");
		
	}
	
	@Override
	public String readLine(long timeout) throws InterruptedException {
		
		return view.dataNeeded("");
		
	}
	
	@Override
	public String readLine(String line) throws InterruptedException {
		
		return view.dataNeeded(line);
		
	}
	
	@Override
	public String readLine(String line, long timeout) throws InterruptedException {
		
		return view.dataNeeded(line);
		
	}
	
	@Override
	public void send(String request) {
		
		view.dataSent(request);
		
	}
	
	/**
	 * Closes the assigned <code>VirtualPortView</code>.
	 */
	@Override
	public void close(){
		
		view.setVisible(false);
		
	}
	
	/**
	 * This method is set the view of the <code>VirtualPort</code>.
	 * @param view The new view.
	 */
	public void setView(VirtualPortView view){
		
		this.view = view;
		
	}
	
}

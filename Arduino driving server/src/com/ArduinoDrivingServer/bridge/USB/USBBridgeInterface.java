package com.ArduinoDrivingServer.bridge.USB;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import com.ArduinoDrivingServer.bridge.AbstractBridgeInterface;
import com.ArduinoDrivingServer.bridge.Bridge;
import com.ArduinoDrivingServer.bridge.BridgeException;
import com.ArduinoDrivingServer.bridge.HID.HID;

/**
 * This class is used to create a <code>bridge</code> for a given port.<br>
 * It contains :
 * <ul>
 * <li>the <code>SerialPort</code></li>
 * <li>a <code>Scanner</code></li>
 * <li>a <code>PrintStream</code></li>
 * 
 * @author Julien Marquet
 *
 */
public class USBBridgeInterface extends AbstractBridgeInterface implements SerialPortEventListener {
	
	/**
	 * This field stores the exception that happened in <code>readLine()</code> 
	 * if an error happened.
	 */
	private SerialPortException readException;
	
	/**
	 * This field stores the handled <code>SerialPort</code>.
	 */
	private SerialPort port;
	
	/**
	 * This field stores the port's name 
	 * (because <code>port.getName()</code> don't work under Windows).
	 */
	private String portName;
	
	/**
	 * This field stores the <code>HID</code> of the hardware.
	 * @see HID
	 */
	private HID hid;
	
	/**
	 * This field stores the last request sent.
	 */
	private volatile String lastSent;
	
	/**
	 * This field stores the count of calls on <code>readLine()</code>.
	 */
	private int remaining;
	
	/**
	 * This field stores the received String while it contains any \n.
	 */
	private String receiveCache;
	
	/**
	 * This constructor uses the given port to define the fields <code>in</code> and <code>out</code>.
	 * @param port The port to handle.
	 * @param name The port's name.
	 * @throws BridgeException If an exception occurs.
	 * @see HID
	 * @see HIDGetter
	 */
	public USBBridgeInterface(SerialPort port, String portName) throws BridgeException{
		
		super("USB");
		
		remaining = 0;
		receiveCache = "";
		this.port = port;
		
		try{
			
			port.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			port.addEventListener(this);
			
		}catch(SerialPortException e){
			
			throw new BridgeException(e);
			
		}
		
		this.portName = portName;
		
		if(System.getProperty("os.name").toUpperCase().contains("LINUX")){
			
			// Linux WTF fix
			//TODO : Better method for this fix...
			try{
				
				Thread.sleep(3000);
				
			}catch(InterruptedException e){
				
				throw new BridgeException(e);
				
			}
			
		}
		
		try{
			
			updateHID();
			
		}catch(BridgeException e){
			
			System.out.println("Error when updating HID. Closing port...");
			
			if(!close())
				throw new BridgeException("Can't close PortBridge (thrown after catching a BridgeException)", e);
			
			throw e;
			
		}
		
		System.out.println("hardware's HID : " + hid.hid);
		
		
	}
	
	@Override
	public boolean close() throws BridgeException{
		
		try{
			
			return port.closePort();
			
		}catch(Exception e){
			
			throw new BridgeException(e);
			
		}
		
	}
	
	@Override
	public HID getHID(){
		
		return hid;
		
	}
	
	@Override
	public String readLine() throws BridgeException{
		
		try{
			
			remaining++;
			
			synchronized(this){
				
				wait();
				
			}
			
			remaining--;
			
			if(readException != null){
				
				readException = null;
				throw readException;
				
			}
			
			if(remaining == 0){
				
				String toReturn = lastSent;
				lastSent = null;
				return toReturn;
				
			}
			
		}catch(Exception e){
			
			throw new BridgeException(e);
			
		}
		
		return lastSent;
		
	}
	
	@Override
	public String readLine(String line) throws BridgeException{
		
		send(line);
		
		return readLine();
		
	}
	
	@Override
	public String readLine(String line, long timeout) throws BridgeException{
		
		send(line);
		
		return readLine(timeout);
		
	}
	
	@Override
	public String readLine(long timeout) throws BridgeException{
		
		try{
			
			remaining++;
			
			synchronized(this){
				
				wait(timeout);
				
			}
			
			remaining--;
			
			if(readException != null){
				
				readException = null;
				throw readException;
				
			}
			
			if(remaining == 0){
				
				String toReturn = lastSent;
				lastSent = null;
				return toReturn;
				
			}
			
		}catch(Exception e){
			
			throw new BridgeException(e);
			
		}
		
		return lastSent;
		
	}
	
	@Override
	public void send(String request) throws BridgeException{
		
		try{
			
			port.writeString(request);
			
		}catch(Exception e){
			
			throw new BridgeException(e);
			
		}
		
	}
	
	@Override
	public void serialEvent(SerialPortEvent arg0) {
		
		if(arg0.isRXCHAR()){
			
			synchronized(port){ // if there is a really little time between two calls
				
				try{
					
					String received = port.readString();
					
					if(received.contains("\n")){
						
						int i;
						
						if((i = received.indexOf("\n")) != 0)
							lastSent = receiveCache + received.substring(0, i-1);
						else
							lastSent = receiveCache;
						
						receiveCache = received.substring(received.indexOf("\n"));
						
						synchronized(this){
							
							notifyAll();
							
						}
						
					}else{
						
						receiveCache += received;
						
					}
					
				}catch (SerialPortException e){
					
					readException = e;
					
				}
				
			}
			
		}else if(arg0.isCTS()){
			
			if(arg0.getEventValue() == 1){
				
				System.out.println("Port " + port.getPortName() + " is now ready !");
				
			}else{
				
				System.out.println("Port " + port.getPortName() + " is not longer available.");
				Bridge.fireDisconnected(this);
				
			}
			
		}
		
	}
	
	public void updateHID() throws BridgeException{
		
		System.out.println("getting HID...");
		System.out.println("sending request...");
		
		String hid = null;
		
		hid = readLine("HID", 5000);
		
		if(hid == null)
			throw new BridgeException("Timeout ended before hardware's response.");
		
		if(hid.indexOf('#') == -1)
			throw new BridgeException("The HID don't contains a \"#\"");
		
		System.out.println("Got a valid answer !");
		
		String creator = hid.substring(0, hid.indexOf('#'));
		String name = hid.substring(hid.indexOf('#') + 1);
		
		HID theHID = new HID();
		theHID.hid = hid;
		theHID.creator = creator;
		theHID.name = name;
		
		System.out.println("Successful.");
		
		this.hid = theHID;
		
	}
	
	public String getPortName(){
		
		return portName;
		
	}
	
	/**
	 * This method is used to get the handled <code>SerialPort</code>.
	 * @return The handled <code>SerialPort</code>.
	 * @see port
	 */
	public SerialPort getPort(){return port;}
	
}

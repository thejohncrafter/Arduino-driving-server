package com.ArduinoDrivingServer.bridge;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import com.ArduinoDrivingServer.bridge.HID.HID;
import com.ArduinoDrivingServer.bridge.HID.HIDGetter;
import com.ArduinoDrivingServer.bridge.HID.InvalidHIDException;

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
public class PortBridge extends AbstractBridge implements SerialPortEventListener {
	
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
	 * @param name The port's name 
	 * (because <code>port.getName()</code> don't work under Windows).
	 * @throws IOException If an IO exception occurates when getting port's <code>input</code> and <code>output stream</code> 
	 * (but should rarely happen).
	 * @throws InvalidHIDException If the <code>HID</code> of the hardware is invalid.
	 * @throws TimeoutException Id the timeout ends when getting hardware's <code>HID</code>.
	 * @throws SerialPortException If a <code>SerialPortException</code> happens (should never happen).
	 * @throws InterruptedException If the thread is interrupted when waiting for HID (should never happen).
	 * @see HID
	 * @see HIDGetter
	 */
	public PortBridge(SerialPort port, String portName) throws IOException, TimeoutException, InvalidHIDException, SerialPortException, InterruptedException{
		
		super(false);
		
		remaining = 0;
		receiveCache = "";
		this.port = port;
		
		port.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		
		port.addEventListener(this);
		
		this.portName = portName;
		
		if(System.getProperty("os.name").toUpperCase().contains("LINUX")){
			
			// Linux WTF fix
			//TODO : Better method for this fix...
			Thread.sleep(3000);
			
		}
		
		updateHID();
		
		System.out.println("hardware's HID : " + hid.hid);
		
	}
	
	@Override
	public void close() throws SerialPortException{
		
		port.closePort();
		
	}
	
	@Override
	public HID getHID(){
		
		return hid;
		
	}
	
	@Override
	public String readLine() throws InterruptedException, SerialPortException{
		
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
		
		return lastSent;
		
	}
	
	@Override
	public String readLine(String line) throws InterruptedException, SerialPortException{
		
		send(line);
		
		return readLine();
		
	}
	
	@Override
	public String readLine(String line, long timeout) throws InterruptedException, SerialPortException{
		
		send(line);
		
		return readLine(timeout);
		
	}
	
	@Override
	public String readLine(long timeout) throws InterruptedException, SerialPortException{
		
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
		
		return lastSent;
		
	}
	
	@Override
	public void send(String request) throws SerialPortException{
		
		port.writeString(request);
		
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
	
	public void updateHID() throws TimeoutException, InvalidHIDException, SerialPortException, InterruptedException{
		
		System.out.println("getting HID...");
		System.out.println("sending request...");
		
		String hid = null;
		
		hid = readLine("HID", 5000);
		
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

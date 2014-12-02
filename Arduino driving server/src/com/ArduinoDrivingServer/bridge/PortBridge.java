package com.ArduinoDrivingServer.bridge;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeoutException;

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
public class PortBridge implements SerialPortEventListener {
	
	/**
	 * This field stores the handled <code>SerialPort</code>.
	 */
	private SerialPort port;
	
	/**
	 * This field stores the port's name 
	 * (because <code>port.getName()</code> don't work under Windows).
	 */
	private String name;
	
	/**
	 * This field stores the <code>DataInputStream</code> used to read datas.
	 */
	private Scanner in;
	
	/**
	 * This field stores the <code>DataOutputStream</code> used to write datas.
	 */
	private PrintStream out;
	
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
	 * This constructor uses the given port to define the fields <code>in</code> and <code>out</code>.
	 * @param port The port to handle.
	 * @param name The port's name 
	 * (because <code>port.getName()</code> don't work under Windows).
	 * @throws IOException If an IO exception occurates when getting port's <code>input</code> and <code>output stream</code> 
	 * (but should rarely happen).
	 * @throws InvalidHIDException If the <code>HID</code> of the hardware is invalid.
	 * @throws TimeoutException Id the timeout ends when getting hardware's <code>HID</code>.
	 * @see HID
	 * @see HIDGetter
	 */
	public PortBridge(SerialPort port, String name) throws IOException, TimeoutException, InvalidHIDException{
		
		remaining = 0;
		in = new Scanner(port.getInputStream());
		out = new PrintStream(port.getOutputStream(), true);
		this.port = port;
		
		try {
			
			port.removeEventListener();
			port.addEventListener(this);
			port.notifyOnBreakInterrupt(true);
			port.notifyOnDataAvailable(true);
			port.notifyOnCTS(true);
			
		} catch (TooManyListenersException e) {
			
			e.printStackTrace(); // should never happen
			
		}
		
		this.name = name;
		
		if(System.getProperty("os.name").toUpperCase().contains("LINUX")){
			//TODO : better solution here
			
			/*
			 * Linux BUG :
			 * The hardware needs time between SerialPort initalization and sending data...
			 * Someone has an idea ?
			 */
			
			try {
				
				Thread.sleep(3000);
				
			} catch (InterruptedException e) {
				
				e.printStackTrace();
				
			}
			
		}
		
		updateHID();
		
		System.out.println("hardware's HID : " + hid.hid);
		
	}
	
	/**
	 * This method is used to close the <code>PortBridge</code>.
	 */
	public void close(){
		
		port.removeEventListener();
		port.close();
		
	}
	
	/**
	 * This method is used to get the HID (HardwareIDentifier) of the port.
	 * @return The HID of the port.
	 * @see HIDGetter
	 * @see HIDGetter#getHID()
	 * @see HID 
	 */
	public HID getHID(){
		
		return hid;
		
	}
	
	/**
	 * This method is used to read a line from the input stream.<br>
	 * It waits for data and returns the read line.
	 * @return The read line.
	 * @throws InterruptedException If the thread is interrupted when waiting for data.
	 */
	public String readLine() throws InterruptedException{
		
		remaining++;
		
		synchronized(this){
			
			wait();
			
		}
		
		remaining--;
		
		if(remaining == 0){
			
			String toReturn = lastSent;
			lastSent = null;
			return toReturn;
			
		}
		
		return lastSent;
		
	}
	
	/**
	 * This method is used to read a line from the input stream.<br>
	 * It sends the given line, waits for data and returns the read line.<br>
	 * Calling this method is the same as calling <code>send(line); realLine();</code>.
	 * @param lin The line to send.
	 * @return The read line.
	 * @throws InterruptedException If the thread is interrupted when waiting for data.
	 */
	public String readLine(String line) throws InterruptedException{
		
		send(line);
		
		return readLine();
		
	}
	
	/**
	 * This method is used to read a line from the input stream until a given timeout.<br>
	 * It sends the given line, waits for data and returns the read line.<br>
	 * Calling this method is the same as calling <code>send(line); realLine();</code>.
	 * @param lin The line to send.
	 * @param timeout The maximum time to wait in milliseconds.
	 * @return The read line.
	 * @throws InterruptedException If the thread is interrupted when waiting for data.
	 */
	public String readLine(String line, long timeout) throws InterruptedException{
		
		/**
		 * Not read error under Linux fixed by waiting 3 seconds.
		 */
		
		send(line);
		
		return readLine(timeout);
		
	}
	
	/**
	 * This method is used to read a line from the input stream until a given timeout.<br>
	 * It waits for data and returns the read line.
	 * @param timeout The maximum time to wait in milliseconds.
	 * @return The read line.
	 * @throws InterruptedException If the thread is interrupted when waiting for data.
	 */
	public String readLine(long timeout) throws InterruptedException{
		
		remaining++;
		
		synchronized(this){
			
			wait(timeout);
			
		}
		
		remaining--;
		
		if(remaining == 0){
			
			String toReturn = lastSent;
			lastSent = null;
			return toReturn;
			
		}
		
		return lastSent;
		
	}
	
	/**
	 * This method is used to send a request through the port.
	 * @param request The request to send.
	 */
	public void send(String request){
		
		out.println(request);
		out.flush();
		
	}
	
	@Override
	public void serialEvent(SerialPortEvent arg0) {
		
		Thread.currentThread().setName(port.getName() + " request handler");
		
		if(arg0.getEventType() == SerialPortEvent.BI){
			
			System.out.println("break interrupt on port " + port.getName());
			Bridge.fireDisconnected(this);
			
		}else if(arg0.getEventType() == SerialPortEvent.DATA_AVAILABLE){

			lastSent = in.nextLine();
			System.out.println("data available : " + lastSent + " !\nnotifying...");
			
			synchronized(this){
				
				notify();
				
			}
			
		}else if(arg0.getEventType() == SerialPortEvent.CTS){
			
			System.out.println("Port " + port.getName() + " is now ready !");
			
		}
		
	}
	
	/**
	 * This method is used to update the HID.
	 * @throws TimeoutException If the <code>HIDGetter</code>'s timeout ends.
	 * @throws InvalidHIDException If the <code>HID</code> is invalid.
	 * @throws IOException If an IO exception occurates.
	 */
	public void updateHID() throws TimeoutException, InvalidHIDException{
		
		try{
			
			hid = new HIDGetter(this).getHID();
			
		}catch(InvalidHIDException e){
			
			System.out.println("given HID is invalid.");
			System.out.println("retrying...");
			hid = new HIDGetter(this).getHID();
			
		}
		
	}
	
	/**
	 * This method is used to get the port's name 
	 * (because <code>port.getName()</code> don't work under Windows).
	 * @return
	 */
	public String getName(){
		
		return name;
		
	}
	
	/**
	 * This method is used to get the input stream of the <code>PortBridge</code>.
	 * @return The input stream of the <code>PortBridge</code>.
	 */
	public Scanner getIn(){return in;}
	
	/**
	 * This method is used to get the output stream of the <code>PortBridge</code>.
	 * @return The output stream of the <code>PortBridge</code>.
	 */
	public PrintStream getOut(){return out;}
	
	/**
	 * This method is used to get the handled <code>SerialPort</code>.
	 * @return The handled <code>SerialPort</code>.
	 * @see port
	 */
	public SerialPort getPort(){return port;}
	
}

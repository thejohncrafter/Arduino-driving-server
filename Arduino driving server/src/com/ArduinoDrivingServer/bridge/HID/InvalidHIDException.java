package com.ArduinoDrivingServer.bridge.HID;

/**
 * This exception is thrown by <code>HIDGetter</code> when the HID is invalid.
 * 
 * @author Julien Marquet
 *
 */
public class InvalidHIDException extends Exception {
	
	/**
	 * Used by Serializable.
	 */
	private static final long serialVersionUID = 8451156024355923056L;
	
	/**
	 * This constructor defines the message of the exception.
	 * @param message The message.
	 */
	public InvalidHIDException(String message){super(message);}
	
}

package com.ArduinoDrivingServer.bridge;

/**
 * This exception is thrown by Bridge objects.
 * 
 * @author Julien Marquet
 *
 */
public class BridgeException extends Exception {

	/**
	 * Used by Serializable.
	 */
	private static final long serialVersionUID = -6830455326755840636L;

	/**
	 * This constructor just creates a <code>BridgeException</code>.
	 */
	public BridgeException() {
		super();
	}

	/**
	 * This constructor creates a <code>BridgeException</code> with a message.
	 * 
	 * @param message
	 *            The message.
	 */
	public BridgeException(String message) {
		super(message);
	}

	/**
	 * This constructor creates a <code>BridgeException</code> with a cause.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public BridgeException(Throwable cause) {
		super(cause);
	}

	/**
	 * This constructor creates a <code>BridgeException</code> with a massage
	 * and a cause.
	 * 
	 * @param message
	 *            The message.
	 * @param cause
	 *            the cause.
	 */
	public BridgeException(String message, Throwable cause) {
		super(message, cause);
	}

}

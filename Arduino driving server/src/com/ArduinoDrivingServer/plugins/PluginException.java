package com.ArduinoDrivingServer.plugins;

/**
 * This exception is used to centralize all exceptions thrown from the plugin
 * system.
 * 
 * @author thejohncrafter
 *
 */
public class PluginException extends Exception {

	/**
	 * Used by Serializable.
	 */
	private static final long serialVersionUID = 4244314121855977062L;

	/**
	 * This constructor just creates a <code>PluginException</code>.
	 */
	public PluginException() {
		super();
	}

	/**
	 * This constructor just a <code>PluginException</code> with a message.
	 * 
	 * @param message
	 *            The message.
	 */
	public PluginException(String message) {
		super(message);
	}

	/**
	 * This constructor just a <code>PluginException</code> with a cause.
	 * 
	 * @param cause
	 *            the cause.
	 */
	public PluginException(Throwable cause) {
		super(cause);
	}

	/**
	 * This constructor just a <code>PluginException</code> with a massage and a
	 * cause.
	 * 
	 * @param message
	 *            The message.
	 * @param cause
	 *            the cause.
	 */
	public PluginException(String message, Throwable cause) {
		super(message, cause);
	}

}

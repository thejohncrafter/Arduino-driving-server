package com.ArduinoDrivingServer.server;

/**
 * This <code>interface</code> is used to create <code>listeners</code> for the server events.
 * 
 * @author Julien Marquet
 *
 */
public interface ServerEventListener {
	
	/**
	 * This method is called when the <code>server</code> is opened.
	 */
	public void fireServerOpened();
	
	/**
	 * This method is called when the <code>server</code> is closed.
	 */
	public void fireServerClosed();
	
}

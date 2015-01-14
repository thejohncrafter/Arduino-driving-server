package com.ArduinoDrivingServer.bridge;

import java.util.HashMap;

/**
 * This interface is used to create custom Bridges.
 * 
 * @author Julien Marquet
 *
 */
public abstract class AbstractBridge {
	
	/**
	 * This boolean is used to know if the bridge is activated.
	 * @see isActivated
	 * @see setActivated
	 */
	private boolean activated;
	
	/**
	 * This field stores the bridge's name.
	 * @see getName
	 * @see setName
	 */
	private String name;
	
	/**
	 * This field stores the bridge's description.
	 * @see getDesc
	 * @see setDesc
	 */
	private String desc;
	
	/**
	 * This method is used to setup the bridge.
	 * @return The created <code>AbstractBridgeInterface</code>s.
	 */
	public abstract HashMap<String, AbstractBridgeInterface> init();
	
	/**
	 * This method is used to update the bridge. It creates new 
	 * <code>AbstractBridgeInterface</code>s for each new connected hardware.
	 * @return The new <code>AbstractBridgeInterface</code>s.
	 */
	public abstract HashMap<String, AbstractBridgeInterface> update();
	
	/**
	 * This method is called by Bridge when closing. All the <code>AbstractBridgeInterface</code> are 
	 * closed before closing the bridge.
	 * @see AbstractBridgeInterface#close()
	 */
	public abstract void destroy();
	
	/**
	 * This method is used to get the Bridge's name.
	 * @return The Bridge's name.
	 */
	public final String getName(){return name;}
	
	/**
	 * This method is used to set the Bridge's name.
	 * @param name The Bridge's name.
	 */
	public final void setName(String name){this.name = name;}
	
	/**
	 * This method is used to get the Bridge's description.
	 * @return The Bridge's description.
	 */
	public final String getDesc() {return desc;}
	
	/**
	 * This method is used to get the Bridge's description.
	 * @return The Bridge's description.
	 */
	public final void setDesc(String desc) {this.desc = desc;}
	
	/**
	 * This method id used to know is the bridge is activated.
	 * @return True if the bridge is activated, otherwise false.
	 */
	public final boolean isActivated() {return activated;}
	
	/**
	 * This method id used to set the "activated" property of the bridge.
	 * <div style="color:red;">But it does not change bridge's state 
	 * (use <code>init()</code> and <code>destroy</code> for this) !</div>
	 * @param activated The new value of the "activated" property.
	 * @see init
	 * @see destroy
	 */
	public final void setActivated(boolean activated) {this.activated = activated;}
	
}

package com.ArduinoDrivingServer.bridge;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.ArduinoDrivingServer.web.servlets.ArduinoDriving;

/**
 * This singleton is the <code>bridge</code> between the software and the
 * hardware.
 * 
 * @author Julien Marquet
 *
 */
public class Bridge {

	/**
	 * Stores the instanse of the singleton.
	 */
	private static Bridge instance;

	/**
	 * This <code>HashMap</code> stores all the available
	 * <code>AbstractBridgeInteface</code>s.
	 * 
	 * @see HashMap
	 */
	private HashMap<String, AbstractBridgeInterface> ifaces = new HashMap<String, AbstractBridgeInterface>();

	/**
	 * This <code>HashMap</code> stores the available
	 * <code>AbstractBridge</code>s.
	 */
	private HashMap<String, AbstractBridge> bridges = new HashMap<String, AbstractBridge>();

	/**
	 * This field is used by the method <code>isClosed()</code>
	 */
	private boolean closed = true;

	/**
	 * Just calls {@link Bridge#init()}. Throws an Error if a
	 * {@link BridgeException} occurs.
	 */
	private Bridge() {

		try {

			init();

		} catch (BridgeException e) {

			throw new Error(e);

		}

	}

	/**
	 * This method is used to initialize the <code>Bridge</code>.
	 * 
	 * @throws IOException
	 *             If an error occurs when creating a <code>PortBridge</code>
	 *             (but should rarely happen).
	 * @see USBBridgeInterface
	 */
	public void init() throws BridgeException {

		closed = false;
		String[] bridgesKeys = bridges.keySet().toArray(
				new String[bridges.size()]);

		for (String key : bridgesKeys)
			bridges.get(key).init();

	}

	/**
	 * This method is used to destroy the bridge.
	 * 
	 * @throws BridgeException
	 *             If an exception occurs when closing a bridge.
	 */
	public void destroy() throws BridgeException {

		if (closed)
			throw new IllegalStateException("Bridge is already closed !");

		System.out.println("destroying bridge...");

		closed = true;

		String[] ifacesKeys = ifaces.keySet()
				.toArray(new String[ifaces.size()]);

		for (String key : ifacesKeys)
			ifaces.remove(key).close();

		String[] bridgesKeys = bridges.keySet().toArray(
				new String[bridges.size()]);

		for (String key : bridgesKeys)
			bridges.get(key).destroy();

		System.out.println("Successful !");

	}

	/**
	 * This method is used to get all the available <code>PortBridge</code>s.<br>
	 * This method automatically destroys the invalid bridges.
	 * 
	 * @throws BridgeException
	 *             If an exception occurs when closing an invalid bridge, or if
	 *             it can't be closed.
	 */
	public void updateBridges() throws BridgeException {

		System.out.println("Updating bridges...");

		for (int i = 0; i < bridges.size(); i++) {

			bridges.get(i).update();

		}

	}

	/**
	 * This method is used to initialize a given bridge. It automatically stores
	 * the created <code>AbstractBridgeInterface</code> in the
	 * <code>ifaces</code> field.
	 * 
	 * @param bridge
	 */
	public void initBridge(AbstractBridge bridge) {

		HashMap<String, AbstractBridgeInterface> ifaces = bridge.init();
		String[] keys = ifaces.keySet().toArray(new String[ifaces.size()]);

		for (String key : keys) {

			this.ifaces.put(key, ifaces.get(key));

		}

	}

	/**
	 * This method is used to close the given bridge.<br>
	 * If closes the bridge, then all the associated
	 * <code>AbstractBridgeinterface</code>s.
	 * 
	 * @param bridge
	 *            The bridge to close.
	 * @throws BridgeException
	 *             If an exception occurs when closing the bridge.
	 */
	public void destroyBridge(AbstractBridge bridge) throws BridgeException {

		bridge.destroy();

		String[] keys = ifaces.keySet().toArray(new String[ifaces.size()]);

		for (String key : keys) {

			AbstractBridgeInterface iface = ifaces.get(key);

			if (iface.getBridgeName().equals(bridge.getName())) {

				if (!ifaces.get(key).close())
					throw new BridgeException("Can't close iface "
							+ iface.getPortName() + " !");

			}

		}

	}

	/**
	 * This method is used to change the name of a given AbstractBridge to a
	 * given name. <div style="color:red;">This method does not edit bridge.xml
	 * !</div>
	 * 
	 * @param bridge
	 *            The bridge where changing name.
	 * @param id
	 *            The new name.
	 */
	public void changeBridgeName(AbstractBridge bridge, String id) {

		String[] keys = ifaces.keySet().toArray(new String[ifaces.size()]);

		for (String key : keys) {

			AbstractBridgeInterface iface = ifaces.get(key);

			if (iface.getBridgeName().equals(bridge.getName())) {

				ifaces.get(key).setBridgeName(id);

			}

		}

		bridge.setName(id);

	}

	/**
	 * This method is called by <code>PortBridge</code> when the hardware is
	 * disconnected.
	 * 
	 * @param pb
	 *            The <code>PortBridge</code>.
	 */
	public void fireDisconnected(AbstractBridgeInterface pb) {

		try {

			pb.close();

		} catch (Exception e) {

			System.out.println("Can't close port " + pb.getPortName());
			e.printStackTrace();

		} finally {

			ifaces.remove(pb.getPortName());

		}

	}

	/**
	 * This method is used to know if a driver exists for the given HID.
	 * 
	 * @param hid
	 *            The HID to test.
	 * @return True if the driver exists, otherwise false.
	 */
	public boolean hasDriver(String hid) {
		return new File(ArduinoDriving.getRealPath("drivers/"
				+ hid.replace(" ", "_") + "/driver.jsp")).exists();
	}

	/**
	 * This method is used to get a <code>PortBridge</code> by getting its name.
	 * 
	 * @param port
	 *            The name of the <code>PortBridge</code> to get.
	 * @return The <code>PortBridge</code>.
	 * @see bridges
	 */
	public AbstractBridgeInterface getPortBridge(String port) {
		return ifaces.get(port);
	}

	/**
	 * This method is used to add a <code>bridge</code> to the list of bridges.<br>
	 * It is exactly like calling
	 * <code>Bridge.getbridges().put(bridge.getName(), bridge);</code>.
	 * 
	 * @param bridge
	 *            The bridge to add.
	 */
	public void addPortBridge(AbstractBridgeInterface bridge) {
		ifaces.put(bridge.getPortName(), bridge);
	}

	/**
	 * This method is used to get the list of the available interfaces.
	 * 
	 * @return The list of the available interfaces.
	 */
	public HashMap<String, AbstractBridgeInterface> getIFaces() {
		return ifaces;
	}

	/**
	 * This method is used to get the list of available bridges.
	 * 
	 * @return The list of available bridges.
	 */
	public HashMap<String, AbstractBridge> getBridges() {
		return bridges;
	}

	/**
	 * This Method is used to know if the <code>Bridge</code> is opened.
	 * 
	 * @return True if it is opened, otherwise false.
	 * @see Bridge#closed
	 */
	public boolean isOpened() {
		return !closed;
	}

	/**
	 * Used to access the instance.
	 * 
	 * @return The instance if it has been created, else it creates then returns
	 *         it.
	 */
	public static Bridge getInstance() {

		return instance != null ? instance : (instance = new Bridge());

	}

}

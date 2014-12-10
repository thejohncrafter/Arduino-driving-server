package com.ArduinoDrivingServer.gui;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.ArduinoDrivingServer.bridge.AbstractPortBridge;
import com.ArduinoDrivingServer.bridge.Bridge;

/**
 * This class is used to create the <code>JTable</code> containing the datas of all the hardwares.
 * 
 * @author Julien Marquet
 * 
 */
public class HardwareTable extends JTable {
	
	/**
	 * Used by Serializable.
	 */
	private static final long serialVersionUID = 4632048381192708983L;
	
	/**
	 * This constructor creates the GUI of the table.
	 */
	public HardwareTable(){
		
		bridgeClosed();
		
	}
	
	/**
	 * This method is used to generate the <code>TableModel</code> of the table.
	 * @return The generated <code>DefaultTableModel</code>.
	 * @see DefaultTableModel
	 */
	private DefaultTableModel generateTableModel(){
		
		String[] headers;
		
		AbstractPortBridge[] bridges = Bridge.getbridges().values().toArray(new AbstractPortBridge[Bridge.getbridges().size()]);
		
		String[][] datas;
		
		if(bridges.length == 0){
			
			headers = new String[] {"No hardware found."};
			datas = new String[][] {{"No hardware found."}};
			
		}else{
			
			headers = new String[] {"Port", "HID", "Name", "Creator", "virtual"};
			
			String[] ports    = new String[bridges.length];
			String[] hids     = new String[bridges.length];
			String[] names    = new String[bridges.length];
			String[] creators = new String[bridges.length];
			String[] virtual  = new String[bridges.length];
			
			for(int i = 0; i < bridges.length; i++){
				
				ports[i]    = bridges[i].getPortName();
				hids[i]     = bridges[i].getHID().hid;
				names[i]    = bridges[i].getHID().name;
				creators[i] = bridges[i].getHID().creator;
				
				if(bridges[i].isVirtual())
					virtual[i] = "true";
				else
					virtual[i] = "false";
				
			}
			
			datas = new String[Bridge.getbridges().size()][4];
			
			for(int i = 0; i < datas.length; i++){
				
				datas[i][0] = ports[i];
				datas[i][1] = hids[i];
				datas[i][2] = names[i];
				datas[i][3] = creators[i];
				datas[i][4] = virtual[i];
				
			}
			
		}
		
		return new DefaultTableModel(datas, headers);
		
	}
	
	/**
	 * This method is used to update the table.<br>
	 * It updates the <code>Bridge</code> then the table.
	 * @see Bridge
	 */
	public void update(){
		
		setModel(generateTableModel());
		repaint();
		
	}
	
	/**
	 * This method is called when the Bridge is closed.
	 */
	public void bridgeClosed(){
		
		setModel(new DefaultTableModel(new String[][]{{"Bridge is closed."}}, new String[]{"Bridge is closed."}));
		
	}
	
}

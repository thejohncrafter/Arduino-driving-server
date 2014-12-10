package com.ArduinoDrivingServer.gui.VirtualPort;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ArduinoDrivingServer.bridge.Bridge;
import com.ArduinoDrivingServer.bridge.VirtualPort;
import com.ArduinoDrivingServer.bridge.HID.HID;

/**
 * This class is used to create a <code>JDialog</code> used to create a <code>VirtualPort</code>.
 * 
 * @author thejohncrafter
 * @see JDialog
 * @see VirtualPort
 * @øee VirtualPortView
 * 
 */
public class VPCreationDialog extends JDialog {

	/**
	 * Used by Serializable.
	 */
	private static final long serialVersionUID = -515417412557395386L;
	
	/**
	 * This <code>JTextField</code> is used to define the virtual port's port name.
	 * @see VirtualPort#getPortName()
	 */
	private JTextField portName;
	
	/**
	 * This <code>JTextArea</code> is used to show infos of the port name.
	 * @see portName
	 */
	private JTextArea portNameInfo;
	
	/**
	 * This <code>JTextField</code> is used to define the virtual port's <code>HID</code>.
	 * @see HID
	 * @see VirtualPort#getHID()
	 */
	private JTextField hid;
	
	/**
	 * This <code>JTextArea</code> is used to show infos of the port name.
	 * @see hid
	 */
	private JTextArea hidInfo;
	
	/**
	 * This <code>boolean</code> is used to know if the dialog has been canceled.
	 */
	private boolean canceled;
	
	public VPCreationDialog(){
		
		this.setModal(true);
		this.setSize(500, 300);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setTitle("Create new virtual port");
		
		this.setLayout(new BorderLayout());
		
		JPanel center = new JPanel();
		JPanel south = new JPanel();
		
		center.setLayout(new GridLayout(2, 1));
		south.setLayout(new FlowLayout());
		
		south.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		{ // center
			
			JPanel top = new JPanel();
			JPanel bot = new JPanel();
			
			top.setLayout(new GridLayout(2, 1));
			bot.setLayout(new GridLayout(2, 1));
			
			top.setBorder(BorderFactory.createTitledBorder("Port name"));
			bot.setBorder(BorderFactory.createTitledBorder("HID"));
			
			{ // top
				
				portName = new JTextField();
				portNameInfo = new JTextArea();
				
				portNameInfo.setEditable(false);
				portNameInfo.setText("Please choose a port name.\n(example : \"my port\")");
				
				top.add(portName);
				top.add(portNameInfo);
				
			}
			
			{ // right
				
				hid = new JTextField();
				hidInfo = new JTextArea();
				
				hidInfo.setEditable(false);
				hidInfo.setText("Please choose a HID (HardwareIDentifier) for your port.\n(Example : me#emulator)");
				
				bot.add(hid);
				bot.add(hidInfo);
				
			}
			
			center.add(top);
			center.add(bot);
		}
		
		{ // south
			
			JButton ok = new JButton("OK");
			JButton cancel = new JButton("cancel");
			
			ok.addActionListener(new ActionListener(){ // "OK" button
	
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					System.out.println("Verifying datas validity...");
					
					boolean valid = true;
					
					{ // portName
						
						if(Bridge.getbridges().containsKey(portName.getText()) ||
								Bridge.getbridges().containsKey(' ' +portName.getText())){
							
							portName.setBackground(new Color(255, 0, 0));
							portNameInfo.setText("Please choose another port name.\n"
									+ "(the one you entered already exixts)");
							
							valid = false;
							
						}else if(portName.getText().equals("")){
							
							portName.setBackground(new Color(255, 0, 0));
							portNameInfo.setText("Please choose another port name.\n"
												+ "(the one you enter is empty)");
							
							valid = false;
							
						}else{
							
							portName.setBackground(new Color(0, 255, 0));
							portNameInfo.setText("OK");
							
						}
						
					}
					
					{ // HID
						
						String hidText = ' ' + hid.getText();
						
						if(!hidText.contains("#")){
							
							hid.setBackground(new Color(255, 0, 0));
							hidInfo.setText("Please choose a HID (HardwareIDentifier) for your port.\n"
												+ "(the one you entered does not contain a \"#\")");
							
							valid = false;
							
						}else{
							
							hid.setBackground(new Color(0, 255, 0));
							hidInfo.setText("OK");
							
						}
						
					}
					
					portName.repaint();
					hid.repaint();
					
					if(valid){
						
						System.out.println("Datas valid. Creating virtual port...");
						canceled = false;
						setVisible(false);
						
					}
					
				}
				
			});
			
			cancel.addActionListener(new ActionListener(){ // "cancel" button

				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					System.out.println("User canceled.");
					canceled = true;
					setVisible(false);
					
				}
				
			});
			
			south.add(ok, FlowLayout.LEFT);
			south.add(cancel, FlowLayout.LEFT);
			
		}
		
		this.add(center, BorderLayout.CENTER);
		this.add(south, BorderLayout.SOUTH);
		
	}
	
	/**
	 * This method is used to show the dialog.<br>
	 * If the dialog is canceled, it returns <code>null</code>. 
	 * Otherwise, it returns the created <code>VirtualPort</code>.
	 * @return <code>null</code> if the dialog have been canceled, 
	 * otherwise, the created <code>VirtualPort</code>.
	 */
	public VirtualPort showDialog(){
		
		this.setVisible(true); // locks until the dialog is closed
		
		if(!canceled){
			
			String entered = hid.getText();
			
			String creator = entered.substring(0, entered.indexOf('#'));
			String name = entered.substring(entered.indexOf('#') + 1);
			
			HID theHID = new HID();
			theHID.hid =  entered;
			theHID.creator = creator;
			theHID.name = name;
			
			return new VirtualPort(portName.getText(), theHID);
			
		}
		
		return null;
		
	}
	
	/**
	 * This method is used to show a dialog.<br>
	 * If the dialog is canceled, it returns <code>null</code>. 
	 * Otherwise, it returns the created <code>VirtualPort</code>.
	 * @return <code>null</code> if the dialog have been canceled, 
	 * otherwise, the created <code>VirtualPort</code>.
	 */
	public static VirtualPort askForVirtualPort(){
		
		return new VPCreationDialog().showDialog();
		
	}
	
}

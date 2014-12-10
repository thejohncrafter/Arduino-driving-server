package com.ArduinoDrivingServer.gui.VirtualPort;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ArduinoDrivingServer.bridge.Bridge;
import com.ArduinoDrivingServer.bridge.VirtualPort;

/**
 * This class is used to make the user able to interact with a <code>VirtualPort</code>.
 * @author Julien Marquet
 *
 */
public class VirtualPortView extends JFrame {

	/**
	 * Used by Serializable.
	 */
	private static final long serialVersionUID = -2483069409357670658L;
	
	/**
	 * This field is used by internal classes to have a reference to the <code>VirtualPortView</code>.
	 */
	private VirtualPortView _this;
	
	/**
	 * This field stores the handled <code>VirtualPort</code>.
	 * @see VirtualPort
	 */
	private VirtualPort vp;
	
	/**
	 * This <code>JTextArea</code> is used to show the logs of the <code>VirtualPort</code>.
	 * @see VirtualPort
	 */
	private JTextArea logs;
	
	/**
	 * This <code>JTextField</code> is used to make the user able to 
	 * send data like a connected hardware.
	 */
	private JTextField textField;
	
	/**
	 * This <code>JButton</code> is used to make the user able to 
	 * send data like a connected hardware.
	 */
	private JButton send;
	
	/**
	 * This <code>Runnable</code> is ran when data is sent (and if it isn't null).
	 */
	private Runnable onData;
	
	/**
	 * This <code>String</code> is used to know what was answered the last time.
	 */
	private String answer;
	
	/**
	 * This constructor creates the GUI using the given <code>VirtualPort</code>.
	 * @param p The <code>VirtualPort</code> used to create the view.
	 */
	public VirtualPortView(VirtualPort p){
		
		_this = this;
		this.vp = p;
		
		p.setView(this);
		
		this.setMinimumSize(new Dimension(500, 300));
		this.setSize(700, 500);
		this.setTitle(p.getPortName() + " controller");
		this.setLayout(new BorderLayout());
		
		this.addWindowListener(new WindowAdapter(){
			
			@Override
			public void windowClosing(WindowEvent e){
				
				Bridge.fireDisconnected(vp);
				
			}
			
		});
		
		logs = new JTextArea();
		logs.setEditable(false);
		
		JScrollPane scroll = new JScrollPane(logs);
		
		logs.setAutoscrolls(true);
		scroll.setAutoscrolls(true);
		
		add(scroll, BorderLayout.CENTER);
		
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout());
		south.setBorder(BorderFactory.createTitledBorder("Send"));
		
		{ // the JTextField
			
			textField = new JTextField();
			send = new JButton("send");
			
			send.addActionListener(new ActionListener(){
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					if(onData != null)
						onData.run();
					else
						logs.append("[sent] " + textField.getText());
					
					textField.setText("");
					
				}
				
			});
			
			south.add(textField, BorderLayout.CENTER);
			south.add(send, BorderLayout.WEST);
			
		}
		
		add(south, BorderLayout.SOUTH);
		
	}
	
	/**
	 * This method is called when data is needed (after a call of 
	 * <code>readLine()</code> on the handled <code>VirtualPort</code>).
	 * @param request The sent request.
	 * @return The answer.
	 * @see VirtualPort#readLine()
	 * @see VirtualPort#readLine(long)
	 * @see VirtualPort#readLine(String)
	 * @see VirtualPort#readLine(String, long)
	 */
	public String dataNeeded(String request){
		
		onData = new Runnable(){
			
			public void run(){
				
				logs.append("[answer] " + textField.getText());
				onData = null;
				answer = textField.getText();
				
				synchronized(_this){
					
					_this.notify();
					
				}
				
			}
			
		};
		
		logs.append("[data needed] " + request + "\n");
		
		try {
			
			synchronized(_this){
				
				wait();
				
			}
			
		} catch (InterruptedException e) { // should never happen
			
			e.printStackTrace();
			
		}
		
		return answer;
		
	}

	/**
	 * This method is called when data is sent (after a call of 
	 * <code>send()</code> on the handled <code>VirtualPort</code>).
	 * @param request The sent request.
	 * @return The answer.
	 * @see VirtualPort#send(String)
	 */
	public void dataSent(String request){

		System.out.println("[virtual port] Sending : " + request);
		logs.append("[data sent] " + request + "\n");
		
	}
	
}

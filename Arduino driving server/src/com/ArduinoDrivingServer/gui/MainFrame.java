package com.ArduinoDrivingServer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultCaret;

import com.ArduinoDrivingServer.bridge.Bridge;
import com.ArduinoDrivingServer.server.HTTPServer;
import com.ArduinoDrivingServer.server.ServerEventListener;

/**
 * This JFrame contains the user interface.
 * 
 * @author Julien Marquet
 *
 */
public class MainFrame extends JFrame implements ServerEventListener {

	/**
	 * sed by Serializable.
	 */
	private static final long serialVersionUID = 2088476259446118872L;
	
	/**
	 * This field stores an instances of the frame.
	 */
	private static MainFrame instance;
	
	/**
	 * This <code>JLabel</code> is used to show the server's state.
	 */
	private JLabel serverState;
	
	/**
	 * This <code>JButton</code> is used to open and close the HTTPServer.
	 */
	private JButton open;
	
	/**
	 * This <code>JButton</code> is used to init/destroy the Bridge.
	 */
	private JButton init;
	
	/**
	 * This field stores the table of the hardwares.
	 */
	private HardwareTable table;
	
	/**
	 * This <code>JButton</code> is used to update the <code>HardwareTable</code>.
	 * @see table
	 * @see HardwareTable
	 */
	private JButton update;
	
	/**
	 * This <code>JTextarea</code> is used to show the programm's output.
	 */
	private JTextArea area;
	
	/**
	 * This <code>JButton</code> is used to clear the <code>JTextArea</code> 
	 * showing the programm's outout (area).
	 * @see area
	 */
	private JButton clear;
	
	/**
	 * This interface is used to create <code>ActionListenrs</code>s that implements 
	 * <code>Runnable</code> (used to call <code>invokeThreaded(Runnable);</code>.
	 * 
	 * @author Julien Marquet
	 * @see ActionListener
	 * @ee Runnable
	 * 
	 */
	private interface RunnableListener extends ActionListener, Runnable{};
	
	/**
	 * This constructor creates the GUI of the frame.
	 */
	public MainFrame(){
		
		try {
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			
			System.out.println("Error when updating UI :"); // should never happen
			e.printStackTrace();
			
		}
		
		this.setSize(700, 400); 
		this.setMinimumSize(new Dimension(400, 300));
		this.setLayout(new GridLayout(0, 2));
		this.setTitle("arduino driving server 1.0");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		HTTPServer.addServerEventListener(this);
		
		{
			
			JPanel left = new JPanel();
			left.setLayout(new BorderLayout());
			
			{ // the server informations
				
				JPanel jp = new JPanel();
				
				jp.setBorder(BorderFactory.createTitledBorder("Server"));
				jp.setLayout(new BorderLayout());
				
				{ // the open/close button
					
					JPanel buttons = new JPanel();
					buttons.setLayout(new BorderLayout());
					
					open = new JButton("Open");
					
					open.setEnabled(false);
					open.setToolTipText("Please initialize Bridge before opening HTTPServer.");
					
					open.addActionListener(new RunnableListener(){

						@Override
						public void actionPerformed(ActionEvent arg0) {
							
							invokeThreaded(this, "Server opener/closer");
							
						}

						@Override
						public void run() {
							
							open.setEnabled(false);
							
							if(HTTPServer.isClosed()){
								
								if(!HTTPServer.open()){
									
									JOptionPane.showMessageDialog(instance, "Can't open server (see error in logs) !", "Error", JOptionPane.ERROR_MESSAGE);
									HTTPServer.close();
									
								}
								
							}else{
								
								closeServer();
								
							}
							
							open.setEnabled(true);
							
						}
						
					});
					
					buttons.add(open);
					jp.add(buttons, BorderLayout.NORTH);
					
				}
				
				{ // the server state
					
					serverState = new JLabel("Closed");
					
					serverState.setBorder(BorderFactory.createTitledBorder("Server state"));
					
					jp.add(serverState);
					
				}
				
				left.add(jp, BorderLayout.NORTH);
				
			}
			
			{ // the Bridge informations
				
				JPanel jp = new JPanel();
				
				jp.setBorder(BorderFactory.createTitledBorder("Bridge"));
				jp.setLayout(new BorderLayout());
				
				{ // The init/destroy button
					
					init = new JButton("Initialize");
					init.setToolTipText("Initializes the bridge.");
					
					init.addActionListener(new RunnableListener(){
						
						@Override
						public void actionPerformed(ActionEvent e) {
							
							invokeThreaded(this, "Bridge initializer/destroyer");
							
						}

						@Override
						public void run() {
							
							if(Bridge.isClosed()){
								
								PleaseWaitDialog pwd = new PleaseWaitDialog(instance, true, "Opening bridge...");
								pwd.setLocation(getX() + (getWidth() /2) - (pwd.getWidth()/2), getY() + (getHeight() / 2) - (pwd.getHeight()/2));
								
								pwd.setVisible(true);
								
								Bridge.init();
								init.setText("Destroy");
								init.setToolTipText("Destroys the Bridge.");
								open.setEnabled(true);
								open.setToolTipText("Opens the HTTPServer.");
								update.setEnabled(true);
								update.setToolTipText("Updates the list of hardwares.");
								table.update();
								
								pwd.setVisible(false);
								
							}else{
								
								open.setEnabled(false);
								open.setToolTipText("Please initialize Bridge before opening HTTPServer.");
								Bridge.destroy();
								init.setText("Initialize");
								init.setToolTipText("Initializes the Bridge.");
								update.setEnabled(false);
								update.setToolTipText("Please initialize Bridge.");
								
							}
							
						}
						
					});
					
					jp.add(init, BorderLayout.NORTH);
					
				}
				
				{ // The table and the update button
					
					table = new HardwareTable();
					
					update = new JButton("update table");
					update.setToolTipText("Please initialize Bridge.");
					update.setEnabled(false);
					
					update.addActionListener(new RunnableListener(){

						@Override
						public void actionPerformed(ActionEvent arg0) {
							
							invokeThreaded(this, "table updater");
							
						}

						@Override
						public void run() {
							
							table.update();
							
						}
						
					});
					
					jp.add(update, BorderLayout.SOUTH);
					
				}

				jp.add(new JScrollPane(table));
				left.add(jp);
				
			}
			
			add(left);
			
		}
		
		{ // the logging JTextArea
			
			JPanel right = new JPanel();
			right.setLayout(new BorderLayout());
			right.setBorder(BorderFactory.createTitledBorder("Logs"));
			
			area = new JTextArea();
			clear = new JButton("clear");
			JScrollPane sp = new JScrollPane(area);
			
			area.setEditable(false);
			
			area.setAutoscrolls(true);
			sp.setAutoscrolls(true);
			DefaultCaret caret = (DefaultCaret)area.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
			
			clear.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					area.setText("|| Area cleared !\n");
					
				}
				
			});
			
			area.append("|| arduino constrollers\n");
			area.append("|| by Julien Marquet\n");
			area.append("|| created on 20 october 2014\n");
			
			PrintStream stream = new PrintStream(new OutputStream(){
				
				private PrintStream out = System.out;
				
				private boolean wasNewLine = true;
				
				@Override
				public void write(int b) throws IOException {
					
					if(wasNewLine){
						
						String datas = '[' + Thread.currentThread().getName() + ']';
						out.print(datas);
						area.append(datas);
						
						wasNewLine = false;
						
					}
					
					out.write(b);
					area.append(String.valueOf((char) b));
					
					if(b == '\n')
						wasNewLine = true;
					
				}
				
			});
			
			System.setOut(stream);
			System.setErr(stream);
			
			right.add(sp);
			right.add(clear, BorderLayout.SOUTH);
			
			add(right);
			
		}
		
		{ // the dialog opened when closing
			
			addWindowListener(new WindowAdapter(){
				
				@Override
				public void windowClosing(WindowEvent e) {
					
					if(!HTTPServer.isClosed())
						closeServer();
					
					if(!Bridge.isClosed())
						Bridge.destroy();
					
				}
				
			});
			
		}
		
	}
	
	/**
	 * This method is used to close the server : it shows a <code>PleaseWaitDialog</code>, 
	 * closes the server and closes the <code>PleaseWaitDialog</code>.
	 */
	private void closeServer(){
		
		PleaseWaitDialog pwd = new PleaseWaitDialog(instance, true, "Closing server...");
		pwd.setLocation(getX() + (getWidth() /2) - (pwd.getWidth()/2), getY() + (getHeight() / 2) - (pwd.getHeight()/2));
		
		pwd.setVisible(true);
		
		HTTPServer.close();
		
		pwd.setVisible(false);
		
	}
	
	/**
	 * This method is used to call a given a given <code>Runnable</code> 
	 * in a <code>Thread</code> with a given name.
	 * @param r The runnable to call.
	 * @param name The name of the <code>Thread</code> that calls the <code>Runnable</code>.
	 * @see Runnable
	 * @ee Thread
	 */
	private void invokeThreaded(Runnable r, String name){
		
		new Thread(r, name).start();
		
	}
	
	/**
	 * This method initializes the frame.
	 */
	public static void init(){
		
		instance = new MainFrame();
		instance.setVisible(true);
		
	}
	
	@Override
	public void fireServerOpened() {
		
		System.out.println("Notifying GUI...");
		serverState.setText("Opened");
		open.setText("Close");
		open.setToolTipText("Closes the HTTPServer.");
		init.setEnabled(false);
		init.setToolTipText("Please close the server before destroying Bridge.");
		
	}
	
	@Override
	public void fireServerClosed() {

		System.out.println("Notifying GUI...");
		serverState.setText("Closed");
		open.setText("Open");
		open.setToolTipText("Opens the HTTPServer.");
		init.setEnabled(true);
		init.setToolTipText("Destroys the bridge.");
		
	}
	
	/**
	 * This method is uesd to get the instance of <code>MainFrame</code>.
	 * @return The instance (stored in <code>instance</code>).
	 * @see instance
	 */
	public static MainFrame getInstance(){return instance;}
	
}

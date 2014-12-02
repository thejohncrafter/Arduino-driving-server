package com.ArduinoDrivingServer.gui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * This <code>JDialog</code> creates a simple window that contains a 
 * <code>JLabel</code> displaying the current task and a 
 * <code>JProgressBar</code> with indeterminate time. That's all !
 * 
 * @author Julien Marquet
 *
 */
public class PleaseWaitDialog extends JDialog implements Runnable {

	/**
	 * Used by Serializable
	 */
	private static final long serialVersionUID = 2315994666425164248L;
	
	/**
	 * Creating GUI.
	 * @param owner the Frame from which the dialog is displayed
	 * @param modal specifies whether dialog blocks user input to other top-level windows when shown. 
	 * If true, the modality type property is set to DEFAULT_MODALITY_TYPE otherwise the dialog is modeless
	 * @param task The current task (displayed in the label).
	 */
	public PleaseWaitDialog(JFrame owner, boolean modal, String task){
		
		super(owner, "Please wait...", modal);
		
		this.setSize(300, 100);
		this.setResizable(false);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		if(owner != null)
			setLocation(owner.getLocation());
		
		JProgressBar bar = new JProgressBar();
		JLabel label = new JLabel(task);
		
		bar.setIndeterminate(true);
		
		add(label, BorderLayout.CENTER);
		add(bar, BorderLayout.SOUTH);
		
	}
	
	public void setVisible(final boolean b){
		
		if(b)
			new Thread(this).start();
		else
			super.setVisible(false);
		
	}

	@Override
	public void run() {
		
		super.setVisible(true);
		
	}
	
}

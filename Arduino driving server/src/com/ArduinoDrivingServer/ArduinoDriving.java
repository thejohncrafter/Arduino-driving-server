package com.ArduinoDrivingServer;

import com.ArduinoDrivingServer.gui.MainFrame;


/**
 * This class contains the main method.
 * 
 * @author Julien Marquet
 *
 */
public class ArduinoDriving {
	
	/**
	 * This is the main method.<br>
	 * We load the main part of the program.
	 * @param args The launch arguments. Actually unused.
	 */
	public static void main(String[] args){
		
		System.out.println("========================");
		System.out.println("|arduino driving server|");
		System.out.println("|by Julien Marquet     |");
		System.out.println("|created on            |");
		System.out.println("|20 october 2014       |");
		System.out.println("========================");
		
		System.setProperty("user.dir", ArduinoDriving.class.getClassLoader().getResource(".").getPath().replace("%20", " "));
		
		MainFrame.init();
		
	}
	
}

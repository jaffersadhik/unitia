
package com.winnovature.unitia.util.misc;
/**
 * 	@(#)AdminSocketServer.java	1.0
 *
 * 	Copyright 2000-2008 Air2web India Pvt Ltd. All Rights Reserved.
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * 	This class is Admin Health check for instances. 
 *
 * 	@author 	M Ravikumar (ravikumar@air2web.co.in)
 *	@version 	AdminSocketServer.java v1.0<br>
 *				Created: 26 Jun 2008 10:10<br>
 *				Last Modified 26 Jun 2008 10:10 by M Ravikumar
 */
public class AdminSocketServer extends Thread {

	// Variable holds the class name for Logger.
	private static final String className = "[AdminSocketServer] ";
	
	// Holds port
	private int port = 0;

	/**
	 * Default constructor
	 */
	public AdminSocketServer(){}
	
	/**
	 * Single parameter constructor 
	 */
	public AdminSocketServer(int port_){
		this.port = port_;
	}
	
	
	/**
	 * Method will start listening the specified port. if any client 
	 * hits this port with value HEALTH or KILL, this method will respond
	 * as OK or Stop respectively. Otherwise ERROR will be responded. :) 
	 */
	public void run() {
		//Logger.debug(className + "run() port ["  + port + "]");
		
		ServerSocket ssock = null;
		
		try {
			/*   
			 * This ServerSocket connection will avoid the duplicate instance running in
			 * the same machine. Basically this port will listen to the port. And if you
			 * try to run the same instance, it will again try to open the same port, then
			 * exception will thro' to say the instance is already started. Also you can   
			 * use this port number to ping aften to check the health of this instance.
			 */
			try {
				ssock = new ServerSocket(port);
				
//			} catch (java.net.BindException e) { // 
			} catch (Exception e) { //
				//Logger.warn(className + "Exception. Already an instance is running !!! ", e);
				// exit when instance is already running.
				System.exit(0);
			}
			
			try {
				String message;
				String messageout = "";
				while (true) {
					Socket connsock = ssock.accept();
					InputStreamReader instr = new InputStreamReader(connsock.getInputStream());
					DataOutputStream outstr = new DataOutputStream(connsock.getOutputStream());
					BufferedReader innet = new BufferedReader(instr);
					message = innet.readLine();

					if (message.equalsIgnoreCase("HEALTH")) 
						messageout = "OK" + "\n";
					
					else if(message.equalsIgnoreCase("KILL")){
						messageout = "OK" + "\n";
						outstr.writeBytes(messageout);
						connsock.close();
						//Logger.warn(className + "Process is Killed !!! ");
						System.exit(0);
						
					}else{
						messageout = "ERROR" + "\n";
					}
					
					outstr.writeBytes(messageout);
					connsock.close();
				}
			} catch (Exception e) {
				//Logger.error(className + "Exception while processing HEALTHCHECK ");
			}
			 
		}catch(Exception e){
			//Logger.info(className + "Exception. Already an instance is running !!! ", e);
		}
	}
}

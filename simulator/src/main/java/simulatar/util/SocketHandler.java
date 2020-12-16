
package simulatar.util;

//java imports
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import simulatar.manager.SessionManager;
import simulatar.manager.util.SessionRoundRobin;

public class SocketHandler extends Thread 	{
	
	Socket socket = null;
	
	BufferedReader br = null;
	PrintWriter out = null;
	final String className = "[SocketHandler] ";
	private static Log logger = LogFactory.getLog(SocketHandler.class);

	public SocketHandler( Socket _socket)	{
		this.socket = _socket;
	
		this.start();
	}

	public void run()	{
		String methodName = "[run] ";
		//final int profileKey = 19191919;
		try	{
			br = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
			out = new PrintWriter( socket.getOutputStream(), true );
	        out.println("Welcome to Air2Web SMPP Interface Control Center");
		}
		catch(Exception e )	{
			//TrapSender.sendTrap(ComponentType.VSMSC, null, SNMPConstants.SOCKET_CONNECTION_ERROR, "Failed SMPP Interface Control Center", e);
			logger.error( className+methodName+"Error while getting stream. Error: ", e );
			return;
		}
		int optionSelected = -1;
		String option = "1";
		try	{
			while ( true ) {
	            out.println("- 1 List Clients");
	            out.println("- 3 Exit VSMSC Control Center");
	            out.println(">>>");
	            out.flush();
	            optionSelected = -1;
	            if ( socket == null )	return ;
	            try {
	                option = br.readLine();
	                if( option == null )	{
		                socket.close();
		                return;
	                }
	                logger.info( className + methodName + "Selected Option: " + option );
	                optionSelected = Integer.parseInt(option);
	            }
	            catch(java.net.SocketTimeoutException ste)	{
	            	//TrapSender.sendTrap(ComponentType.VSMSC, null, SNMPConstants.SOCKET_CONNECTION_ERROR, "Socket Time Out", ste);
		            logger.error(className+methodName+"Socket Timed Out. Error: " , ste );
		            exit();
		            return;
	            }
	            catch(java.net.SocketException se)	{
	            	//TrapSender.sendTrap(ComponentType.VSMSC, null, SNMPConstants.SOCKET_CONNECTION_ERROR, "Socket Error", se);
		            logger.error(className+methodName+"Socket Exception. Error: " , se);
		            exit();
		            return;
	            }
	            catch (Exception e) {
	            	//TrapSender.sendTrap(ComponentType.VSMSC, null, SNMPConstants.SOCKET_CONNECTION_ERROR, "Reading SMPP Ineterface Control options", e);
	                logger.error(className+methodName+"Exception while reading the option. Error: " , e);
	                optionSelected = -1;
	            }
	            switch ( optionSelected ) {
		            case 1:
		                listClients( br, out );
		                break;
		            case 3:
		                exit();
		                break;
		            case -1:
		                out.println("Invalid option. Choose 1 or 3.");
		                break;
		            default:
		                out.println("Invalid option. Choose 1 or 3.");
		                break;
				}
	        }
		}
		catch(Exception e)	{
		}
	}

    private void exit()	{
	    try	{
	    	out.println("Exiting....");
		    if(br != null)	br.close();
		    if(out != null)	out.close();
			if(socket != null)	socket.close();
			socket = null;
	    }
	    catch(Exception ee)	{
		    try	{
		    	if(socket != null)	socket.close();
	    	}
	    	catch(Exception e){}
	    }
    }
    private void listClients(BufferedReader br,PrintWriter out) {
    	ConcurrentHashMap<String,SessionRoundRobin> map=SessionManager.getInstance().getTxSessionsMap();
    	Set<String> keySet=map.keySet();  
    	out.println("Listing client ****");
    	for(String user:keySet) {
    		out.println(user + " TX active " + map.get(user).getHandlersCount());
    	}
    	ConcurrentHashMap<String,SessionRoundRobin> map2=SessionManager.getInstance().getRxTrxSessionsMap();
    	Set<String> keySet2=map2.keySet();   
    	for(String user:keySet2) {
    		out.println(user + " rx/trx active " + map2.get(user).getHandlersCount());
    	}
    	out.println("list completed ****");
    	
    }

}
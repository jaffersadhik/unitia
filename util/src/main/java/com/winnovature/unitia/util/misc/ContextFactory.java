/**
 * 	@(#)ContextFactory.java	1.0
 *
 * 	Copyright 2000-2008 Air2web India Pvt Ltd. All Rights Reserved.
 */

package com.winnovature.unitia.util.misc;

//Java Imports
import java.util.HashMap;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.winnovature.unitia.util.constants.Connectors;

/**
 * 	This class is used to establishes the initialContext with
 *  the Application Server.
 * 	This is a singleton class. So the class can be accessed without creating
 * 	objects.
 *
 * 	@author 	M Ravikumar (ravikumar@air2web.co.in)
 *	@version 	ContextFactory.java v1.0<br>
 *				Created: 26 Jun 2008 23:10<br>
 *				Last Modified 26 Jun 2008 23:10 by M Ravikumar
 */

public class ContextFactory {

	/**
	 *	ContextFactory instance.
	 */
	private static ContextFactory instance = new ContextFactory();

	/**
	 *	class name for logging purpose.
	 */
	private static final String className = "[ContextFactory] ";

	/**
	 * A2wi Logger instance
	 */
	Log log = LogFactory.getLog(this.getClass());
	
	/**
	 *	Private constructor.
	 */
	private ContextFactory() {
	}

	/**
	 *	This method returns the instance of this class.
	 *	@return ContextFactory instance of ContextFactory class
	 */
	public static ContextFactory getInstance() {
		return instance;
	}

	/**
	 *	This method is used to establishes a intialcontext with application server.
	 *	@return Context initial context object.
	 */
	public Context getInitialContext() throws javax.naming.NamingException {
		return new InitialContext();
	}

	/**
	 *	This method is used to establishes a intialcontext with application server.
	 *	@param String context factory class
	 *	@param String context provider url
	 *	@param String context provider user name
	 *	@param String context provider password
	 *	@return Context initial context object.
	 */
	public Context getInitialContext(String contextFactory, String providerURL,
							String userName, String password) throws Exception {

		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
			env.put(Context.PROVIDER_URL, providerURL);
			env.put(Context.SECURITY_PRINCIPAL, userName);
			env.put(Context.SECURITY_CREDENTIALS, password);

			return new InitialContext(env);
		} catch(Exception e) {
			//Logger.error(className + "getInitialContext(String,String,String,String); Not able to establish initial context; ", e);
			throw e;
		}
	}
	
	public Context getInitialContext(HashMap map) throws Exception {

		try {
			//logger.debug(className + "Routing Map   - " + map);
			
			//logger.debug(className + "PROVIDERCLASS - " + (String)map.get(Connectors.JMS_CONNECTOR_PROVIDER_CLASS));
			//logger.debug(className + "URL 			- " + (String)map.get(Connectors.JMS_CONNECTOR_PROVIDER_URL));
			//logger.debug(className + "USR 			- " + (String)map.get(Connectors.JMS_CONNECTOR_USR));
			//logger.debug(className + "PWD			- " + (String)map.get(Connectors.JMS_CONNECTOR_PWD));
			
			Hashtable env = new Hashtable();			
			env.put(Context.INITIAL_CONTEXT_FACTORY, (String)map.get(Connectors.JMS_CONNECTOR_PROVIDER_CLASS));
			env.put(Context.PROVIDER_URL, (String)map.get(Connectors.JMS_CONNECTOR_PROVIDER_URL));
			env.put(Context.SECURITY_PRINCIPAL, (String)map.get(Connectors.JMS_CONNECTOR_USR));
			env.put(Context.SECURITY_CREDENTIALS, (String)map.get(Connectors.JMS_CONNECTOR_PWD));
		
			return new InitialContext(env);
		} catch(Exception e) {
		//Logger.error(className + "getInitialContext(String,String,String,String); Not able to establish initial context; ", e);
			//logger.error(className + "Exception - ",e);
		throw e;
		}
	}
	
	public Context getInitialContext(String providerURL) throws Exception
	{
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,"org.jnp.interfaces.NamingContextFactory");
	    env.put(Context.PROVIDER_URL, providerURL);
	    
	    return new InitialContext(env);
	}
	
}
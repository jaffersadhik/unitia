/**
 * 	@(#)Connectors.java	1.0
 *
 * 	Copyright 2000-2008 Air2web India Pvt Ltd. All Rights Reserved.
 */

package com.winnovature.unitia.util.constants;

/**
 * 	This interface is declaration of Connectors constants 
 * 	
 *
 * 	@author 	N Ribai (ribai@air2web.co.in)
 *	@version 	FeatureCode.java v1.0<br>
 *				Created: 19 Jul 2008 11:15<br>
 *				Last Modified 19 Jul 2008 11:15 by N Ribai
 */
public interface Connectors {
	
	/**
	 * connector id
	 */
	public final String CONNECTOR_ID = "CONNECTORID";
	
	/**
	 * JMS connector type id
	 */
	public final int JMS_CONECTOR_TYPE_ID	=	1;
	/**
	 * DB connector type id
	 */
	public final int DB_CONECTOR_TYPE_ID	=	2;
	/**
	 * HTTP connector type id
	 */
	public final int HTTP_CONECTOR_TYPE_ID	=	3;
	
	/**
	 * JMS Connector's provider class
	 */
	public final String JMS_CONNECTOR_PROVIDER_CLASS = "PROVIDERCLASS";
	
	/**
	 * JMS Connector's provider url
	 */
	public final String JMS_CONNECTOR_PROVIDER_URL = "URL";
	
	/**
	 * JMS Connector's user name
	 */
	public final String JMS_CONNECTOR_USR = "USR";
	
	/**
	 * JMS connector's password
	 */
	public final String JMS_CONNECTOR_PWD = "PWD";
	
	/**
	 * JMS Connector's jndi name
	 */
	public final String JMS_CONNECTOR_JNDI = "JNDINAME";
	
	/**
	 * HTTP Connector's IP
	 */
	public final String HTTP_CONNECTOR_IP = "IP";
	
	/**
	 * HTTP connector's Port
	 */
	public final String HTTP_CONNECTOR_PORT = "PORT";
	
	/**
	 * HTTP Connector's URL
	 */
	public final String HTTP_CONNECTOR_URL = "URL";
	
	/**
	 * HTTP connectos' url response
	 */
	public final String HTTP_CONNECTOR_RESPONSE = "RESPONSE";
	
	/**
	 * DB Connector's IP
	 */
	public final String DB_CONNECTOR_IP	= "IP";
	
	/**
	 * DB Connector's Port
	 */
	public final String DB_CONNECTOR_PORT = "PORT";
	
	/**
	 * DB Connector's type
	 */
	public final String DB_CONNECTOR_TYPE = "TYPE";
	
	/**
	 * DB Connector's username
	 */
	public final String DB_CONNECTOR_USR = "USR";
	
	/**
	 * DB Connector's password
	 */
	public final String DB_CONNECTOR_PWD = "PWD";
	
	/**
	 * DB Connector's schema
	 */
	public final String DB_CONNECTOR_SCHEMA = "SCHEMA";
	
	/**
	 * DB Connector's service name
	 */
	public final String DB_CONNECTOR_SERVICE_NAME = "SERVICENAME";
	
	/**
	 * DB Connector's driver type
	 */
	public final String DB_CONNECTOR_DRIVER_TYPE = "DRIVERTYPE";
	
	/**
	 * DB Connector's connect string
	 */
	public final String DB_CONNECTOR_CONNECT_STRING = "CONNECTSTRING";
	
	/**
	 * DB Connector's data source
	 */
	public final String DB_CONNECTOR_DATA_SOURCE = "DATASOURCE";
	
	/**
	 * DB Connector's driver class
	 */
	public final String DB_CONNECTOR_DRIVER_CLASS = "DRIVERCLASS";
	
	/**
	 * Maximum Database Connection pool
	 */
	public final String DB_MAX_POOL = "MAXPOOL";
	
	/**
	 * Maximum Database Connection
	 */
	public final String DB_MAX_CONN = "MAXCONN";

}
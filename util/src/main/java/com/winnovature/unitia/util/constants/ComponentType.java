/**
 * 	@(#)MessageStatus.java	1.0
 *
 * 	Copyright 2000-2008 Air2web India Pvt Ltd. All Rights Reserved.
 */
package com.winnovature.unitia.util.constants;

/**
 * 	This interface declaration of message status. 	
 *
 * 	@author 	Ribai N (ribai@air2web.co.in)
 *	@version 	ComponentType.java v1.0<br>
 *				Created: 03 Jul 2008 15:45<br>
 *				Last Modified 03 Jul 2008 15:45 by Ribai N
 */
public interface ComponentType {
		
	/**
	 * Msg Poller
	 */
	public final String MSGPOLLER = "MSGPOLLER";
	
	/**
	 * Email Poller
	 */
	public final String EMAILPOLLER = "EMAILPOLLER";
	
	/**
	 * Trans MBL Poller
	 */
	public final String TRANSMBLPOLLER = "TRANSMBLPOLLER";
	
	/**
	 * Scheduler Poller
	 */
	public final String SCHEDULEPOLLER = "SCHEDULEPOLLER";
	
	/**
	 * Blockout Poller
	 */
	public final String BLOCKOUTPOLLER = "BLOCKOUTPOLLER";
	
	/**
	 * MSG Retry Poller
	 */
	public final String MSGRETRYPOLLER = "MSGRETRYPOLLER";
	
	/**
	 * BLOCKOUT Retry Poller
	 */
	public final String BLOCKOUTRETRYPOLLER = "BLOCKOUTRETRYPOLLER";
	
	/**
	 * ICICIPOLLER
	 */
	public final String ICICIPOLLER = "ICICIPOLLER";
	
	/**
	 * REQ_ROUTER_POLLER
	 */
	public final String REQ_ROUTER_POLLER = "REQ_ROUTER_POLLER";
	
	/**
	 * Trans File Poller
	 */
	public final String TRANSFILEPOLLER = "TRANSFILEPOLLER";
	
	
	/**
	 * SFTP Interface component
	 */
	public final String SFTP_INTERFACE = "SFTPINTERFACE";
	
	
	/**
	 * HTTP Query String Interface
	 */
	public final String HTTP_QS_INTERFACE = "HTTP_QS_INTERFACE";
	
	/**
	 * HTTP XML Interface
	 */
	public final String HTTP_XML_INTERFACE = "HTTP_XML_INTERFACE";
	
	/**
	 * ICICI HTTP Interface
	 */
	public final String ICICI_HTTP_INTERFCE = "ICICI_HTTP_INTERFACE";	
	
	
	/**
	 * Ezeeconnect Interface
	 */
	public final String EZEECONNECT_INTERFACE = "EZEECONNECT_INTERFACE";

	/**
	 * VSMSC Version
	 */
	public final String VSMSC = "VSMSC";

	
	/**
	 * Prerouter core
	 */
	public final String PREROUTER = "PREROUTER";
	
	/**
	 * DN Component core
	 */
	public final String DN_CORE = "DN_CORE";
	
	/**
	 * DN component File Poller
	 */
	public final String DN_FILE_POLLER = "DN_FILE_POLLER";
	
	/**
	 * DN Component HTTP Poller
	 */
	public final String DN_HTTP_POLLER = "DN_HTTP_POLLER";
	
	/**
	 * NG Utils
	 */
	public final String NG_UTILS = "NG_UTILS";
	

	/**
	 * Pull Retry component
	 */
	public final String PULL_RETRY = "PULLRETRY";
	

	public final String FTP_INTERFACE = "FTP_INTERFACE";


	public final String DNMSGRETRYPOLLER = "DNMSGRETRYPOLLER";
	
	public final String DELIVERYSMFAILOVER = "DELIVERYSMRETRY";
	
	/**
	 * Prepaid & Credit Engine
	 */
	
	public final String CREDIT_ENGINE = "CREDIT_ENGINE";
	
	public final String DUP_CHECK= "NGDUPE";

	public final String CONCAT_POLLER="RCONCATCONSUMER";

	
}
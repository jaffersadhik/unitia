/**
 * 	@(#)NGJNDINames.java	1.0
 *
 * 	Copyright 2000-2008 Air2web India Pvt Ltd. All Rights Reserved.
 */
package com.winnovature.unitia.util.constants;
/**
 * 	This interface is declaration of Constants 
 * 	
 *
 * 	@author 	M Ravikumar (ravikumar@air2web.co.in)
 *	@version 	NGJNDINames.java v1.0<br>
 *				Created: 07 Jul 2008 15:15<br>
 *				Last Modified 07 Jul 2008 15:15 by M Ravikumar
 */
public interface NGJNDINames {
	
	

	/**
	 * Pull response queue
	 */
	public final String NG_PULL_RESP_Q	=	"queue/NG.PULL.RESP.Q";	
	
	
	/**
	 * Async pull queue
	 */
	public final String NG_ASYNC_PULL_Q   	=	"queue/NG.ASYNC.PULL.Q";
		
	/**
	 * Pull request MBL Queue
	 */
	public final String NG_PULLREQ_MBL_Q = "queue/NG.PULLREQ.MBL.Q";
	
	/**
	 * Pull response MBL Queue
	 */
	public final String NG_PULLRESP_MBL_Q = "queue/NG.PULLRESP.MBL.Q";

	/**
	 * Pull retry sql Queue
	 */
	public final String NG_PULL_RETRY_Q = "queue/NG.PULL.RETRY.Q";	
	
	/**
	 * Connection Factory
	 */                  
	public final String NG_JMS_CF         	=	"NG.JMS.CF";	
	
	/**
	 * core data source
	 */
	public final String NG_CORE_DS        	=	"java:NG.CORE.DS";
	

	/**
	 * Request router mysql data source
	 */
	public final String NG_REQROUTER_DS		=  "java:NG.REQROUTER.DS";

	

}

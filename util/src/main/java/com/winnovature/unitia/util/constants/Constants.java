/**
 * 	@(#)Constants.java	1.0
 *
 * 	Copyright 2000-2008 Air2web India Pvt Ltd. All Rights Reserved.
 */

package com.winnovature.unitia.util.constants;

/**
 * 	This interface is declaration of Constants 
 * 	
 *
 * 	@author 	M Ravikumar (ravikumar@air2web.co.in)
 *	@version 	ConfigParamKeys.java v1.0<br>
 *				Created: 25 Jun 2008 19:15<br>
 *				Last Modified 25 Jun 2008 19:15 by M Ravikumar
 */
public interface Constants {


	/**
	 * Reference value to define all the instances can share the same database
	 * properties.
	 */
	public final String SHARED_INSTANCE_KEY = "SHARED";
	
	/**
	 * Account type pull
	 */
	public final int ACC_TYPE_PULL	=	1;
	
	/**
	 * Account type push
	 */
	public final int ACC_TYPE_PUSh	=	2;
	
	/**
	 *	Pull & Push account 
	 */
	public final int ACC_TYPE_PULL_PUSH	=	3;
	
	/**
	 * 	Normal priority
	 */
	public final int PRIORITY_NORMAL	=	0;
	
	/**
	 * 	High priority
	 */
	public final int PRIORITY_HIGH	=	1;
	
	/**
	 * 	Active status
	 */
	public final int STATUS_ACTIVE	=	1;
	
	/**
	 * 	In active status
	 */
	public final int STATUS_INACTIVE	=	0;

	/**
	 * 	Deleted status
	 */
	public final int STATUS_DELETED	=	-1;
	
	/**
	 * http xml type SS old
	 */
	public final String HTTP_XML_TYPE_SS_OLD	=	"OSS";
	
	/**
	 * http xml type SM old
	 */
	public final String HTTP_XML_TYPE_SM_OLD	=	"OSM";
	
	/**
	 * http xml type MM old
	 */
	public final String HTTP_XML_TYPE_MM_OLD	=	"OMM";
	
	/**
	 * http xml type SS
	 */
	public final String HTTP_XML_TYPE_SS	=	"SS";
	
	/**
	 * http xml type SM
	 */
	public final String HTTP_XML_TYPE_SM	=	"SM";
	
	/**
	 * http xml type MM
	 */
	public final String HTTP_XML_TYPE_MM	=	"MM";
	
	/**
	 * Pin number
	 */ 
	public final String PIN = "PIN";
	/**
	 * transaction id for specific instance
	 */ 
	public final String TRANS_ID = "TRANS_ID";
	
	/**
	 * Delivery SM Service type
	 */
	public final String	DELIVER_SM_SERVICE_TYPE	=	"A2W";
	
	/**
	 * Bill referance
	 */
	public final String	BILLREF						= "BILLREF";

	/**
	 * Received Time Stamp.
	 */
	public final String	RTS							= "RTS";

	/**
	 * Time Stamp
	 */
	public final String	TS							= "TS";

	/**
	 * Mobile Number
	 */
	public final String	MOBILE						= "MOBILE";

	/**
	 * Message
	 */
	public final String	MSG							= "MSG";

	/**
	 * UDH
	 */
	public final String	UDH							= "UDH";

	/**
	 * Language Code
	 */
	public final String	LANGCD						= "LANGCD";

	/**
	 * Message Type
	 */
	public final String	MSGTYPE						= "MSGTYPE";

	/**
	 * Message Source
	 */
	public final String	MSGSRC						= "MSGSRC";

	/**
	 * Customer referance
	 */
	public final String	CUSTREF						= "CUSTREF";

	/**
	 * Sender ID
	 */
	public final String	SENDERID					= "SENDERID";

	/**
	 * Carrier ID
	 */
	public final String	CARRIERID					= "CARRIERID";

	/**
	 * Groupd ID
	 */
	public final String	GROUPID						= "GROUPID";

	/**
	 * Status ID
	 */
	public final String	STATUSID					= "STATUSID";

	/**
	 * Split Sequence
	 */
	public final String	SPLIT_SEQ					= "SPLIT_SEQ";

	/**
	 * Retry count
	 */
	public final String	RETRY_COUNT					= "RETRY_COUNT";

	/**
	 * Process Status.
	 */
	public final String	PROCESS_STATUS				= "PROCESS_STATUS";

	/**
	 * Fetching Time.
	 */
	public final String	FETCH_TIME					= "FETCH_TIME";

	/**
	 * Circel ID
	 */
	public final String	CIRCLEID					= "CIRCLEID";

	/**
	 * Route ID
	 */
	public final String	ROUTEID						= "ROUTEID";


	/*
	 * NULL Constants
	 */
	public final String NULL = "NULL";
	/*
	 * Code to indentify the Domestic mobile number
	 */
	public final String INDIA_COUNTRYCODE = "IN";
	/*
	 * Code to identify the Foreign mobile number
	 */
	public final String FOREIGN_COUNTRYCODE = "FO";
	
	/*
	 * code to identify the UNKNOWN CARRIER mobile number
	 */
	public final String UNKNOWN_COUNTRYCODE = "UN";
	
	/*
	 * Split Messge constant
	 */
	public final String SPLIT = "SPLIT";
	
	/*
	 * Truncate Message constant
	 */
	public final String TRUNC = "TRUNC";
	
	/*
	 * Concatenate Message Constant
	 */
	public final String CONCAT = "CONCAT";	

	
	/**
	 * Usage		: Query string name - Pull
	 */
	public static final String	QS	= "QS";

	/**
	 * Usage		: Static service type - Pull
	 */
	public static final String	STATIC	= "STATIC";

	/**
	 * Usage		: Dynamic service type. - Pull
	 */
	public static final String	DYNAMIC	= "DYNAMIC";

	/**
	 * Usage		: Double service type - Pull
	 */
	public static final String	DOUBLE	= "DOUBLE";	
	
	/**
	 * SCHEDULE TIME FORMAT
	 */
	public final String SCHEDULE_TS_FORMAT = "yyyy/MM/dd/HH/mm";
	
	/**
	 * TS_FORMAT
	 */
	public final String TS_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * code to identify the UNKNOWN CARRIER mobile number
	 */
	public final String UNKNOWN_CARRIER = "UNKNOWN";
	
	
	/**
	 * code to identify the UNKNOWN CARRIER mobile number
	 */
	public final String UNKNOWN_CIRCLE = "UNKNOWN";
	
	/**
	 * Internal reference code for landline
	 */
	public final String LANDLINE_CODE = "LL";
	
	/**
	 * DN DR Constant
	 */
	public final String DN_DR = "%25a";
	
	/**
	 * DN Statuscd Constant
	 */
	public final String DN_STATUSCD = "%25d";
	
	/**
	 * ACK DR Constant
	 */
	public final String ACK_DR = "ACK";
	
	
	//###############################--NG 3--######################################################
	
	
	/**
	 * application name in global properties
	 * properties.
	 */
	public final String A2WI_UTILS_NG_KEY = "ng3.properties.loc";
	
	//public final String HTTPINTERFACE_PROPERTY_FILE_KEY_NAME = "ng.failsafe.properties.loc";
	
	/*
	 * code to identify Primary route in Round Robin 
	 */
	public final String PRIMARY_ROUTE = "PRIMARY";
	
	/*
	 * code to identify the alternate route in round robin
	 */
	public final String ALTERNATE_ROUTE = "ALTERNATE";

	public final String PAYLOAD = "PAYLOAD";

	//public final String BATCH_ID = "BATCHID";

	//public final String DB_TYPE = "DB_TYPE";

	public final String JNDI_NAME = "DATASOURCE";

	public final String INTERIM_TBL_NAME = "TABLENAME";

	public final String STATUS_COMPLETED = "COMPLETED";

	public final String STATUS_HFAILURE = "HFAILURE";

	public final String SMS_INDENTIFIER = "SMS";
	
	public final String EMAIL_INDENTIFIER = "EMAIL";
	
	public final String DEF_POLLER = "DEFAULT_POLLER";
	
	public final String TRANSMSG = "1";
	
	public final String PROMOMSG = "2";
	
	//NG change made for TRAI
	public final String PRE ="1";
	
	public final String POST ="0";
	
	public final String CREDIT ="2";

	public final String PREROUTERQUEUENAME="PREROUTER-STATS-QUEUE";
	
	public final String PREROUTERQUEUEREAPERNAME="PREROUTER-STATS-QUEUE-REAPER";
	
	public final String DNRETRYQUEUENAME="DNRETRY-STATS-QUEUE";
	
	public final String DNRETRYQUEUEREAPERNAME="DNRETRY-STATS-QUEUE-REAPER";

	String UNKNOWN_DN_ERROR_CODE = "unknown.dn.error.code";
	String UNKNOWN_DN_ERROR_DESC = "unknown.dn.error.desc";
	String DS_JNDI_NAME = "ds.jndi";
}
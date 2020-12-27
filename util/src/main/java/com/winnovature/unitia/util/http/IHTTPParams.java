package com.winnovature.unitia.util.http;

public interface IHTTPParams
{
	
	public final String SHORTCODE="shortcode";
	
	public final String VMN="vmn";
	
	public final String TIMESTAMP="timestamp";
	
	public final String CIRCLE="circle";

	public final String OPERATOR="operator";

	/**
	 * Pin
	 */
	public final String PIN = "password";
	
	/**
	 * Mobile number / email address received from clients
	 */
	public final String MNUMBER = "to";
	
	/**
	 * Message text
	 */
	public final String MESSAGE = "content";
	
	/**
	 * Sender id
	 */
	public final String SIGNATURE = "from";
	
	
	/**
	 * Schedule time
	 */
	public final String SCHETIME = "scheduletime";
	
	/**
	 * Message type
	 */
	public final String MSGTYPE = "msgtype";
	
	/**
	 * User Data Header
	 */
	public final String UDH = "udh";
	
	
	
	/**
	 * Message id
	 */
	public final String MSGID = "msgid";
	
	/**
	 * Air2web acknowledgement id
	 */
	public final String ACKID = "ackid";
	
	/**
	 * Mobile number
	 */
	public final String MOBILE = "mobile";
	
	/**
	 * Client generated reference id 
	 */
	public final String CUSTREF = "custref";
	
	/**
	 * DLR type
	 */
	public final String DLRTYPE = "dlrtype";
	
	/**
	 * Sender id for URL carrier message router
	 */
	public final String FROMADDRESS = "fromaddress";
	
	/**
	 * Email address for email delivery router
	 */
	public final String EMAIL = "email";
	
	/**
	 * Message sending source - email delivery router
	 */
	public final String MSGFROM = "msgfrom";
	
	/**
	 * Customer IP address
	 */
	public final String CUSTIP = "custip";
	
	/**
	 * Prerouter id
	 */
	public final String PRID = "prid";
	
	/**
	 * Kannel's SMSC Id
	 */
	public final String SMSCID = "smscid";
	
	/**
	 * kannel's / internal error status code
	 */
	public final String STATUSCD = "statuscd";
	
	/**
	 * Kannel's delivery receipt string
	 */
	public final String DR = "dr";
	
	/**
	 * Client ack dn layer's air2web acknowledgement id
	 */
	public final String A2WACKID = "a2wackid";
	
	/**
	 * Client ack dn layer's submit date
	 */
	public final String SUBMITDT = "submitdt";
	
	/**
	 * Last updated time
	 */
	public final String LASTUTIME = "lastutime";
	
	/**
	 * Air2web status
	 */
	public final String A2WSTATUS = "a2wstatus";
	
	/**
	 * Carrier delivery status
	 */
	public final String CARRIERSTATUS = "carrierstatus";
	
	/**
	 * Carrier error status
	 */
	public final String ERR = "err";

	public final String USERNAME = "username";

	public final String PARAM1 = "param1";

	public final String PARAM2 = "param2";

	public final String PARAM3 = "param3";

	public final String PARAM4 = "param4";

	public final String CONTENT = "content";

	/**
	 * Usage		: Parameter from carrier, holds mobile no. 
	 */
	public static final String	CLI		= "cli";

	/**
	 * Usage		: Parameter from carrier, holds short code.
	 */
	public static final String	MSISDN	= "msisdn";

	/**
	 * Usage		: Parameter from carrier, holds message.
	 */
	public static final String	MSG		= "msg";	
	
	/**
	 * WAP URL
	 */
	public static final String WAPURL = "wapurl";
	
	public static final String CNAME = "cname";
	
	public static final String CNO = "cno";
	
	public static final String DELIMITER = "delimiter";
	
	public static final String FILECLASS = "fileclass";
	
	
	

}

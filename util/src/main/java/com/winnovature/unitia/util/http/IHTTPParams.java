package com.winnovature.unitia.util.http;

public interface IHTTPParams
{
	/**
	 * Account id
	 */
	public final String AID = "aid";
	
	/**
	 * Partner id
	 */
	public final String PID = "pid";
	
	/**
	 * Request received time stamp (msg accepted date time)
	 */
	public final String RTS = "rts";
	
	/**
	 * Source of the request; campaign manager / bct...etc
	 */
	public final String SOURCE = "source";
	
	/**
	 * Partner code
	 */
	public final String PCODE = "pcode";
	
	/**
	 * Application code
	 */
	public final String ACODE = "acode";
	
	/**
	 * Pin
	 */
	public final String PIN = "password";
	
	/**
	 * Mobile number / email address received from clients
	 */
	public final String MNUMBER = "mobile";
	
	/**
	 * Message text
	 */
	public final String MESSAGE = "message";
	
	/**
	 * Sender id
	 */
	public final String SIGNATURE = "senderid";
	
	/**
	 * Language code
	 */
	public final String LANG = "lang";
	
	/**
	 * Schedule time
	 */
	public final String SCHETIME = "scheduletime";
	
	/**
	 * Ondemand / pull id
	 */
	public final String ODREQID = "odreqid";
	
	/**
	 * Priority flag
	 */
	public final String PRTY = "prty";
	
	/**
	 * Message type
	 */
	public final String MSGTYPE = "msgtype";
	
	/**
	 * User Data Header
	 */
	public final String UDH = "udh";
	
	/**
	 * Port
	 */
	public final String PORT = "port";
	
	/**
	 * Country type
	 */
	public final String CNTRY = "cntry";
	
	/**
	 * Expiry minutes
	 */
	public final String EXPIRY = "expiry";
	
	/**
	 * Split algorithm
	 */
	public final String SPLITALGM = "splitAlgm";
	
	/**
	 * Bill reference id
	 */
	public final String BILLREF = "billref";
	
	/**
	 * ICICI generated unique reference id
	 */
	public final String MID = "mid";
	
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

/**
 * 	@(#)MessageStatus.java	1.0
 *
 * 	Copyright 2000-2008 Air2web India Pvt Ltd. All Rights Reserved.
 */
package com.winnovature.unitia.util.misc;

public interface MessageStatus2 {
		
	/**
	 * Air2web accepted. (PENDING)
	 */
	public final int AIR2WEB_ACCEPTED = 0;

	/**
	 * System error / exception (FAILURE)
	 */
	public final int SYSTEM_ERROR = -1;
	
	/**
	 * Invalid credentials. (REJECTED)
	 */
	public final int HTTP_QS_INVALID_CREDENTIALS = -2;
	
	/**
	 * Empty mnumber. (REJECTED)
	 */
	public final int HTTP_QS_EMPTY_MNUMBER = -3;
	
	/**
	 * Empty message. (REJECTED)
	 */
	public final int HTTP_QS_EMPTY_MESSAGE = -4;
	
	/**
	 * Query HTTPS is Disabled for this account. (REJECTED)
	 */
	public final int HTTP_QS_HTTPS_DISABLED = -5;
	
	/**
	 * Query HTTP is Disabled for this account. (REJECTED)
	 */
	public final int HTTP_QS_HTTP_DISABLED = -6;
	
	/**
	 * Invalid credentials in xml. (REJECTED)
	 */
	public final int HTTP_XML_INVALID_CREDENTIALS = -7;
	
	/**
	 * Invalid XML. Not able to parse. (REJECTED)
	 */
	public final int INVALID_XML = -8;
	
	/**
	 * Invalid Client.
	 */
	public final int INVALID_CLIENT = -9;
	
	/**
	 * Invalid Contact name for V Card
	 */
	public final int HTTP_QS_EMPTY_CONTACT_NAME = -10;
	
	/**
	 * Invalid Contact number for V Card
	 */
	public final int HTTP_QS_EMPTY_CONTACT_NUMBER = -11;
	
	/**
	 * Invalid WAP URL
	 */
	public final int HTTP_QS_EMPTY_WAPURL = -12;
	
	/**
	 * Internal ERROR
	 */
	public final int INTERNAL_ERROR = -13;
	
	/**
	 * File format not supported(REJECTED)
	 */
	public final int INVALID_FILE_CLASS = -15;
	
	/**
	 * File format not supported(REJECTED)
	 */
	public final int FILE_NOT_FOUND = -16;	

	/**
	 * Bulk upload disabled
	 */
	public final int BULK_UPLOAD_DISABLED = -17;	
	/**
	 * Bulk upload disabled
	 */
	public final int INVALID_INPUT_FILETYPE = -18;	
	/**
	 * Pull request successfully handed over to enterprise. (SUCCESS)
	 */
	public static final int	PULL_REQ_STATUSID	= 100;
	
	/**
	 * Junk pull request.  (REJECTED)
	 */
	public static final int	PULL_JUNK_REQ_STATUSID	= -101;
	
	/**
	 * Pull Retry Handover failed after trying max attempts. (FAILURE) 
	 */
	public static final int PULL_RETRY_FAILED = -102;
	
	/**
	 * Email delivery success (SUCCESS)
	 */
	public final int EMAIL_DELIVERY_SUCCESS = 200;
	
	/**
	 * Email Delivery Disabled (REJECTED)
	 */
	public final int EMAIL_DELIVERY_DISABLED = -201;
	
	/**
	 * EMAIL MAX Retry Count Exceeded. (FAILURE)
	 */
	public final int EMAIL_MAX_RETRY_EXCEEDED = -203;
	
	
	 /**

     * Suppressed Email

     */

    public final int SUPPRESSED_EMAIL = -204;

    

    /**

     * Invalid  Email

     */

    public final int INVALID_EMAIL = -205;


    public final int EMAIL_GROUP_NOT_FOUND = -206;


    public final int EMAIL_RRID_NOT_FOUND = -207;
    
    public final int EMAIL_ROUTEID_NOT_FOUND = -208;
    
	/**
	 * Invalid Schedule Time (REJECTED)
	 */
	public final int INVALID_SCHEDULE_TIME = -401;
	
	/**
	 * Invalid Message Text (REJECTED)
	 */
	public final int INVALID_MESSAGE_TEXT = -402;

	/**
	 * Invalid sender id (REJECTED)
	 */
	public final int INVALID_SOURCE_ADDRESS = -403;

	/**
	 * Invalid Message Type (REJECTED)
	 */
	public final int INVALID_MSG_TYPE = -404;

	/**
	 * Invalid UDH (REJECTED)
	 */
	public final int INVALID_UDH = -405;

	/**
	 * Invalid Port (REJECTED)
	 */
	public final int INVALID_PORT = -406;

	/**
	 * Invalid Expiry Minutes (REJECTED)
	 */
	public final int INVALID_EXPIRY_MINUTES = -407;
	
	/**
	 * Invalid Customer Reference Number (REJECTED)
	 */
	public final int INVALID_CUST_REFERENCE_ID = -408;

	/**
	 * Invalid Bill Reference ID (REJECTED)
	 */
	public final int INVALID_BILL_REF_ID = -409;

	/**
	 * Invalid Destination Address (REJECTED)
	 */
	public final int INVALID_DESTINATION_ADDRESS = -410;

	/**
	 * Invalid SS XML (REJECTED)
	 */
	public final int INVALID_SS_XML = -411;

	/**
	 * Invalid OSS XML (REJECTED)
	 */
	public final int INVALID_OSS_XML = -412;

	/**
	 * Invalid SM XML (REJECTED)
	 */
	public final int INVALID_SM_XML = -413;

	/**
	 * Invalid OSM XML (REJECTED)
	 */
	public final int INVALID_OSM_XML = -414;

	/**
	 * Invalid MM XML (REJECTED)
	 */
	public final int INVALID_MM_XML = -415;	
	
	/**
	 * Pull Response Delivery from Q to prerouter Failed (FAILURE)
	 */
	public final int PULL_RESP_DELV_FAILED = -416;

	/**
	 * Priority Delivery from Q to prerouter Failed (FAILURE)
	 */
	public final int PRTY_DELV_FAILED = -417;
	
	/**
	 * Message filtered in DND database (REJECTED)
	 */
	public final int DND_REJECTED = -418;

	/**
	 * GroupID NOT FOUND - Routing Configuration missed (FAILURE)
	 */
	public final int INVALID_GROUPID = -419;

	/**
	 * Invalid message length for FL and SP (REJECTED)
	 */
	public final int MSG_REJECTED = -420;

	/**
	 * Carrier not supporting Binary Message (REJECTED)
	 */
	public final int BM_REJECTED_CARRIER_NOT_SUPPORT = -421;

	/**
	 * Concatention Message of Invalid Length over HTTP with UDH (REJECTED)
	 */
	public final int CM_HTTP_REJECTED_INVALID_LENGTH = -422;
	
	/**
	 * Invalid Prepaid Message Quota.
	 */
	public final int INVALID_PREPAID_MSG_QUOTA = -423;

	/**
	 * Invalid Credit Message Quota.
	 */
	public final int INVALID_CREDIT_MSG_QUOTA = -424;
	
	/**
	 * Route not available
	 */
	public final int INVALID_ROUTE = -425;

	/**
	 * Maximum attempts exceeded for message retry
	 */
	public final int MAX_RETRY_ATTEMPTED = -426;

	/**
	 * Route template not available
	 */
	public final int INVALID_DR_TEMPLATE = -427;
	
	/**
	 * Message expired due to expiry_minutes
	 */
	public final int MSG_EXPIRED = -428;

	/**
	 * Email delivery handover failed to Email delivery router
	 */
	public final int EMAIL_DELV_FAILED = -429;
	
	/**
	 * Invalid OD Request ID
	 */
	public final int INVALID_OD_REQUEST_ID = -430;
	
	/**
	 * Invalid Account.
	 */
	public final int INVALID_ACCOUNT = -431;
	
	/**
	 * Message Successfully handed over to MR or Kannel (SUCCESS)
	 */
	public final int MSG_DEVL_SMSC_GW = 500;

	/**
	 * INVALID_BILL_REF_ID_LENGTH
	 */
	public final int INVALID_BILL_REF_ID_LENGTH = -432;

	/**
	 * INVALID_CUST_REFERENCE_ID_LENGTH
	 */
	public final int INVALID_CUST_REFERENCE_ID_LENGTH = -433;

	/**
	 * INVALID_DESTINATION_ADDRESS_LENGTH
	 */
	public final int INVALID_DESTINATION_ADDRESS_LENGTH = -434;

	/**
	 * Invalid DR Split length
	 */
	public final int INVALID_DR_SPLIT_LENGTH = -437;
		
	/**
	 * DND Message
	 */
	public final int BLACKLIST_REJECTED = -436;

	/**
	 * Invalid OD REQ ID
	 */
	public final int INVALID_OD_REQUEST_ID_LENGTH = -435;
	

	public final int INVALID_ALPHANUMERIC_DESTINATION_ADDRESS = -438;
	
	
	/**
	 * Message Filter Rejected
	 */
	public final int MSGFILTER_REJECTED = -439;
	
	/* Rejected status for Invalid Dlr Type, its considered invalid if the incoming value is not in DLRTYPE table     */
    public final int INVALID_DLRTYPE = -440;
    
    /**
     *  TRAI Rejected Blockout 
     */
    public final int MSG_REJECTED_TRAI_BLOCKOUT = -441;
    
    /**
     * Template Exception REJECTION
     */
    
    public final int INVALID_TEMPLATE = -442;
    
    /**
     *  Message Rejected Customer disable Schedule flag
     */
    
    public final int MSG_REJECTED_SCHEDULE_OPTION_DISABLE = -443;
        
    

    public final int DN_MSG_RETRY_ROUTE_NOT_FOUND = -444;
    
    public final int DN_MSG_RETRY_NOT_IN_MBL = -445;


    public final int DN_MSG_RETRY_HANDOVER_PR = 446;

	public final int ACCOUNT_INACTIVATED = -447;
	
	public final int ACCOUNT_EXPIRED = -448;
	
	public final int OPTIN_REJECTED = -449;
	
	public final int AIRTEL_ALTERNATE_ROUTE_NOT_FOUND = -451;
	
	public final int AIRTEL_ALTERNATE_GROUP_NOT_FOUND = -452;
	
	public final int SPECIFIC_BLOCKOUT_DROP = -460;
	
	public final int KOTAK_DUPLICATE_REJECT = -461;
	 
	/* 04NOV2014 OTP RETRY CHANGE*/
	public final int KANNEL_HANDOVER_FAILURE = -462;

	public final int GLOBAL_SENDERID_REJECTION = -475;

	public final int GLOBAL_MSG_REJECTION = -476;

	public final int GLOBAL_MOBILE_REJECTION = -477;
	
	public final int DAY_TRAFFIC_LIMIT_EXCEEDED=-478;
	
	public final int MONTH_TRAFFIC_LIMIT_EXCEEDED=-479;
	
	public final int INVALID_SEGMENT=-480;

	public final int OPTOUT_REJECTED=-481;
	
	public final int CONCAT_MSG_EXPIRED=-482;
	
	public final int INVALID_COUNTRY_CODE=-490;
	
	public final int INVALID_MOBILE_NUMBER_LENGTH=-491;

	public final int INVALID_NSN_RANGE=-492;

	public final int INVALID_HID=-493;

	public final int INTL_DISABLED=-494;
	
	public final int OTP_DROP=-495;


}

package com.winnovature.unitia.util.misc;

import java.util.HashMap;
import java.util.Map;

public class MessageStatus {
		
	public static final int INVALID_USERNAME = 101;
	public static final int INVALID_PASSWORD = 102;
	public static final int ACCOUNT_INACTIVATED = 103;
	public static final int HTTP_QS_EMPTY_MNUMBER = 104;
	public static final int HTTP_QS_EMPTY_MESSAGE = 105;
	public static final int SCHEDULETIME_PARSE_ERROR = 106;
	public static final int INVALID_SCHEDULE_TIME = 107;
	public static final int MSG_REJECTED_SCHEDULE_OPTION_DISABLE = 108;
	public static final int INVALID_DESTINATION_ADDRESS = 109;
	public static final int SENT_TO_QUEUE_FAILED = 110;
	public static final int MOBILE_SERIES_NOT_REGISTERED_NP = 111;
	public static final int ROUTEGROUP_NOT_FOUND = 112;
	public static final int INVALID_MSGTYPE = 113;
	public static final int INVALID_SPLITGROUP = 114;
	public static final int INVALID_ROUTE_GROUP = 115;
	public static final int INVALID_SMSCID = 116;
	public static final int OPTOUT_MOBILE_NUMBER = 117;
	public static final int MOBILE_NOT_REGISTERED_OPTIN = 118;
	public static final int BLACKLIST_MOBILE = 119;
	public static final int BLACKLIST_SENDERID = 120;
	public static final int BLACKLIST_SMS_PATTERN = 121;
	public static final int FILTERING_SMS_PATTERN = 122;
	public static final int DND_REJECTED = 123;
	
	private static Map<String,String> status=new HashMap<String,String>();
	private static MessageStatus obj=null;
	
	static{
		
		status.put(""+MessageStatus.INVALID_USERNAME, "Username not Registered");
		status.put(""+MessageStatus.INVALID_PASSWORD, "Invalid password");
		status.put(""+MessageStatus.ACCOUNT_INACTIVATED, "account inactivated");
		status.put(""+MessageStatus.HTTP_QS_EMPTY_MNUMBER, "mobile parametered value invalid");
		status.put(""+MessageStatus.HTTP_QS_EMPTY_MESSAGE, "message parametered value invalid");
		status.put(""+MessageStatus.SCHEDULETIME_PARSE_ERROR, "scheduletime parametered parsing error,scheduletime should be in the format of yyyy/MM/dd/HH/mm");
		status.put(""+MessageStatus.INVALID_SCHEDULE_TIME, "Invalid scheduletime parameter value");
		status.put(""+MessageStatus.INVALID_DESTINATION_ADDRESS, "Invalid mobile value");
		status.put(""+MessageStatus.MSG_REJECTED_SCHEDULE_OPTION_DISABLE, "schedule feature disable for the account");
		status.put(""+MessageStatus.SENT_TO_QUEUE_FAILED, "Sent Queue fail in Interface Level");
		status.put(""+MessageStatus.MOBILE_SERIES_NOT_REGISTERED_NP, "Mobile Number Series Not Registered in Numbering Plan Table");
		status.put(""+MessageStatus.ROUTEGROUP_NOT_FOUND, "Route Group Not Available in Route table");
		status.put(""+MessageStatus.INVALID_MSGTYPE, "Msgtype not Available for particular split group in splitgroup table");
		status.put(""+MessageStatus.INVALID_SPLITGROUP, "split group not available in splitgroup table");
		status.put(""+MessageStatus.INVALID_ROUTE_GROUP, "routegroup not available in routegroup table");
		status.put(""+MessageStatus.INVALID_SMSCID, "smscid not available in kannel table");
		status.put(""+MessageStatus.OPTOUT_MOBILE_NUMBER, "Message Rejected due to Optout mobile number for that acoount");
		status.put(""+MessageStatus.MOBILE_NOT_REGISTERED_OPTIN, "Message Rejected due to mobile number not registered in optin list for that acoount");
		status.put(""+MessageStatus.BLACKLIST_MOBILE, "Message Rejected due to mobile number blacklisted by Platform");
		status.put(""+MessageStatus.BLACKLIST_SENDERID, "Message Rejected due to senderid blacklisted by Platform");
		status.put(""+MessageStatus.BLACKLIST_SMS_PATTERN, "Message Rejected due to sms pattern is blacklisted by Platform");
		status.put(""+MessageStatus.FILTERING_SMS_PATTERN, "Message Rejected due to sms pattern is filetred by Account Level");
		status.put(""+MessageStatus.DND_REJECTED, "Message Rejected due to Mobile number Registered As DND");

	}

	private MessageStatus(){
		
		
		
	}
	public static MessageStatus getInstance() {
		
		if(obj==null){
			
			obj=new MessageStatus();
		}
		
		return obj;
		
	}
	public String getStatus(int statusId) {
		return status.get(""+statusId);
	}
}

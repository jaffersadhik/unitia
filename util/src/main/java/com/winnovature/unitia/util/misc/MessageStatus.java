/**
 * 	@(#)MessageStatus.java	1.0
 *
 * 	Copyright 2000-2008 Air2web India Pvt Ltd. All Rights Reserved.
 */
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

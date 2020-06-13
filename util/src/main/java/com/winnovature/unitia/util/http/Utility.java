package com.winnovature.unitia.util.http;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.winnovature.unitia.util.datacache.account.PushAccount;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.redis.QueueSender;
import com.winnovature.unitia.util.threadpool.SMSWorkerPoolRouter;

public class Utility
{
	
	
	public boolean sendQueue(Map<String,String> msgmap,Map<String,String> logmap){
	

		Map<String,String> accountmap=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME));
	
		String poolname=SMSWorkerPoolRouter.getInstance().getPoolName(accountmap);
	
		
		String schetime=msgmap.get(MapKeys.SCHEDULE_TIME);
		
		if(schetime!=null&&schetime.trim().length()>0){
			
			poolname="schedulepool";
		}
		
		if(new QueueSender().sendL(poolname, msgmap, false,logmap)){
			
			logmap.put("sendQueue status", "message sent to "+poolname+" queue Successfully");
		
			return true;
		}else{
			
			logmap.put("sendQueue status", "message sent to "+poolname+" queue failed");
			return false;
		}
	}
	
		
	public int mobileValidation(Map<String,String> msgmap) {
		
		int flag =0;
		
		try {
			
			String	mnumber = msgmap.get(MapKeys.MOBILE);
		
			mnumber = StringUtils.stripStart(mnumber, "+0 ");
			msgmap.put(MapKeys.MOBILE, mnumber);
			
			if(StringUtils.isNumeric(mnumber))	// Valid number
			{
				
				int length = mnumber.length();
				
				// Check for the min and max length 
				int max = Integer.parseInt(ConfigParams.getInstance().getProperty(ConfigKey.MAX_MOBILE_LENGTH_ALLOWED));
				int min = Integer.parseInt(ConfigParams.getInstance().getProperty(ConfigKey.MIN_MOBILE_LENGTH_ALLOWED));
				if((length < min) || (length > max))
					flag =  MessageStatus.INVALID_DESTINATION_ADDRESS;
			}
			else	// Invalid mobile number
			{				
				flag = MessageStatus.INVALID_DESTINATION_ADDRESS;			
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			flag = MessageStatus.INVALID_DESTINATION_ADDRESS;
		}
		return flag;
	}
	
	public int validateScheduleBlockout(String scheTime, Map<String,String> msgmap) {
		
		int flag = 0;
		
		try {
			String status ="";
			HTTPDeliveryTimeCheck hdtc = new HTTPDeliveryTimeCheck();
			/*	Validate sche time	*/
			if(StringUtils.isNotEmpty(scheTime))
			{
				String sehedule_yn = PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME)).get(MapKeys.SCHEDULE_YN);
				
				if("1".equals(sehedule_yn)) {
					
					status = hdtc.isValidScheduleTime(msgmap);
					if("EXCEPTION".equalsIgnoreCase(status))
						flag = MessageStatus.SCHEDULETIME_PARSE_ERROR;
					else if ("CURRENT".equalsIgnoreCase(status))
					{
						msgmap.put(MapKeys.SCHEDULE_TIME, "");
					}else if ("INVALID".equalsIgnoreCase(status)) 						
						flag = MessageStatus.INVALID_SCHEDULE_TIME;					
				} else {
					// Treat this as current msg
					//dto.setScheTime(null);
					flag = MessageStatus.MSG_REJECTED_SCHEDULE_OPTION_DISABLE;	
				}
			}else{
				msgmap.put(MapKeys.SCHEDULE_TIME, "");

			}
			
			if(flag == 0) {
				/*	Validate blockout	*/				
				if(!status.equals("SCHEDULE")){
			
				    	
						status = "CURRENT";

			}
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			flag = MessageStatus.SCHEDULETIME_PARSE_ERROR;
		}
		return flag;
	}
	
	
}

package com.winnovature.unitia.util.http;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.ConfigKey;
import com.winnovature.unitia.util.misc.ConfigParams;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.redis.QueueSender;

public class Utility
{
	
	public String getPoolName(Map<String ,String > accountmap){
		
		

		if(accountmap.get(MapKeys.OTP_YN).equals("1")){
		
			return "otppool";
		}else if(accountmap.get(MapKeys.OPTIN_TYPE).equals("1")){
			
			return "optin";
			
		}else if(accountmap.get(MapKeys.OPTIN_TYPE).equals("2")){
			
			return "optout";
		}else if(!accountmap.get(MapKeys.DUPLICATE_TYPE).equals("0")){
			
			return "duplicate";
		}else{
		
			return "commonpool";
			
		}
	}
	public boolean sendQueue(Map<String,Object> msgmap,Map<String,Object> logmap){
	

		Map<String,String> accountmap=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString());
	
		String poolname=getPoolName(accountmap);
	
		
		String schetime=(String)msgmap.get(MapKeys.SCHEDULE_TIME);
		
		if(schetime!=null&&schetime.trim().length()>0){
			
			poolname="schedulepool";
		}
		
		String concateyn=(String)msgmap.get(MapKeys.CONCATE_YN);
		
		if(concateyn!=null&&concateyn.equals("y")){
			
			poolname="concatepool";

		}
		
		if(new QueueSender().sendL(poolname, msgmap, false,logmap)){
			
			logmap.put("sendQueue status", "message sent to "+poolname+" redis queue Successfully");
		
			return true;
		}else{
			
			logmap.put("sendQueue status", "message sent to "+poolname+" queue failed");
			return false;
		}
	}
	
		
	public int mobileValidation(Map<String,Object> msgmap) {
		
		int flag =0;
		
		try {
			
			String	mnumber = msgmap.get(MapKeys.MOBILE).toString();
		
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
	
	public int validateScheduleBlockout(String scheTime, Map<String,Object> msgmap) {
		
		int flag = 0;
		
		try {
			String status ="";
			HTTPDeliveryTimeCheck hdtc = new HTTPDeliveryTimeCheck();
			/*	Validate sche time	*/
			if(StringUtils.isNotEmpty(scheTime))
			{
				String sehedule_yn = PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.SCHEDULE_YN);
				
				if("1".equals(sehedule_yn)) {
					
					status = hdtc.isValidScheduleTime(msgmap);
					msgmap.put("schedule status", status);
					if("EXCEPTION".equalsIgnoreCase(status)){
						flag = MessageStatus.SCHEDULETIME_PARSE_ERROR;
						msgmap.put(MapKeys.SCHEDULE_TIME, "");
					}else if ("CURRENT".equalsIgnoreCase(status))
					{
						msgmap.put(MapKeys.SCHEDULE_TIME+"_org",msgmap.get(MapKeys.SCHEDULE_TIME));

						msgmap.put(MapKeys.SCHEDULE_TIME, "");
					}else if ("INVALID".equalsIgnoreCase(status)) 						
						flag = MessageStatus.INVALID_SCHEDULE_TIME;	
					msgmap.put(MapKeys.SCHEDULE_TIME, "");

				} else {
					flag = MessageStatus.MSG_REJECTED_SCHEDULE_OPTION_DISABLE;	
					msgmap.put(MapKeys.SCHEDULE_TIME, "");

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

			flag = MessageStatus.SCHEDULETIME_PARSE_ERROR;
			msgmap.put(MapKeys.SCHEDULE_TIME, "");

		}
		return flag;
	}
	
	
}

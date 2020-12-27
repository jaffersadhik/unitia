package simulatar.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

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
		
	public int mobileValidation(Map<String,Object> msgmap) {
		return 0;
	}
	
	public int validateScheduleBlockout(String scheTime, Map<String,Object> msgmap) {
		
		return 0;
	}
	
	
}

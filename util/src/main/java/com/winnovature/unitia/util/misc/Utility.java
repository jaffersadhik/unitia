package com.winnovature.unitia.util.misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.winnovature.unitia.util.account.PushAccount;

public class Utility {
	
	private static Log log = LogFactory.getLog(Utility.class);

	public String prefix91map(Map<String,Object> msgmap,String username, String mobile) {
		

		try{
		Map partnerDetails = PushAccount.instance().getPushAccount(username);
		msgmap.put("intl", partnerDetails.get("intl").toString());
		
		mobile=mobile.trim();
		
		msgmap.put("mobile.length()", mobile.length());

		if (mobile.length() == 10
				&& Integer.parseInt(partnerDetails.get("intl")
						.toString()) == 0
				) {
			mobile = "91" + mobile;
		}else if (mobile.length() == 10
				&& Integer.parseInt(partnerDetails.get("intl")
						.toString()) == 1
				&& Integer.parseInt(partnerDetails.get("prefix91")
						.toString()) == 1&&startWith91uppeder(mobile)) {
			// msgMap.put("MOBILE","91"+mobile);
			mobile = "91" + mobile;
		}
		}catch(Exception e){
			
		}
		return mobile;
	}

	
	public String prefix91(String username, String mobile) {
		

		try{
		Map partnerDetails = PushAccount.instance().getPushAccount(username);
		mobile=mobile.trim();
		if (mobile.length() == 10
				&& Integer.parseInt(partnerDetails.get("intl")
						.toString()) == 0
				) {
			mobile = "91" + mobile;
		}else if (mobile.length() == 10
				&& Integer.parseInt(partnerDetails.get("intl")
						.toString()) == 1
				&& Integer.parseInt(partnerDetails.get("prefix91")
						.toString()) == 1&&startWith91uppeder(mobile)) {
			// msgMap.put("MOBILE","91"+mobile);
			mobile = "91" + mobile;
		}
		}catch(Exception e){
			
		}
		return mobile;
	}
	
	private boolean startWith91uppeder(String mobile){
		
		try{
		StringTokenizer st=new StringTokenizer(ConfigParams.getInstance().getProperty(ConfigKey.PREFIX_START_NUMBER),"~");
		
		while(st.hasMoreTokens()){
			
			String startnumber=st.nextToken();
			
			if(mobile.startsWith(startnumber)){
				
				return true;
			}
		}
		}catch(Exception ignore){
		}
		
		return false;
	}
	public boolean isAccountExpiry(String expiry_dt) {
		
		boolean expiryStatus = false;
		
		try {
			
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date expiryDate = dateFormat.parse(expiry_dt);

			long longExpiryDate = expiryDate.getTime();
			
			long currentTime = System.currentTimeMillis();

			if(longExpiryDate < currentTime) {
				
				expiryStatus = true;
			}
			
			if(log.isDebugEnabled())
			log.debug("Utility.isAccountExpiry() - Account Expiry Status - " + expiryStatus);
		} catch (ParseException e) {
			log.error("Exception occer while parsing Expiry date -", e);
		}
	    
		return expiryStatus;
	}
	
}

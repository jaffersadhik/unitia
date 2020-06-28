package com.winnovature.unitia.util.http;

import java.util.HashMap;
import java.util.Map;


import org.apache.commons.lang.StringUtils;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.ToJsonString;
import com.winnovature.unitia.util.misc.WinDate;


public class RequestProcessor 
{
	Map<String,String> msgmap=null;
	Map<String,String> logmap=null;
	
	public RequestProcessor(){
		
	}
	
	public String processRequest(Map<String,String> msgmap,Map<String,String> logmap)
	{		
		String[] splittedMnumber = null;
		
	
			this.logmap=logmap;
			this.msgmap=msgmap;
		
			boolean isEmail = false;
			//RequestObject requestObj	=	null;
			String password		=	msgmap.get(IHTTPParams.PIN);
			String username		=	msgmap.get(IHTTPParams.USERNAME);
			String mnumber		=	msgmap.get(IHTTPParams.MNUMBER);
			String message		=	msgmap.get(IHTTPParams.MESSAGE);
			String signature	=	msgmap.get(IHTTPParams.SIGNATURE);
			String scheTime		=	msgmap.get(IHTTPParams.SCHETIME);	// Format yyyy/MM/dd/HH/mm
			String msgType		=	msgmap.get(IHTTPParams.MSGTYPE);
			String udh			=	msgmap.get(IHTTPParams.UDH);
			String dlr			=	msgmap.get(IHTTPParams.DLRTYPE);
			String expiry		=	msgmap.get(IHTTPParams.EXPIRY);
		//	String custIP		=	req.getRemoteAddr();
			
			/*	Validate Account */
			
			if(msgType==null){
				
				msgmap.put(MapKeys.MSGTYPE, "EM");
			}else{
				
				msgmap.put(MapKeys.MSGTYPE, msgType);

			}
			
			msgmap.put(MapKeys.USERNAME, username.toLowerCase());

			Map<String,String> partnerMap =PushAccount.instance().getPushAccount(username);
			if(partnerMap == null || partnerMap.isEmpty())
				return getRejectedResponse(MessageStatus.INVALID_USERNAME);// invalid credentials

		
			msgmap.put(MapKeys.SUPERADMIN, partnerMap.get(MapKeys.SUPERADMIN).toLowerCase());
			msgmap.put(MapKeys.ADMIN, partnerMap.get(MapKeys.ADMIN).toLowerCase());

			
			if(password==null||!password.equals(partnerMap.get("password"))){
			
				logmap.put("partnerMap ", partnerMap.toString());
				logmap.put("income password", password);
				return getRejectedResponse(MessageStatus.INVALID_PASSWORD);// invalid credentials

			}
			/* Identifying account is Active/De-Active */
			String accStatus = partnerMap.get("status");
			if(accStatus.equals("0")) 
				return getRejectedResponse(MessageStatus.ACCOUNT_INACTIVATED);// Account inactivated
			
			
			
			/* Identifying Message is Transactional or Promotional */
			String msgClass = partnerMap.get("msgclass");
		
			
			/*	Validate parameters	*/
			if(StringUtils.isBlank(mnumber))
				return getRejectedResponse(MessageStatus.HTTP_QS_EMPTY_MNUMBER);
			
			mnumber = mnumber.trim();
		
			
			if(StringUtils.isBlank(message))
				return getRejectedResponse(MessageStatus.HTTP_QS_EMPTY_MESSAGE);
			message = message.trim();
			
		
			msgmap.put(MapKeys.FULLMSG, message);
			msgmap.put(MapKeys.MESSAGE, message);

			if(StringUtils.isNotEmpty(signature))
			{
				signature = signature.trim();
				if(signature.length()>15)
					signature = signature.substring(0, 15);
			}
			
			
			
			
			
		

			
			if(StringUtils.contains(mnumber, ","))
			{
				splittedMnumber = StringUtils.splitByWholeSeparator(mnumber, ",");
				if(splittedMnumber.length == 2 && StringUtils.isBlank(splittedMnumber[1])) {
					mnumber = splittedMnumber[0];
					splittedMnumber = new String[0];
				}				
			} 
			
			/*	Consttruct the req obj	*/

			msgmap.put(MapKeys.REGISTERED_DELIVERY, dlr);
	//		msgmap.put(MapKeys.REQUEST_IP, custIP);
			msgmap.put(MapKeys.EXPIRY, expiry);			
			msgmap.put(MapKeys.MOBILE,mnumber);
			msgmap.put(MapKeys.SCHEDULE_TIME_STRING, scheTime);
			msgmap.put(MapKeys.SENDERID,signature);
			msgmap.put(MapKeys.SENDERID_ORG,signature);

			msgmap.put(MapKeys.UDH, udh);
			msgmap.put(MapKeys.MSGCLASS, msgClass);
			
			String status = "";
			logmap.putAll(msgmap);
			
			HTTPDeliveryTimeCheck hdtc = new HTTPDeliveryTimeCheck();
			
			int len =0;
	
			if(splittedMnumber != null)
				 len = splittedMnumber.length;
			
			if(len > 1){ // Multiple Mobile Number (Or) Email
			
				
				
				int errorFlag = doMultipleMnumbers(splittedMnumber, msgmap, len,logmap);
				if(errorFlag !=0)
					return getRejectedResponse(errorFlag);	
				
			} else { // Single Mobile Number (Or) Email
				
			
					
					int mobileFlag = new Utility().mobileValidation(msgmap);
					if(mobileFlag !=0) 
						return getRejectedResponse(mobileFlag);
				
				
				if(!isEmail) {
					
					/* Prefix 91 to Mobile Number */
					mnumber = new com.winnovature.unitia.util.misc.Utility().prefix91(username, msgmap.get(MapKeys.MOBILE) ); 
				
					// Set the mnumber					
					msgmap.put(MapKeys.MOBILE, mnumber);
					
										
					int sbFlag = new Utility().validateScheduleBlockout(scheTime, msgmap);
					if(sbFlag !=0) 
						return getRejectedResponse(sbFlag);
				}
			
				if(!new Utility().sendQueue(msgmap,logmap)){
				
				return getRejectedResponse(MessageStatus.SENT_TO_QUEUE_FAILED);
				}

			}
		
		
	
			
			return getWhitelableAcceptedResponse();
		
	}
	
	
	private String getRejectedResponse(int statusId)
	{
	
		Map status=new HashMap();
		status.put("code", ""+statusId);
		status.put("status", MessageStatus.getInstance().getStatus(statusId));
		status.put("rtime",new WinDate().getLogDate());



		return ToJsonString.toString(status);
	}
	
	
	private String getWhitelableAcceptedResponse()
	{
		Map status=new HashMap();
		status.put("ackid", msgmap.get(MapKeys.ACKID));
		status.put("code", "100");
		status.put("status", "Platform accepted");
		status.put("rtime",new WinDate().getLogDate());


		

		return ToJsonString.toString(status);
	}

	
	public int doMultipleMnumbers(String _splittedMnumber[],Map<String,String> msgmap, int _len,Map<String,String> logmap) {
		HTTPDeliveryTimeCheck hdtc = new HTTPDeliveryTimeCheck();
		int flag = 0;
		
		try {
			
			for(int i=0;i<_len;i++) {
					
				boolean isEmail = false;
				
				Map<String,String> dtoobj =new HashMap<String,String>();
				dtoobj.putAll(msgmap);
				
				String _mnumber = _splittedMnumber[i];
				
				
				if(StringUtils.isBlank(_mnumber))
					continue;				
				
				dtoobj.put(MapKeys.MOBILE, _mnumber);
				
				
				
				if(!isEmail && flag == 0) {
					
					/* Prefix 91 to Mobile Number */
					_mnumber = new com.winnovature.unitia.util.misc.Utility().prefix91(dtoobj.get(MapKeys.USERNAME), dtoobj.get(MapKeys.MOBILE) ); 
				
					dtoobj.put(MapKeys.MOBILE, _mnumber);
						
					if(flag == 0)
						flag =	new Utility().validateScheduleBlockout(dtoobj.get(MapKeys.SCHEDULE_TIME_STRING), dtoobj);
				 
				}
			
				if(flag == 0) {
					
					if(!new Utility().sendQueue(msgmap,logmap)){
						
						return MessageStatus.SENT_TO_QUEUE_FAILED;
					}

				} 
			}}	catch(Exception e)
					{
				
				
					}

				
		return flag;

			} // end loop
			
		
	
}

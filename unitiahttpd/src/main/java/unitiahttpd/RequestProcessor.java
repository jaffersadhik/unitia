package unitiahttpd;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.WhiteListedIP;
import com.winnovature.unitia.util.dao.Insert;
import com.winnovature.unitia.util.http.HTTPDeliveryTimeCheck;
import com.winnovature.unitia.util.http.IHTTPParams;
import com.winnovature.unitia.util.http.Utility;
import com.winnovature.unitia.util.misc.ACKIdGenerator;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.ToJsonString;
import com.winnovature.unitia.util.misc.WinDate;
import com.winnovature.unitia.util.redis.QueueSender;



public class RequestProcessor 
{
	Map<String,Object> msgmap=null;
	Map<String,Object> logmap=null;
	
	public RequestProcessor(){
		
	}
	
	public String processRequest(HttpServletRequest request,Map<String,Object> msgmap,Map<String,Object> logmap) throws UnsupportedEncodingException
	{		
		
			try{
		String[] splittedMnumber = null;
		
			msgmap.put(MapKeys.INTERFACE_TYPE, "qs");
			msgmap.put(MapKeys.PROTOCOL, "http");
			this.logmap=logmap;
			this.msgmap=msgmap;
		
			boolean isEmail = false;
			//RequestObject requestObj	=	null;
			String password		=	request.getParameter(IHTTPParams.PIN);
			String username		=	request.getParameter(IHTTPParams.USERNAME);
			String mnumber		=	request.getParameter(IHTTPParams.MNUMBER);
			String message		=	request.getParameter(IHTTPParams.MESSAGE);
			String signature	=	request.getParameter(IHTTPParams.SIGNATURE);
			String scheTime		=	request.getParameter(IHTTPParams.SCHETIME);	// Format yyyy/MM/dd/HH/mm
			String msgType		=	request.getParameter(IHTTPParams.MSGTYPE);
			String param1		=	request.getParameter(IHTTPParams.PARAM1);
			String param2		=	request.getParameter(IHTTPParams.PARAM2);
			String param3		=	request.getParameter(IHTTPParams.PARAM3);
			String param4		=	request.getParameter(IHTTPParams.PARAM4);
			String custIP		=	request.getHeader("X-FORWARDED-FOR");
			String templateid		=	request.getParameter(MapKeys.TEMPLATEID);
			String entityid		=	request.getParameter(MapKeys.ENTITYID);

			if(custIP==null){
				custIP=request.getRemoteHost();
			}
			
			if(custIP==null){
				custIP="";
			}
			msgmap.put(MapKeys.CUSTOMERIP, custIP);
			msgmap.put(MapKeys.MOBILE, mnumber);
			msgmap.put(MapKeys.SENDERID_ORG, signature);
			msgmap.put(MapKeys.PARAM1, param1);
			msgmap.put(MapKeys.PARAM2, param2);
			msgmap.put(MapKeys.PARAM3, param3);
			msgmap.put(MapKeys.PARAM4, param4);
			msgmap.put(MapKeys.SCHEDULE_TIME_STRING, scheTime);
			msgmap.put(MapKeys.SENDERID,signature);
		
			if(templateid==null){
				templateid="";
			}
			
			if(templateid.length()>30){
				templateid=templateid.substring(0,30);
			}
			if(entityid==null){
				entityid="";
			}
			
			if(entityid.length()>30){
				entityid=entityid.substring(0,30);
			}
            msgmap.put(MapKeys.CONTENT_LENGTH,request.getContentLength());

			msgmap.put(MapKeys.TEMPLATEID_CUSTOMER,templateid);
			msgmap.put(MapKeys.ENTITYID_CUSTOMER,entityid);

				msgmap.put(MapKeys.MSGTYPE, msgType);

				if(StringUtils.isBlank(message))
					return getRejectedResponse(MessageStatus.HTTP_QS_EMPTY_MESSAGE);
				message = message.trim();
				
			
				msgmap.put(MapKeys.MESSAGE, message);
				msgmap.put(MapKeys.FULLMSG, message);

			replaceSpace(msgmap);	
			
			logHeader(request,logmap);
			
			setMsgType();
			
			if(username==null){
				username="";
			}
			msgmap.put(MapKeys.USERNAME, username.toLowerCase());
			

			Map<String,String> partnerMap =PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString());
			if(partnerMap == null || partnerMap.isEmpty()){
				return getRejectedResponse(MessageStatus.INVALID_USERNAME);// invalid credentials
			}
			if(password==null||!password.equals(partnerMap.get("password"))){
			
				logmap.put("partnerMap ", partnerMap.toString());
				logmap.put("income password", password);
				return getRejectedResponse(MessageStatus.INVALID_PASSWORD);// invalid credentials

			}
			
			if(!WhiteListedIP.getInstance().isWhiteListedIP(msgmap.get(MapKeys.USERNAME).toString(), custIP)){
				logmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_IP);
				logmap.put("customerIP ",custIP);
				return getRejectedResponse(MessageStatus.INVALID_IP);
			}
			msgmap.put(MapKeys.SUPERADMIN,PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.SUPERADMIN));
			msgmap.put(MapKeys.ADMIN,PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.ADMIN));

			/* Identifying account is Active/De-Active */
			String accStatus = partnerMap.get("status");
			if(accStatus.equals("0")) {
				return getRejectedResponse(MessageStatus.ACCOUNT_INACTIVATED);// Account inactivated
			}
			
		

			
			/* Identifying Message is Transactional or Promotional */
			String msgClass = partnerMap.get("msgclass");
		
			msgmap.put(MapKeys.MSGCLASS, msgClass);
			
			/*	Validate parameters	*/
			if(StringUtils.isBlank(mnumber))
				return getRejectedResponse(MessageStatus.HTTP_QS_EMPTY_MNUMBER);
			
			mnumber = mnumber.trim();
		
			
		
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
			
			
			String status = "";
			logmap.putAll(msgmap);
			
			HTTPDeliveryTimeCheck hdtc = new HTTPDeliveryTimeCheck();
			
			int len =0;
	
			
			if(splittedMnumber != null){
				 len = splittedMnumber.length;
			}
			
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
					mnumber = new com.winnovature.unitia.util.misc.Utility().prefix91map(msgmap,username, msgmap.get(MapKeys.MOBILE).toString() ); 
				
					// Set the mnumber					
					msgmap.put(MapKeys.MOBILE, mnumber);
					
										
					int sbFlag = new Utility().validateScheduleBlockout(scheTime, msgmap);
					if(sbFlag !=0) 
						return getRejectedResponse(sbFlag);
				}
			
				if(!new Utility().sendQueue(msgmap,logmap)){
				
				return getRejectedResponse(MessageStatus.SENT_TO_QUEUE_FAILED);
				}else{
					List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
					list.add(msgmap);
					new QueueSender().sendLtoRequestLog(list, false, logmap);
				}

			
		
		
			}
			
			return getWhitelableAcceptedResponse();

			}catch(Exception e){
				
				return getSystemErrorResponse(e);

			}
		
	}
	
	
	private void logHeader(HttpServletRequest request, Map<String, Object> msgmap2) {
		
		
		Enumeration<String> head=request.getHeaderNames();
		
		while(head.hasMoreElements()){
			String key=head.nextElement();
			msgmap2.put(key.toLowerCase(), request.getHeader(key));
		}
		
	}

	public void replaceSpace(Map<String, Object> msgmap2) {
		
		
		try {
			String msg=msgmap2.get(MapKeys.FULLMSG).toString();

			String	str = URLEncoder.encode(msg, "UTF-8");
		
	       String replacemsg=URLEncoder.encode(" ", "UTF-8");
	       str=str.replace("%C2%A0", replacemsg);
	       
	       msgmap2.put(MapKeys.FULLMSG, URLDecoder.decode(str, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			
		}
	}

	public String getRejectedResponse(int statusId)
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

	
	public String getSystemErrorResponse(Exception e)
	{
		Map status=new HashMap();
		status.put("ackid", msgmap.get(MapKeys.ACKID));
		status.put("code", "-1");
		status.put("status", "Invalid Request");
		status.put("rtime",new WinDate().getLogDate());
		status.put("error", ErrorMessage.getMessage(e));

		

		return ToJsonString.toString(status);
	}
	
	public int doMultipleMnumbers(String _splittedMnumber[],Map<String,Object> msgmap, int _len,Map<String,Object> logmap) {
		HTTPDeliveryTimeCheck hdtc = new HTTPDeliveryTimeCheck();
		int flag = 0;
		
		try {
			List<Map<String,Object>> msgmaplist=new ArrayList<Map<String,Object>>();
			for(int i=0;i<_len;i++) {
					
				boolean isEmail = false;
				
				Map<String,Object> dtoobj =new HashMap<String,Object>();
				dtoobj.putAll(msgmap);
				msgmaplist.add(dtoobj);
				String _mnumber = _splittedMnumber[i];
				
				
				if(StringUtils.isBlank(_mnumber))
					continue;				
				
				dtoobj.put(MapKeys.MOBILE, _mnumber);
				dtoobj.put(MapKeys.MSGID, ACKIdGenerator.getAckId());

				
				
				if(!isEmail && flag == 0) {
					
					/* Prefix 91 to Mobile Number */
					_mnumber = new com.winnovature.unitia.util.misc.Utility().prefix91map(dtoobj,dtoobj.get(MapKeys.USERNAME).toString(), dtoobj.get(MapKeys.MOBILE).toString() ); 
				
					dtoobj.put(MapKeys.MOBILE, _mnumber);
						
					if(flag == 0)
						flag =	new Utility().validateScheduleBlockout((String)dtoobj.get(MapKeys.SCHEDULE_TIME_STRING), dtoobj);
				 
				}
			
				if(flag == 0) {
					/*
					if(!new Utility().sendQueue(dtoobj,logmap)){
						
						return MessageStatus.SENT_TO_QUEUE_FAILED;
					}
					*/
				} 
			}
			
			if(!new Insert().insertA(msgmaplist)){
				
				return MessageStatus.SENT_TO_QUEUE_FAILED;
			}else{
			
				new QueueSender().sendLtoRequestLog(msgmaplist, false, logmap);
			}
			
		}	catch(Exception e)
					{
				
				
					}

				
		return flag;

			} // end loop
			
		
	 private void setMsgType() throws UnsupportedEncodingException{
		 
		 
		 
			
		 	String msg=msgmap.get(MapKeys.FULLMSG).toString();
	    	
	    	String [] msgarray=msg.split(" ");
	    	
	    	
	    	if(msgarray.length==1){
	    		
	    	

	    		if(msgarray[0].matches("-?[0-9a-fA-F]+")){
	    			
	    			if(msgmap.get(MapKeys.MSGTYPE)==null||msgmap.get(MapKeys.MSGTYPE).toString().length()<1){
    			    	
        				msgmap.put(MapKeys.MSGTYPE, "UM");
    		    	}
	    		}else{
	    			for(int i=0;i<msgarray.length;i++){
	        			
	        			if(!isASCII(msgarray[i])){
	        				msgmap.put(MapKeys.FULLMSG,Util.toHexString(msg).replaceAll("u", "").replaceAll("\\\\", ""));
	        				if(msgmap.get(MapKeys.MSGTYPE)==null||msgmap.get(MapKeys.MSGTYPE).toString().length()<1){
	        			    	
		        				msgmap.put(MapKeys.MSGTYPE, "UM");
	        		    	}
	        			}
	        		}
	    		}
	    		
	    	}else{
	    		
	    		for(int i=0;i<msgarray.length;i++){
	    			
	    			if(!isASCII(msgarray[i])){
	    			
	    				msgmap.put(MapKeys.FULLMSG,Util.toHexString(msg).replaceAll("u", "").replaceAll("\\\\", ""));
	    				
	    				if(msgmap.get(MapKeys.MSGTYPE)==null||msgmap.get(MapKeys.MSGTYPE).toString().length()<1){
        			    	
	        				msgmap.put(MapKeys.MSGTYPE, "UM");
        		    	}
	    			}
	    		}
	    	}
	    	
	    	
	    	if(msgmap.get(MapKeys.MSGTYPE)==null||msgmap.get(MapKeys.MSGTYPE).toString().length()<1){
	    	
	    		msgmap.put(MapKeys.MSGTYPE, "EM");
	    	}
	    }
	    
	  private static boolean isASCII(String word) throws UnsupportedEncodingException{
		    for (char c: word.toCharArray()){
		    	  
		    	String encode=URLEncoder.encode(""+c,"UTF-16");
		    	encode=encode.replace("%", "");
		    	if(encode.startsWith("FEFF")){
		    		
		    		encode=encode.substring(4,encode.length());
		    	}

		    	if(!(encode.startsWith("00")||encode.startsWith("20")||encode.length()==1)){
		    	
		    		return false;
		    	}
		    	}
		    	return true;
		    }


	
}

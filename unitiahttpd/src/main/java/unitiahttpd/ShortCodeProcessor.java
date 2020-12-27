package unitiahttpd;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

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



public class ShortCodeProcessor 
{
	Map<String,Object> msgmap=null;
	Map<String,Object> logmap=null;
	
	public ShortCodeProcessor(){
		
	}
	
	public String processRequest(HttpServletRequest request,Map<String,Object> msgmap,Map<String,Object> logmap) throws UnsupportedEncodingException
	{		
		
			try{
				
			msgmap.put(MapKeys.USERNAME, "sys");

			msgmap.put(MapKeys.INTERFACE_TYPE, "shortcode");
			msgmap.put(MapKeys.PROTOCOL, "http");
			this.logmap=logmap;
			this.msgmap=msgmap;
		
			//RequestObject requestObj	=	null;
			String param2		=	request.getParameter(IHTTPParams.SHORTCODE);
			String operator		=	request.getParameter(IHTTPParams.OPERATOR);
			String circle		=	request.getParameter(IHTTPParams.CIRCLE);
			String mnumber		=	request.getParameter(IHTTPParams.MNUMBER);
			String scheTime		=	request.getParameter(IHTTPParams.TIMESTAMP);
			String custIP		=	request.getHeader("X-FORWARDED-FOR");
			String fullmessage	=	request.getParameter(IHTTPParams.CONTENT);

			if(custIP==null){
				custIP=request.getRemoteHost();
			}
			
			if(custIP==null){
				custIP="";
			}
			
			
			
			msgmap.put(MapKeys.OPERATOR, operator);
			msgmap.put(MapKeys.CIRCLE, circle);

			msgmap.put(MapKeys.PARAM2, param2);
			msgmap.put(MapKeys.CUSTOMERIP, custIP);
			msgmap.put(MapKeys.MOBILE, mnumber);
			msgmap.put(MapKeys.ATTEMPT_TYPE, "8");
			msgmap.put(MapKeys.FULLMSG, fullmessage);
			String format = "yyyyMMddHHmmss";
			try {
				SimpleDateFormat formater = new SimpleDateFormat(format);
				formater.setLenient(false);
				
					Date scheduleDate = formater.parse(scheTime);
					System.out.print(scheduleDate.getTime());
					msgmap.put(MapKeys.RTIME,""+ scheduleDate.getTime());
				
				} catch (ParseException pe) {
					
				}
					
					
			
				if(!new QueueSender().sendL("shortcodepool", msgmap, false,logmap)){
				
				return getRejectedResponse(MessageStatus.SENT_TO_QUEUE_FAILED);
				}

			
		
		
		
			
			return getWhitelableAcceptedResponse();

			}catch(Exception e){
				
				return getSystemErrorResponse(e);

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
			
			for(int i=0;i<_len;i++) {
					
				boolean isEmail = false;
				
				Map<String,Object> dtoobj =new HashMap<String,Object>();
				dtoobj.putAll(msgmap);
				
				String _mnumber = _splittedMnumber[i];
				
				
				if(StringUtils.isBlank(_mnumber))
					continue;				
				
				dtoobj.put(MapKeys.MOBILE, _mnumber);
				dtoobj.put(MapKeys.MSGID, ACKIdGenerator.getAckId());

				
				
				if(!isEmail && flag == 0) {
					
					/* Prefix 91 to Mobile Number */
					_mnumber = new com.winnovature.unitia.util.misc.Utility().prefix91(dtoobj.get(MapKeys.USERNAME).toString(), dtoobj.get(MapKeys.MOBILE).toString() ); 
				
					dtoobj.put(MapKeys.MOBILE, _mnumber);
						
					if(flag == 0)
						flag =	new Utility().validateScheduleBlockout((String)dtoobj.get(MapKeys.SCHEDULE_TIME_STRING), dtoobj);
				 
				}
			
				if(flag == 0) {
					
					if(!new Utility().sendQueue(dtoobj,logmap)){
						
						return MessageStatus.SENT_TO_QUEUE_FAILED;
					}

				} 
			}}	catch(Exception e)
					{
				
				
					}

				
		return flag;

			} // end loop
			
		
	 private void setMsgType() throws UnsupportedEncodingException{
		 
		 	String msg=msgmap.get(MapKeys.FULLMSG).toString();
	    	
	    	String [] msgarray=msg.split(" ");
	    	
	    	
	    	if(msgarray.length==1){
	    		
	    	

	    		if(msgarray[0].matches("-?[0-9a-fA-F]+")){
	    			
    	    		msgmap.put(MapKeys.MSGTYPE, "UM");
	    		}else{
	    			for(int i=0;i<msgarray.length;i++){
	        			
	        			if(!isASCII(msgarray[i])){
	        				msgmap.put(MapKeys.FULLMSG,Util.toHexString(msg).replaceAll("u", "").replaceAll("\\\\", ""));
		    	    		msgmap.put(MapKeys.MSGTYPE, "UM");
	        			}
	        		}
	    		}
	    		
	    	}else{
	    		
	    		for(int i=0;i<msgarray.length;i++){
	    			
	    			if(!isASCII(msgarray[i])){
	    				msgmap.put(MapKeys.FULLMSG,Util.toHexString(msg).replaceAll("u", "").replaceAll("\\\\", ""));
	    	    		msgmap.put(MapKeys.MSGTYPE, "UM");
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

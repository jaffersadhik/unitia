package unitiahttpd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.WhiteListedIP;
import com.winnovature.unitia.util.dao.Insert;
import com.winnovature.unitia.util.http.Utility;
import com.winnovature.unitia.util.misc.ACKIdGenerator;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.ToJsonString;
import com.winnovature.unitia.util.misc.WinDate;
import com.winnovature.unitia.util.redis.QueueSender;

public class GsonProcessor {
	
	Map<String,Object> msgmap=null;
	Map<String,Object> logmap=null;
	
	public String processRequestA(HttpServletRequest request,Map<String,Object> msgmap,Map<String,Object> logmap) throws UnsupportedEncodingException
	{		
		
			try{
				
				String custIP		=	request.getHeader("X-FORWARDED-FOR");

				if(custIP==null){
					custIP=request.getRemoteHost();
				}
				
				if(custIP==null){
					custIP="";
				}
				msgmap.put(MapKeys.CUSTOMERIP, custIP);
			
				////System.out.println("msgmap : "+msgmap);
				
				
            msgmap.put(MapKeys.CONTENT_LENGTH,request.getContentLength());
            
			msgmap.put(MapKeys.PROTOCOL, "http");
			msgmap.put(MapKeys.INTERFACE_TYPE, "gson");

			this.logmap=logmap;
			this.msgmap=msgmap;
		
			boolean isEmail = false;
			//RequestObject requestObj	=	null;
            String gsonstring=getRequestFromBody(request);

            if(	gsonstring==null && gsonstring.trim().length()<1){
    			logmap.put("logname", "invalidjson");
    			logmap.put("gsonstring", gsonstring);
            	return new RequestProcessor().getRejectedResponse(MessageStatus.HTTP_QS_EMPTY_MESSAGE);
            }
            
            ////System.out.println("String : "+toMap(gsonstring));

            Map<String,Object> requestmap=toMap(gsonstring);
            ////System.out.println("requestmap : "+requestmap);
            if(requestmap==null&&requestmap.size()<1){
    			logmap.put("logname", "invalidjson");
    			logmap.put("gsonstring", gsonstring);
            	return new RequestProcessor().getRejectedResponse(MessageStatus.HTTP_QS_EMPTY_MESSAGE);
            }
					
			String username=(String)requestmap.get("username");
			if(username==null){
				username="";
			}
			msgmap.put(MapKeys.USERNAME, username.toLowerCase());
			

			Map<String,String> partnerMap =PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString());
			if(partnerMap == null || partnerMap.isEmpty()){
				return new RequestProcessor().getRejectedResponse(MessageStatus.INVALID_USERNAME);// invalid credentials
			}
			
			String password=(String)requestmap.get("password");

			if(password==null||!password.equals(partnerMap.get("password"))){
    			logmap.put("logname", "invalidrequest");
				logmap.put("partnerMap ", partnerMap.toString());
				logmap.put("income password", password);
				return new RequestProcessor().getRejectedResponse(MessageStatus.INVALID_PASSWORD);// invalid credentials

			}
			
			if(!WhiteListedIP.getInstance().isWhiteListedIP(msgmap.get(MapKeys.USERNAME).toString(), custIP)){
    			logmap.put("logname", "invalidip");
				logmap.put(MapKeys.STATUSID, ""+MessageStatus.INVALID_IP);
				logmap.put("customerIP ",custIP);
				return new RequestProcessor().getRejectedResponse(MessageStatus.INVALID_IP);
			}
			msgmap.put(MapKeys.SUPERADMIN,PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.SUPERADMIN));
			msgmap.put(MapKeys.ADMIN,PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.ADMIN));

			/* Identifying account is Active/De-Active */
			String accStatus = partnerMap.get("status");
			if(accStatus.equals("0")) {
				return new RequestProcessor().getRejectedResponse(MessageStatus.ACCOUNT_INACTIVATED);// Account inactivated
			}
			
			String msgClass = partnerMap.get("msgclass");
		
			msgmap.put(MapKeys.MSGCLASS, msgClass);
			
			logmap.putAll(msgmap);
		
			List<Map<String,Object>> msgmaplist=getList(msgmap,requestmap,logmap);
			
			if(msgmaplist.size()<1){
				
				logmap.put("logname", "invalidjson");
    			logmap.put("gsonstring", gsonstring);
    			
            	return new RequestProcessor().getRejectedResponse(MessageStatus.HTTP_QS_EMPTY_MESSAGE);

			}else if(msgmaplist.size()==1){
				
				if(!new Utility().sendQueue(msgmaplist.get(0),logmap)){
					
					return new RequestProcessor().getRejectedResponse(MessageStatus.SENT_TO_QUEUE_FAILED);
				}else{
					List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
					list.add(msgmaplist.get(0));
					new QueueSender().sendLtoRequestLog(list, false, logmap);
				}
				
			}else{
				
				////System.out.println("Multiple");
				if(!new Insert().insertA(msgmaplist)){
					
					return new RequestProcessor().getRejectedResponse(MessageStatus.SENT_TO_QUEUE_FAILED);
				}else{
					
					new QueueSender().sendLtoRequestLog(msgmaplist, false, logmap);
				}
			}
			
			return getWhitelableAcceptedResponse();

			}catch(Exception e){
				
				return getSystemErrorResponse(e);

			}
				
	}
	
	private Mojo getMojo(String json){
		
        Gson gson = new Gson(); 
        
        Mojo mojo=gson.fromJson(json, Mojo.class);
        
        ////System.out.println(mojo);
        ////System.out.println(mojo.getUsername());
        ////System.out.println(mojo.getPassword());
        ////System.out.println(mojo.getSmslist());

        ////System.out.println(mojo);

        return mojo;

        
	}
	
	private Map<String,Object> getJsonMap(Mojo mojo){
		
		Map<String,Object> map=new HashMap<String,Object>();
		if(mojo.getPassword()!=null){
			map.put("password", mojo.getPassword());
		}
		if(mojo.getUsername()!=null){
			map.put("username", mojo.getUsername());
		}
		
		
		List<SMS> smslist=mojo.getSmslist();
		List<Map<String,Object>> smslistarray=new ArrayList<Map<String,Object>>();
		if(smslist!=null){
			
			for(int i=0;i<smslist.size();i++){
				
				SMS sms=smslist.get(i);
				
				Map<String,Object> bean=new HashMap<String,Object>();
				
				if(sms.getParam1()!=null){
					
					bean.put("param1", sms.getParam1());
				}
				
				if(sms.getParam2()!=null){
					
					bean.put("param2", sms.getParam2());
				}

				if(sms.getParam3()!=null){
					
					bean.put("param3", sms.getParam3());
				}
				
				if(sms.getParam4()!=null){
					
					bean.put("param4", sms.getParam4());
				}

				
				if(sms.getScheduletime()!=null){
					
					bean.put("scheduletime", sms.getScheduletime());
				}
				
				if(sms.getEntityid()!=null){
					
			//		bean.put("entityid", sms.getEntityid());
					
					String entityid=sms.getEntityid();

					if(entityid==null){
						entityid="";
					}
					
					if(entityid.length()>30){
						entityid=entityid.substring(0,30);
					}

					bean.put(MapKeys.ENTITYID_CUSTOMER, entityid);

				}

				if(sms.getTemplateid()!=null){
					
				//	bean.put("templateid", sms.getTemplateid());
					String templateid=sms.getTemplateid();
					if(templateid==null){
						templateid="";
					}
					
					if(templateid.length()>30){
						templateid=templateid.substring(0,30);
					}
		
					bean.put(MapKeys.TEMPLATEID_CUSTOMER,templateid );
				}
				
				
				if(sms.getFrom()!=null){
					
					bean.put("from", sms.getFrom());
				}

				
				if(sms.getContent()!=null){
					
					bean.put("content", sms.getContent());
				}

				
				if(sms.getTolist()!=null){
					
					bean.put("tolist", sms.getTolist());
				}

				////System.out.println(bean);
				smslistarray.add(bean);
			}
		}
		
		
		if(smslistarray.size()>0){
			
			map.put("smslist",smslistarray);

		}
		
		////System.out.println("smslist : "+map);
		return map;
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
	

    private List<Map<String, Object>> getList(Map<String, Object> msgmap2, Map<String, Object> requestmap, Map<String, Object> logmap) {

    	List<Map<String, Object>> resultlist=new ArrayList<Map<String, Object>>();

    	try{
    	List<Map<String,Object>> smslist=(List<Map<String,Object>>)requestmap.get("smslist");
    	if(smslist==null||smslist.size()<1){
    		
    		return null;
    	}
    	

    	for(int i=0,max=smslist.size();i<max;i++){
    		
    		Map<String,Object> smsmap=smslist.get(i);
    		
    		String scheTime=(String)smsmap.get("scheduletime");
    				
    		smsmap.put(MapKeys.SCHEDULE_TIME_STRING, scheTime);
    		
    		smsmap.put(MapKeys.USERNAME, msgmap2.get(MapKeys.USERNAME));
    		
		
    		List<String> mobilelist=(List<String>)smsmap.get("tolist");
    		
    		if(mobilelist==null||mobilelist.size()<1){
    			
    			continue;
    		}
    		
    		Map<String,Object> clonemap=null;
    		
    		for(int j=0,max1=mobilelist.size();j<max1;j++){
    			
    			 
    			String mnumber=mobilelist.get(j);
    			
    			if(mnumber==null || mnumber.trim().length()<1){
    				
    				continue;
    			}
    			
    			clonemap=clonemap(msgmap2,smsmap);

    			clonemap.put(MapKeys.MSGID, ACKIdGenerator.getAckId());
    			clonemap.put(MapKeys.MOBILE, mnumber);
    			
    			int mobileFlag = new Utility().mobileValidation(clonemap);
    			
    			
				if(mobileFlag !=0){
    				continue;
				}
				
				mnumber = new com.winnovature.unitia.util.misc.Utility().prefix91map(clonemap,clonemap.get(MapKeys.USERNAME).toString(), clonemap.get(MapKeys.MOBILE).toString() ); 

	    		new Utility().validateScheduleBlockout(scheTime, clonemap);

    			clonemap.put(MapKeys.MOBILE, mnumber);

    			resultlist.add(clonemap);
    		}
    	}
		}catch(Exception e){
			
			logmap.put("error", ErrorMessage.getMessage(e));
		}
		return resultlist;
	}



	private Map<String, Object> clonemap(Map<String, Object> msgmap2, Map<String, Object> smsmap) {
		Map<String,Object> resultmap=new HashMap<String,Object>();
		
		
		String content=(String)smsmap.get("content");
		
		if(content==null||content.trim().length()<1){
			
			return null;
		}
		
		String senderid=(String)smsmap.get("from");
		
		if(StringUtils.isNotEmpty(senderid))
		{
			senderid = senderid.trim();
			if(senderid.length()>15)
				senderid = senderid.substring(0, 15);
		}
		
		resultmap.put(MapKeys.SENDERID, senderid);
		resultmap.put(MapKeys.SENDERID_ORG, senderid);

		resultmap.put(MapKeys.MESSAGE, content);
		resultmap.put(MapKeys.FULLMSG, content);
		
		new RequestProcessor().replaceSpace(resultmap);
		
		try {
			setMsgType(resultmap);
		} catch (UnsupportedEncodingException e) {
			
		}
		
		Iterator itr=msgmap2.keySet().iterator();
		
		while(itr.hasNext()){
			String key=itr.next().toString();
			Object value=msgmap2.get(key);
			resultmap.put(key, value);
		}
		
		Iterator itr1=smsmap.keySet().iterator();
		
		while(itr1.hasNext()){
		
			String key=itr1.next().toString();
			
			Object value=smsmap.get(key);
			
			if(key.equals("tolist")){
				continue;
			}
			resultmap.put(key, value);
		}
		return resultmap;
	}



	public   String getRequestFromBody(HttpServletRequest aRequest)

    {
		try {
			return read(aRequest.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    }
	
	private String read(InputStream is){


	    BufferedReader br = null;

	    StringBuffer sb = new StringBuffer();

	    String reqString = null;

	     

	     int bytesRead = -1;

	      try

	    {

	      char[] charBuffer = new char[1024];

	      br = new BufferedReader(new InputStreamReader(is));

	     

	     while ((bytesRead = br.read(charBuffer)) > 0)

	    {

	      sb.append(charBuffer, 0, bytesRead);

	    }

	      reqString = sb.toString();

	    }

	      catch (Exception e)

	    {


	    	  e.printStackTrace();
	    }

	      finally

	    {

	      try

	    {

	      if (br != null)

	      br.close();

	    }

	      catch (Exception ex)

	    {

	      ex.printStackTrace();

	    }

	    }

	      ////System.out.println("reqString" +reqString);
	      return reqString;

	    
	}

public  Map<String, Object> toMap(String jsonstring) {
		/*
		ObjectMapper mapper = new ObjectMapper();
		
		   try { 
	        	  
	        	return  mapper.readValue(jsonstring, Map.class);
	  
	        } 
	  
	        catch (Exception e) { 
	            e.printStackTrace(); 
	        } 
		return null;
		
		
		*/
	
	
		Mojo mojo=getMojo(jsonstring);
		
		return getJsonMap(mojo);
	}

public static void main(String args[]){

	File file=new File("E:\\fadhil1\\json.txt");
	GsonProcessor obj=new GsonProcessor();
	try {
		String str=obj.read(new FileInputStream(file));
		
		////System.out.println(obj.toMap(str));
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

private void setMsgType(Map<String,Object>  msgmap) throws UnsupportedEncodingException{
	 
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
	  private  boolean isASCII(String word) throws UnsupportedEncodingException{
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

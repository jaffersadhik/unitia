package com.winnovature.unitia.util.processor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.account.MissedCallForward;
import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.connect.OnewayHTTPSURLConnector;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.redis.QueueSender;


public class DNHttpPost
{
    
	public static final String DATE_FORMAT = "yyMMddHHmmss";

	Map<String, Object> msgmap=null;
    
	public DNHttpPost(Map<String, Object> data) {
		msgmap=data;
	}

	public void doProcess()
    {
		
		
		String attempttype=null;
		
		if(msgmap.get(MapKeys.ATTEMPT_TYPE)!=null){
			attempttype=msgmap.get(MapKeys.ATTEMPT_TYPE).toString();
		}
		if(attempttype!=null&&attempttype.equals("9")){
			
			String url=MissedCallForward.getInstance().getUrl(msgmap.get(MapKeys.PARAM2).toString());
			
			 connect(url,attempttype);

		}else{
			
		String url=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.DLR_POST_URL);
    
		 connect(url,attempttype);
		 
		}
    }
    
    private void connect(String url, String attempttype) {
		

		long start=System.currentTimeMillis();
		
		String response="";
		
		
				
				if(url.startsWith("https")){
					
					OnewayHTTPSURLConnector connector=new OnewayHTTPSURLConnector();
					
					try {
						
						response=connector.connectPostMethod(url, getURLParamsMap(attempttype));
					} catch (Exception e) {
						response=ErrorMessage.getMessage(e);
					}
				}else{
					
					if(!url.endsWith("?")){
						url+="?";
					}
					url+=getURLParams(attempttype);

					response=deliverThroughURL(url);
				}
				
				
		
		if(response.length()>500){
			
			response=response.substring(0, 499);
		}
		msgmap.put(MapKeys.DNPOSTSTATUS, response);
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		
		logmap.put("username", "sys");		

		logmap.putAll(msgmap);
		
		logmap.put("module", "DNHTTPPOST");
		
		logmap.put("logname", "clientdnhttppost");
		long end=System.currentTimeMillis();
		logmap.put("clientdnhttpposttimetaken",""+(end-start));

		sendToQ(msgmap,logmap);
		
		
        new FileWrite().write(logmap);

		
	}

	private void sendToQ(Map<String, Object> msgmap, Map<String, Object> logmap) {
    	
		
		new QueueSender().sendL("dnpostpool", msgmap, false,logmap);
		
		logmap.put("sendQueue status", "message sent to dnpostpool Single Sent Successfully");

		
	}

    
	private HashMap getURLParamsMap(String attempttype) {
		
    	SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT);
    	HashMap<String,String> data=new HashMap<String,String> ();
    	data.put("username", URLEncoder.encode(msgmap.get(MapKeys.USERNAME).toString()));
    	data.put("rtime", sdf.format(new Date(Long.parseLong(msgmap.get(MapKeys.RTIME).toString()))));
    	
    	if(msgmap.get(MapKeys.CARRIER_DONETIME)!=null){
    	data.put("ctime", sdf.format(new Date(Long.parseLong(msgmap.get(MapKeys.CARRIER_DONETIME).toString()))));
    	}
    	if(msgmap.get(MapKeys.STATUSID)!=null){
    	data.put("statusid", URLEncoder.encode(msgmap.get(MapKeys.STATUSID).toString()));
    	data.put("status", URLEncoder.encode(MessageStatus.getInstance().getState(msgmap.get(MapKeys.STATUSID).toString())));

    	}
    	data.put("operator", URLEncoder.encode(msgmap.get(MapKeys.OPERATOR).toString()));
    	data.put("circle", URLEncoder.encode(msgmap.get(MapKeys.CIRCLE).toString()));
    	
    	
    	if(msgmap.get(MapKeys.SENDERID_ORG)!=null){
    	data.put("from", URLEncoder.encode(msgmap.get(MapKeys.SENDERID_ORG).toString()));
    	}
    	if(msgmap.get(MapKeys.MOBILE)!=null){
    	data.put("to", URLEncoder.encode(msgmap.get(MapKeys.MOBILE).toString()));
    	}
    	data.put("ackid", URLEncoder.encode(msgmap.get(MapKeys.ACKID).toString()));
    	
    	if(attempttype==null || !attempttype.equals("9")){
    	data.put("totalsmscount", URLEncoder.encode(msgmap.get(MapKeys.TOTAL_MSG_COUNT)==null?"0":msgmap.get(MapKeys.TOTAL_MSG_COUNT).toString()));
    	data.put("usedcredit",URLEncoder.encode(msgmap.get(MapKeys.CREDIT)==null?"0":msgmap.get(MapKeys.CREDIT).toString()));

    	}
    	
    	
    	if(msgmap.get(MapKeys.PARAM1)!=null){
    	
        	data.put("param1",URLEncoder.encode(msgmap.get(MapKeys.PARAM1).toString()));

    	}

    	if(msgmap.get(MapKeys.PARAM2)!=null){

        	data.put("param2",URLEncoder.encode(msgmap.get(MapKeys.PARAM2).toString()));
    	}
    	
    	if(msgmap.get(MapKeys.PARAM3)!=null){
        	

        	data.put("param3",URLEncoder.encode(msgmap.get(MapKeys.PARAM3).toString()));
    	}
    	
    	if(msgmap.get(MapKeys.PARAM4)!=null){
        	

        	data.put("param4",URLEncoder.encode(msgmap.get(MapKeys.PARAM4).toString()));

    	}
    	
    	return data;
    }

	private String getURLParams(String attempttype) {
		
    	SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT);
    	StringBuffer sb=new StringBuffer();
    	
    	sb.append("username=").append(URLEncoder.encode(msgmap.get(MapKeys.USERNAME).toString())).append("&");
    	sb.append("rtime=").append(sdf.format(new Date(Long.parseLong(msgmap.get(MapKeys.RTIME).toString())))).append("&");
    	
    	if(msgmap.get(MapKeys.CARRIER_DONETIME)!=null){
    	sb.append("ctime=").append(sdf.format(new Date(Long.parseLong(msgmap.get(MapKeys.CARRIER_DONETIME).toString())))).append("&");
    	}
    	
    	if(msgmap.get(MapKeys.STATUSID)!=null){

    	sb.append("statusid=").append(URLEncoder.encode(msgmap.get(MapKeys.STATUSID).toString())).append("&");
    	sb.append("status=").append(URLEncoder.encode(MessageStatus.getInstance().getState(msgmap.get(MapKeys.STATUSID).toString()))).append("&");
    	
    	}
    	sb.append("operator=").append(URLEncoder.encode(msgmap.get(MapKeys.OPERATOR).toString())).append("&");
    	sb.append("circle=").append(URLEncoder.encode(msgmap.get(MapKeys.CIRCLE).toString())).append("&");
    	
    	if(msgmap.get(MapKeys.SENDERID_ORG)!=null){

    	sb.append("from=").append(URLEncoder.encode(msgmap.get(MapKeys.SENDERID_ORG).toString())).append("&");
    	}
    	
    	
    	sb.append("to=").append(URLEncoder.encode(msgmap.get(MapKeys.MOBILE).toString())).append("&");
    	sb.append("ackid=").append(URLEncoder.encode(msgmap.get(MapKeys.ACKID).toString())).append("&");
    	
    	if(attempttype==null || !attempttype.equals("9")){

    	sb.append("totalsmscount=").append(URLEncoder.encode(msgmap.get(MapKeys.TOTAL_MSG_COUNT)==null?"0":msgmap.get(MapKeys.TOTAL_MSG_COUNT).toString())).append("&");
    	sb.append("usedcredit=").append(URLEncoder.encode(msgmap.get(MapKeys.CREDIT)==null?"0":msgmap.get(MapKeys.CREDIT).toString())).append("&");

    	}
    	
    	
    	if(msgmap.get(MapKeys.PARAM1)!=null){
    	
        	sb.append("param1=").append(URLEncoder.encode(msgmap.get(MapKeys.PARAM1).toString())).append("&");

    	}

    	if(msgmap.get(MapKeys.PARAM2)!=null){
        	
        	sb.append("param2=").append(URLEncoder.encode(msgmap.get(MapKeys.PARAM2).toString())).append("&");

    	}
    	
    	if(msgmap.get(MapKeys.PARAM3)!=null){
        	
        	sb.append("param3=").append(URLEncoder.encode(msgmap.get(MapKeys.PARAM3).toString())).append("&");

    	}
    	
    	if(msgmap.get(MapKeys.PARAM4)!=null){
        	
        	sb.append("param4=").append(URLEncoder.encode(msgmap.get(MapKeys.PARAM4).toString())).append("&");

    	}
    	
    	return sb.toString();
    }

	private String deliverThroughURL( String urlstring)
    {
        HttpURLConnection httpConnection = null;
        final int responseCode = 0;
        String urlResponse = "";
        BufferedReader reader = null;
        final OutputStream outStream = null;
        try
        {
            URL url=new URL(urlstring);
        	httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            httpConnection.setUseCaches(false);
            httpConnection.setAllowUserInteraction(false);
            httpConnection.setRequestMethod("POST");
            reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            int val = 0;
            while ((val = reader.read()) > -1)
            {
                urlResponse = String.valueOf(urlResponse) + (char) val;
            }
        }
        catch (Exception e)
        {
             e.printStackTrace();
             urlResponse=ErrorMessage.getMessage(e);
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (Exception ex)
            {}
        }
        try
        {
            reader.close();
        }
        catch (Exception ex2)
        {}
        
        return urlResponse;
    }
    
  
    
}

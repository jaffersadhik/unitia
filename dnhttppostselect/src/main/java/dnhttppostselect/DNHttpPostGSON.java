package dnhttppostselect;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.misc.ToJsonString;
import com.winnovature.unitia.util.redis.QueueSender;


public class DNHttpPostGSON
{
    
	public static final String DATE_FORMAT = "yyMMddHHmmss";

	List<Map<String, Object>> msglist=null;
    
	public DNHttpPostGSON(List<Map<String, Object>> data) {
		
		msglist=data;
	}

	public void doProcess()
    {
		
		List<Map<String,String>> bodylist=new ArrayList<Map<String,String>>();
		
		for(int i=0;i<msglist.size();i++){
		
			Map<String, Object> msgmap=msglist.get(i);
			
			String url=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.DLR_POST_URL);

			Map<String,String> extraparam=getExtraParam(url);

			msgmap.put("clinet_url", url);
			
			String attempttype=null;
			
			if(msgmap.get(MapKeys.ATTEMPT_TYPE)!=null){
				
				attempttype=msgmap.get(MapKeys.ATTEMPT_TYPE).toString();
				
			}else{
				
				attempttype="0";
			}
			
			HashMap reqmap1=getURLParamsMap(msgmap,attempttype) ;
			reqmap1.putAll(extraparam);
			msgmap.put("body",reqmap1);
			bodylist.add(reqmap1);
			


		}
 

		String url=getUrl(msglist);

		String response= connect(url,ToJsonString.toString(bodylist));
		
		sendToQ(msglist,response);
    }
    
    private String connect(String url, String gsonstring) {

		

		long start=System.currentTimeMillis();
		
		String response="";
		
		
				
				if(url.startsWith("https")){

					
						response=new HttpsConnector().send(url, gsonstring);
					
				}else{
					
					response=new HttpConnector().send(url, gsonstring);

				}
				
				
		
		if(response.length()>500){
			
			response=response.substring(0, 499);
		}
		Map<String,Object> logmap=new HashMap<String,Object>();
		
		logmap.put("username", "sys");		

		
		logmap.put("module", "DNHTTPPOST");
		
		logmap.put("body", gsonstring);

		logmap.put("logname", "clientdnhttppost");
		long end=System.currentTimeMillis();
		logmap.put("clientdnhttpposttimetaken",""+(end-start));

		
		
        new FileWrite().write(logmap);

		return response;
	
		
	}

	private String getUrl(List<Map<String, Object>> msglist) {
		Map<String, Object> msgmap=msglist.get(0);
		
		String url=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.DLR_POST_URL);

		if(url.indexOf("?")>0){
			
			url=url.substring(0,url.indexOf("?"));
		}
		
		return url;
	}

	private Map<String, String> getExtraParam(String urls) {
		
    		if(!(urls.indexOf("?")>0)){
    			
    			return new HashMap<String,String>();
    		}
    	   URL url=null;
			try {
				url = new URL(urls);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			 Map<String,String> reqmap=null;
			if(url!=null){
				
				String qs=url.getQuery();
				if(qs!=null){
				reqmap= getRequestParam(url.getQuery());
				}else{

					reqmap=new HashMap<String,String>();
					
				}
			}else{
				reqmap=new HashMap<String,String>();
				
			}
    	
		return reqmap;
	}


	
	public Map getRequestParam(final String queryString)
    {
        final StringTokenizer st = new StringTokenizer(queryString, "&");
        final HashMap reqParam = new HashMap();
        while (st.hasMoreTokens())
        {
            final StringTokenizer st2 = new StringTokenizer(st.nextToken(), "=");
            String key = "";
            String value = "";
            if (st2.hasMoreTokens())
            {
                key = st2.nextToken();
                if (st2.hasMoreTokens())
                {
                    value = st2.nextToken();
                }
            }
            reqParam.put(key, value);
        }
        return reqParam;
    }
	
	private void sendToQ(List<Map<String, Object>> msglist, String response) {
    	
		
		for(int i=0;i<msglist.size();i++){
			
			Map<String, Object> msgmap=msglist.get(i);
			msgmap.put(MapKeys.DNPOSTSTATUS, response);

			new QueueSender().sendL("dnpostpool", msgmap, false,new HashMap());

		}
		
		

		
	}

    
	private HashMap<String,String> getURLParamsMap(Map<String,Object> msgmap,String attempttype) {
		
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
        	data.put("phone", URLEncoder.encode(msgmap.get(MapKeys.MOBILE).toString()));

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
    	
    	
    	if(msgmap.get(MapKeys.FULLMSG)!=null&&msgmap.get(MapKeys.FULLMSG).toString().trim().length()>0){
    		
        	data.put("content",URLEncoder.encode(msgmap.get(MapKeys.FULLMSG).toString()));

    	}
    	return data;
    }

  
    
}

package dnhttppostselect;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    	data.put("statusid", msgmap.get(MapKeys.STATUSID).toString());
    	String status=(String)MessageStatus.getInstance().getState(msgmap.get(MapKeys.STATUSID).toString());
    	
    	if(status==null){
    		status="UNKNOWN";
    	}
    	data.put("status", status);

    	String statusdescription=MessageStatus.getInstance().getDescription(msgmap.get(MapKeys.STATUSID).toString());
    	if(statusdescription==null){
    		statusdescription="unknown Exception";
    	}
    	
    	if(msgmap.get(MapKeys.STATUSID).toString().equals("000")){
    		statusdescription="SMS Delivered to HandSet Successfully";
    	}
    	data.put("statusdescription", statusdescription);

    	}

    	if(msgmap.get(MapKeys.SENDERID)!=null){
        	data.put("from", URLEncoder.encode(msgmap.get(MapKeys.SENDERID).toString()));
        }
        if(msgmap.get(MapKeys.MOBILE)!=null){
        
        	data.put("mobile", URLEncoder.encode(msgmap.get(MapKeys.MOBILE).toString()));
       }

    	
        Set<String> customparam =DNCustomParameter.getInstance().getCustomParam(msgmap.get(MapKeys.USERNAME).toString());
        
        if(customparam!=null){
        	
        Iterator<String> itr=customparam.iterator();
        
        while(itr.hasNext()){
        	
        	String paramname=itr.next();
        	
        	String paramvalue=(String)msgmap.get(paramname);
        	
        	if(paramvalue!=null&&paramvalue.length()>0){
        		
            	data.put(paramname, paramvalue);

        	}
        }
        }
        return data;
    }

  
    
}

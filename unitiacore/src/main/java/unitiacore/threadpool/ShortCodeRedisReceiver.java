package unitiacore.threadpool;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.winnovature.unitia.util.account.MissedCallForward;
import com.winnovature.unitia.util.account.MissedCallSMS;
import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.ShortCodeAccount;
import com.winnovature.unitia.util.misc.ACKIdGenerator;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.redis.QueueSender;
import com.winnovature.unitia.util.redis.RedisReader;


public class ShortCodeRedisReceiver extends  Thread {

	static boolean GRACESTOP=false;
	String poolname=null;
	String redisid =null;
	int threadid=0;
	long lastupdate=System.currentTimeMillis();
	String tname="";
	String id =ACKIdGenerator.getAckId();
	
	
	public ShortCodeRedisReceiver(int threadid,String poolname,String redisid){
		this.threadid=threadid;
		this.redisid=redisid;
		this.poolname=poolname;
		this.tname=this.threadid+" : "+this.redisid+" : "+ this.poolname;
	}
	public void run(){
		
		RedisReader reader=new RedisReader();
		
		while(!GRACESTOP){
			
			try{	
			long start=System.currentTimeMillis();	
			ping();	
			Map<String,Object> data=null;

			lastupdate=System.currentTimeMillis();
			
				data=reader.getData(poolname,redisid);

				
			
			if(data!=null){
				
				data.put("tname", tname);
				data.put(MapKeys.INSERT_TYPE, "submit");
				
				List<Map<String,String>> datalist=ShortCodeAccount.getInstance().getData(data.get(MapKeys.PARAM2).toString());
	
				if(datalist==null){
					
					log(data,"shortcode not configured");
					continue;
				}
				
				if(findKeyWord(data,datalist)){
				
				
					

					Map<String,Object> msgmap=new HashMap<String,Object>();
					
					msgmap.putAll(data);
					
					msgmap.put(MapKeys.SENDERID,data.get("senderid"));

					msgmap.put(MapKeys.SENDERID_ORG, data.get("senderid"));

					String ackid=ACKIdGenerator.getAckId();
					msgmap.put(MapKeys.ACKID,ackid );

					msgmap.put(MapKeys.MSGID, ackid);
					
					String mnumber = new com.winnovature.unitia.util.misc.Utility().prefix91(data.get("username").toString(), msgmap.get(MapKeys.MOBILE).toString() ); 
					
					msgmap.put(MapKeys.MOBILE,mnumber);

					data.put(MapKeys.PARAM1,ackid);

					msgmap.put(MapKeys.FULLMSG, msgmap.get("sms_content"));
					
					msgmap.put(MapKeys.MESSAGE, msgmap.get("sms_content"));

					setMsgType(msgmap);
					
					new SMSWorker("commonpool",msgmap).doOtp();
				
				
				
				
				new QueueSender().sendL("submissionpool", data, false, new HashMap<String,Object>());

				
				String post_yn=data.get("post_yn").toString();
				
				
				if(post_yn.equals("1")){
					
					String forwardurl=(String)data.get("post_url");
					
					if(forwardurl!=null&&forwardurl.trim().length()>0){
				
						new QueueSender().sendL("httpdn", data, false, new HashMap<String,Object>());

					}
					
				}
				
				
			
			
				}else{
					
					log(data,"keyword/ message pattern  not configured");

				}
			}else{
				
				gotosleep();		
				
				
			}
			
			}catch(Exception e){
				
				ping(e);
			}
			}
			
		
			
		}
	
	
	  private boolean findKeyWord(Map<String, Object> data, List<Map<String, String>> datalist) {
		
		  if(datalist!=null){
			  
			  for(int i=0,max=datalist.size();i<max;i++){
			  
				  Map<String,String> datamap=datalist.get(i);
			  
				  String pattern =datamap.get("message_pattern");
				  
				  try{
					if(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(data.get(MapKeys.FULLMSG).toString()).matches())
					{
						data.putAll(datamap);
						
						return true;
					}
				  }catch(Exception e){
					  
				  }
			  }
		  }
		return false;
	}
	private void log(Map<String, Object> data, String errormessage) {
		
			Map<String,Object> logmap=new HashMap<String,Object>();
			
			logmap.putAll(data);
			logmap.put("module", "shortcode");
			logmap.put("logname", "shortcodeerror");
			logmap.put("errormessage", errormessage);

	        new FileWrite().write(logmap);
		
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
	  
	 private void setMsgType(Map<String,Object> msgmap) throws UnsupportedEncodingException{
		 
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


	private void stats(String poolname2, String redisid2, long start, long end) {
		
		Map<String,Object> logmap1=new HashMap<String,Object>();
		
		logmap1.put("username", "sys");
		logmap1.put("processtime",""+ (end-start));
		logmap1.put("queuename", poolname2);
		logmap1.put("redisid", redisid2);
		logmap1.put("logname", "totaltime");


        new FileWrite().write(logmap1);
		
	}
	private void ping(Exception e) {
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("logname", "routerpingerror");
		logmap.put("tname", tname);
		logmap.put("id",id);

		logmap.put("username", "sys");
		logmap.put("Threadname", this.getName());

		logmap.put("error", ErrorMessage.getMessage(e));

        new FileWrite().write(logmap);
		
	}
	private void ping() {
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.put("username", "sys");
		logmap.put("logname", "routerping");
		logmap.put("tname", tname);
		logmap.put("id",id);

		logmap.put("Threadname", this.getName());

        new FileWrite().write(logmap);
		
	}
	private void gotosleep() {
		
		try{
			
			Thread.sleep(50L);
		}catch(Exception e){
			
		}
	}
	
	public boolean isDisplay(){
		
		long diff=System.currentTimeMillis()-lastupdate;
		
		if(diff>60000){
			
			return true;
		}
		
		return false;
	}
	
	public String getTName(){
		
		return tname;
	}
}

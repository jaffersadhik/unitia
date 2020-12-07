package processor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.account.MissedCallForward;
import com.winnovature.unitia.util.account.MissedCallSMS;
import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.VMNAccount;
import com.winnovature.unitia.util.misc.ACKIdGenerator;
import com.winnovature.unitia.util.misc.ErrorMessage;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.redis.QueueSender;
import com.winnovature.unitia.util.redis.RedisReader;



public class MissedCallRedisReceiver extends  Thread {

	static boolean GRACESTOP=false;
	String poolname=null;
	String redisid =null;
	int threadid=0;
	long lastupdate=System.currentTimeMillis();
	String tname="";
	String id =ACKIdGenerator.getAckId();
	
	
	public MissedCallRedisReceiver(int threadid,String poolname,String redisid){
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
				
				String username=VMNAccount.getInstance().getUsername(data.get(MapKeys.PARAM2).toString());
	
				if(username==null||PushAccount.instance().getPushAccount(username)==null){
					
					data.put(MapKeys.STATUSID, ""+MessageStatus.VMN_USERNAME_MAPPING_MISSING);
					
					new QueueSender().sendL("submissionpool", data, false, new HashMap<String,Object>());
					
					continue;
				}
				
				data.put(MapKeys.USERNAME, username);
				
				Map<String,String> smsdata=MissedCallSMS.getInstance().getSMS(data.get(MapKeys.PARAM2).toString());
				
				if(smsdata!=null){
					

					Map<String,Object> msgmap=new HashMap<String,Object>();
					
					msgmap.putAll(data);
					
					msgmap.put(MapKeys.SENDERID,smsdata.get("senderid"));

					msgmap.put(MapKeys.SENDERID_ORG, smsdata.get("senderid"));

					String ackid=ACKIdGenerator.getAckId();
					msgmap.put(MapKeys.ACKID,ackid );

					msgmap.put(MapKeys.MSGID, ackid);
					
					String mnumber = new com.winnovature.unitia.util.misc.Utility().prefix91(username, msgmap.get(MapKeys.MOBILE).toString() ); 
					
					msgmap.put(MapKeys.MOBILE,mnumber);

					data.put(MapKeys.PARAM1,ackid);

					msgmap.put(MapKeys.FULLMSG, smsdata.get("sms"));
					
					msgmap.put(MapKeys.MESSAGE, smsdata.get("sms"));

					msgmap.put(MapKeys.PARAM1, data.get(MapKeys.ACKID));
					
					setMsgType(msgmap);
					
					new SMSWorker("commonpool",msgmap).doOtp(redisid,tname);
				
				}
				
				
				new QueueSender().sendL("submissionpool", data, false, new HashMap<String,Object>());

				String forwardurl=MissedCallForward.getInstance().getUrl(data.get(MapKeys.PARAM2).toString());
				
				if(forwardurl!=null){
					
					new QueueSender().sendL("httpdn", data, false, new HashMap<String,Object>());

				}
				
				long end=System.currentTimeMillis();
				
				stats(poolname,redisid,start,end);
			}else{
				
				gotosleep();		
				
				
			}
			
			}catch(Exception e){
				
				ping(e);
			}
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

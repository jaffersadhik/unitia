package com.winnovature.unitia.util.optin;

import java.util.HashMap;
import java.util.Map;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
import com.winnovature.unitia.util.misc.MessageStatus;
import com.winnovature.unitia.util.redis.QueueSender;

public class SMSProcessor {
	
	String logname=null;
	Map<String,Object> msgmap=null;
	
	private boolean isfurtherprocess=true;
	
	public SMSProcessor(){
		
	}
	public SMSProcessor(String logname,Map<String,Object> msgmap,boolean isfurtherprocess){
		
		this.logname=logname;
		this.msgmap=msgmap;
		this.isfurtherprocess=isfurtherprocess;
	}
	
	
		
	public void doOptin() throws Exception{
		
		if(isfurtherprocess){
			String username=msgmap.get(MapKeys.USERNAME).toString();
			String mobile=msgmap.get(MapKeys.MOBILE).toString();
			
			if(PushAccount.instance().getPushAccount(username).get(MapKeys.OPTIN_TYPE).equals("1")){
			if(new OptinProcessor().isOptin(username, mobile)){
				msgmap.put("optin", "yes");

				return ;
			}else{
				msgmap.put("optin", "no");

				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.MOBILE_NOT_REGISTERED_OPTIN);

				isfurtherprocess=false;
			}
			
			}
		}
		
		return ;
	}
	
	public void doOptout()  throws Exception{
		
		if(isfurtherprocess){
			String username=msgmap.get(MapKeys.USERNAME).toString();
			String mobile=msgmap.get(MapKeys.MOBILE).toString();
			
			if(PushAccount.instance().getPushAccount(username).get(MapKeys.OPTIN_TYPE).equals("2")){
				if(new OptoutProcessor().isOptout(username, mobile)){
					msgmap.put("optout", "yes");

				isfurtherprocess=false;
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.OPTOUT_MOBILE_NUMBER);

				return ;
				}
			
				msgmap.put("optout", "no");

			}
		}
		
		return ;
	}
	
	
	


	

	
	
	private void gotosleep() {
		
		try{
			
			Thread.sleep(1000L);
			
		}catch(Exception e){
			
		}
		
	}

	public void sentToNextLevel() throws Exception {
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.putAll(msgmap);
		logmap.put("module", "SMSProcessor");
		logmap.put("logname", logname);
	
		if(isfurtherprocess){
			
			String duplicatetype=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.DUPLICATE_TYPE);
			
			if(duplicatetype.equals("1")){
				sendTOCommonPool("duplicate",logmap);
			}else{
				sendTOCommonPool("commonpool",logmap);

			}
			
		}else{

			
		
			doBilling(logmap);
			

		}
		
		new FileWrite().write(logmap);

		
	}

	
	
	
	
		



	
	private void doBilling(Map<String,Object> logmap)  throws Exception {
	
			String queuename="submissionpool";
			
			String smscid="";
			
			if(msgmap.get(MapKeys.SMSCID)!=null){
				
				smscid=msgmap.get(MapKeys.SMSCID).toString();
			}
			
			if(smscid.equals("apps")||smscid.equals("reapps")){
				
				queuename="appspool";
			}
			if(new QueueSender().sendL(queuename, msgmap, false, logmap)){
				
				logmap.put("sms processor status", "Message Sent to billing Queue Successfully");
			
			}else{
				
				logmap.put("sms processor status", "Message Sent to billing Queue Failed message will be loss");

			}
		
		
		
	}
	
	private void sendTOCommonPool(String queuename,Map<String,Object> logmap)  throws Exception{
		
	
		
		if(new QueueSender().sendL(queuename, msgmap, false, logmap)){
			
			logmap.put("sms processor status", "Message Sent to "+queuename+" Queue Successfully");
		
		}else{
			
			logmap.put("sms processor status", "Message Sent to "+queuename+" Queue Failed message will be loss");

		}
	
	
	
}

}

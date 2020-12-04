package dlt;

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
	

	public void doEntityValidation() {
		
		
			
			msgmap.put(MapKeys.ENTITYID, Entity.getInstance().getEntity(msgmap.get(MapKeys.USERNAME).toString(), msgmap.get(MapKeys.SENDERID).toString()));
		
	}
	
	public void dodefaultEntityValidation() {
		
		String entityid=(String)msgmap.get(MapKeys.ENTITYID);
		
		if(entityid==null||entityid.trim().length()<1){
		
			Map<String,String> data=Entity.getInstance().getEntity(msgmap.get(MapKeys.USERNAME).toString());
			
			if(data!=null){
				
				msgmap.put(MapKeys.SENDERID, data.get("senderid"));
				msgmap.put(MapKeys.ENTITYID, data.get("entityid"));

	
			}
		}
		
	}


	public void isDLT() {

		if(isfurtherprocess){
		
			
			String entityid=(String)msgmap.get(MapKeys.ENTITYID);
			
			if(entityid==null||entityid.trim().length()<1){
				
				msgmap.put(MapKeys.STATUSID, ""+MessageStatus.NO_ENTITYID);

				isfurtherprocess=false;
		
		}
		
	}
	}

	public void sentToNextLevel() throws Exception {
		
		Map<String,Object> logmap=new HashMap<String,Object>();
		logmap.putAll(msgmap);
		logmap.put("module", "SMSProcessor");
		logmap.put("logname", logname);
	
		if(isfurtherprocess){
			
	
				sendTOCommonPool("routegroup",logmap);

			
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

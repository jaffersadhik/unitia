package numberingplan;

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

	public void doNumberingPlan() throws Exception{
		
		if(isfurtherprocess){
		
			String mobile=msgmap.get(MapKeys.MOBILE).toString();

			if(!mobile.startsWith("91")){
				
				return ;
			}
			for(int i=7;i>5;i--) {
				
				String series =mobile.substring(2, i);
				
				Map<String,String> npinfo=NumberingPlan.getInstance().getNPInfo(series);
				
				if(npinfo!=null) {
					
					msgmap.put(MapKeys.OPERATOR, npinfo.get(MapKeys.OPERATOR));
					msgmap.put(MapKeys.CIRCLE, npinfo.get(MapKeys.CIRCLE));
		            msgmap.put(MapKeys.OPERATOR_NAME, NumberingPlan.getInstance().getOperatorName(npinfo.get(MapKeys.OPERATOR)));
		            msgmap.put(MapKeys.CIRCLE_NAME, NumberingPlan.getInstance().getCircleName(npinfo.get(MapKeys.CIRCLE)));
		
					
					return ;
				}
			}
			
			if(mobile.trim().length()==12){
				
				msgmap.put(MapKeys.OPERATOR, " ");
				msgmap.put(MapKeys.CIRCLE, " ");
	            msgmap.put(MapKeys.OPERATOR_NAME," ");
	            msgmap.put(MapKeys.CIRCLE_NAME, " ");
	
				return;
			}
			msgmap.put(MapKeys.STATUSID, ""+MessageStatus.MOBILE_SERIES_NOT_REGISTERED_NP);

			isfurtherprocess=false;
		
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

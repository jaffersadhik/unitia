package senderidcheck;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.winnovature.unitia.util.account.PushAccount;
import com.winnovature.unitia.util.account.Route;
import com.winnovature.unitia.util.misc.FileWrite;
import com.winnovature.unitia.util.misc.MapKeys;
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

	
	public void doSenderCheck() throws Exception{
	
	if(isfurtherprocess){
	
		String senderidtype=PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.SENDERID_TYPE);
	
		String senderid=msgmap.get(MapKeys.SENDERID_ORG)==null?"":msgmap.get(MapKeys.SENDERID_ORG).toString();

		if(senderidtype.equals("multiple")){
			
			if(senderid!=null&&senderid.length()>1){
				
				if(!WhiteListedSenderid.getInstance().isWhiteListedSenderid(msgmap.get(MapKeys.USERNAME).toString(), senderid)){
				
					setDefaultSenderID();
					
				}else{
					
					msgmap.put(MapKeys.SENDERID, senderid);
				}
		
			}else{
			
				setDefaultSenderID();
			}
		}else if(senderidtype.equals("static")){
			
			setDefaultSenderID();
		}else{
			
			if(senderid!=null&&senderid.length()<1){

				setDefaultSenderID();
			}else{
				
				msgmap.put(MapKeys.SENDERID, senderid);

			}
		}
		
		senderid =msgmap.get(MapKeys.SENDERID).toString();
		
		if(msgmap.get(MapKeys.ROUTECLASS).toString().equals("2")){
			Pattern pattern = Pattern.compile(".*[^0-9].*");

			if(senderid==null ||senderid.trim().length()<1 ||  !pattern.matcher(senderid).matches()){
				senderid= PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.SENDERID_PROMO);
			}
		}
		
		
		String senderidmask=null;
		
		if(msgmap.get(MapKeys.COUNTRYCODE).toString().equals("91")){
			
			for(int i=1;i<5;i++){
				String key=getKey(msgmap.get(MapKeys.OPERATOR).toString(),msgmap.get(MapKeys.CIRCLE).toString(),i);	
				senderidmask=SenderidSwapping.getInstance().getSwapingSenderid(key, senderid);
			
				if(senderidmask!=null){
					
					break;
				}
				
				}
			
		}else{
		
			senderidmask=InternationalSenderidSwapping.getInstance().getSwapingSenderid(msgmap.get(MapKeys.COUNTRYCODE).toString());
			
		}
		
		
		if(senderidmask==null||senderidmask.trim().length()<1){
			
			senderidmask=senderid;
		}
		if(msgmap.get(MapKeys.SENDERID_ORG)==null||msgmap.get(MapKeys.SENDERID_ORG).toString().trim().length()<1){
			
			msgmap.put(MapKeys.SENDERID_ORG, senderidmask);

		}
		msgmap.put(MapKeys.SENDERID, senderidmask);
	}

	return ;
}

private void setDefaultSenderID() {
	
	String senderid=null;
	if(msgmap.get(MapKeys.ROUTECLASS).toString().equals("1")){
		senderid= PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.SENDERID_TRANS);
}else{

		senderid= PushAccount.instance().getPushAccount(msgmap.get(MapKeys.USERNAME).toString()).get(MapKeys.SENDERID_PROMO);
}
	msgmap.put(MapKeys.SENDERID, senderid);
}


private String getKey(String operator, String circle, int logic) {
	switch(logic) {
	
	case 1:
		 return Route.CONJUNCTION+operator+Route.CONJUNCTION+circle+Route.CONJUNCTION;
	case 2:
		 return Route.CONJUNCTION+operator+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
	case 3:
		 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+circle+Route.CONJUNCTION;
	case 4:
		 return Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION+Route.NULL+Route.CONJUNCTION;
			
	}
	
	return "";
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
